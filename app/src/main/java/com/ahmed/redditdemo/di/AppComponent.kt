package com.ahmed.redditdemo.di

import android.content.Context
import com.ahmed.redditdemo.main.di.PostsComponent
import com.ahmed.redditmodellayer.di.ModelLayerModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [ModelLayerModule::class, SubcomponentsModule::class, ViewModelBuilderModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun postsComponent(): PostsComponent.Factory
}

@Module(subcomponents = [PostsComponent::class])
object SubcomponentsModule