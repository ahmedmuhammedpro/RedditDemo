package com.ahmed.redditdemo.main.postslist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.redditdemo.R
import com.ahmed.redditdemo.commonadapters.PostsListAdapter
import com.ahmed.redditdemo.databinding.FragmentPostsListBinding
import com.ahmed.redditdemo.main.MainActivity
import com.ahmed.redditdemo.main.postslistsearching.SearchResultFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PostsListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: FragmentPostsListBinding
    private val postsListViewModel by viewModels<PostsListViewModel> { viewModelFactory }
    private var isLoadingMore = false
    private var after: String? = null
    private var rootView: View? = null
    private val postsListAdapter = PostsListAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).postsComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (rootView == null) {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_posts_list, container, false)
            binding.postsListRecyclerView.adapter = postsListAdapter
            setupViewListener()

            postsListViewModel.getPosts()
            setupObservers()

            rootView = binding.root
        }

        return rootView
    }

    private fun setupObservers() {

        lifecycleScope.launch {
            launch {
                postsListViewModel.firstPostsStateFlow.collect { result ->
                    result?.let {
                        try {
                            val postsList = result.getOrThrow()
                            after = postsList.after
                            postsListAdapter.addNewData(postsList.posts)
                            binding.contentView.visibility = View.VISIBLE
                            if (postsList.isCached) {
                                showSnackBar("Can't fetch posts from our server!")
                            }
                        } catch (ex: Throwable) {
                            binding.errorViewContainer.visibility = View.VISIBLE
                            Timber.e(ex)
                        }
                    }
                }
            }

            launch {
                postsListViewModel.morePostsStateFlow.collect { result ->
                    result?.let {
                        try {
                            val postsList = result.getOrThrow()
                            after = if (after != postsList.after) { postsList.after } else { null }
                            postsListAdapter.addNewData(postsList.posts)
                        } catch (ex: Throwable) {
                            showSnackBar("Something went wrong!")
                            Timber.e(ex)
                        }

                        isLoadingMore = false
                        binding.loadMore.visibility = View.GONE
                    }
                }
            }

            launch {
                postsListViewModel.loadingLiveData.observe(viewLifecycleOwner) {
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
                    postsListViewModel.getMorePosts(after = after!!)
                }
            }
        })

        binding.tryAgain.setOnClickListener {
            postsListViewModel.getPosts()
        }

        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchTerm = binding.searchEditText.text.toString()
                if (searchTerm.isNotEmpty()) {
                    binding.searchEditText.setText("")
                    navigateToSearchFragment(searchTerm)
                    hideKeyboard()
                }
                return@setOnEditorActionListener true
            }

            false
        }

        binding.searchButton.setOnClickListener {
            val searchTerm = binding.searchEditText.text.toString()
            binding.searchEditText.setText("")
            if (searchTerm.isNotEmpty()) {
                navigateToSearchFragment(searchTerm)
                hideKeyboard()
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val inputManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun navigateToSearchFragment(searchTerm: String) {
        val bundle = Bundle().apply {
            putString(SearchResultFragment.EXTRA_SEARCH_TERM, searchTerm)
        }
        findNavController().navigate(R.id.searchResultFragment, bundle)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PostsListFragment().apply {
            }
    }
}