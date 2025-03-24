package dev.fileeditor.oauth2.entities.impl

import dev.fileeditor.oauth2.OAuth2Client
import dev.fileeditor.oauth2.Scope
import dev.fileeditor.oauth2.entities.OAuth2Guild
import dev.fileeditor.oauth2.entities.OAuth2User
import dev.fileeditor.oauth2.exceptions.InvalidStateException
import dev.fileeditor.oauth2.exceptions.MissingScopeException
import dev.fileeditor.oauth2.requests.OAuth2Action
import dev.fileeditor.oauth2.requests.OAuth2Requester
import dev.fileeditor.oauth2.requests.OAuth2URL
import dev.fileeditor.oauth2.session.DefaultSessionController
import dev.fileeditor.oauth2.session.Session
import dev.fileeditor.oauth2.session.SessionController
import dev.fileeditor.oauth2.session.SessionData
import dev.fileeditor.oauth2.state.DefaultStateController
import dev.fileeditor.oauth2.state.StateController
import net.dv8tion.jda.api.exceptions.HttpException
import net.dv8tion.jda.api.requests.Method
import net.dv8tion.jda.internal.utils.Checks
import net.dv8tion.jda.internal.utils.EncodingUtil
import net.dv8tion.jda.internal.utils.IOUtil
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.time.OffsetDateTime
import java.util.*

