package dev.fileeditor.oauth2.session

import dev.fileeditor.oauth2.Scope
import dev.fileeditor.oauth2.session.DefaultSessionController.DefaultSession
import java.time.OffsetDateTime

/**
 * The default [SessionController] implementation.
 */
class DefaultSessionController : SessionController<DefaultSession> {
    private val sessions = HashMap<String, DefaultSession>()

    override fun getSession(identifier: String): DefaultSession? {
        return sessions[identifier]
    }

    override fun createSession(data: SessionData): DefaultSession {
        val created = DefaultSession(data)
        sessions[data.identifier] = created
        return created
    }

    override fun endSession(identifier: String) {
        sessions.remove(identifier)
    }

    inner class DefaultSession : Session {
        override val accessToken: String
        override val refreshToken: String
        override val tokenType: String
        override val expiration: OffsetDateTime
        override val scopes: Array<Scope>

        private constructor(
            accessToken: String,
            refreshToken: String,
            tokenType: String,
            expiration: OffsetDateTime,
            scopes: Array<Scope>
        ) {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
            this.tokenType = tokenType
            this.expiration = expiration
            this.scopes = scopes
        }

        constructor(data: SessionData) {
            this.accessToken = data.accessToken
            this.refreshToken = data.refreshToken
            this.tokenType = data.tokenType
            this.expiration = data.expiration
            this.scopes = data.scopes
        }
    }
}
