package com.ahmed.redditdemo.main.postslistsearching

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.redditmodellayer.SearchRepository
import com.ahmed.redditmodellayer.models.PostsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchResultViewModel @Inject constructor(private val searchRepository: SearchRepository) : ViewModel() {

    private val _searchPostsStateFlow = MutableStateFlow<Result<PostsList>?>(null)
    val searchPostsStateFlow: StateFlow<Result<PostsList>?> = _searchPostsStateFlow

    private val _morePostsStateFlow = MutableStateFlow<Result<PostsList>?>(null)
    val morePostsStateFlow: StateFlow<Result<PostsList>?> = _morePostsStateFlow

    private val _loadingLiveData = MutableLiveData(true)
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    fun getSearchPosts(searchTerm: String, limit: Int = 25, after: String = "") {
        _loadingLiveData.value = true
        viewModelScope.launch {
            searchRepository.search(searchTerm, limit, after)
                .collect { result ->
                    _searchPostsStateFlow.value = result
                    _loadingLiveData.value = false
                }
        }
    }

    fun getMoreSearchPosts(searchTerm: String, limit: Int = 10, after: String) {
        viewModelScope.launch {
            searchRepository.search(searchTerm, limit, after)
                .collect { result ->
                    _morePostsStateFlow.value = result
                }
        }
    }

}