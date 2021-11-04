package com.ahmed.redditdemo.postslistsearching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.redditdemo.R
import com.ahmed.redditdemo.commonadapters.PostsListAdapter
import com.ahmed.redditdemo.databinding.FragmentSearchResultBinding
import com.ahmed.redditmodellayer.RepositoryImp
import com.ahmed.redditmodellayer.SearchRepository
import com.ahmed.redditmodellayer.local.PostsDatabase
import com.ahmed.redditmodellayer.remote.PostsApi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchResultFragment : Fragment() {

    private var searchTerm: String? = null
    private lateinit var binding: FragmentSearchResultBinding
    private lateinit var searchResultViewModel: SearchResultViewModel
    private var isLoadingMore = false
    private var after: String? = null
    private val postsListAdapter = PostsListAdapter()
    private val searchRepository: SearchRepository by lazy {
        val postsDao = PostsDatabase.getDatabase(requireContext()).postsDao
        RepositoryImp(PostsApi.getPostsRestApi(), postsDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchTerm = it.getString(EXTRA_SEARCH_TERM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search_result, container, false)
        searchTerm?.let {
            binding.postsListRecyclerView.adapter = postsListAdapter
            setupViewListener()
            searchResultViewModel = ViewModelProvider(
                this,
                SearchResultViewModelFactory(searchRepository)
            )[SearchResultViewModel::class.java]
            searchResultViewModel.getSearchPosts(it)
            setupObservers()
        }


        return binding.root
    }

    private fun setupObservers() {

        lifecycleScope.launch {
            launch {
                searchResultViewModel.searchPostsStateFlow.collect { result ->
                    result?.let {
                        try {
                            val postsList = result.getOrThrow()
                            after = postsList.after
                            postsListAdapter.addNewData(postsList.posts)
                            binding.contentView.visibility = View.VISIBLE
                        } catch (ex: Throwable) {
                            binding.errorViewContainer.visibility = View.VISIBLE
                            Timber.e(ex)
                        }
                    }
                }
            }

            launch {
                searchResultViewModel.morePostsStateFlow.collect { result ->
                    result?.let {
                        try {
                            val postsList = result.getOrThrow()
                            after = if (after != postsList.after) { postsList.after } else { null }
                            postsListAdapter.addNewData(postsList.posts)
                        } catch (ex: Throwable) {
                            Snackbar.make(binding.root, "Something went wrong!", Snackbar.LENGTH_SHORT).show()
                            Timber.e(ex)
                        }

                        isLoadingMore = false
                        binding.loadMore.visibility = View.GONE
                    }
                }
            }

            launch {
                searchResultViewModel.loadingLiveData.observe(viewLifecycleOwner) {
                    if (it) {
                        binding.firstLoading.visibility = View.VISIBLE
                        binding.errorViewContainer.visibility = View.GONE
                        binding.contentView.visibility = View.GONE
                    } else {
                        binding.firstLoading.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupViewListener() {

        binding.postsListRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                if (!after.isNullOrEmpty() && !isLoadingMore && lastVisibleItem == linearLayoutManager.itemCount - 1) {
                    binding.loadMore.visibility = View.VISIBLE
                    isLoadingMore = true
                    searchResultViewModel.getMoreSearchPosts(searchTerm!!, 10, after!!)
                }
            }
        })
    }

    companion object {

        const val EXTRA_SEARCH_TERM = "extra_search_term"

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(searchTerm: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_SEARCH_TERM, searchTerm)
                }
            }
    }
}