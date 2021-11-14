package com.ahmed.redditmodellayer.di

import android.content.Context
import androidx.room.Room
import com.ahmed.redditmodellayer.BuildConfig
import com.ahmed.redditmodellayer.Repository
import com.ahmed.redditmodellayer.RepositoryImp
import com.ahmed.redditmodellayer.SearchRepository
import com.ahmed.redditmodellayer.local.PostsDao
import com.ahmed.redditmodellayer.local.PostsDatabase
import com.ahmed.redditmodellayer.remote.PostsRestApi
import com.ahmed.redditmodellayer.remote.PostsRestApi.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object ModelLayerModule {

    @JvmStatic
    @Provides
    fun provideRepository(postsApi: PostsRestApi, postsDao: PostsDao): Repository {
        return RepositoryImp(postsApi, postsDao)
    }

    @Provides
    fun provideSearchRepository(postsApi: PostsRestApi, postsDao: PostsDao): SearchRepository {
        return RepositoryImp(postsApi, postsDao)
    }

    @JvmStatic
    @Provides
    fun providePostsRestApi(): PostsRestApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttp())
            .build()

        return retrofit.create(PostsRestApi::class.java)
    }

    /**
     * Instantiated only once
     */
    @JvmStatic
    @Provides
    @Singleton
    fun providePostsDatabase(context: Context): PostsDao {
        return synchronized(PostsDatabase::class) {
            Room.databaseBuilder(
                context.applicationContext,
                PostsDatabase::class.java,
                "posts_db"
            )
                .build()
                .postsDao
        }
    }
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