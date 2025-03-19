package dev.fileeditor.oauth2.requests;

import dev.fileeditor.oauth2.entities.impl.OAuth2ClientImpl;
import net.dv8tion.jda.api.requests.Method;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * An adaptable lookalike of JDA's {@link net.dv8tion.jda.api.requests.RestAction RestAction}.
 *
 * <p>OAuth2Actions can either be completed <i>asynchronously</i> using {@link OAuth2Action#queue() queue},
 * or synchronously using {@link OAuth2Action#complete() complete}.
 *
 * <p>Note that OAuth2Action does not extend JDA's RestAction.
 *
 * @author Kaidan Gustave
 */
public abstract class OAuth2Action<T> {
	protected final Consumer<T> DEFAULT_SUCCESS = t -> {};
	protected static final Consumer<Throwable> DEFAULT_FAILURE = t ->
		OAuth2Requester.LOGGER.error("Requester encountered an error while processing response!", t);

	protected final OAuth2ClientImpl client;
	protected final Method method;
	protected final String url;

	public OAuth2Action(OAuth2ClientImpl client, Method method, String url) {
		Checks.notNull(client, "OAuth2Client");
		Checks.notNull(method, "Request method");
		Checks.notEmpty(url, "URL");

		this.client = client;
		this.method = method;
		this.url = url;
	}

	protected RequestBody getBody() {
		return OAuth2Requester.EMPTY_BODY;
	}

	protected Headers getHeaders() {
		return Headers.of();
	}

	protected Request buildRequest() {
		Request.Builder builder = new Request.Builder();

		switch(method) {
			case GET:
				builder.get();
				break;
			case POST:
				builder.post(getBody());
				break;
			default:
				throw new IllegalArgumentException(method.name() + " requests are not supported!");
		}

		builder.url(url);
		builder.header("User-Agent", OAuth2Requester.USER_AGENT);
		builder.headers(getHeaders());

		return builder.build();
	}

	protected Method getMethod() {
		return method;
	}

	protected String getUrl() {
		return url;
	}

	/**
	 * Asynchronously executes this OAuth2Action.
	 */
	public void queue() {
		queue(DEFAULT_SUCCESS);
	}

	/**
	 * Asynchronously executes this OAuth2Action, providing the value constructed from the response
	 * as the parameter given to the success {@link java.util.function.Consumer Consumer}.
	 *
	 * @param  success
	 *         The success consumer, executed when this OAuth2Action gets a successful response.
	 */
	public void queue(Consumer<T> success) {
		queue(success, DEFAULT_FAILURE);
	}

	/**
	 * Asynchronously executes this OAuth2Action, providing the value constructed from the response
	 * as the parameter given to the success {@link java.util.function.Consumer Consumer} if the
	 * response is successful, or the exception to the failure Consumer if it's not.
	 *
	 * @param  success
	 *         The success consumer, executed when this OAuth2Action gets a successful response.
	 * @param  failure
	 *         The failure consumer, executed when this OAuth2Action gets a failed response.
	 */
	public void queue(Consumer<T> success, Consumer<Throwable> failure) {
		client.getRequester().submitAsync(this, success, failure);
	}

	/**
	 * Synchronously executes this OAuth2Action, returning the value constructed from the response
	 * if it was successful, or throwing the {@link java.lang.Exception Exception} if it was not.
	 *
	 * <p>Bear in mind when using this, that this method blocks the thread it is called in.
	 * @return the value constructed from the response
	 * @throws java.io.IOException on unsuccessful execution
	 */
	@Nullable
	public T complete() throws IOException {
		return client.getRequester().submitSync(this);
	}

	/**
	 * Submits a Request for execution and provides a CompletableFuture representing its completion task.
	 * Cancelling the returned Future will result in the cancellation of the Request!
	 *
	 * @return CompletableFuture to be used.z
	 */
	@NotNull
	public CompletableFuture<T> future() {
		return client.getRequester().submit(this);
	}

	/**
	 * Gets the {@link dev.fileeditor.oauth2.OAuth2Client client} responsible
	 * for creating this OAuth2Action.
	 *
	 * @return The OAuth2Client responsible for creating this.
	 */
	@NotNull
	public OAuth2ClientImpl getClient() {
		return client;
	}

	protected abstract T handle(Response response) throws IOException;
}
