package dev.fileeditor.oauth2.requests

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CompletableFuture

class OkHttpResponseFuture : Callback {
	val future: CompletableFuture<Response> = CompletableFuture()

    override fun onResponse(call: Call, response: Response) {
        future.complete(response)
    }

    override fun onFailure(call: Call, e: IOException) {
        future.completeExceptionally(e)
    }
}
