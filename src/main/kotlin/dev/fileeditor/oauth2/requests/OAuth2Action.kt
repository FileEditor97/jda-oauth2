package dev.fileeditor.oauth2.requests

import dev.fileeditor.oauth2.entities.impl.OAuth2ClientImpl
import net.dv8tion.jda.api.requests.Method
import net.dv8tion.jda.internal.utils.Checks
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * An adaptable lookalike of JDA's [RestAction][net.dv8tion.jda.api.requests.RestAction].
 *
 *
 * OAuth2Actions can either be completed *asynchronously* using [queue][OAuth2Action.queue],
 * or synchronously using [complete][OAuth2Action.complete].
 *
 *
 * Note that OAuth2Action does not extend JDA's RestAction.
 *
 * @author Kaidan Gustave
 */
abstract class OAuth2Action<T>(client: OAuth2ClientImpl, method: Method, url: String) {
    private val defaultSuccess: Consumer<T> = Consumer {}

    /**
     * Gets the [client][dev.fileeditor.oauth2.OAuth2Client] responsible
     * for creating this OAuth2Action.
     *
     * @return The OAuth2Client responsible for creating this.
     */
    private val client: OAuth2ClientImpl
	val method: Method
	val url: String

    init {
        Checks.notNull(client, "OAuth2Client")
        Checks.notNull(method, "Request method")
        Checks.notEmpty(url, "URL")

        this.client = client
        this.method = method
        this.url = url
    }

    open val body: RequestBody
        get() = OAuth2Requester.EMPTY_BODY

    open val headers: Headers
        get() = Headers.headersOf()

    fun buildRequest(): Request {
        val builder = Request.Builder()

        when (method) {
            Method.GET -> builder.get()
            Method.POST -> builder.post(body)
            else -> throw IllegalArgumentException(method.name + " requests are not supported!")
        }

        builder.url(url)
        builder.header("User-Agent", OAuth2Requester.USER_AGENT)
        builder.headers(headers)

        return builder.build()
    }

    /**
     * Asynchronously executes this OAuth2Action, providing the value constructed from the response
     * as the parameter given to the success [Consumer][java.util.function.Consumer] if the
     * response is successful, or the exception to the failure Consumer if it's not.
     *
     * @param  success
     * The success consumer, executed when this OAuth2Action gets a successful response.
     * @param  failure
     * The failure consumer, executed when this OAuth2Action gets a failed response.
     */
    fun queue(success: Consumer<T> = defaultSuccess, failure: Consumer<Throwable> = DEFAULT_FAILURE) {
        client.requester.submitAsync(this, success, failure)
    }

    /**
     * Synchronously executes this OAuth2Action, returning the value constructed from the response
     * if it was successful, or throwing the [Exception][java.lang.Exception] if it was not.
     *
     *
     * Bear in mind when using this, that this method blocks the thread it is called in.
     * @return the value constructed from the response
     * @throws java.io.IOException on unsuccessful execution
     */
    @Throws(IOException::class)
    fun complete(): T {
        return client.requester.submitSync(this)
    }

    /**
     * Submits a Request for execution and provides a CompletableFuture representing its completion task.
     * Cancelling the returned Future will result in the cancellation of the Request!
     *
     * @return CompletableFuture to be used.
     */
    fun future(): CompletableFuture<T> {
        return client.requester.submit(this)
    }

    @Throws(IOException::class)
    abstract fun handle(response: Response): T

    companion object {
        val DEFAULT_FAILURE: Consumer<Throwable> =
            Consumer { t: Throwable ->
                OAuth2Requester.LOGGER.error(
                    "Requester encountered an error while processing response!",
                    t
                )
            }
    }
}