class OAuth2ClientImpl(
    clientId: Long,
    clientSecret: String,
    sessionController: SessionController<out Session>?,
    stateController: StateController?,
    httpClient: OkHttpClient?
) : OAuth2Client {
    //	private static final Logger LOG = JDALogger.getLog(OAuth2Client.class);
    override val id: Long
    override val secret: String
    override val sessionController: SessionController<out Session>
    override val stateController: StateController
    private val httpClient: OkHttpClient

    /**
     * Gets the internal OAuth2Requester used by this OAuth2Client.
     *
     * @return The internal OAuth2Requester used by this OAuth2Client.
     */
    val requester: OAuth2Requester

    init {
        Checks.check(clientId >= 0, "Invalid Client ID")
        Checks.notNull(clientSecret, "Client Secret")

        this.id = clientId
        this.secret = clientSecret
        this.sessionController = sessionController ?: DefaultSessionController()
        this.stateController = stateController ?: DefaultStateController()
        this.httpClient = httpClient ?: OkHttpClient.Builder().build()
        this.requester = OAuth2Requester(this.httpClient)
    }

    override fun generateAuthorizationURL(redirectUri: String, vararg scopes: Scope): String {
        Checks.notNull(redirectUri, "Redirect URI")

        return OAuth2URL.AUTHORIZE.compile(
            id, EncodingUtil.encodeUTF8(redirectUri),
            Scope.join(*scopes), stateController.generateNewState(redirectUri)
        )
    }

    @Throws(InvalidStateException::class)
    override fun startSession(
        code: String,
        state: String,
        identifier: String,
        vararg scopes: Scope
    ): OAuth2Action<Session> {
        Checks.notEmpty(code, "code")
        Checks.notEmpty(state, "state")

        val redirectUri = stateController.consumeState(state)
            ?: throw InvalidStateException(String.format("No state '%s' exists!", state))

        val oAuth2URL = OAuth2URL.TOKEN

        return object : OAuth2Action<Session>(this@OAuth2ClientImpl, Method.POST, oAuth2URL.routeWithBaseUrl) {
            override val headers: Headers
                get() = Headers.headersOf("Content-Type", "x-www-form-urlencoded")

            override val body: RequestBody
                get() = oAuth2URL.compileQueryParams(
                    id, secret,
                    EncodingUtil.encodeUTF8(redirectUri), code, Scope.join(true, *scopes)
                ).toRequestBody("application/x-www-form-urlencoded".toMediaType())

            @Throws(IOException::class)
            override fun handle(response: Response): Session {
                if (!response.isSuccessful) throw failure(response)

                val body = JSONObject(JSONTokener(IOUtil.getBody(response)))

                return sessionController.createSession(
                    SessionData(
                        identifier,
                        body.getString("access_token"),
                        body.getString("refresh_token"),
                        body.getString("token_type"),
                        OffsetDateTime.now().plusSeconds(body.getInt("expires_in").toLong()),
                        parseScopes(body.getString("scope"))
                    )
                )
            }
        }
    }

    override fun getUser(session: Session): OAuth2Action<OAuth2User> {
        Checks.notNull(session, "Session")
        return object : OAuth2Action<OAuth2User>(this@OAuth2ClientImpl, Method.GET, OAuth2URL.CURRENT_USER.compile()) {
            override val headers: Headers
                get() = Headers.headersOf("Authorization", generateAuthorizationHeader(session))

            @Throws(IOException::class)
            override fun handle(response: Response): OAuth2User {
                if (!response.isSuccessful) throw failure(response)

                val body = JSONObject(JSONTokener(IOUtil.getBody(response)))

                return OAuth2UserImpl(
                    this@OAuth2ClientImpl, session, body.getLong("id"),
                    body.getString("username"), body.optString("global_name", null),
                    body.optString("avatar", null), body.optString("email", null),
                    body.optBoolean("verified", false), body.getBoolean("mfa_enabled"),
                    body.optString("banner", null), body.optIntegerObject("accent_color", null),
                    body.optString("locale", null)
                )
            }
        }
    }

    @Throws(MissingScopeException::class)
    override fun getGuilds(session: Session): OAuth2Action<List<OAuth2Guild>> {
        Checks.notNull(session, "session")
        if (!session.scopes.contains(Scope.GUILDS)) throw MissingScopeException(
            "get guilds for a Session",
            Scope.GUILDS
        )
        // If with user count
        val url = if (session.scopes.contains(Scope.GUILDS_MEMBERS)) OAuth2URL.CURRENT_USER_GUILDS_COUNT
        else OAuth2URL.CURRENT_USER_GUILDS

        return object : OAuth2Action<List<OAuth2Guild>>(this@OAuth2ClientImpl, Method.GET, url.compile()) {
            override val headers: Headers
                get() = Headers.headersOf("Authorization", generateAuthorizationHeader(session))

            @Throws(IOException::class)
            override fun handle(response: Response): List<OAuth2Guild> {
                if (!response.isSuccessful) throw failure(response)

                val body = JSONArray(JSONTokener(IOUtil.getBody(response)))
                val list: MutableList<OAuth2Guild> = LinkedList()
                var obj: JSONObject
                for (i in 0..<body.length()) {
                    obj = body.getJSONObject(i)
                    list.add(
                        OAuth2GuildImpl(
                            this@OAuth2ClientImpl, obj.getLong("id"),
                            obj.getString("name"), obj.optString("icon", null),
                            obj.optString("banner", null), obj.getBoolean("owner"),
                            obj.getLong("permissions"), obj.optInt("approximate_presence_count", -1),
                            obj.optInt("approximate_member_count", -1)
                        )
                    )
                }
                return list
            }
        }
    }

    override fun refreshSession(session: SessionData): OAuth2Action<Session> {
        Checks.notNull(session, "session")

        val oAuth2URL = OAuth2URL.TOKEN_REFRESH

        return object : OAuth2Action<Session>(this@OAuth2ClientImpl, Method.POST, oAuth2URL.routeWithBaseUrl) {
            override val headers: Headers
                get() = Headers.headersOf("Content-Type", "x-www-form-urlencoded")

            override val body: RequestBody
                get() = oAuth2URL.compileQueryParams(
                    id, secret, session.refreshToken
                ).toRequestBody("application/x-www-form-urlencoded".toMediaType())

            @Throws(IOException::class)
            override fun handle(response: Response): Session {
                if (!response.isSuccessful) throw failure(response)

                val body = JSONObject(JSONTokener(IOUtil.getBody(response)))

                val scopes = parseScopes(body.getString("scope"))

                return sessionController.createSession(
                    SessionData(
                        session.identifier,
                        body.getString("access_token"),
                        body.getString("refresh_token"),
                        body.getString("token_type"),
                        OffsetDateTime.now().plusSeconds(body.getInt("expires_in").toLong()),
                        scopes
                    )
                )
            }
        }
    }

    override fun revokeSession(session: Session) {
        Checks.notNull(session, "session")

        val oAuth2URL = OAuth2URL.TOKEN_REVOKE

        return object : OAuth2Action<Unit>(this@OAuth2ClientImpl, Method.POST, oAuth2URL.routeWithBaseUrl) {
            override val headers: Headers
                get() = Headers.headersOf("Content-Type", "x-www-form-urlencoded")

            override val body: RequestBody
                get() = oAuth2URL.compileQueryParams(
                    id, secret, session.accessToken
                ).toRequestBody("application/x-www-form-urlencoded".toMediaType())

            @Throws(IOException::class)
            override fun handle(response: Response) {
                if (!response.isSuccessful) throw failure(response)
            }
        }.queue()
    }

    override fun shutdown() {
        httpClient.dispatcher.executorService.shutdown()
    }

    // Generates an authorization header 'X Y', where 'X' is the session's
    // token-type and 'Y' is the session's access token.
    private fun generateAuthorizationHeader(session: Session): String {
        return "${session.tokenType} ${session.accessToken}"
    }

    private fun parseScopes(text: String): Array<Scope> {
        return text.split(" ")
            .filter { it.isNotEmpty() }
            .map { Scope.from(it) }
            .toTypedArray()
    }

    companion object {
        @Throws(IOException::class)
        fun failure(response: Response): HttpException {
            val stream = IOUtil.getBody(response)
            val responseBody = String(IOUtil.readFully(stream))
            return HttpException("Request returned failure ${response.code}: $responseBody")
        }
    }
}
