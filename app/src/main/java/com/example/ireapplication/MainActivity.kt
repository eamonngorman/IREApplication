package com.example.ireapplication

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ireapplication.databinding.ActivityMainBinding
import com.example.ireapplication.util.ErrorHandler
import com.example.ireapplication.util.ErrorHandling
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ErrorHandling {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "en") ?: "en"
        val fontScale = prefs.getFloat("font_size", 1.0f)
        
        val locale = Locale(language)
        val config = Configuration(newBase.resources.configuration)
        
        // Set locale
        Locale.setDefault(locale)
        config.setLocale(locale)
        
        // Set font scale
        config.fontScale = fontScale
        
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Check if high contrast is enabled and apply theme before super.onCreate
        val prefs = getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
        val highContrastEnabled = prefs.getBoolean("high_contrast", false)
        val fontScale = prefs.getFloat("font_size", 1.0f)
        
        // Apply font scale to current activity
        val configuration = resources.configuration
        configuration.fontScale = fontScale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        
        if (highContrastEnabled) {
            setTheme(R.style.Theme_IREApplication_HighContrast)
        }
        
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            // Log app startup for debugging
            ErrorHandler.logDebug("App started successfully")
            
            // Set up the toolbar
            setSupportActionBar(binding.innerToolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            // Set navigation icon color
            binding.innerToolbar.navigationIcon?.setTint(getColor(R.color.gold))

            // Set up navigation
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            // Configure top-level destinations (no back button)
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_tour,
                    R.id.navigation_share,
                    R.id.navigation_scan,
                    R.id.navigation_settings
                )
            )

            // Set up touch listener to dismiss search on outside touch
            binding.root.setOnTouchListener { _, _ ->
                val searchItem = binding.innerToolbar.menu?.findItem(R.id.action_search)
                if (searchItem?.isActionViewExpanded == true) {
                    searchItem.collapseActionView()
                    true
                } else {
                    false
                }
            }

            // Set up the action bar and bottom navigation with the nav controller
            setupActionBarWithNavController(navController, appBarConfiguration)
            binding.bottomNavigation.setupWithNavController(navController)

            // Listen for navigation changes to update back button visibility and color
            navController.addOnDestinationChangedListener { _, destination, _ ->
                val isTopLevel = appBarConfiguration.topLevelDestinations.contains(destination.id)
                supportActionBar?.setDisplayHomeAsUpEnabled(!isTopLevel)
                // Update navigation icon color when it changes
                binding.innerToolbar.navigationIcon?.setTint(getColor(R.color.gold))
                
                // Collapse search view on navigation
                val searchItem = binding.innerToolbar.menu?.findItem(R.id.action_search)
                if (searchItem?.isActionViewExpanded == true) {
                    searchItem.collapseActionView()
                }
            }

            setupNavigation()
        } catch (e: Exception) {
            ErrorHandler.handleError(
                this,
                e,
                "Failed to start app properly. Please try again.",
                true
            )
        }
    }

    private fun setupNavigation() {
        try {
            // TODO: Setup navigation
            ErrorHandler.logDebug("Navigation setup completed")
        } catch (e: Exception) {
            ErrorHandler.handleError(
                this,
                e,
                "Failed to setup navigation",
                false
            )
        }
    }

    override fun getRootView(): View = binding.root

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        // Configure SearchView styling
        searchView.apply {
            maxWidth = Int.MAX_VALUE // Allow full width expansion
            
            // Set search text colors
            val searchText = findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
            searchText.setTextColor(getColor(R.color.gold))
            searchText.setHintTextColor(getColor(R.color.gold))
            
            // Set search icon colors
            val searchIcon = findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            searchIcon.setColorFilter(getColor(R.color.gold))
            
            // Set close button color
            val closeButton = findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeButton.setColorFilter(getColor(R.color.gold))

            // Set back arrow color
            val backArrow = findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)?.drawable
            backArrow?.setTint(getColor(R.color.gold))

            // Set plate background to transparent
            val searchPlate = findViewById<View>(androidx.appcompat.R.id.search_plate)
            searchPlate.setBackgroundColor(android.graphics.Color.TRANSPARENT)

            // Remove underline
            val searchBarBackground = findViewById<View>(androidx.appcompat.R.id.search_plate)
            searchBarBackground.setBackgroundColor(android.graphics.Color.TRANSPARENT)

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    // Clear focus and collapse the search view when focus is lost
                    clearFocus()
                    searchItem.collapseActionView()
                }
            }
            
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        if (it.isNotBlank()) {
                            val action = NavGraphDirections.actionGlobalSearchResults(it.trim())
                            navController.navigate(action)
                            clearFocus()
                            searchItem.collapseActionView()
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }

        // Handle SearchView expansion/collapse
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Re-apply gold tint when expanded
                val searchView = item.actionView as SearchView
                searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)?.setColorFilter(getColor(R.color.gold))
                searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)?.setColorFilter(getColor(R.color.gold))
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                val searchView = item.actionView as SearchView
                searchView.clearFocus() // Ensure keyboard is hidden
                return true
            }
        })

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}