package com.ahmed.redditmodellayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ahmed.redditmodellayer.models.Post

@Dao
interface PostsDao {

    @Query("SELECT * FROM POSTS")
    suspend fun getCachedPosts(): List<Post>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(posts: List<Post>)

    @Query("DELETE FROM Posts")
    suspend fun deleteAllCachedPosts()
}