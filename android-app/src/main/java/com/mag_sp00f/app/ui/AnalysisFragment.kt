package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.mag_sp00f.app.R
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.models.CardProfile
import timber.log.Timber

class AnalysisFragment : Fragment() {

    private lateinit var cardProfileManager: CardProfileManager

    companion object {
        private const val TAG = "AnalysisFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cardProfileManager = CardProfileManager.getInstance()
        
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    AnalysisScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AnalysisScreen() {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        
        // Get real card data for analysis
        var cardProfiles by remember { mutableStateOf<List<CardProfile>>(emptyList()) }
        var selectedCard by remember { mutableStateOf<CardProfile?>(null) }
        
        // Load cards on startup
        LaunchedEffect(Unit) {
            cardProfiles = if (::cardProfileManager.isInitialized) {
                cardProfileManager.getAllCardProfiles()
            } else {
                emptyList()
            }
            selectedCard = cardProfiles.firstOrNull()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Analytics Icon",
                                tint = Color(0xFF00FF41),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "nf-sp00f33r",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF0D1117),
                        titleContentColor = Color(0xFF00FF41),
                        navigationIconContentColor = Color(0xFF00FF41),
                        actionIconContentColor = Color(0xFF00FF41)
                    ),
                    scrollBehavior = scrollBehavior
                )
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF21262D).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📊 EMV Data Analysis",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cards: ${cardProfiles.size} | Selected: ${selectedCard?.emvCardData?.detectCardType()?.name ?: "None"}",
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Analysis Tools
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { analyzeData() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF41),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ANALYZE", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { exportAnalysis() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF58A6FF),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("EXPORT", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Analysis Results
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF21262D).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "🔬 Cryptographic Analysis",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            AnalysisItem("AC (Auth Cryptogram)", 
                                selectedCard?.emvCardData?.emvTags?.get("9F26") ?: "No data", 
                                if (selectedCard?.emvCardData?.emvTags?.containsKey("9F26") == true) Color(0xFF4CAF50) else Color(0xFF7D8590))
                            AnalysisItem("TC (Transaction Certificate)", 
                                selectedCard?.emvCardData?.emvTags?.get("9F34") ?: "No data", 
                                if (selectedCard?.emvCardData?.emvTags?.containsKey("9F34") == true) Color(0xFF4CAF50) else Color(0xFF7D8590))
                            AnalysisItem("ATC (Transaction Counter)", 
                                selectedCard?.emvCardData?.applicationTransactionCounter ?: "No data", 
                                if (!selectedCard?.emvCardData?.applicationTransactionCounter.isNullOrEmpty()) Color(0xFF4CAF50) else Color(0xFF7D8590))
                            AnalysisItem("UN (Unpredictable Number)", 
                                selectedCard?.emvCardData?.emvTags?.get("9F37") ?: "No data", 
                                if (selectedCard?.emvCardData?.emvTags?.containsKey("9F37") == true) Color(0xFF4CAF50) else Color(0xFF7D8590))
                        }
                    }

                    // TLV Analysis
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF21262D).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "🏷️ TLV Tag Analysis",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            AnalysisItem("Tag 0x57 (Track2)", 
                                selectedCard?.emvCardData?.track2Data ?: "Waiting for card data", 
                                if (!selectedCard?.emvCardData?.track2Data.isNullOrEmpty()) Color(0xFF4CAF50) else Color(0xFF7D8590))
                            AnalysisItem("Tag 0x5F34 (PAN Sequence)", 
                                selectedCard?.emvCardData?.emvTags?.get("5F34") ?: "Waiting for card data", 
                                if (selectedCard?.emvCardData?.emvTags?.containsKey("5F34") == true) Color(0xFF4CAF50) else Color(0xFF7D8590))
                            AnalysisItem("Tag 0x82 (AIP)", 
                                selectedCard?.emvCardData?.applicationInterchangeProfile ?: "Waiting for card data", 
                                if (!selectedCard?.emvCardData?.applicationInterchangeProfile.isNullOrEmpty()) Color(0xFF4CAF50) else Color(0xFF7D8590))
                            AnalysisItem("Tag 0x94 (AFL)", 
                                selectedCard?.emvCardData?.applicationFileLocator ?: "Waiting for card data", 
                                if (!selectedCard?.emvCardData?.applicationFileLocator.isNullOrEmpty()) Color(0xFF4CAF50) else Color(0xFF7D8590))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AnalysisItem(label: String, value: String, color: Color) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7D8590),
                modifier = Modifier.weight(0.4f)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = color,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
    
    // Function implementations for button actions
    private fun analyzeData() {
        if (::cardProfileManager.isInitialized) {
            val cardCount = cardProfileManager.getAllCardProfiles().size
            Timber.d("$TAG Analyzing $cardCount cards in database")
            // In a real implementation, this would trigger cryptographic analysis
        } else {
            Timber.w("$TAG CardProfileManager not initialized")
        }
    }
    
    private fun exportAnalysis() {
        if (::cardProfileManager.isInitialized) {
            try {
                val exportData = cardProfileManager.exportToJson()
                Timber.d("$TAG Analysis exported successfully")
                // In a real implementation, this would save to file or share
            } catch (e: Exception) {
                Timber.e(e, "$TAG Error exporting analysis")
            }
        } else {
            Timber.w("$TAG CardProfileManager not initialized for export")
        }
    }
}
