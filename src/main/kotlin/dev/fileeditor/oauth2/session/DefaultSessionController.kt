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

    class DefaultSession(data: SessionData) : Session {
        override val accessToken: String = data.accessToken
        override val refreshToken: String = data.refreshToken
        override val tokenType: String = data.tokenType
        override val expiration: OffsetDateTime = data.expiration
        override val scopes: Array<Scope> = data.scopes
    }
}
