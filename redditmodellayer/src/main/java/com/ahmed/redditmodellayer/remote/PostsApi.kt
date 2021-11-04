package com.ahmed.redditmodellayer.remote

import com.ahmed.redditmodellayer.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PostsApi {

    private const val BASE_URL = "https://www.reddit.com/"

    fun getPostsRestApi(): PostsRestApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttp())
            .build()

        return retrofit.create(PostsRestApi::class.java)
    }

    private fun createOkHttp(): OkHttpClient {
        val okBuilder = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okBuilder.addInterceptor(loggingInterceptor)
        }

        return okBuilder.build()
    }
}