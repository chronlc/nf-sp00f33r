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
import com.mag_sp00f.app.data.ApduLogEntry
import com.mag_sp00f.app.data.EmvCardData
import com.mag_sp00f.app.cardreading.CardReadingCallback
import com.mag_sp00f.app.cardreading.NfcCardReader
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import timber.log.Timber

class CardReadingFragment : Fragment(), CardReadingCallback {

    private val cardProfileManager = CardProfileManager()
    private lateinit var nfcCardReader: NfcCardReader
    // Compose-visible state holders so callbacks can update UI from any thread
    private val isReadingState = mutableStateOf(false)
    private val statusMessageState = mutableStateOf("Ready to read card")
    private val apduLogState = mutableStateOf(listOf<ApduLogEntry>())
    private val currentCardDataState = mutableStateOf<EmvCardData?>(null)

    companion object {
        private const val TAG = "CardReadingFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        nfcCardReader = NfcCardReader(requireActivity(), this)

        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    CardReadingScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CardReadingScreen() {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Use fragment-level state holders so CardReadingCallback can update UI
    var isReading by isReadingState
    var statusMessage by statusMessageState
    var apduLog by apduLogState
    var currentCardData by currentCardDataState

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nfc,
                                contentDescription = "NFC Icon",
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
                    // Status Card
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
                                text = "NFC Card Reader Status",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = statusMessage,
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Main Control Panel
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "🎛️ Card Reader Controls",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41)
                            )
                            
