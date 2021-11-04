package com.ahmed.redditdemo.postslist

import androidx.lifecycle.*
import com.ahmed.redditmodellayer.Repository
import com.ahmed.redditmodellayer.models.PostsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostsListViewModel(private val repository: Repository) : ViewModel() {

    private val _firstPostsStateFlow = MutableStateFlow<Result<PostsList>?>(null)
    val firstPostsStateFlow: StateFlow<Result<PostsList>?> = _firstPostsStateFlow

    private val _morePostsStateFlow = MutableStateFlow<Result<PostsList>?>(null)
    val morePostsStateFlow: StateFlow<Result<PostsList>?> = _morePostsStateFlow

    private val _loadingLiveData = MutableLiveData(true)
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    fun getPosts() {
        _loadingLiveData.value = true
        viewModelScope.launch {
            repository.getPosts()
                .collect { result ->
                    _firstPostsStateFlow.value = result
                    _loadingLiveData.value = false
                }
        }
    }

    fun getMorePosts(limit: Int = 10, after: String) {
        viewModelScope.launch {
            repository.getMorePosts(limit, after)
                .collect { result ->
                    _morePostsStateFlow.value = result
                }
        }
    }

}

class PostsListViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsListViewModel::class.java)) {
            return PostsListViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}