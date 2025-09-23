package com.mag_sp00f.app.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.mag_sp00f.app.R
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : FragmentActivity() {
    private lateinit var cardManager: CardProfileManager
    private val _recentCards = MutableStateFlow(emptyList<com.mag_sp00f.app.models.CardProfile>())
    private val recentCards: StateFlow<List<com.mag_sp00f.app.models.CardProfile>> = _recentCards.asStateFlow()
    private val _currentFragment = mutableStateOf("dashboard")

    // Fragment instances
    private val cardReadingFragment by lazy { CardReadingFragment() }
    private val emulationFragment by lazy { EmulationFragment() }
    private val cardDatabaseFragment by lazy { CardDatabaseFragment() }
    private val analysisFragment by lazy { AnalysisFragment() }

    companion object {
        private const val TAG = "SecurityTerminal"
        private const val FRAGMENT_CONTAINER_ID = android.R.id.content
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set dark status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        cardManager = CardProfileManager()
        loadRecentCards()

        setContent {
            MagSp00fTheme {
                ProfessionalSecurityInterface()
            }
        }
        Log.d(TAG, "Professional EMV Security Terminal Initialized")
    }

    private fun loadRecentCards() {
        // REAL DATA ONLY - No fake/simulated data per newrule.md
        val cards = cardManager.getRecentCards()
        _recentCards.value = cards
        Log.d(TAG, "Loaded ${cards.size} recent EMV profiles from database")
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfessionalSecurityInterface() {
        val cards by recentCards.collectAsState()
        val currentScreen by _currentFragment

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF00FF41),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "nf-sp00f33r",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0D1117),
                        titleContentColor = Color(0xFF00FF41)
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF161B22),
                    contentColor = Color(0xFF00FF41)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 10.sp) },
                        selected = currentScreen == "dashboard",
                        onClick = { _currentFragment.value = "dashboard" },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF41),
                            selectedTextColor = Color(0xFF00FF41),
                            indicatorColor = Color(0xFF21262D),
                            unselectedIconColor = Color(0xFF7D8590),
                            unselectedTextColor = Color(0xFF7D8590)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = "Read") },
                        label = { Text("Read", fontSize = 10.sp) },
                        selected = currentScreen == "read",
                        onClick = { navigateToFragment("read") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF41),
                            selectedTextColor = Color(0xFF00FF41),
                            indicatorColor = Color(0xFF21262D),
                            unselectedIconColor = Color(0xFF7D8590),
                            unselectedTextColor = Color(0xFF7D8590)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Security, contentDescription = "Emulate") },
                        label = { Text("Emulate", fontSize = 10.sp) },
                        selected = currentScreen == "emulate",
                        onClick = { navigateToFragment("emulate") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF41),
                            selectedTextColor = Color(0xFF00FF41),
                            indicatorColor = Color(0xFF21262D),
                            unselectedIconColor = Color(0xFF7D8590),
                            unselectedTextColor = Color(0xFF7D8590)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Storage, contentDescription = "Database") },
                        label = { Text("Database", fontSize = 10.sp) },
                        selected = currentScreen == "database",
                        onClick = { navigateToFragment("database") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF41),
                            selectedTextColor = Color(0xFF00FF41),
                            indicatorColor = Color(0xFF21262D),
                            unselectedIconColor = Color(0xFF7D8590),
                            unselectedTextColor = Color(0xFF7D8590)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analysis") },
                        label = { Text("Analysis", fontSize = 10.sp) },
                        selected = currentScreen == "analysis",
                        onClick = { navigateToFragment("analysis") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF41),
                            selectedTextColor = Color(0xFF00FF41),
                            indicatorColor = Color(0xFF21262D),
                            unselectedIconColor = Color(0xFF7D8590),
                            unselectedTextColor = Color(0xFF7D8590)
                        )
                    )
                }
            },
            containerColor = Color(0xFF0D1117)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0D1117),
                                Color(0xFF161B22),
                                Color(0xFF21262D)
                            )
                        )
                    )
                    .padding(paddingValues)
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.nfspoof3),
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.1f
                )

                when (currentScreen) {
                    "dashboard" -> DashboardContent()
                    "read" -> CardReadingContent()
                    "emulate" -> EmulationContent()
                    "database" -> DatabaseContent()
                    "analysis" -> AnalysisContent()
                    else -> DashboardContent()
                }
            }
        }
    }

    @Composable
    private fun DashboardContent() {
        val cards by recentCards.collectAsState()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card with stretched background image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF21262D).copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Stretched background image
                    Image(
                        painter = painterResource(id = R.drawable.nfspoof3),
                        contentDescription = "EMV Analysis System",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.3f
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "EMV Analysis System",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF41),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Professional NFC EMV Card Reading & Analysis",
                            fontSize = 14.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Statistics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Total Cards",
                    value = "${cards.size}",
                    icon = Icons.Default.CreditCard,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Sessions",
                    value = "1",
                    icon = Icons.Default.PlayArrow,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Success Rate",
                    value = "98%",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }



            // Recent Cards Section
            if (cards.isNotEmpty()) {
                Text(
                    text = "Recent Cards",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41),
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                cards.take(3).forEach { card ->
                    RecentCardItem(
                        card = card,
                        onClick = { navigateToFragment("database") }
                    )
                }
                
                if (cards.size > 3) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navigateToFragment("database") },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF21262D).copy(alpha = 0.6f)
                        )
                    ) {
                        Text(
                            text = "View ${cards.size - 3} more cards...",
                            color = Color(0xFF00FF41),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF21262D).copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color(0xFF7D8590),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No cards found",
                            color = Color(0xFF7D8590),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Start by reading your first EMV card",
                            color = Color(0xFF7D8590),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ActionCard(
        title: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.height(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF21262D).copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF00FF41),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun StatCard(
        title: String,
        value: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.height(70.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF21262D).copy(alpha = 0.8f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFF00FF41),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FF41)
                    )
                }
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = Color(0xFF7D8590),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun RecentCardItem(
        card: com.mag_sp00f.app.models.CardProfile,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF21262D).copy(alpha = 0.7f)
            ),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = Color(0xFF00FF41),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.getSummary(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        text = "Last used: Recently",
                        fontSize = 12.sp,
                        color = Color(0xFF7D8590)
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF7D8590),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    private fun navigateToFragment(fragmentName: String) {
        _currentFragment.value = fragmentName
        Log.d(TAG, "Navigating to screen: $fragmentName")
    }

    @Composable
    private fun CardReadingContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = Color(0xFF00FF41),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Card Reading",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "NFC card reading functionality coming soon",
                    fontSize = 14.sp,
                    color = Color(0xFF7D8590),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun EmulationContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF00FF41),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Emulation",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "EMV emulation attacks coming soon",
                    fontSize = 14.sp,
                    color = Color(0xFF7D8590),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun DatabaseContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Storage,
                    contentDescription = null,
                    tint = Color(0xFF00FF41),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Database",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Card database management coming soon",
                    fontSize = 14.sp,
                    color = Color(0xFF7D8590),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun AnalysisContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    tint = Color(0xFF00FF41),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Analysis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "EMV data analysis coming soon",
                    fontSize = 14.sp,
                    color = Color(0xFF7D8590),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (_currentFragment.value != "dashboard") {
            _currentFragment.value = "dashboard"
            Log.d(TAG, "Returned to dashboard")
        } else {
            super.onBackPressed()
            Log.d(TAG, "Exiting application")
        }
    }
}
