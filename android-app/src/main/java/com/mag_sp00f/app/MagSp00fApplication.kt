package com.mag_sp00f.app

import android.app.Application
import timber.log.Timber

/**
 * 🏴‍☠️ Main Application class for nf-sp00f33r
 * Initializes Timber logging for GPO and PDOL debugging
 */
class MagSp00fApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging (always in debug builds)
        Timber.plant(Timber.DebugTree())
        Timber.d("🏴‍☠️ MagSp00fApplication initialized - Timber logging ACTIVE")
    }
}
