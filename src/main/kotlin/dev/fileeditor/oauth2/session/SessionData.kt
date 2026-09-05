package dev.fileeditor.oauth2.session

import dev.fileeditor.oauth2.Scope
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Contains various data necessary for creating a [Session] using a [SessionController].
 */
class SessionData(
    /**
     * @return The session's identifier.
     */
    val identifier: String,

    override val accessToken: String,
    override val refreshToken: String,
    override val tokenType: String,
    override val expiration: OffsetDateTime,
    override val scopes: Array<Scope>
) : Session {
    override fun equals(other: Any?): Boolean {
	    return other is SessionData && identifier == other.identifier
    }

    override fun toString(): String {
        return "SessionData(identifier: $identifier, access-token: $accessToken, refresh-token: $refreshToken, type: $tokenType, expires: ${expiration.format(DateTimeFormatter.ISO_DATE_TIME)})"
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}
