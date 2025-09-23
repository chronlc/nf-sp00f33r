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

class EmulationFragment : Fragment() {

    companion object {
        private const val TAG = "EmulationFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    EmulationScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun EmulationScreen() {
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
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security Icon",
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
                                text = "🛡️ EMV Card Emulation",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "HCE Service & Attack Module System",
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Emulation Controls
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
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("START HCE", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF4444),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("STOP", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Attack Modules
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
                                text = "⚡ Attack Modules",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            AttackModuleItem("PPSE AID Poisoning", "VISA → MasterCard", true)
                            AttackModuleItem("AIP Force Offline", "0x2000 → 0x2008", false)
                            AttackModuleItem("Track2 Spoofing", "PAN Manipulation", false)
                            AttackModuleItem("Cryptogram Downgrade", "ARQC → TC", false)
                            AttackModuleItem("CVM Bypass", "PIN/Signature Skip", false)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AttackModuleItem(name: String, description: String, enabled: Boolean) {
        var moduleEnabled by remember { mutableStateOf(enabled) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (moduleEnabled) Color(0xFF00FF41).copy(alpha = 0.1f) 
                else Color(0xFF30363D).copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (moduleEnabled) Color(0xFF00FF41) else Color(0xFF7D8590)
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color(0xFF7D8590),
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                Switch(
                    checked = moduleEnabled,
                    onCheckedChange = { moduleEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00FF41),
                        checkedTrackColor = Color(0xFF00FF41).copy(alpha = 0.3f),
                        uncheckedThumbColor = Color(0xFF7D8590),
                        uncheckedTrackColor = Color(0xFF30363D)
                    )
                )
            }
        }
    }
}
