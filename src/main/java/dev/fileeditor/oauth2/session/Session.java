package dev.fileeditor.oauth2.session;

import dev.fileeditor.oauth2.Scope;

import java.time.OffsetDateTime;

/**
 * Implementable data type used to allow access to data regarding OAuth2 sessions.
 */
public interface Session {

	/**
	 * @return The session's access token.
	 */
	String getAccessToken();

	/**
	 * @return The session's refresh token.
	 */
	String getRefreshToken();

	/**
	 * @return The session's Scopes.
	 */
	Scope[] getScopes();

	/**
	 * @return The session's token type.
	 */
	String getTokenType();

	/**
	 * @return The session's expiration time.
	 */
	OffsetDateTime getExpiration();

}
