package dev.fileeditor.oauth2.entities.impl;

import dev.fileeditor.oauth2.OAuth2Client;
import dev.fileeditor.oauth2.Scope;
import dev.fileeditor.oauth2.session.DefaultSessionController;
import dev.fileeditor.oauth2.session.Session;
import dev.fileeditor.oauth2.session.SessionController;
import dev.fileeditor.oauth2.session.SessionData;
import dev.fileeditor.oauth2.entities.OAuth2Guild;
import dev.fileeditor.oauth2.entities.OAuth2User;
import dev.fileeditor.oauth2.exceptions.InvalidStateException;
import dev.fileeditor.oauth2.exceptions.MissingScopeException;
import dev.fileeditor.oauth2.requests.OAuth2Action;
import dev.fileeditor.oauth2.requests.OAuth2Requester;
import dev.fileeditor.oauth2.requests.OAuth2URL;
import dev.fileeditor.oauth2.state.DefaultStateController;
import dev.fileeditor.oauth2.state.StateController;
import net.dv8tion.jda.api.exceptions.HttpException;
import net.dv8tion.jda.api.requests.Method;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EncodingUtil;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

public class OAuth2ClientImpl implements OAuth2Client {
//	private static final Logger LOG = JDALogger.getLog(OAuth2Client.class);

	private final long clientId;
	private final String clientSecret;
	private final SessionController<? extends Session> sessionController;
	private final StateController stateController;
	private final OkHttpClient httpClient;
	private final OAuth2Requester requester;

	public OAuth2ClientImpl(long clientId, String clientSecret, SessionController<? extends Session> sessionController,
							StateController stateController, OkHttpClient httpClient) {
		Checks.check(clientId >= 0, "Invalid Client ID");
		Checks.notNull(clientSecret, "Client Secret");

		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.sessionController = sessionController == null ? new DefaultSessionController() : sessionController;
		this.stateController = stateController == null ? new DefaultStateController() : stateController;
		this.httpClient = httpClient == null ? new OkHttpClient.Builder().build() : httpClient;
		this.requester = new OAuth2Requester(this.httpClient);
	}

	@NotNull
	@Override
	public String generateAuthorizationURL(@NotNull String redirectUri, Scope... scopes) {
		Checks.notNull(redirectUri, "Redirect URI");

		return OAuth2URL.AUTHORIZE.compile(clientId, EncodingUtil.encodeUTF8(redirectUri),
			Scope.join(scopes), stateController.generateNewState(redirectUri));
	}

	@NotNull
	@Override
	public OAuth2Action<Session> startSession(@NotNull String code, @NotNull String state, @NotNull String identifier, Scope... scopes) throws InvalidStateException {
		Checks.notEmpty(code, "code");
		Checks.notEmpty(state, "state");

		String redirectUri = stateController.consumeState(state);
		if (redirectUri == null)
			throw new InvalidStateException(String.format("No state '%s' exists!", state));

		OAuth2URL oAuth2URL = OAuth2URL.TOKEN;

		return new OAuth2Action<>(this, Method.POST, oAuth2URL.getRouteWithBaseUrl()) {
			@Override
			protected Headers getHeaders() {
				return Headers.of("Content-Type", "x-www-form-urlencoded");
			}

			@Override
			protected RequestBody getBody() {
				return RequestBody.create(oAuth2URL.compileQueryParams(clientId, clientSecret,
						EncodingUtil.encodeUTF8(redirectUri), code, Scope.join(true, scopes)),
					MediaType.parse("application/x-www-form-urlencoded"));
			}

			@Override
			protected Session handle(Response response) throws IOException {
				if (!response.isSuccessful())
					throw failure(response);

				JSONObject body = new JSONObject(new JSONTokener(IOUtil.getBody(response)));

				Scope[] scopes = parseScopes(body.getString("scope"));

				return sessionController.createSession(new SessionData(identifier,
					body.getString("access_token"), body.getString("refresh_token"),
					body.getString("token_type"), OffsetDateTime.now().plusSeconds(body.getInt("expires_in")), scopes));
			}
		};
	}

	@NotNull
	@Override
	public OAuth2Action<OAuth2User> getUser(@NotNull Session session) {
		Checks.notNull(session, "Session");
		return new OAuth2Action<>(this, Method.GET, OAuth2URL.CURRENT_USER.compile()) {
			@Override
			protected Headers getHeaders() {
				return Headers.of("Authorization", generateAuthorizationHeader(session));
			}

			@Override
			protected OAuth2User handle(Response response) throws IOException {
				if (!response.isSuccessful())
					throw failure(response);
				JSONObject body = new JSONObject(new JSONTokener(IOUtil.getBody(response)));
				return new OAuth2UserImpl(OAuth2ClientImpl.this, session, body.getLong("id"),
					body.getString("username"), body.optString("global_name", null),
					body.optString("avatar", null), body.optString("email", null),
					body.optBoolean("verified", false), body.getBoolean("mfa_enabled"),
					body.optString("banner", null), body.optIntegerObject("accent_color", null),
					body.optString("locale", null));
			}
		};
	}

