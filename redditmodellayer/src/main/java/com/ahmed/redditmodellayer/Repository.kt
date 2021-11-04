package com.ahmed.redditmodellayer

import com.ahmed.redditmodellayer.models.PostsList
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getPosts(): Flow<Result<PostsList>>
    fun getMorePosts(limit: Int, after: String): Flow<Result<PostsList>>
}