package com.mag_sp00f.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.mag_sp00f.app.R
import com.mag_sp00f.app.ui.emulation.EmulationFragment

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, EmulationFragment())
            }
        }
    }
}
