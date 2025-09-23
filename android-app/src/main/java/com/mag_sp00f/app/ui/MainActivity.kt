package com.mag_sp00f.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mag_sp00f.app.R
import com.mag_sp00f.app.cardreading.CardProfileManager
import androidx.core.view.WindowCompat
import timber.log.Timber

/**
 * 🏴‍☠️ PRODUCTION-GRADE MainActivity with Fragment Navigation
 * Fixed: "5 views stacked" issue by using proper Fragment navigation
 * instead of broken Compose-only navigation that caused UI overlays
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var cardManager: CardProfileManager
    private lateinit var bottomNavigation: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Timber.d("🏴‍☠️ MainActivity onCreate - Fragment navigation mode")
        
        // Set dark status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.BLACK  
        window.navigationBarColor = android.graphics.Color.BLACK

        // Initialize shared CardProfileManager singleton
        cardManager = CardProfileManager.getInstance()
        Timber.d("💾 CardProfileManager singleton initialized")
        
        setContentView(R.layout.activity_main)
        
        setupBottomNavigation()
        
        // Load initial fragment (Dashboard)
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
            Timber.d("📊 Initial DashboardFragment loaded")
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_dashboard -> {
                    Timber.d("📊 Navigation: Dashboard selected")
                    DashboardFragment()
                }
                R.id.nav_read -> {
                    Timber.d("📡 Navigation: Read Card selected")
                    CardReadingFragment()
                }
                R.id.nav_emulate -> {
                    Timber.d("💳 Navigation: Emulation selected")
                    EmulationFragment()
                }
                R.id.nav_database -> {
                    Timber.d("💾 Navigation: Database selected") 
                    CardDatabaseFragment()
                }
                R.id.nav_analysis -> {
                    Timber.d("📈 Navigation: Analysis selected")
                    AnalysisFragment()
                }
                else -> return@setOnItemSelectedListener false
            }
            
            loadFragment(fragment)
            true
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        Timber.d("🔄 Loading fragment: ${fragment.javaClass.simpleName}")
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    override fun onBackPressed() {
        Timber.d("⬅️ Back button pressed")
        super.onBackPressed()
    }
}
