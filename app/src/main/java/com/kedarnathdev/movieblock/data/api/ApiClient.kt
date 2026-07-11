package com.kedarnathdev.movieblock.data.api

import com.kedarnathdev.movieblock.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    @Volatile
    private var baseUrl: String = BuildConfig.API_BASE_URL
    
    @Volatile
    private var retrofit: Retrofit? = null
    
    // Singleton OkHttpClient - reused across all requests to prevent resource leaks
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    fun initialize(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
        retrofit = null // Reset to recreate with new URL
    }

    fun getApi(): MovieBlockApi {
        // Thread-safe double-checked locking pattern
        val instance = retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient) // Reuse singleton client
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }

        return instance.create(MovieBlockApi::class.java)
    }
}
