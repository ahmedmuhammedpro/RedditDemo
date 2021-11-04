package com.ahmed.redditmodellayer.remote

import com.ahmed.redditmodellayer.models.RemoteFullResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PostsRestApi {

    @GET("r/aww/top.json?t=all")
    suspend fun getPosts(@Query("limit") limit: Int = 10, @Query("after") after: String = ""): RemoteFullResponse?

    @GET("search.json")
    suspend fun search(@Query("q") term: String, @Query("limit") limit: Int, @Query("after") after: String = ""): RemoteFullResponse?
}