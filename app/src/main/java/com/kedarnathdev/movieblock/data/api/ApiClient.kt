package com.kedarnathdev.movieblock.data.api

import com.kedarnathdev.movieblock.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var baseUrl: String = BuildConfig.API_BASE_URL
    private var retrofit: Retrofit? = null

    fun initialize(url: String) {
        baseUrl = if (url.endsWith("/api/")) url else "${url.trimEnd('/')}/api/"
        retrofit = null // Reset to recreate with new URL
    }

    fun getApi(): MovieBlockApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val instance = retrofit ?: Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .also { retrofit = it }

        return instance.create(MovieBlockApi::class.java)
    }
}
