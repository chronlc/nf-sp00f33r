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

class AnalysisFragment : Fragment() {

    companion object {
        private const val TAG = "AnalysisFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF161B22),
                    contentColor = Color(0xFF00FF41)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = "Read") },
                        label = { Text("Read", fontSize = 10.sp) },
                        selected = false,
                        onClick = { },
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
                        selected = false,
                        onClick = { },
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
                        selected = false,
                        onClick = { },
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
                        selected = true,
                        onClick = { },
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
                                text = "Advanced Cryptographic & TLV Analysis",
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
                            onClick = { },
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
                            onClick = { },
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

                            AnalysisItem("AC (Auth Cryptogram)", "No data", Color(0xFF7D8590))
                            AnalysisItem("TC (Transaction Certificate)", "No data", Color(0xFF7D8590))
                            AnalysisItem("ATC (Transaction Counter)", "No data", Color(0xFF7D8590))
                            AnalysisItem("UN (Unpredictable Number)", "No data", Color(0xFF7D8590))
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

                            AnalysisItem("Tag 0x57 (Track2)", "Waiting for card data", Color(0xFF7D8590))
                            AnalysisItem("Tag 0x5F34 (PAN Sequence)", "Waiting for card data", Color(0xFF7D8590))
                            AnalysisItem("Tag 0x82 (AIP)", "Waiting for card data", Color(0xFF7D8590))
                            AnalysisItem("Tag 0x94 (AFL)", "Waiting for card data", Color(0xFF7D8590))
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
}
