package dev.fileeditor.oauth2.session

import dev.fileeditor.oauth2.Scope
import java.time.OffsetDateTime

/**
 * Implementable data type used to allow access to data regarding OAuth2 sessions.
 */
interface Session {
    /**
     * @return The session's access token.
     */
	val accessToken: String

    /**
     * @return The session's refresh token.
     */
    val refreshToken: String

    /**
     * @return The session's Scopes.
     */
	val scopes: Array<Scope>

    /**
     * @return The session's token type.
     */
	val tokenType: String

    /**
     * @return The session's expiration time.
     */
    val expiration: OffsetDateTime
}
