package dev.fileeditor.oauth2

import dev.fileeditor.oauth2.entities.OAuth2Guild
import dev.fileeditor.oauth2.entities.OAuth2User
import dev.fileeditor.oauth2.entities.impl.OAuth2ClientImpl
import dev.fileeditor.oauth2.exceptions.InvalidStateException
import dev.fileeditor.oauth2.exceptions.MissingScopeException
import dev.fileeditor.oauth2.requests.OAuth2Action
import dev.fileeditor.oauth2.session.Session
import dev.fileeditor.oauth2.session.SessionController
import dev.fileeditor.oauth2.session.SessionData
import dev.fileeditor.oauth2.state.StateController
import net.dv8tion.jda.internal.utils.Checks
import okhttp3.OkHttpClient

/**
 * The central controller for OAuth2 state and session management using the Discord API.
 *
 *
 * OAuth2Client's are made using a [OAuth2Client.Builder],
 * and sessions can be appended using [OAuth2Client.startSession].
 */
interface OAuth2Client {
    /**
     * Generates a formatted authorization URL from the provided redirect URI fragment
     * and [Scopes][Scope].
     *
     * @param  redirectUri
     * The redirect URI.
     * @param  scopes
     * The provided scopes.
     *
     * @return The generated authorization URL.
     */
    fun generateAuthorizationURL(redirectUri: String, vararg scopes: Scope): String

    /**
     * Starts a [dev.fileeditor.oauth2.session.Session] with the provided code,
     * state, and identifier. The state provided should be *unique* and provided through an
     * implementation of [StateController].
     *
     *
     * If the state has already been consumed by the StateController using
     * [StateController#consumeState][StateController.consumeState],
     * then it should return `null` when provided the same state, so that this may throw a
     * [InvalidStateException] to signify it has
     * been consumed.
     *
     * @param  code
     * The code for the Session to start.
     * @param  state
     * The state for the Session to start.
     * @param  identifier
     * The identifier for the Session to start.
     * @param  scopes
     * The provided scopes.
     *
     * @return A [OAuth2Action] for the Session to start.
     *
     * @throws InvalidStateException
     * If the state, when consumed by this client's StateController, results in a `null` redirect URI.
     */
    @Throws(InvalidStateException::class)
    fun startSession(code: String, state: String, identifier: String, vararg scopes: Scope): OAuth2Action<Session>

    /**
     * Requests a [OAuth2User]
     * from the [Session].
     *
     *
     * All Sessions should handle an individual Discord User, and as such this method retrieves
     * data on that User when the session is provided.
     *
     * @param  session
     * The Session to get a OAuth2User for.
     *
     * @return A [OAuth2Action] for
     * the OAuth2User to be retrieved.
     */
    fun getUser(session: Session): OAuth2Action<OAuth2User>

    /**
     * Requests a list of [OAuth2Guilds][OAuth2Guild]
     * from the [Session].
     *
     *
     * All Sessions should handle an individual Discord User, and as such this method retrieves
     * data on all the various Discord Guilds that user is a part of when the session is provided.
     *
     *
     * Note that this can only be performed for Sessions who have the necessary
     * [&#39;guilds&#39;][Scope.GUILDS] scope.
     * <br></br>Trying to call this using a Session without the scope will cause a
     * [MissingScopeException]
     * to be thrown.
     *
     * @param  session
     * The Session to get OAuth2Guilds for.
     *
     * @return A [OAuth2Action] for
     * the OAuth2Guilds to be retrieved.
     *
     * @throws MissingScopeException
     * If the provided Session does not have the 'guilds' scope.
     */
    @Throws(MissingScopeException::class)
    fun getGuilds(session: Session): OAuth2Action<List<OAuth2Guild>>

    /**
     * Refresh [Session&#39;s][dev.fileeditor.oauth2.session.Session] token.
     *
     * @param  session
     * The Session to be refreshed.
     *
     * @return A [OAuth2Action] for the Session to start.
     */
    fun refreshSession(session: SessionData): OAuth2Action<Session>

    /**
     * Revoke [dev.fileeditor.oauth2.session.Session] token.
     *
     * @param  session
     * The Session to be revoked.
     */
    fun revokeSession(session: Session)

    /**
     * Gets the client ID for this OAuth2Client.
     *
     * @return The client ID.
     */
    val id: Long

    /**
     * Gets the client's secret.
     *
     * @return The client's secret.
     */
    val secret: String

    /**
     * Gets the client's [StateController].
     *
     * @return The client's StateController.
     */
    val stateController: StateController

    /**
     * Gets the client's [SessionController].
     *
     * @return The client's SessionController.
     */
    val sessionController: SessionController<out Session>

    /**
     * Shutdown httpClient.
     */
    fun shutdown()

    /**
     * Builder for creating OAuth2Client instances.
     *
     *
     * At minimum, the developer must provide a
     * valid Client ID, as well as a valid secret.
     */
    class Builder {
        private var clientId: Long = -1
        private var clientSecret: String? = null
        private var sessionController: SessionController<out Session>? = null
        private var stateController: StateController? = null
        private var client: OkHttpClient? = null

        /**
         * Finalizes and builds an [OAuth2Client]
         * instance using this builder.
         *
         * @return The OAuth2Client instance build.
         *
         * @throws java.lang.IllegalArgumentException
         * If either:
         *
         *  * The Client ID is not valid.
         *  * The Client Secret is empty.
         *
         */
        fun build(): OAuth2Client {
            Checks.check(clientId >= 0, "Client ID is invalid!")
            Checks.notEmpty(clientSecret, "Client Secret")
            return OAuth2ClientImpl(clientId, clientSecret!!, sessionController, stateController, client)
        }

        /**
         * Sets the OAuth2Client's ID.
         *
         * @param  clientId
         * The OAuth2Client's ID.
         *
         * @return This builder.
         */
        fun setClientId(clientId: Long): Builder {
            this.clientId = clientId
            return this
        }

        /**
         * Sets the OAuth2Client's secret.
         *
         * @param  clientSecret
         * The OAuth2Client's secret.
         *
         * @return This builder.
         */
        fun setClientSecret(clientSecret: String): Builder {
            this.clientSecret = clientSecret
            return this
        }

        /**
         * Sets the OAuth2Client's [StateController].
         *
         * @param  sessionController
         * The OAuth2Client's SessionController.
         *
         * @return This builder.
         */
        fun setSessionController(sessionController: SessionController<out Session>): Builder {
            this.sessionController = sessionController
            return this
        }

        /**
         * Sets the OAuth2Client's [StateController].
         *
         * @param  stateController
         * The OAuth2Client's StateController.
         *
         * @return This builder.
         */
        fun setStateController(stateController: StateController): Builder {
            this.stateController = stateController
            return this
        }

        /**
         * Sets the client's internal [OkHttpClient][okhttp3.OkHttpClient] used for
         * all requests and interactions with Discord.
         *
         * @param  client
         * The OAuth2Client's OkHttpClient.
         *
         * @return This builder.
         */
        fun setOkHttpClient(client: OkHttpClient): Builder {
            this.client = client
            return this
        }
    }

    companion object {
        /**
         * The REST version targeted by JDA-Utilities OAuth2.
         */
        const val DISCORD_REST_VERSION: Int = 10
    }
}
