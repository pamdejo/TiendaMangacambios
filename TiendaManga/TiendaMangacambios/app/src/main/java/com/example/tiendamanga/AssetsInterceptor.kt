package com.example.tiendamanga

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import android.util.Log

class AssetsInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        Log.d("AssetsInterceptor", "path=$path")

        val assetFile = when (path) {
            "/api/products" -> "products.json"
            else -> null
        }

        return if (assetFile != null) {
            val bytes = context.assets.open(assetFile).readBytes()
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(bytes.toResponseBody("application/json".toMediaType()))
                .build()
        } else {
            chain.proceed(request)
        }
    }
}


