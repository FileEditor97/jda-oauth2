package dev.fileeditor.oauth2.requests

import net.dv8tion.jda.internal.utils.JDALogger
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.function.Consumer

class OAuth2Requester(private val httpClient: OkHttpClient) {
    fun <T> submitAsync(request: OAuth2Action<T>, success: Consumer<T>, failure: Consumer<Throwable>) {
        httpClient.newCall(request.buildRequest()).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    response.use {
                        val value: T? = request.handle(response)
                        logSuccessfulRequest(request)

                        // Handle end-user exception differently
                        try {
                            if (value != null) {
                                success.accept(value)
                            }
                        } catch (t: Throwable) {
                            LOGGER.error("OAuth2Action success callback threw an exception!", t)
                        }
                    }
                } catch (t: Throwable) {
                    // Handle end-user exception differently
                    try {
                        failure.accept(t)
                    } catch (t1: Throwable) {
                        LOGGER.error("OAuth2Action success callback threw an exception!", t1)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                LOGGER.error("Requester encountered an error when submitting a request!", e)
            }
        })
    }

    @Throws(IOException::class)
    fun <T> submitSync(request: OAuth2Action<T>): T {
        httpClient.newCall(request.buildRequest()).execute().use { response ->
            val value = request.handle(response)
            logSuccessfulRequest(request)
            return value
        }
    }

    fun <T> submit(request: OAuth2Action<T>): CompletableFuture<T> {
        val callback = OkHttpResponseFuture()
        httpClient.newCall(request.buildRequest()).enqueue(callback)

        return callback.future.thenApply { response: Response ->
            try {
                response.use {
                    val value: T? = request.handle(response)
                    logSuccessfulRequest(request)

                    if (value == null) throw NullPointerException("Value not found!")
                    return@thenApply value
                }
            } catch (t: Throwable) {
                throw CompletionException(t)
            }
        }
    }

    companion object {
        val LOGGER: Logger = JDALogger.getLog(OAuth2Requester::class.java)
        const val USER_AGENT: String = "OAuth2 Util (git @fileeditor97)"
        val EMPTY_BODY: RequestBody = ByteArray(0).toRequestBody()

        private fun <T> logSuccessfulRequest(request: OAuth2Action<T>) {
            LOGGER.debug(
                "Got a response for {} - {}\nHeaders: {}", request.method,
                request.url, request.headers
            )
        }
    }
}
