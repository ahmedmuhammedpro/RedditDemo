package com.ahmed.redditdemo.main.di

import androidx.lifecycle.ViewModel
import com.ahmed.redditdemo.di.ViewModelKey
import com.ahmed.redditdemo.main.postslist.PostsListViewModel
import com.ahmed.redditdemo.main.postslistsearching.SearchResultViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PostsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PostsListViewModel::class)
    abstract fun bindPostsListViewModel(viewModel: PostsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchResultViewModel::class)
    abstract fun bindSearchResultViewModel(viewModel: SearchResultViewModel): ViewModel

}