package com.ahmed.redditdemo

import android.app.Application
import com.ahmed.redditdemo.di.AppComponent
import com.ahmed.redditdemo.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}