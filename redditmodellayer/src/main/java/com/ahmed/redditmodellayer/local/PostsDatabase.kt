package com.ahmed.redditmodellayer.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ahmed.redditmodellayer.models.Post

@Database(entities = [Post::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class PostsDatabase : RoomDatabase() {

    abstract val postsDao: PostsDao
}