	@NotNull
	@Override
	public OAuth2Action<List<OAuth2Guild>> getGuilds(@NotNull Session session) throws MissingScopeException {
		Checks.notNull(session, "session");
		if (!Scope.contains(session.getScopes(), Scope.GUILDS))
			throw new MissingScopeException("get guilds for a Session", Scope.GUILDS);
		// If with user count
		OAuth2URL url = Scope.contains(session.getScopes(), Scope.GUILDS_MEMBERS) ? OAuth2URL.CURRENT_USER_GUILDS_COUNT : OAuth2URL.CURRENT_USER_GUILDS;

		return new OAuth2Action<>(this, Method.GET, url.compile()) {
			@Override
			protected Headers getHeaders() {
				return Headers.of("Authorization", generateAuthorizationHeader(session));
			}

			@Override
			protected List<OAuth2Guild> handle(Response response) throws IOException {
				if (!response.isSuccessful())
					throw failure(response);

				JSONArray body = new JSONArray(new JSONTokener(IOUtil.getBody(response)));
				List<OAuth2Guild> list = new LinkedList<>();
				JSONObject obj;
				for (int i = 0; i < body.length(); i++) {
					obj = body.getJSONObject(i);
					list.add(new OAuth2GuildImpl(OAuth2ClientImpl.this, obj.getLong("id"),
						obj.getString("name"), obj.optString("icon", null),
						obj.optString("banner", null), obj.getBoolean("owner"),
						obj.getLong("permissions"), obj.optInt("approximate_presence_count", -1),
						obj.optInt("approximate_member_count", -1)));
				}
				return list;
			}
		};
	}

	@NotNull
	@Override
	public OAuth2Action<Session> refreshSession(@NotNull SessionData sessionData) {
		Checks.notNull(sessionData, "session");

		OAuth2URL oAuth2URL = OAuth2URL.TOKEN_REFRESH;

		return new OAuth2Action<>(this, Method.POST, oAuth2URL.getRouteWithBaseUrl()) {
			@Override
			protected Headers getHeaders() {
				return Headers.of("Content-Type", "x-www-form-urlencoded");
			}

			@Override
			protected RequestBody getBody() {
				return RequestBody.create(oAuth2URL.compileQueryParams(clientId, clientSecret, sessionData.getRefreshToken()),
					MediaType.parse("application/x-www-form-urlencoded"));
			}

			@Override
			protected Session handle(Response response) throws IOException {
				if (!response.isSuccessful())
					throw failure(response);

				JSONObject body = new JSONObject(new JSONTokener(IOUtil.getBody(response)));

				Scope[] scopes = parseScopes(body.getString("scope"));

				return sessionController.createSession(new SessionData(sessionData.getIdentifier(),
					body.getString("access_token"), body.getString("refresh_token"),
					body.getString("token_type"), OffsetDateTime.now().plusSeconds(body.getInt("expires_in")), scopes));
			}
		};
	}

	@Override
	public void revokeSession(@NotNull Session session) {
		Checks.notNull(session, "session");

		OAuth2URL oAuth2URL = OAuth2URL.TOKEN_REVOKE;

		new OAuth2Action<Void>(this, Method.POST, oAuth2URL.getRouteWithBaseUrl()) {
			@Override
			protected Headers getHeaders() {
				return Headers.of("Content-Type", "x-www-form-urlencoded");
			}

			@Override
			protected RequestBody getBody() {
				return RequestBody.create(oAuth2URL.compileQueryParams(clientId, clientSecret, session.getAccessToken()),
					MediaType.parse("application/x-www-form-urlencoded"));
			}

			@Override
			protected Void handle(Response response) throws IOException {
				if (!response.isSuccessful())
					throw failure(response);

				return null;
			}
		}.queue();
	}

	@Override
	public long getId() {
		return clientId;
	}

	@Override
	@NotNull
	public String getSecret() {
		return clientSecret;
	}

	@Override
	@NotNull
	public StateController getStateController() {
		return stateController;
	}

	@Override
	@NotNull
	public SessionController<? extends Session> getSessionController() {
		return sessionController;
	}

	@Override
	public void shutdown() {
		httpClient.dispatcher().executorService().shutdown();
	}

	/**
	 * Gets the internal OAuth2Requester used by this OAuth2Client.
	 *
	 * @return The internal OAuth2Requester used by this OAuth2Client.
	 */
	public OAuth2Requester getRequester() {
		return requester;
	}

	protected static HttpException failure(Response response) throws IOException {
		final InputStream stream = IOUtil.getBody(response);
		final String responseBody = new String(IOUtil.readFully(stream));
		return new HttpException("Request returned failure " + response.code() + ": " + responseBody);
	}

	// Generates an authorization header 'X Y', where 'X' is the session's
	// token-type and 'Y' is the session's access token.
	private String generateAuthorizationHeader(Session session) {
		return String.format("%s %s", session.getTokenType(), session.getAccessToken());
	}

	private Scope[] parseScopes(String text) {
		String[] scopeStrings = text.split(" ");
		Scope[] scopes = new Scope[scopeStrings.length];
		for (int i = 0; i < scopeStrings.length; i++) {
			scopes[i] = Scope.from(scopeStrings[i]);
		}
		return scopes;
	}
}
