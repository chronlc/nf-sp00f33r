package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
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
import com.mag_sp00f.app.data.EmvWorkflow
import com.mag_sp00f.app.cardreading.CardReadingCallback
import com.mag_sp00f.app.cardreading.NfcCardReaderWithWorkflows
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import timber.log.Timber
import kotlinx.coroutines.delay

/**
 * Professional Brute Force TTQ Card Reader Fragment
 * Features: TTQ manipulation, multiple EMV workflows, unmasked PAN display, 
 * continuous read mode, stealth mode, comprehensive EMV data extraction
 * Enterprise-grade EMV processing with dynamic PDOL parsing and workflow selection
 */
@OptIn(ExperimentalMaterial3Api::class)
class CardReadingFragment : Fragment(), CardReadingCallback {

    private val cardProfileManager = CardProfileManager.getInstance()
    private lateinit var nfcCardReader: NfcCardReaderWithWorkflows
    
    // Compose state holders for real-time EMV callbacks
    private val isReadingState = mutableStateOf(false)
    private val statusMessageState = mutableStateOf("TTQ Brute Force Ready - Select Workflow")
    private val apduLogState = mutableStateOf(listOf<ApduLogEntry>())
    private val currentCardDataState = mutableStateOf<EmvCardData?>(null)
    private val progressState = mutableStateOf("")
    private val emvTagsState = mutableStateOf(mapOf<String, String>())
    private val selectedWorkflowState = mutableStateOf(EmvWorkflow.STANDARD_CONTACTLESS)
    private val showWorkflowSelectorState = mutableStateOf(false)
    private val continuousReadModeState = mutableStateOf(false)
    private val stealthModeState = mutableStateOf(false)
    private val autoStopAfterReadState = mutableStateOf(true)

