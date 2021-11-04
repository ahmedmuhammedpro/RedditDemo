package com.ahmed.redditdemo.postslistsearching

import androidx.lifecycle.*
import com.ahmed.redditmodellayer.SearchRepository
import com.ahmed.redditmodellayer.models.PostsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchResultViewModel(private val searchRepository: SearchRepository) : ViewModel() {

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

class SearchResultViewModelFactory(private val searchRepository: SearchRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchResultViewModel::class.java)) {
            return SearchResultViewModel(searchRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}