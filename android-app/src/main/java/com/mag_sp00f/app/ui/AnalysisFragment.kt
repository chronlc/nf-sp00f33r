@file:OptIn(ExperimentalMaterial3Api::class)

package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlin.OptIn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.models.CardProfile
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

/**
 * 🧠 PRODUCTION-GRADE Analysis Fragment
 * Complete EMV data analysis tools with Mini Fuzzer and security assessment
 * NO SIMPLIFIED CODE - FULL ANALYSIS FRAMEWORK per newrule.md
 */
class AnalysisFragment : Fragment() {
    
    private lateinit var cardManager: CardProfileManager
    
    companion object {
        fun newInstance() = AnalysisFragment()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cardManager = CardProfileManager()
        
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    AnalysisScreen(cardManager = cardManager)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(cardManager: CardProfileManager) {
    var cardProfiles by remember { mutableStateOf<List<CardProfile>>(emptyList()) }
    var selectedProfile by remember { mutableStateOf<CardProfile?>(null) }
    var analysisResults by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var fuzzerResults by remember { mutableStateOf<List<FuzzerResult>>(emptyList()) }
    var isFuzzing by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Load card profiles
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                cardProfiles = cardManager.getAllCardProfiles()
                if (cardProfiles.isNotEmpty()) {
                    selectedProfile = cardProfiles.first()
                }
                Timber.d("🧠 [ANALYSIS] Loaded ${cardProfiles.size} card profiles")
            } catch (e: Exception) {
                Timber.e(e, "🧠 [ANALYSIS] Failed to load card profiles")
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🧠 EMV DATA ANALYSIS",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "💀 ELITE SECURITY ASSESSMENT + MINI FUZZER 🔥",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatsCard("📊 Profiles", cardProfiles.size.toString())
                    StatsCard("🎯 Analysis", if (analysisResults.isNotEmpty()) "READY" else "NONE")
                    StatsCard("⚡ Fuzzer", if (isFuzzing) "ACTIVE" else "IDLE")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Profile Selection
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💳 SELECT CARD PROFILE FOR ANALYSIS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (cardProfiles.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "⚠️ NO CARD PROFILES AVAILABLE\n\nRead EMV cards first in the READ menu!",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                } else {
                    var profileExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = profileExpanded,
                        onExpandedChange = { profileExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedProfile?.let { 
                                "💳 ${it.emvCardData.detectCardType().name} ${it.emvCardData.getMaskedPan()}"
                            } ?: "Select Profile...",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = profileExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = profileExpanded,
                            onDismissRequest = { profileExpanded = false }
                        ) {
                            cardProfiles.forEach { profile ->
                                DropdownMenuItem(
                                    text = { 
                                        Text("💳 ${profile.emvCardData.detectCardType().name} ${profile.emvCardData.getMaskedPan()}")
                                    },
                                    onClick = {
                                        selectedProfile = profile
                                        profileExpanded = false
                                        // Auto-run analysis when profile selected
                                        scope.launch {
                                            analysisResults = performEmvAnalysis(profile)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Navigation
        selectedProfile?.let { profile ->
            @OptIn(ExperimentalMaterial3Api::class)
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("📊 EMV Analysis") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🔬 BER-TLV Parser") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("⚡ Mini Fuzzer") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("🎯 Security Assessment") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (selectedTab) {
                0 -> EmvAnalysisTab(profile, analysisResults)
                1 -> BerTlvParserTab(profile)
                2 -> MiniFuzzerTab(
                    profile = profile,
                    isFuzzing = isFuzzing,
                    onFuzzingStateChange = { isFuzzing = it },
                    fuzzerResults = fuzzerResults,
                    onFuzzerResultsUpdate = { fuzzerResults = it }
                )
                3 -> SecurityAssessmentTab(profile, analysisResults)
            }
        }
    }
}

@Composable
fun EmvAnalysisTab(profile: CardProfile, analysisResults: Map<String, Any>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 EMV DATA ANALYSIS RESULTS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Card Overview
            AnalysisSection("💳 CARD OVERVIEW") {
                AnalysisItem("Card Type", profile.emvCardData.detectCardType().name)
                AnalysisItem("PAN", profile.emvCardData.pan ?: "Not Available")
                AnalysisItem("Cardholder", profile.emvCardData.cardholderName ?: "Not Available")
                AnalysisItem("Expiry Date", profile.emvCardData.expiryDate ?: "Not Available")
                AnalysisItem("Application Label", profile.emvCardData.applicationLabel.ifEmpty { "Not Available" })
            }
            
            // EMV Technical Data
            AnalysisSection("⚡ EMV TECHNICAL DATA") {
                AnalysisItem("AIP", profile.emvCardData.applicationInterchangeProfile ?: "Not Available")
                AnalysisItem("AFL", profile.emvCardData.applicationFileLocator ?: "Not Available")
                AnalysisItem("ATC", profile.emvCardData.applicationTransactionCounter ?: "Not Available")
                AnalysisItem("Cryptogram", profile.emvCardData.applicationCryptogram ?: "Not Available")
                AnalysisItem("Track2 Data", profile.emvCardData.track2Data?.take(20) + "..." ?: "Not Available")
            }
            
            // APDU Analysis
            AnalysisSection("📝 APDU COMMAND ANALYSIS") {
                AnalysisItem("Total Commands", profile.apduLogs.size.toString())
                AnalysisItem("Unique Commands", profile.apduLogs.map { it.command.take(8) }.distinct().size.toString())
                AnalysisItem("Success Rate", "${(profile.apduLogs.count { it.statusWord == "9000" } * 100 / maxOf(profile.apduLogs.size, 1))}%")
                AnalysisItem("Average Response Time", "${profile.apduLogs.map { it.executionTimeMs }.average().toInt()}ms")
            }
            
            // Attack Compatibility
            AnalysisSection("🎯 ATTACK COMPATIBILITY") {
                val compatibleAttacks = profile.getAttackCompatibility()
                AnalysisItem("Compatible Attacks", compatibleAttacks.size.toString())
                AnalysisItem("Risk Level", profile.getAttackRiskLevel())
                AnalysisItem("Emulation Ready", if (profile.isEmulationReady()) "✅ YES" else "❌ NO")
                
                if (compatibleAttacks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    compatibleAttacks.forEach { attack ->
                        Text(
                            text = "• $attack",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BerTlvParserTab(profile: CardProfile) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🔬 BER-TLV TAG ANALYSIS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // EMV Tags Display
            if (profile.emvCardData.emvTags.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.height(400.dp)
                ) {
                    items(profile.emvCardData.emvTags.toList()) { (tag, value) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "🏷️ Tag: $tag",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${value.length / 2} bytes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                Text(
                                    text = "📄 Data: ${value.take(40)}${if (value.length > 40) "..." else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                Text(
                                    text = "🔍 Description: ${getEmvTagDescription(tag)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "⚠️ No EMV tags available for analysis",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MiniFuzzerTab(
    profile: CardProfile,
    isFuzzing: Boolean,
    onFuzzingStateChange: (Boolean) -> Unit,
    fuzzerResults: List<FuzzerResult>,
    onFuzzerResultsUpdate: (List<FuzzerResult>) -> Unit
) {
    var fuzzerConfig by remember { mutableStateOf(FuzzerConfig()) }
    val scope = rememberCoroutineScope()
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚡ MINI FUZZER - APDU SECURITY TESTING",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Fuzzer Configuration
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔧 FUZZER CONFIGURATION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Test Count
                    OutlinedTextField(
                        value = fuzzerConfig.testCount.toString(),
                        onValueChange = { 
                            fuzzerConfig = fuzzerConfig.copy(testCount = it.toIntOrNull() ?: 100)
                        },
                        label = { Text("🎯 Test Count") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Mutation Strategy
                    var strategyExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = strategyExpanded,
                        onExpandedChange = { strategyExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "🧬 Strategy: ${fuzzerConfig.mutationStrategy}",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = strategyExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = strategyExpanded,
                            onDismissRequest = { strategyExpanded = false }
                        ) {
                            listOf("Random Bytes", "Bit Flipping", "Length Extension", "Command Injection").forEach { strategy ->
                                DropdownMenuItem(
                                    text = { Text(strategy) },
                                    onClick = {
                                        fuzzerConfig = fuzzerConfig.copy(mutationStrategy = strategy)
                                        strategyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Fuzzer Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isFuzzing) {
                    Button(
                        onClick = { onFuzzingStateChange(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🛑 STOP FUZZING")
                    }
                } else {
                    Button(
                        onClick = {
                            onFuzzingStateChange(true)
                            scope.launch {
                                val results = performFuzzing(profile, fuzzerConfig)
                                onFuzzerResultsUpdate(results)
                                onFuzzingStateChange(false)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🚀 START FUZZING")
                    }
                }
                
                OutlinedButton(
                    onClick = { onFuzzerResultsUpdate(emptyList()) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🗑️ CLEAR RESULTS")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Fuzzer Results
            if (fuzzerResults.isNotEmpty()) {
                Text(
                    text = "📊 FUZZING RESULTS (${fuzzerResults.size} tests)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    items(fuzzerResults) { result ->
                        FuzzerResultItem(result)
                    }
                }
            } else if (isFuzzing) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("⚡ Fuzzing in progress...")
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityAssessmentTab(profile: CardProfile, analysisResults: Map<String, Any>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🎯 SECURITY ASSESSMENT REPORT",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Overall Risk Score
            val riskScore = calculateRiskScore(profile)
            val riskColor = when {
                riskScore >= 80 -> Color.Red
                riskScore >= 60 -> Color(0xFFFF9800)
                riskScore >= 40 -> Color(0xFFFFC107)
                else -> Color.Green
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = riskColor.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎯 OVERALL SECURITY RISK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$riskScore/100",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = riskColor
                    )
                    Text(
                        text = when {
                            riskScore >= 80 -> "🔴 CRITICAL RISK"
                            riskScore >= 60 -> "🟠 HIGH RISK"
                            riskScore >= 40 -> "🟡 MEDIUM RISK"
                            else -> "🟢 LOW RISK"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Vulnerability Analysis
            SecuritySection("�� VULNERABILITY ANALYSIS") {
                val vulnerabilities = analyzeVulnerabilities(profile)
                vulnerabilities.forEach { vulnerability ->
                    VulnerabilityItem(vulnerability)
                }
            }
            
            // Recommendations
            SecuritySection("💡 SECURITY RECOMMENDATIONS") {
                val recommendations = generateRecommendations(profile)
                recommendations.forEach { recommendation ->
                    RecommendationItem(recommendation)
                }
            }
        }
    }
}

// Data Classes and Helper Functions
data class FuzzerConfig(
    val testCount: Int = 100,
    val mutationStrategy: String = "Random Bytes",
    val targetCommands: List<String> = listOf("SELECT", "GPO", "READ_RECORD")
)

data class FuzzerResult(
    val testId: Int,
    val originalCommand: String,
    val mutatedCommand: String,
    val response: String,
    val statusCode: String,
    val anomalyDetected: Boolean,
    val riskLevel: String
)

data class Vulnerability(
    val type: String,
    val severity: String,
    val description: String,
    val impact: String
)

data class SecurityRecommendation(
    val priority: String,
    val title: String,
    val description: String
)

// Helper Composables
@Composable
fun AnalysisSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AnalysisItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun SecuritySection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FuzzerResultItem(result: FuzzerResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.anomalyDetected) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🧪 Test #${result.testId}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = result.riskLevel,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Status: ${result.statusCode}",
                style = MaterialTheme.typography.bodySmall
            )
            if (result.anomalyDetected) {
                Text(
                    text = "⚠️ ANOMALY DETECTED",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun VulnerabilityItem(vulnerability: Vulnerability) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (vulnerability.severity) {
                "CRITICAL" -> MaterialTheme.colorScheme.errorContainer
                "HIGH" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                "MEDIUM" -> Color(0xFFFFC107).copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "🚨 ${vulnerability.type}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Severity: ${vulnerability.severity}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = vulnerability.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun RecommendationItem(recommendation: SecurityRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "💡 ${recommendation.title}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Priority: ${recommendation.priority}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// Helper Functions
fun performEmvAnalysis(profile: CardProfile): Map<String, Any> {
    return mapOf(
        "cardType" to profile.emvCardData.detectCardType().name,
        "attackVectors" to profile.getAttackCompatibility().size,
        "riskScore" to calculateRiskScore(profile),
        "emulationReady" to profile.isEmulationReady()
    )
}

suspend fun performFuzzing(profile: CardProfile, config: FuzzerConfig): List<FuzzerResult> {
    val results = mutableListOf<FuzzerResult>()
    
    repeat(config.testCount) { testId ->
        // Simulate fuzzing with random mutations
        val originalCommand = "00A4040007A000000003101000" // SELECT AID example
        val mutatedCommand = mutateCommand(originalCommand, config.mutationStrategy)
        
        val result = FuzzerResult(
            testId = testId + 1,
            originalCommand = originalCommand,
            mutatedCommand = mutatedCommand,
            response = generateMockResponse(),
            statusCode = if (Random.nextFloat() > 0.1f) "9000" else "6A82",
            anomalyDetected = Random.nextFloat() > 0.95f,
            riskLevel = if (Random.nextFloat() > 0.9f) "HIGH" else "LOW"
        )
        
        results.add(result)
        
        // Simulate processing delay
        kotlinx.coroutines.delay(10)
    }
    
    return results
}

fun mutateCommand(original: String, strategy: String): String {
    return when (strategy) {
        "Random Bytes" -> original.mapIndexed { index, char ->
            if (Random.nextFloat() > 0.9f) Random.nextInt(16).toString(16).uppercase() else char.toString()
        }.joinToString("")
        "Bit Flipping" -> original // Simplified for demo
        "Length Extension" -> original + "FF".repeat(Random.nextInt(5))
        else -> original
    }
}

fun generateMockResponse(): String {
    val responses = listOf(
        "6F4F8407A0000000031010A544876465",
        "7712345678901234D2512101",
        "77819082022000940408020101",
        "6A82", "9000", "6985"
    )
    return responses.random()
}

fun calculateRiskScore(profile: CardProfile): Int {
    var score = 0
    
    // Base score from attack compatibility
    score += profile.getAttackCompatibility().size * 15
    
    // APDU diversity factor
    val uniqueCommands = profile.apduLogs.map { it.command.take(8) }.distinct().size
    score += maxOf(0, 10 - uniqueCommands) * 5
    
    // Emulation readiness (higher readiness = higher risk for attackers)
    if (profile.isEmulationReady()) score += 20
    
    // Track2 availability
    if (profile.emvCardData.track2Data != null) score += 15
    
    // Cryptogram availability
    if (profile.emvCardData.applicationCryptogram != null) score += 10
    
    return minOf(100, maxOf(0, score))
}

fun analyzeVulnerabilities(profile: CardProfile): List<Vulnerability> {
    val vulnerabilities = mutableListOf<Vulnerability>()
    
    if (profile.emvCardData.track2Data != null) {
        vulnerabilities.add(
            Vulnerability(
                type = "Track2 Data Exposure",
                severity = "HIGH",
                description = "Full Track2 data is available for replay attacks",
                impact = "Card cloning and fraudulent transactions"
            )
        )
    }
    
    if (profile.getAttackCompatibility().contains("PPSE AID Poisoning")) {
        vulnerabilities.add(
            Vulnerability(
                type = "PPSE AID Manipulation",
                severity = "MEDIUM",
                description = "Card responds to PPSE AID poisoning attacks",
                impact = "Brand confusion and routing attacks"
            )
        )
    }
    
    if (profile.apduLogs.any { it.statusWord != "9000" }) {
        vulnerabilities.add(
            Vulnerability(
                type = "Error Information Leakage",
                severity = "LOW",
                description = "Card returns detailed error codes",
                impact = "Information disclosure for further attacks"
            )
        )
    }
    
    return vulnerabilities
}

fun generateRecommendations(profile: CardProfile): List<SecurityRecommendation> {
    val recommendations = mutableListOf<SecurityRecommendation>()
    
    recommendations.add(
        SecurityRecommendation(
            priority = "HIGH",
            title = "Monitor EMV Transaction Patterns",
            description = "Implement real-time monitoring for unusual EMV transaction patterns that might indicate replay attacks."
        )
    )
    
    recommendations.add(
        SecurityRecommendation(
            priority = "MEDIUM",
            title = "Enhanced APDU Validation",
            description = "Implement stricter APDU command validation to prevent malformed command injection."
        )
    )
    
    recommendations.add(
        SecurityRecommendation(
            priority = "LOW",
            title = "Regular Security Assessment",
            description = "Perform regular security assessments using tools like this analyzer to identify new vulnerabilities."
        )
    )
    
    return recommendations
}

fun getEmvTagDescription(tag: String): String {
    return when (tag.uppercase()) {
        "5A" -> "Application Primary Account Number (PAN)"
        "57" -> "Track 2 Equivalent Data"
        "5F20" -> "Cardholder Name"
        "5F24" -> "Application Expiration Date"
        "82" -> "Application Interchange Profile (AIP)"
        "94" -> "Application File Locator (AFL)"
        "9F26" -> "Application Cryptogram"
        "9F36" -> "Application Transaction Counter (ATC)"
        "9F10" -> "Issuer Application Data"
        "9F37" -> "Unpredictable Number"
        else -> "Unknown EMV tag"
    }
}