    companion object {
        private const val TAG = "CardReadingFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        nfcCardReader = NfcCardReaderWithWorkflows(requireActivity(), this)
        
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

        // Real-time state from EMV callbacks
        var isReading by isReadingState
        var statusMessage by statusMessageState
        var apduLog by apduLogState
        var currentCardData by currentCardDataState
        var progress by progressState
        var emvTags by emvTagsState
        var selectedWorkflow by selectedWorkflowState
        var showWorkflowSelector by showWorkflowSelectorState
        var continuousReadMode by continuousReadModeState
        var stealthMode by stealthModeState
        var autoStopAfterRead by autoStopAfterReadState

        // Professional dark gradient
        val backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0A0A0A), Color(0xFF1A1A1A))
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Brute Force TTQ Card Reader",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00BB22)
                            )
                            if (isReading) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (stealthMode) "STEALTH" else "ACTIVE",
                                    color = if (stealthMode) Color(0xFFFF6600) else Color(0xFF00FF00),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0D0D0D),
                        titleContentColor = Color(0xFF00BB22)
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color(0xFF00FF00)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // TTQ Workflow Dropdown
                        TTQWorkflowDropdown(
                            selectedWorkflow = selectedWorkflow,
                            onWorkflowSelected = { workflow ->
                                selectedWorkflow = workflow
                                statusMessage = "TTQ Workflow set to: ${workflow.name}"
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        
                        // Options Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Continuous Read Mode Checkbox
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { 
                                    continuousReadMode = !continuousReadMode 
                                }
                            ) {
                                Checkbox(
                                    checked = continuousReadMode,
                                    onCheckedChange = { continuousReadMode = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF00BB22),
                                        uncheckedColor = Color(0xFF666666)
                                    )
                                )
                                Text(
                                    "Continuous",
                                    color = Color(0xFF00AAFF),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            // Stealth Mode Checkbox
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { 
                                    stealthMode = !stealthMode 
                                }
                            ) {
                                Checkbox(
                                    checked = stealthMode,
                                    onCheckedChange = { stealthMode = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFFF6600),
                                        uncheckedColor = Color(0xFF666666)
                                    )
                                )
                                Text(
                                    "Stealth",
                                    color = Color(0xFFFF6600),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Read Card Button
                        Button(
                            onClick = {
                                if (isReading) {
                                    nfcCardReader.stopReading()
                                } else {
                                    nfcCardReader.setWorkflow(selectedWorkflow)
                                    nfcCardReader.startReading()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReading) Color(0xFFFF4444) else Color(0xFF00BB22),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = if (isReading) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = if (isReading) "STOP READING" else "READ CARD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundBrush)
                    .padding(innerPadding)
            ) {
                // Background image with transparency
                Image(
                    painter = painterResource(id = R.drawable.nfspoof3),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.05f
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current Workflow Status
                    CurrentWorkflowCard(selectedWorkflow, isReading, continuousReadMode, stealthMode)

                    // Status Section
                    StatusCard(statusMessage, progress, isReading)

                    // Virtual EMV Card Display (UNMASKED)
                    currentCardData?.let { cardData ->
                        VirtualEmvCard(cardData)
                    }

                    // TTQ Analysis (if available)
                    if (emvTags.containsKey("9F66")) {
                        TtqAnalysisCard(emvTags["9F66"] ?: "", selectedWorkflow)
                    }

                    // Live APDU Transaction Log
                    if (apduLog.isNotEmpty()) {
                        LiveApduLogCard(apduLog, stealthMode)
                    }

                    // EMV Tag Analysis
                    if (emvTags.isNotEmpty()) {
                        EmvTagAnalysis(emvTags)
                    }

                    // EMV Data Analysis
                    currentCardData?.let { cardData ->
                        EmvDataBreakdown(cardData)
                    }

                    // Workflow Results Analysis
                    currentCardData?.let { cardData ->
                        WorkflowResultsCard(cardData, selectedWorkflow)
                    }
                }
            }
        }
    }

    @Composable
    private fun TTQWorkflowDropdown(
        selectedWorkflow: EmvWorkflow,
        onWorkflowSelected: (EmvWorkflow) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
        ) {
            OutlinedTextField(
                value = selectedWorkflow.name,
                onValueChange = { },
                readOnly = true,
                label = { 
                    Text(
                        "TTQ Workflow",
                        color = Color(0xFF00BB22),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    ) 
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00BB22),
                    unfocusedBorderColor = Color(0xFF666666),
                    focusedTextColor = Color(0xFF00AAFF),
                    unfocusedTextColor = Color(0xFF00AAFF)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                EmvWorkflow.getAllWorkflows().forEach { workflow ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = workflow.name,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00AAFF),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "TTQ: ${workflow.ttqValue}",
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFFFAA00),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = workflow.description,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF888888),
                                    fontSize = 10.sp
                                )
                            }
                        },
                        onClick = {
                            onWorkflowSelected(workflow)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun CurrentWorkflowCard(workflow: EmvWorkflow, isReading: Boolean, continuousMode: Boolean, stealthMode: Boolean) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "⚡ CURRENT TTQ WORKFLOW",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = workflow.name,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00AAFF),
                            fontSize = 16.sp
                        )
                        Text(
                            text = workflow.description,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF888888),
                            fontSize = 12.sp
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        if (isReading) {
                            Text(
                                text = if (stealthMode) "STEALTH MODE" else "ACTIVE",
                                fontFamily = FontFamily.Monospace,
                                color = if (stealthMode) Color(0xFFFF6600) else Color(0xFF00FF00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (continuousMode) {
                            Text(
                                text = "CONTINUOUS",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00CCCC),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // TTQ Details
                Text(
                    text = "🏷️ TTQ: ${workflow.ttqValue}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFFAA00),
                    fontSize = 12.sp
                )
                
                Text(
                    text = "🛠️ Terminal Caps: ${workflow.terminalCapabilities}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFF6666),
                    fontSize = 12.sp
                )
                
                Text(
                    text = "⚙️ Expected Data: ${workflow.expectedDataPoints.joinToString(", ")}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00CCCC),
                    fontSize = 11.sp
                )
            }
        }
    }

    @Composable
    private fun TtqAnalysisCard(ttqHex: String, workflow: EmvWorkflow) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🔍 TTQ ANALYSIS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Card TTQ: $ttqHex",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFFAA00),
                    fontSize = 12.sp
                )
                
                Text(
                    text = "Workflow TTQ: ${workflow.ttqValue}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val ttqAnalysis = EmvWorkflow.analyzeTtq(ttqHex)
                ttqAnalysis.forEach { (capability, enabled) ->
                    Row(
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Text(
                            text = if (enabled) "✅" else "❌",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.width(20.dp)
                        )
                        Text(
                            text = capability,
                            fontFamily = FontFamily.Monospace,
                            color = if (enabled) Color(0xFF00FF00) else Color(0xFF666666),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun WorkflowResultsCard(cardData: EmvCardData, workflow: EmvWorkflow) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📊 WORKFLOW RESULTS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Expected vs Extracted Data
                val extractedDataPoints = mutableListOf<String>()
                cardData.pan?.let { extractedDataPoints.add("PAN") }
                cardData.track2Data?.let { extractedDataPoints.add("Track2") }
                cardData.applicationInterchangeProfile?.let { extractedDataPoints.add("AIP") }
                cardData.applicationFileLocator?.let { extractedDataPoints.add("AFL") }
                cardData.applicationLabel?.let { extractedDataPoints.add("App Label") }
                cardData.emvTags["8E"]?.let { extractedDataPoints.add("CVM List") }
                cardData.emvTags["9F34"]?.let { extractedDataPoints.add("CVM Results") }
                cardData.emvTags["9F17"]?.let { extractedDataPoints.add("PIN Try Counter") }
                cardData.emvTags["91"]?.let { extractedDataPoints.add("Issuer Auth Data") }
                
                Text(
                    text = "🎯 Expected: ${workflow.expectedDataPoints.joinToString(", ")}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 11.sp
                )
                
                Text(
                    text = "✅ Extracted: ${extractedDataPoints.joinToString(", ")}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00FF88),
                    fontSize = 11.sp
                )
                
                val successRate = if (workflow.expectedDataPoints.contains("User Defined")) {
                    100 // Custom workflow always 100%
                } else {
                    val matchedCount = workflow.expectedDataPoints.count { expected ->
                        extractedDataPoints.any { extracted -> 
                            extracted.contains(expected, ignoreCase = true) 
                        }
                    }
                    if (workflow.expectedDataPoints.isNotEmpty()) {
                        (matchedCount * 100) / workflow.expectedDataPoints.size
                    } else 0
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "💯 Success Rate: $successRate%",
                    fontFamily = FontFamily.Monospace,
                    color = when {
                        successRate >= 80 -> Color(0xFF00FF00)
                        successRate >= 50 -> Color(0xFFFFAA00)
                        else -> Color(0xFFFF4444)
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "📈 Total EMV Tags: ${cardData.emvTags.size}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00CCCC),
                    fontSize = 11.sp
                )
            }
        }
    }

    @Composable
    private fun StatusCard(statusMessage: String, progress: String, isReading: Boolean) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "��‍☠️ TTQ STATUS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00BB22),
                        fontSize = 14.sp
                    )
                    if (isReading) {
                        Text(
                            text = "⚡ READING",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFF0040),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = statusMessage,
                    fontFamily = FontFamily.Monospace,
                    color = if (isReading) Color(0xFFFFFF00) else Color(0xFF00AAFF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (progress.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📈 $progress",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00AAFF),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun VirtualEmvCard(cardData: EmvCardData) {
        val cardBrand = getCardBrand(cardData.pan)
        val cardColor = getEliteCardColor(cardBrand)
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(cardColor, cardColor.copy(alpha = 0.8f), Color.Black.copy(alpha = 0.3f))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Card brand and elite labeling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "🏴‍☠️ $cardBrand",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = cardData.applicationLabel ?: "EMV CARD",
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 💳 FULLY UNMASKED PAN (for security research)
                    Text(
                        text = formatFullyUnmaskedPan(cardData.pan ?: ""),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp,
                        letterSpacing = 3.sp
                    )

                    // Cardholder and expiry with elite styling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = cardData.cardholderName ?: "SECURITY RESEARCHER",
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "🏴‍☠️ TTQ RESEARCH",
                                fontFamily = FontFamily.Monospace,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = formatEliteExpiry(cardData.expiryDate ?: ""),
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun LiveApduLogCard(apduLog: List<ApduLogEntry>, stealthMode: Boolean) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📡 LIVE APDU TX/RX LOG (${apduLog.size} commands)",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00BB22),
                        fontSize = 14.sp
                    )
                    if (stealthMode) {
                        Text(
                            text = "🔇 STEALTH",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFF6600),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                apduLog.takeLast(10).forEach { logEntry ->
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "🏴‍☠️ ${logEntry.description}",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00AAFF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "📤 TX: ${logEntry.command}",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFFAA00),
                            fontSize = 11.sp
                        )
                        
                        val responseColor = when (logEntry.statusWord) {
                            "9000" -> Color(0xFF00FF88)
                            else -> Color(0xFFFF4444)
                        }
                        Text(
                            text = "📥 RX: ${logEntry.response} (${logEntry.statusWord})",
                            fontFamily = FontFamily.Monospace,
                            color = responseColor,
                            fontSize = 11.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun EmvTagAnalysis(emvTags: Map<String, String>) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🏷️ EMV TAG ANALYSIS (${emvTags.size} tags)",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                emvTags.forEach { (tag, value) ->
                    val tagName = getEmvTagName(tag)
                    val tagColor = getEliteTagColor(tag)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = tag,
                            fontFamily = FontFamily.Monospace,
                            color = tagColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(60.dp)
                        )
                        Text(
                            text = tagName,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF888888),
                            fontSize = 10.sp,
                            modifier = Modifier.width(120.dp)
                        )
                        Text(
                            text = value,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun EmvDataBreakdown(cardData: EmvCardData) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📊 EMV DATA BREAKDOWN",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Key EMV fields with elite styling - FULLY UNMASKED
                cardData.pan?.let {
                    EliteDataRow("💳 PAN (5A)", it, Color(0xFFFF6B6B))
                }
                
                cardData.track2Data?.let {
                    EliteDataRow("🛤️ Track2 (57)", it, Color(0xFF4ECDC4))
                }
                
                cardData.applicationInterchangeProfile?.let {
                    EliteDataRow("⚙️ AIP (82)", it, Color(0xFFFFE66D))
                }
                
                cardData.applicationFileLocator?.let {
                    EliteDataRow("📂 AFL (94)", it, Color(0xFF95E1D3))
                }
                
                Text(
                    text = "🔍 Total EMV Tags: ${cardData.emvTags.size}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 12.sp
                )
                
                Text(
                    text = "📡 APDU Commands: ${cardData.apduLog.size}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 12.sp
                )
            }
        }
    }

    @Composable
    private fun EliteDataRow(label: String, value: String, color: Color) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(120.dp)
            )
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Helper functions
    private fun formatFullyUnmaskedPan(pan: String): String {
        return if (pan.length >= 16) {
            "${pan.substring(0, 4)} ${pan.substring(4, 8)} ${pan.substring(8, 12)} ${pan.substring(12, 16)}"
        } else if (pan.length >= 8) {
            "${pan.substring(0, 4)} ${pan.substring(4)}"
        } else {
            pan.ifEmpty { "PAN NOT AVAILABLE" }
        }
    }

    private fun formatEliteExpiry(expiry: String): String {
        return if (expiry.length >= 4) {
            "${expiry.substring(2, 4)}/${expiry.substring(0, 2)}"
        } else {
            expiry.ifEmpty { "MM/YY" }
        }
    }

    private fun getCardBrand(pan: String?): String {
        return when {
            pan?.startsWith("4") == true -> "VISA"
            pan?.startsWith("5") == true -> "MASTERCARD"
            pan?.startsWith("3") == true -> "AMEX"
            pan?.startsWith("6") == true -> "DISCOVER"
            else -> "EMV CARD"
        }
    }

    private fun getEliteCardColor(brand: String): Color {
        return when (brand) {
            "VISA" -> Color(0xFF1A1F71)
            "MASTERCARD" -> Color(0xFFEB001B)
            "AMEX" -> Color(0xFF006FCF)
            "DISCOVER" -> Color(0xFFFF6000)
            else -> Color(0xFF424242)
        }
    }

    private fun getEmvTagName(tag: String): String {
        return when (tag) {
            "4F" -> "AID"
            "50" -> "App Label"
            "57" -> "Track2"
            "5A" -> "PAN"
            "5F20" -> "Cardholder"
            "5F24" -> "Expiry"
            "82" -> "AIP"
            "94" -> "AFL"
            "9F38" -> "PDOL"
            "9F66" -> "TTQ"
            "9F33" -> "Term Caps"
            "8E" -> "CVM List"
            "9F34" -> "CVM Results"
            "9F17" -> "PIN Counter"
            "91" -> "Issuer Auth"
            else -> "EMV Tag"
        }
    }

    private fun getEliteTagColor(tag: String): Color {
        return when (tag) {
            "5A" -> Color(0xFFFF6B6B)
            "57" -> Color(0xFF4ECDC4)
            "82" -> Color(0xFFFFE66D)
            "94" -> Color(0xFF95E1D3)
            "9F66" -> Color(0xFFFF9800)
            "9F33" -> Color(0xFF9C27B0)
            "8E" -> Color(0xFF00BCD4)
            else -> Color(0xFF888888)
        }
    }

    // CardReadingCallback implementations
    override fun onReadingStarted() {
        isReadingState.value = true
        statusMessageState.value = "🏴‍☠️ ${selectedWorkflowState.value.name} TTQ scan active - place card near device"
        apduLogState.value = emptyList()
        currentCardDataState.value = null
        progressState.value = ""
        emvTagsState.value = emptyMap()
        
        if (!stealthModeState.value) {
            Timber.d("$TAG 🚀 TTQ Reading started with workflow: ${selectedWorkflowState.value.name}")
        }
    }

    override fun onReadingStopped() {
        isReadingState.value = false
        statusMessageState.value = "🏴‍☠️ TTQ Brute Force scanner stopped"
        progressState.value = ""
        
        if (!stealthModeState.value) {
            Timber.d("$TAG ⏹️ TTQ Reading stopped")
        }
    }

    override fun onCardDetected() {
        statusMessageState.value = "💳 Card detected! Reading with ${selectedWorkflowState.value.name} TTQ..."
        progressState.value = "Connecting to EMV card..."
        
        if (!stealthModeState.value) {
            Timber.d("$TAG 💳 Card detected for TTQ workflow: ${selectedWorkflowState.value.name}")
        }
    }

    override fun onProgress(step: String, current: Int, total: Int) {
        progressState.value = "Step $current/$total: $step"
        
        if (!stealthModeState.value) {
            Timber.d("$TAG 📊 TTQ Progress: $step ($current/$total)")
        }
    }

    override fun onApduExchanged(apduEntry: ApduLogEntry) {
        val currentLog = apduLogState.value.toMutableList()
        currentLog.add(apduEntry)
        apduLogState.value = currentLog
        
        if (!stealthModeState.value) {
            Timber.d("$TAG 📡 TTQ APDU logged: ${apduEntry.description}")
        }
    }

    override fun onCardRead(cardData: EmvCardData) {
        currentCardDataState.value = cardData
        emvTagsState.value = cardData.emvTags
        statusMessageState.value = "✅ ${selectedWorkflowState.value.name} TTQ read complete! Auto-saving to database..."
        progressState.value = "Processing ${cardData.emvTags.size} EMV tags..."
        
        // Auto-save to database (persistent data)
        cardProfileManager.saveCard(cardData)
        
        statusMessageState.value = "🎯 Card saved to persistent database! ${cardData.emvTags.size} EMV tags analyzed"
        progressState.value = if (continuousReadModeState.value) "Continuous mode - ready for next card" else "Single read complete"
        
        // Auto-stop after read if not in continuous mode
        if (autoStopAfterReadState.value && !continuousReadModeState.value) {
            nfcCardReader.stopReading()
        }
        
        if (!stealthModeState.value) {
            Timber.d("$TAG ✅ TTQ Workflow read complete: PAN=${cardData.pan}, Tags=${cardData.emvTags.size}, Workflow=${selectedWorkflowState.value.name}")
        }
    }

    override fun onError(error: String) {
        statusMessageState.value = "❌ TTQ Workflow Error: $error"
        progressState.value = ""
        
        if (!stealthModeState.value) {
            Timber.e("$TAG ❌ TTQ Workflow error: $error")
        }
    }
}
