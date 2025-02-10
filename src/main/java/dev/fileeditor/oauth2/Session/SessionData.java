package dev.fileeditor.oauth2.Session;

import dev.fileeditor.oauth2.Scope;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains various data necessary for creating a {@link Session} using a {@link SessionController}.
 */
public class SessionData {

	private final String identifier, accessToken, refreshToken, tokenType;
	private final OffsetDateTime expiration;
	private final Scope[] scopes;

	public SessionData(String identifier, String accessToken, String refreshToken, String tokenType, OffsetDateTime expiration, Scope[] scopes) {
		this.identifier = identifier;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.tokenType = tokenType;
		this.expiration = expiration;
		this.scopes = scopes;
	}

	/**
	 * @return The session's identifier.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return The session's access token.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @return The session's refresh token.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @return The session's token type.
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * @return The session's expiration time.
	 */
	public OffsetDateTime getExpiration() {
		return expiration;
	}

	/**
	 * @return The session's Scopes.
	 */
	public Scope[] getScopes() {
		return scopes;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SessionData data)) {
			return false;
		}

		return getIdentifier().equals(data.getIdentifier()) && getTokenType().equals(data.getTokenType());
	}

	@Override
	public String toString() {
		return String.format("SessionData(identifier: %s, access-token: %s, refresh-token: %s, type: %s, expires: %s)",
			getIdentifier(), getAccessToken(), getRefreshToken(), getTokenType(),
			getExpiration().format(DateTimeFormatter.ISO_DATE_TIME));
	}

}