                            // Primary Control Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        statusMessageState.value = "🔍 Starting NFC scan..."
                                        nfcCardReader.startReading()
                                    },
                                    enabled = !isReading,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00FF41),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("START", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        nfcCardReader.stopReading()
                                        statusMessageState.value = "NFC reading stopped"
                                    },
                                    enabled = isReading,
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
                            
                            // Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // Clear APDU log
                                        apduLogState.value = emptyList()
                                        statusMessageState.value = "APDU log cleared"
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear Log", fontSize = 12.sp)
                                }
                                
                                OutlinedButton(
                                    onClick = {
                                        // Export APDU log
                                        currentCardData?.let { data ->
                                            // Export functionality would go here
                                            statusMessageState.value = "📤 Export ready: ${data.apduLog.size} APDUs"
                                        }
                                    },
                                    enabled = apduLog.isNotEmpty(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export", fontSize = 12.sp)
                                }
                                
                                OutlinedButton(
                                    onClick = {
                                        // Save card profile
                                        currentCardData?.let { data ->
                                            cardProfileManager.saveCard(data)
                                            statusMessageState.value = "💾 Card saved to database"
                                        }
                                    },
                                    enabled = currentCardData != null,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    
                    // Settings & Configuration Panel
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "⚙️ Reader Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41)
                            )
                            
                            // Hardware Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Hardware:",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        onClick = { },
                                        label = { Text("NFC", fontSize = 12.sp) },
                                        selected = true,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF00FF41),
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                    FilterChip(
                                        onClick = { },
                                        label = { Text("PN532", fontSize = 12.sp) },
                                        selected = false
                                    )
                                }
                            }
                            
                            // Auto-save Setting
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Auto-save cards:",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Switch(
                                    checked = true,
                                    onCheckedChange = { },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF00FF41),
                                        checkedTrackColor = Color(0xFF00FF41).copy(alpha = 0.5f)
                                    )
                                )
                            }
                            
                            // Connection Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "NFC Status:",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                Color(0xFF00FF41),
                                                shape = androidx.compose.foundation.shape.CircleShape
                                            )
                                    )
                                    Text(
                                        text = "Ready",
                                        fontSize = 12.sp,
                                        color = Color(0xFF00FF41)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Statistics Panel
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
                                text = "📊 Session Statistics",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatCard("Commands", apduLog.size.toString())
                                StatCard("Cards Read", if (currentCardData != null) "1" else "0")
                                StatCard("Success Rate", if (apduLog.isNotEmpty()) "100%" else "0%")
                                StatCard("Errors", "0")
                            }
                        }
                    }

                    // Current Card Data - Comprehensive EMV Display
                    currentCardData?.let { cardData ->
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
                                    text = "💳 EMV Card Data Extracted",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF41),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Core Card Data
                                CardDataField("PAN (5A)", cardData.pan ?: "N/A")
                                CardDataField("Track2 (57)", cardData.track2Data ?: "N/A") 
                                CardDataField("Cardholder (5F20)", cardData.cardholderName ?: "N/A")
                                CardDataField("Expiry (5F24)", cardData.expiryDate ?: "N/A")
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Application Data
                                CardDataField("AID", cardData.applicationIdentifier ?: "N/A")
                                CardDataField("App Label (50)", cardData.applicationLabel)
                                CardDataField("AIP (82)", cardData.applicationInterchangeProfile ?: "N/A")
                                CardDataField("AFL (94)", cardData.applicationFileLocator ?: "N/A")
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Cryptographic Data
                                if (cardData.applicationCryptogram != null) {
                                    CardDataField("Cryptogram (9F26)", cardData.applicationCryptogram!!)
                                }
                                if (cardData.cryptogramInformationData != null) {
                                    CardDataField("CID (9F27)", cardData.cryptogramInformationData!!)
                                }
                                if (cardData.applicationTransactionCounter != null) {
                                    CardDataField("ATC (9F36)", cardData.applicationTransactionCounter!!)
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // EMV Tags Summary
                                Text(
                                    text = "📊 EMV Tags: ${cardData.emvTags.size} parsed",
                                    fontSize = 12.sp,
                                    color = Color(0xFF58A6FF),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "🎯 Available AIDs: ${cardData.availableAids.size}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF58A6FF)
                                )
                                Text(
                                    text = "🔍 APDU Commands: ${cardData.apduLog.size}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF58A6FF)
                                )
                            }
                        }
                    }

                    // APDU Log - Comprehensive TX/RX Display
                    if (apduLog.isNotEmpty()) {
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
                                    text = "📡 Live APDU Transaction Log",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF41),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                
                                Text(
                                    text = "Total Commands: ${apduLog.size} | Showing latest ${minOf(10, apduLog.size)}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7D8590),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                apduLog.takeLast(10).forEach { entry ->
                                    ComprehensiveApduLogItem(entry)
                                    if (entry != apduLog.last()) {
                                        Divider(
                                            color = Color(0xFF30363D),
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CardDataField(label: String, value: String) {
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
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                modifier = Modifier.weight(0.7f)
            )
        }
    }

    @Composable
    private fun ComprehensiveApduLogItem(entry: ApduLogEntry) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            // Command Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.description.split(" | ").firstOrNull() ?: "APDU Command",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${entry.executionTimeMs}ms",
                        fontSize = 11.sp,
                        color = Color(0xFF7D8590)
                    )
                    // Status indicator
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (entry.statusWord == "9000") Color(0xFF00FF41) else Color(0xFFFF4444),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // TX (Transmit) Section
            Text(
                text = "📤 TX (${entry.command.length / 2} bytes):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF58A6FF),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            
            Text(
                text = formatHexString(entry.command),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFE6EDF3),
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            // RX (Receive) Section
            Text(
                text = "📥 RX (${entry.response.length / 2} bytes):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF58A6FF),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            
            Text(
                text = formatHexString(entry.response),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFE6EDF3),
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            // Status Word
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${entry.statusWord}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (entry.statusWord == "9000") Color(0xFF00FF41) else Color(0xFFFF4444)
                )
                
                Text(
                    text = getStatusMeaning(entry.statusWord),
                    fontSize = 10.sp,
                    color = Color(0xFF7D8590)
                )
            }
        }
    }
    
    private fun formatHexString(hex: String): String {
        // Format hex string with spaces every 2 characters and line breaks every 32 characters
        return hex.chunked(2).joinToString(" ").let { formatted ->
            formatted.chunked(48).joinToString("\n") // 16 bytes per line (48 chars with spaces)
        }
    }
    
    private fun getStatusMeaning(statusWord: String): String {
        return when (statusWord) {
            "9000" -> "Success"
            "6200" -> "Warning"
            "6300" -> "Authentication failed"
            "6700" -> "Wrong length"
            "6900" -> "Command not allowed"
            "6A82" -> "File not found"
            "6A84" -> "Not enough memory"
            "6D00" -> "Instruction not supported"
            "6E00" -> "Class not supported"
            "ERROR" -> "Communication error"
            else -> "See logs"
        }
    }
    
    @Composable
    private fun StatCard(label: String, value: String) {
        Card(
            modifier = Modifier
                .width(80.dp)
                .height(70.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41)
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color(0xFF7D8590),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Interface implementation with proper thread safety
    override fun onReadingStarted() {
        Timber.tag(TAG).d("🔥 NFC reading started")
        requireActivity().runOnUiThread {
            isReadingState.value = true
            statusMessageState.value = "🔍 NFC reader active - Present card to antenna"
        }
    }

    override fun onReadingStopped() {
        Timber.tag(TAG).d("⚡ NFC reading stopped")
        requireActivity().runOnUiThread {
            isReadingState.value = false
            statusMessageState.value = "NFC reader stopped"
        }
    }

    override fun onCardRead(cardData: EmvCardData) {
        Timber.tag(TAG).d("💳 Card read complete: PAN=${cardData.pan}")
        
        // Save to database
        cardProfileManager.saveCard(cardData)
        
        // Update UI state on main thread
        requireActivity().runOnUiThread {
            currentCardDataState.value = cardData
            apduLogState.value = cardData.apduLog
            statusMessageState.value = "✅ Card read: ${cardData.getMaskedPan()} | ${cardData.apduLog.size} APDUs"
            Timber.tag(TAG).d("📊 UI updated with ${cardData.emvTags.size} EMV tags, ${cardData.apduLog.size} APDU commands")
        }
    }

    override fun onError(error: String) {
        Timber.tag(TAG).e("❌ Card read error: $error")
        requireActivity().runOnUiThread {
            statusMessageState.value = "❌ Error: $error"
            isReadingState.value = false
        }
    }
    
    override fun onApduExchanged(apduEntry: ApduLogEntry) {
        Timber.tag(TAG).d("📡 APDU exchanged: ${apduEntry.description}")
        requireActivity().runOnUiThread {
            // Add new APDU entry to log in real-time
            val currentLog = apduLogState.value.toMutableList()
            currentLog.add(apduEntry)
            apduLogState.value = currentLog
        }
    }
    
    override fun onCardDetected() {
        Timber.tag(TAG).d("💳 Card detected")
        requireActivity().runOnUiThread {
            statusMessageState.value = "💳 Card detected - Reading..."
        }
    }
    
    override fun onProgress(step: String, progress: Int, total: Int) {
        Timber.tag(TAG).d("📊 Progress: $step ($progress/$total)")
        requireActivity().runOnUiThread {
            statusMessageState.value = "📊 $step ($progress/$total)"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nfcCardReader.stopReading()
    }
}
