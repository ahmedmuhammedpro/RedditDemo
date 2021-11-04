package com.ahmed.redditdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ahmed.redditdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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