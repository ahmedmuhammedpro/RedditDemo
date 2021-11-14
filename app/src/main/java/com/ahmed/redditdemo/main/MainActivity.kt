package com.ahmed.redditdemo.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ahmed.redditdemo.App
import com.ahmed.redditdemo.R
import com.ahmed.redditdemo.databinding.ActivityMainBinding
import com.ahmed.redditdemo.main.di.PostsComponent

class MainActivity : AppCompatActivity() {

    lateinit var postsComponent: PostsComponent
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        postsComponent = (application as App).appComponent.postsComponent().create()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        WindowInsetsControllerCompat(window, binding.root).isAppearanceLightStatusBars = true
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        NavigationUI.setupWithNavController(binding.toolbar, navHost.navController)
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbarTitle.text = when (destination.id) {
                R.id.postsListFragment -> resources.getString(R.string.posts)
                R.id.searchResultFragment -> resources.getString(R.string.search_results)
                else -> ""
            }
        }
    }
}