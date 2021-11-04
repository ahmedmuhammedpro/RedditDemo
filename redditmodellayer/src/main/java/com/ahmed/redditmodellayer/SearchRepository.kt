package com.ahmed.redditmodellayer

import com.ahmed.redditmodellayer.models.PostsList
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(term: String, limit: Int, after: String): Flow<Result<PostsList>>
}