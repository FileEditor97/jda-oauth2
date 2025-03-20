package dev.fileeditor.oauth2.requests

import dev.fileeditor.oauth2.OAuth2Client

/**
 * Simple formattable constants for various URLs used in the JDA-Utilities OAuth2.
 */
enum class OAuth2URL(
    private val route: String,
    vararg queryParams: String?
) {
    AUTHORIZE(
        "/oauth2/authorize",
        "client_id=%d",
        "redirect_uri=%s",
        "response_type=code",
        "scope=%s",
        "state=%s"
    ),
    TOKEN(
        "/oauth2/token",
        "client_id=%d",
        "client_secret=%s",
        "redirect_uri=%s",
        "grant_type=authorization_code",
        "code=%s",
        "scope=%s"
    ),
    TOKEN_REFRESH(
        "/oauth2/token",
        "client_id=%d",
        "client_secret=%s",
        "redirect_uri=%s",
        "grant_type=refresh_token",
        "refresh_token=%s"
    ),
    TOKEN_REVOKE(
        "/oauth2/token/revoke",
        "client_id=%d",
        "client_secret=%s",
        "token=%s",
        "token_type_hint=access_token"
    ),
    CURRENT_USER("/users/@me"),
    CURRENT_USER_GUILDS("/users/@me/guilds"),
    CURRENT_USER_GUILDS_COUNT(
        "/users/@me/guilds",
        "with_counts=true"
    );

    private var formattableRoute: String
    private val hasQueryParams: Boolean = queryParams.isNotEmpty()
    private var queryParams: String

    init {
        if (hasQueryParams) {
            val builder = StringBuilder()

            for (i in queryParams.indices) {
                builder.append(if (i == 0) '?' else '&')
                builder.append(queryParams[i])
            }

            this.formattableRoute = route + builder
            this.queryParams = builder.toString()
        } else {
            this.formattableRoute = route
            this.queryParams = ""
        }
    }

    fun compileQueryParams(vararg values: Any?): String {
        return String.format(queryParams, *values).replace("\\?".toRegex(), "")
    }

    val routeWithBaseUrl: String
        get() = BASE_API_URL + route

    fun compile(vararg values: Any?): String {
        return BASE_API_URL + (if (hasQueryParams) String.format(formattableRoute, *values) else formattableRoute)
    }

    companion object {
        const val BASE_API_URL: String = "https://discord.com/api/v${OAuth2Client.DISCORD_REST_VERSION}"
    }
}

