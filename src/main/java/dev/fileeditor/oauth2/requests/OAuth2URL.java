package dev.fileeditor.oauth2.requests;

import dev.fileeditor.oauth2.OAuth2Client;

/**
 * Simple formattable constants for various URLs used in the JDA-Utilities OAuth2.
 */
public enum OAuth2URL {
	AUTHORIZE("/oauth2/authorize",
		"client_id=%d",
		"redirect_uri=%s",
		"response_type=code",
		"scope=%s",
		"state=%s"),
	TOKEN("/oauth2/token",
		"client_id=%d",
		"client_secret=%s",
		"redirect_uri=%s",
		"grant_type=authorization_code",
		"code=%s",
		"scope=%s"),
	TOKEN_REFRESH("/oauth2/token",
		"client_id=%d",
		"client_secret=%s",
		"redirect_uri=%s",
		"grant_type=refresh_token",
		"refresh_token=%s"),
	TOKEN_REVOKE("/oauth2/token/revoke",
		"client_id=%d",
		"client_secret=%s",
		"token=%s",
		"token_type_hint=access_token"),
	CURRENT_USER("/users/@me"),
	CURRENT_USER_GUILDS("/users/@me/guilds"),
	CURRENT_USER_GUILDS_COUNT("/users/@me/guilds",
		"with_counts=true");

	public static final String BASE_API_URL = String.format("https://discord.com/api/v%d", OAuth2Client.DISCORD_REST_VERSION);

	private final String route;
	private final String formattableRoute;
	private final boolean hasQueryParams;
	private final String queryParams;

	OAuth2URL(String route, String... queryParams) {
		this.route = route;
		this.hasQueryParams = queryParams.length > 0;

		if (hasQueryParams) {
			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < queryParams.length; i++) {
				builder.append(i == 0 ? '?' : '&');
				builder.append(queryParams[i]);
			}

			this.formattableRoute = route + builder;
			this.queryParams = builder.toString();
		} else {
			this.formattableRoute = route;
			this.queryParams = "";
		}
	}

	public String getRoute() {
		return route;
	}

	public boolean isHasQueryParams() {
		return hasQueryParams;
	}

	public String compileQueryParams(Object... values) {
		return String.format(queryParams, values).replaceAll("\\?", "");
	}

	public String getRouteWithBaseUrl() {
		return BASE_API_URL + route;
	}

	public String compile(Object... values) {
		return BASE_API_URL + (hasQueryParams ? String.format(formattableRoute, values) : formattableRoute);
	}
}

