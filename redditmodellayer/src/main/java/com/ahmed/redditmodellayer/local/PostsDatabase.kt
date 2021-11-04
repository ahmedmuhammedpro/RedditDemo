package com.ahmed.redditmodellayer.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ahmed.redditmodellayer.models.Post

@Database(entities = [Post::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class PostsDatabase : RoomDatabase() {

    abstract val postsDao: PostsDao

    companion object {

        @JvmStatic
        @Volatile
        private var INSTANCE: PostsDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): PostsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PostsDatabase::class.java,
                    "posts_db"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}