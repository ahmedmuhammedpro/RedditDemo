package com.ahmed.redditmodellayer

import com.ahmed.redditmodellayer.local.PostsDao
import com.ahmed.redditmodellayer.models.Post
import com.ahmed.redditmodellayer.models.PostsList
import com.ahmed.redditmodellayer.remote.PostsRestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class RepositoryImp(private val postsApi: PostsRestApi, private val postsDao: PostsDao) :
    Repository,
    SearchRepository {

    /**
     * Get posts for first time from remote data source else get cached posts.
     * emit remote posts or cached if remote fail.
     *
     * @return flow
     */
    override fun getPosts(): Flow<Result<PostsList>> {
        return flow {
            // A flag to indicate if emitted posts are got from remote or local data source.
            // updated the value only if there is an error with remote data source
            var shouldFetchFromDB = false
            try {
                val remotePostsList = postsApi.getPosts()?.remotePostsList
                if (remotePostsList != null) {
                    val mappedPosts = arrayListOf<Post>()
                    remotePostsList.posts.forEach {
                        mappedPosts.add(it.post)
                    }

                    val postsList = PostsList(
                        remotePostsList.after, remotePostsList.dist,
                        mappedPosts, remotePostsList.before
                    )

                    emit(Result.success(postsList))

                    postsDao.deleteAllCachedPosts()
                    postsDao.insert(mappedPosts)
                } else {
                    shouldFetchFromDB = true
                }
            } catch (e: Throwable) {
                Timber.e(e)
                shouldFetchFromDB = true
            } finally {
                if (shouldFetchFromDB) {
                    val cachedPosts = postsDao.getCachedPosts()
                    if (!cachedPosts.isNullOrEmpty()) {
                        val postsList = PostsList(null, 0, cachedPosts, null, shouldFetchFromDB)
                        emit(Result.success(postsList))
                    } else {
                        emit(Result.failure(Exception("Can't fetch remote or cached posts")))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Get mote posts.
     *
     * @param limit number of posts per request.
     * @param after next post.
     * @return flow.
     */
    override fun getMorePosts(limit: Int, after: String): Flow<Result<PostsList>> {
        return flow {
            try {
                val remotePostsList = postsApi.getPosts(limit, after)?.remotePostsList
                if (remotePostsList != null) {
                    val mappedPosts = arrayListOf<Post>()
                    remotePostsList.posts.forEach {
                        mappedPosts.add(it.post)
                    }
                    postsDao.insert(mappedPosts)
                    val postsList = PostsList(
                        remotePostsList.after,
                        remotePostsList.dist,
                        mappedPosts,
                        remotePostsList.before
                    )
                    emit(Result.success(postsList))
                } else {
                    emit(Result.failure(Exception("null posts results")))
                }
            } catch (e: Throwable) {
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Search for posts
     *
     * @param term search term
     * @param limit number of posts per request.
     * @param after next post.
     * @return flow.
     */
    override fun search(term: String, limit: Int, after: String): Flow<Result<PostsList>> {
        return flow {
            try {
                val remotePostsList = postsApi.search(term, limit)?.remotePostsList
                if (remotePostsList != null) {
                    val mappedPosts = arrayListOf<Post>()
                    remotePostsList.posts.forEach {
                        mappedPosts.add(it.post)
                    }
                    val postsList = PostsList(
                        remotePostsList.after,
                        remotePostsList.dist,
                        mappedPosts,
                        remotePostsList.after
                    )
                    emit(Result.success(postsList))
                } else {
                    emit(Result.failure(Exception("null posts results")))
                }
            } catch (e: Throwable) {
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)
    }


}