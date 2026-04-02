package com.gaminghub.musicplayer

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody

class NewPipeDownloader(private val client: OkHttpClient) : Downloader() {

    private val tag = "NewPipeDownloader"
    
    // Updated to a very recent Chrome stable version (March 2024)
    private val browserUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

    override fun execute(request: Request): Response {
        val method = request.httpMethod()
        var url = request.url()
        val headers = request.headers()
        val data = request.dataToSend()

        // Use standard bypass for watch pages only
        if ((url.contains("youtube.com/watch") || url.contains("youtu.be/")) && !url.contains("bpctr")) {
            val separator = if (url.contains("?")) "&" else "?"
            url += "${separator}bpctr=9999999999&has_verified=1"
        }

        val okHttpRequestBuilder = okhttp3.Request.Builder().url(url)
        
        okHttpRequestBuilder.header("User-Agent", browserUserAgent)
        okHttpRequestBuilder.header("Accept", "*/*")
        okHttpRequestBuilder.header("Accept-Language", "en-US,en;q=0.9")
        
        if (url.contains("youtube.com") || url.contains("googlevideo.com")) {
            val isMusic = url.contains("music.youtube.com")
            val domain = if (isMusic) "music.youtube.com" else "www.youtube.com"
            val origin = "https://$domain"
            
            okHttpRequestBuilder.header("Referer", "$origin/")
            okHttpRequestBuilder.header("Origin", origin)
            
            // Critical: Match these versions to current Chrome expectations
            if (url.contains("youtubei/v1")) {
                okHttpRequestBuilder.header("X-YouTube-Client-Name", if (isMusic) "67" else "1")
                okHttpRequestBuilder.header("X-YouTube-Client-Version", if (isMusic) "1.20240312.01.00" else "2.20240313.05.00")
                okHttpRequestBuilder.header("X-Goog-Api-Format-Version", "2")
            }
            
            // Sec headers help verify the request isn't a simple script
            okHttpRequestBuilder.header("Sec-Fetch-Dest", "empty")
            okHttpRequestBuilder.header("Sec-Fetch-Mode", "cors")
            okHttpRequestBuilder.header("Sec-Fetch-Site", "same-origin")
        }

        if (headers != null) {
            for (key in headers.keys) {
                val values = headers[key]
                if (!values.isNullOrEmpty()) {
                    // Don't let NewPipeExtractor override our synchronized headers
                    if (key.equals("User-Agent", true) || 
                        key.equals("Referer", true) || 
                        key.equals("Origin", true) ||
                        key.startsWith("Sec-", true)) continue

                    okHttpRequestBuilder.header(key, values[0])
                    for (i in 1 until values.size) {
                        okHttpRequestBuilder.addHeader(key, values[i])
                    }
                }
            }
        }

        if ("POST".equals(method, ignoreCase = true)) {
            val contentType = headers?.get("Content-Type")?.get(0) ?: "application/json"
            val body = data?.toRequestBody(contentType.toMediaTypeOrNull()) ?: "".toByteArray().toRequestBody()
            okHttpRequestBuilder.post(body)
        } else {
            okHttpRequestBuilder.method(method, null)
        }

        val okHttpRequest = okHttpRequestBuilder.build()
        val okHttpResponse = try {
            client.newCall(okHttpRequest).execute()
        } catch (e: Exception) {
            Log.e(tag, "Network error: ${e.message}")
            throw e
        }

        val responseBody = okHttpResponse.body.string()
        
        // Log if we are still hitting the reload loop
        if (responseBody.contains("window.location.reload()") || okHttpResponse.code == 429) {
            Log.w(tag, "Bot detection triggered for $url. Code: ${okHttpResponse.code}")
        }
        
        return Response(
            okHttpResponse.code, 
            okHttpResponse.message, 
            okHttpResponse.headers.toMultimap(),
            responseBody, 
            okHttpResponse.request.url.toString()
        )
    }
}
