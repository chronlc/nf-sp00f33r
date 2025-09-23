package com.mag_sp00f.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.mag_sp00f.app.R
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import kotlinx.coroutines.delay

/**
 * Professional Splash Screen for nf-sp00f33r
 * Dark theme with logo and smooth transition
 */
class SplashActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set dark status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        
        setContent {
            MagSp00fTheme {
                SplashScreen()
            }
        }
    }

    @Composable
    fun SplashScreen() {
        var isVisible by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            isVisible = true
            delay(3000) // Show splash for 3 seconds
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // nfspoof3.png image
                Image(
                    painter = painterResource(id = R.drawable.nfspoof3),
                    contentDescription = "nf-sp00f33r Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(32.dp),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "nf-sp00f33r",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BCD4),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "EMV Security Research Terminal",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Loading indicator
                if (isVisible) {
                    CircularProgressIndicator(
                        color = Color(0xFF00BCD4),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
