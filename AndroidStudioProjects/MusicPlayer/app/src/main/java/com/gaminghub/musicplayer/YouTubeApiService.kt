package com.gaminghub.musicplayer

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}

object YouTubeRetrofitInstance {
    private const val TAG = "YouTubeRetrofit"
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Android-Package", "com.gaminghub.musicplayer")
                    // SHA-1 fingerprint of the signing certificate
                    .addHeader("X-Android-Cert", "C1F6ACF6CAD20E6F5C8CD2516F3A7796A9D63254")
                    .build()
                Log.d(TAG, "Sending request to: ${request.url}")
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    val api: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }
}
