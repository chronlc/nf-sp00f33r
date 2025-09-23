package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.cardreading.NfcCardReader
import com.mag_sp00f.app.cardreading.CardReadingCallback
import com.mag_sp00f.app.data.EmvCardData
import com.mag_sp00f.app.data.ApduLogEntry
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import timber.log.Timber

/**
 * Production-grade Card Reading Fragment with LIVE APDU LOG DISPLAY
 * Real-time EMV parsing with unmasked PAN for database organization
 * Per newrule.md: NO SIMPLIFIED CODE - FULL PRODUCTION FUNCTIONALITY
 */
class CardReadingFragment : Fragment(), CardReadingCallback {
    
    private lateinit var cardProfileManager: CardProfileManager
    private lateinit var nfcCardReader: NfcCardReader
    
    // Live state for real-time updates
    private var isReading = mutableStateOf(false)
    private var apduLog = mutableStateOf<List<ApduLogEntry>>(emptyList())
    private var currentCardData = mutableStateOf<EmvCardData?>(null)
    private var statusMessage = mutableStateOf("💀 NFC CARD READER READY\n\nTap START to begin live EMV analysis")
    
    companion object {
        private const val TAG = "🏴‍☠️ CardReadingFragment"
        
        fun newInstance() = CardReadingFragment()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    CardReadingScreen()
                }
            }
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize components
        cardProfileManager = CardProfileManager()
        nfcCardReader = NfcCardReader(requireActivity(), this)
        
        Timber.d("$TAG 🎯 Production-grade card reading fragment initialized")
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CardReadingScreen() {
        val reading by isReading
        val logs by apduLog
        val cardData by currentCardData
        val status by statusMessage
        val scrollState = rememberScrollState()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Control Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🔥 LIVE EMV ANALYSIS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { startReading() },
                            enabled = !reading,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("START")
                        }
                        
                        Button(
                            onClick = { stopReading() },
                            enabled = reading,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("STOP")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Live Status Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📊 STATUS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        status,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (reading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Card Data Display (Unmasked PAN)
            if (cardData != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "�� EXTRACTED EMV DATA (UNMASKED)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            if (cardData!!.pan != null) {
                                Text("🔢 PAN: ${cardData!!.pan}", fontFamily = FontFamily.Monospace)
                            }
                            if (cardData!!.track2Data != null) {
                                Text("💾 Track2: ${cardData!!.track2Data}", fontFamily = FontFamily.Monospace)
                            }
                            if (cardData!!.cardholderName != null) {
                                Text("👤 Cardholder: ${cardData!!.cardholderName}", fontFamily = FontFamily.Monospace)
                            }
                            if (cardData!!.expiryDate != null) {
                                Text("📅 Expiry: ${cardData!!.expiryDate}", fontFamily = FontFamily.Monospace)
                            }
                            if (cardData!!.applicationInterchangeProfile != null) {
                                Text("🎯 AIP: ${cardData!!.applicationInterchangeProfile}", fontFamily = FontFamily.Monospace)
                            }
                            if (cardData!!.applicationFileLocator != null) {
                                Text("📂 AFL: ${cardData!!.applicationFileLocator}", fontFamily = FontFamily.Monospace)
                            }
                            if (cardData!!.availableAids.isNotEmpty()) {
                                Text("🎭 AIDs: ${cardData!!.availableAids.joinToString()}", fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Live APDU Log Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📞 LIVE APDU LOG",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${logs.size} entries",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (logs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Waiting for APDU commands...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            logs.forEach { entry ->
                                ApduLogEntryDisplay(entry)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    fun ApduLogEntryDisplay(entry: ApduLogEntry) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header with timestamp and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "📡 ${entry.description}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "${entry.executionTimeMs}ms",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Command
                Text(
                    "📤 CMD: ${entry.command}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF4CAF50)
                )
                
                // Response
                Text(
                    "📥 RSP: ${entry.response}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF2196F3)
                )
                
                // Status Word with interpretation
                val statusColor = when (entry.statusWord) {
                    "9000" -> Color(0xFF4CAF50)
                    else -> Color(0xFFFF5722)
                }
                
                Text(
                    "🎯 SW: ${entry.statusWord} ${interpretStatusWord(entry.statusWord)}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = statusColor
                )
            }
        }
    }
    
    private fun interpretStatusWord(sw: String): String {
        return when (sw) {
            "9000" -> "✅ SUCCESS"
            "6A82" -> "❌ FILE NOT FOUND"
            "6985" -> "❌ CONDITIONS NOT SATISFIED"
            "6A86" -> "❌ INCORRECT P1 P2"
            "6D00" -> "❌ INSTRUCTION NOT SUPPORTED"
            "6E00" -> "❌ CLASS NOT SUPPORTED"
            else -> "⚠️ UNKNOWN"
        }
    }
    
    // CardReadingCallback implementation
    override fun onReadingStarted() {
        isReading.value = true
        statusMessage.value = "🔥 NFC Reading Active\n\nPlace EMV card near device for live analysis..."
        apduLog.value = emptyList()
        currentCardData.value = null
        Timber.d("$TAG 📡 Reading started")
    }
    
    override fun onReadingStopped() {
        isReading.value = false
        statusMessage.value = "⚡ NFC Reading Stopped\n\nTap START to begin new session"
        Timber.d("$TAG 🛑 Reading stopped")
    }
    
    override fun onCardRead(cardData: EmvCardData) {
        currentCardData.value = cardData
        
        // Update APDU log from card data
        if (cardData.apduLog.isNotEmpty()) {
            apduLog.value = cardData.apduLog
        }
        
        statusMessage.value = buildString {
            append("💀 Card Analysis Complete!\n\n")
            append("🎯 EMV Data Extracted & Saved\n")
            append("📊 APDU Log: ${cardData.apduLog.size} commands\n")
            append("💾 Auto-saved to database")
        }
        
        // Auto-save with unmasked PAN for database organization
        cardProfileManager.saveCard(cardData)
        
        Timber.d("$TAG 💾 Card data saved with unmasked PAN: ${cardData.pan}")
    }
    
    override fun onError(error: String) {
        statusMessage.value = "❌ Error: $error\n\n🔄 Check NFC settings and try again"
        isReading.value = false
        Timber.e("$TAG ❌ Reading error: $error")
    }
    
    fun startReading() {
        nfcCardReader.startReading()
    }
    
    fun stopReading() {
        nfcCardReader.stopReading()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        if (isReading.value) {
            nfcCardReader.stopReading()
        }
    }
}
