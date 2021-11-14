package com.ahmed.redditdemo.main.di

import com.ahmed.redditdemo.main.postslist.PostsListFragment
import com.ahmed.redditdemo.main.postslistsearching.SearchResultFragment
import dagger.Subcomponent

@Subcomponent(modules = [PostsModule::class])
interface PostsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): PostsComponent
    }

    fun inject(fragment: PostsListFragment)
    fun inject(fragment: SearchResultFragment)
}