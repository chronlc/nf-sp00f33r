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
    
    // State holders for real-time updates
    private val isReadingState = mutableStateOf(false)
    private val statusMessageState = mutableStateOf("Ready to read EMV cards - Press READ CARD to start")
    private val apduLogState = mutableStateOf<List<ApduLogEntry>>(emptyList())
    private val currentCardDataState = mutableStateOf<EmvCardData?>(null)
    private val progressState = mutableStateOf(0f)
    private val emvTagsState = mutableStateOf<Map<String, String>>(emptyMap())
    private val selectedWorkflowState = mutableStateOf(EmvWorkflow.STANDARD_CONTACTLESS)
    private val showWorkflowSelectorState = mutableStateOf(false)
    private val continuousReadModeState = mutableStateOf(false)
    private val stealthModeState = mutableStateOf(false)
    private val autoStopAfterReadState = mutableStateOf(true)
    
    // Enhanced control states for proper NFC management
    private val nfcDetectionEnabledState = mutableStateOf(false)
    private val cardsReadInSessionState = mutableStateOf(0)
    private val workflowCompleteState = mutableStateOf(false)

    companion object {
        private const val TAG = "CardReadingFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize NFC card reader now that we're attached to activity
        nfcCardReader = NfcCardReaderWithWorkflows(requireActivity(), this)
        
        Timber.d("$TAG CardReadingFragment initialized - reading state: ${isReadingState.value}")
        
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    CardReadingScreen()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        Timber.d("$TAG CardReadingFragment onResume - NFC detection DISABLED until READ CARD pressed")
        // CRITICAL: DO NOT enable NFC detection on resume - only when user explicitly presses READ CARD
        nfcDetectionEnabledState.value = false
    }
    
    override fun onPause() {
        super.onPause()
        Timber.d("$TAG CardReadingFragment onPause - stopping all NFC operations")
        if (::nfcCardReader.isInitialized) {
            nfcCardReader.stopReading()  // Always stop NFC on pause
            isReadingState.value = false
            nfcDetectionEnabledState.value = false
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
        var nfcDetectionEnabled by nfcDetectionEnabledState
        var cardsReadInSession by cardsReadInSessionState
        var workflowComplete by workflowCompleteState

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
                    // Options Checkboxes
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "READ OPTIONS",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00BB22),
                                fontSize = 14.sp
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
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
                                        "Continuous Read",
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
                                        "Stealth Read",
                                        color = Color(0xFFFF6600),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                
                                // Read All AID Checkbox
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { 
                                        autoStopAfterRead = !autoStopAfterRead 
                                    }
                                ) {
                                    Checkbox(
                                        checked = !autoStopAfterRead,
                                        onCheckedChange = { autoStopAfterRead = !it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF00AAFF),
                                            uncheckedColor = Color(0xFF666666)
                                        )
                                    )
                                    Text(
                                        "Read All AID",
                                        color = Color(0xFF00AAFF),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                    
                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!isReading) {
                                    if (::nfcCardReader.isInitialized) {
                                        // ENABLE NFC DETECTION - user explicitly requested card reading
                                        nfcDetectionEnabled = true
                                        workflowComplete = false
                                        cardsReadInSession = 0
                                        
                                        nfcCardReader.setWorkflow(selectedWorkflow)
                                        nfcCardReader.startReading()
                                        
                                        statusMessage = if (continuousReadMode) {
                                            "🔄 CONTINUOUS MODE: Place multiple cards to read with ${selectedWorkflow.name}"
                                        } else {
                                            "📱 Place card near device to read with ${selectedWorkflow.name}"
                                        }
                                        
                                        Timber.d("$TAG NFC Detection ENABLED - User pressed READ CARD")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isReading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (nfcDetectionEnabled) Color(0xFF00BB22) else Color(0xFF555555),
                                contentColor = Color.Black,
                                disabledContainerColor = Color(0xFF333333)
                            )
                        ) {
                            Icon(
                                imageVector = if (nfcDetectionEnabled) Icons.Filled.PlayArrow else Icons.Filled.TouchApp,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = if (nfcDetectionEnabled) "SCANNING..." else "READ CARD(S)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        
                        Button(
                            onClick = {
                                if (isReading || nfcDetectionEnabled) {
                                    if (::nfcCardReader.isInitialized) {
                                        nfcCardReader.stopReading()
                                        // DISABLE NFC DETECTION - user manually stopped
                                        nfcDetectionEnabled = false
                                        workflowComplete = true
                                        statusMessage = "🛑 NFC Detection STOPPED - $cardsReadInSession cards read"
                                        Timber.d("$TAG NFC Detection DISABLED - User pressed STOP")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = isReading || nfcDetectionEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF4444),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF333333)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "STOP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    // TTQ Workflow Dropdown - moved above Current Workflow card
                    TTQWorkflowDropdown(
                        selectedWorkflow = selectedWorkflow,
                        onWorkflowSelected = { workflow ->
                            selectedWorkflow = workflow
                            statusMessage = "TTQ Workflow set to: ${workflow.name}"
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

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

                    // Extended EMV Tag Analysis
                    if (emvTags.isNotEmpty()) {
                        EmvTagAnalysis(emvTags)
                    }

                    // EMV Data Breakdown (if card data available)
                    currentCardData?.let { cardData ->
                        EmvDataBreakdown(cardData)
                    }

                    // Workflow-Specific Results Card
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
                                    text = "TTQ: ${workflow.ttqValue} | Terminal: ${workflow.terminalCapabilities}",
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
                                Column(
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "Expected Data:",
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF666666),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    workflow.expectedDataPoints.forEach { dataPoint ->
                                        Text(
                                            text = "• $dataPoint",
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF00BB22),
                                            fontSize = 8.sp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
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
    private fun StatusCard(message: String, progress: Float, isReading: Boolean) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "TTQ ANALYSIS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = message,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 12.sp
                )
                
                if (isReading && progress > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF00BB22)
                    )
                }
            }
        }
    }

    // Complete implementations for APDU log and EMV parsing display
    @Composable 
    private fun VirtualEmvCard(cardData: EmvCardData) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "VIRTUAL EMV CARD (UNMASKED)",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Card brand and PAN
                Text(
                    "${cardData.detectCardType().name} • ${cardData.getUnmaskedPan()}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Cardholder and expiry
                cardData.cardholderName?.let { name ->
                    Text(
                        "Cardholder: $name",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFAA00),
                        fontSize = 12.sp
                    )
                }
                
                cardData.expiryDate?.let { expiry ->
                    Text(
                        "Expires: $expiry",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFAA00),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
    
    @Composable 
    private fun TtqAnalysisCard(ttq: String, workflow: EmvWorkflow) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "TTQ ANALYSIS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Current TTQ: $ttq",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 12.sp
                )
                
                Text(
                    "Workflow: ${workflow.name}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFFAA00),
                    fontSize = 12.sp
                )
            }
        }
    }
    
    @Composable 
    private fun LiveApduLogCard(log: List<ApduLogEntry>, stealth: Boolean) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "LIVE APDU TRANSACTION LOG (${log.size} commands)",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Show all APDU commands with full details
                log.takeLast(20).forEach { apdu ->
                    Column {
                        // Command sent (TX)
                        Text(
                            "TX: ${apdu.command}",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF4CAF50),
                            fontSize = 10.sp
                        )
                        
                        // Response received (RX)
                        Text(
                            "RX: ${apdu.response} (SW: ${apdu.statusWord})",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF2196F3),
                            fontSize = 10.sp
                        )
                        
                        // Description/parsing
                        if (apdu.description.isNotEmpty()) {
                            Text(
                                "   ${apdu.description}",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFFFAA00),
                                fontSize = 9.sp
                            )
                        }
                        
                        // Execution time
                        Text(
                            "   Execution: ${apdu.executionTimeMs}ms",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF666666),
                            fontSize = 8.sp
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                
                if (log.size > 20) {
                    Text(
                        "... showing last 20 of ${log.size} commands",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF666666),
                        fontSize = 8.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
    
    @Composable 
    private fun EmvTagAnalysis(tags: Map<String, String>) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "EMV TAG ANALYSIS (${tags.size} tags)",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Show key EMV tags with descriptions
                tags.forEach { (tag, value) ->
                    val description = getEmvTagDescription(tag)
                    Column {
                        Text(
                            "$tag: $value",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00AAFF),
                            fontSize = 10.sp
                        )
                        if (description.isNotEmpty()) {
                            Text(
                                "   $description",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFFFAA00),
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
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
                    "EMV DATA BREAKDOWN",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Core EMV data
                Text(
                    "PAN: ${cardData.getUnmaskedPan()}",
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00AAFF),
                    fontSize = 12.sp
                )
                
                cardData.track2Data?.let { track2 ->
                    Text(
                        "Track2: $track2",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00AAFF),
                        fontSize = 10.sp
                    )
                }
                
                cardData.applicationInterchangeProfile?.let { aip ->
                    Text(
                        "AIP: $aip",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFAA00),
                        fontSize = 10.sp
                    )
                }
                
                cardData.applicationFileLocator?.let { afl ->
                    Text(
                        "AFL: $afl",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFAA00),
                        fontSize = 10.sp
                    )
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
                    "WORKFLOW RESULTS: ${workflow.name}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Check if expected data points were found
                workflow.expectedDataPoints.forEach { expectedData ->
                    val found = when {
                        expectedData.contains("PAN") && !cardData.pan.isNullOrEmpty() -> true
                        expectedData.contains("Track2") && !cardData.track2Data.isNullOrEmpty() -> true
                        expectedData.contains("AIP") && !cardData.applicationInterchangeProfile.isNullOrEmpty() -> true
                        expectedData.contains("AFL") && !cardData.applicationFileLocator.isNullOrEmpty() -> true
                        expectedData.contains("Cardholder") && !cardData.cardholderName.isNullOrEmpty() -> true
                        else -> false
                    }
                    
                    Text(
                        "${if (found) "✓" else "✗"} $expectedData",
                        fontFamily = FontFamily.Monospace,
                        color = if (found) Color(0xFF4CAF50) else Color(0xFFFF5722),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
    
    // Helper function for EMV tag descriptions
    private fun getEmvTagDescription(tag: String): String {
        return when (tag.uppercase()) {
            "9F66" -> "TTQ (Terminal Transaction Qualifiers)"
            "9F33" -> "Terminal Capabilities"
            "9F34" -> "CVM Results"
            "9F26" -> "Application Cryptogram"
            "9F36" -> "Application Transaction Counter"
            "9F37" -> "Unpredictable Number"
            "95" -> "Terminal Verification Results"
            "9B" -> "Transaction Status Information"
            "9F02" -> "Amount Authorized"
            "9F03" -> "Amount Other"
            "9F1A" -> "Terminal Country Code"
            "9F35" -> "Terminal Type"
            "5F2A" -> "Transaction Currency Code"
            "9A" -> "Transaction Date"
            "9F21" -> "Transaction Time"
            "82" -> "Application Interchange Profile"
            "94" -> "Application File Locator"
            "5A" -> "Application Primary Account Number"
            "57" -> "Track 2 Equivalent Data"
            "5F20" -> "Cardholder Name"
            "5F24" -> "Application Expiration Date"
            "50" -> "Application Label"
            "9F12" -> "Application Preferred Name"
            else -> ""
        }
    }

    // CardReadingCallback implementation
    override fun onReadingStarted() {
        isReadingState.value = true
        nfcDetectionEnabledState.value = true
        apduLogState.value = emptyList()
        progressState.value = 0.1f
        
        val isContinuousMode = continuousReadModeState.value
        val isStealthMode = stealthModeState.value
        
        if (!isStealthMode) {
            statusMessageState.value = if (isContinuousMode) {
                "🔄 CONTINUOUS ${selectedWorkflowState.value.name} scan ACTIVE - place cards in range"
            } else {
                "📱 ${selectedWorkflowState.value.name} scan ACTIVE - place card near device"
            }
        }
        
        Timber.d("$TAG NFC Reading started with workflow: ${selectedWorkflowState.value.name}, Continuous: $isContinuousMode, Stealth: $isStealthMode")
    }

    override fun onReadingStopped() {
        isReadingState.value = false
        nfcDetectionEnabledState.value = false
        progressState.value = 0f
        
        val cardsCount = cardsReadInSessionState.value
        statusMessageState.value = if (cardsCount > 0) {
            "🛑 Reading session COMPLETE - $cardsCount cards processed"
        } else {
            "🛑 NFC Reading stopped - ready for next session"
        }
        
        Timber.d("$TAG NFC Reading stopped - session complete with $cardsCount cards")
    }

    override fun onProgress(step: String, progress: Int, total: Int) {
        statusMessageState.value = "Analyzing: $step"
        progressState.value = if (total > 0) progress.toFloat() / total.toFloat() else 0.1f
        Timber.d("Progress: $step ($progress/$total)")
    }

    override fun onCardDetected() {
        // CRITICAL: Only process card detection if NFC detection is explicitly enabled
        if (!nfcDetectionEnabledState.value) {
            Timber.d("$TAG 📱 Card detected but NFC detection DISABLED - ignoring")
            return
        }
        
        val isStealthMode = stealthModeState.value
        val isContinuousMode = continuousReadModeState.value
        
        if (!isStealthMode) {
            // Normal mode: Notify user of card detection
            statusMessageState.value = "📱 Card detected - starting ${selectedWorkflowState.value.name} workflow"
            Timber.d("$TAG 📱 Card detected - NFC detection enabled, starting workflow")
        } else {
            // Stealth mode: Silent detection
            Timber.d("$TAG 🥷 Card detected - STEALTH MODE, silent processing")
        }
        
        // Clear previous card data for new read
        currentCardDataState.value = null
        apduLogState.value = emptyList()
        progressState.value = 0.1f
    }

    override fun onApduExchanged(apduEntry: ApduLogEntry) {
        val currentLog = apduLogState.value.toMutableList()
        currentLog.add(apduEntry)
        apduLogState.value = currentLog
        Timber.d("$TAG APDU exchanged: ${apduEntry.description}")
    }

    override fun onCardRead(cardData: EmvCardData) {
        try {
            val cardUid = cardData.cardUid ?: "Unknown_UID_${System.currentTimeMillis()}"
            val cardPan = cardData.getUnmaskedPan()
            
            Timber.d("$TAG Card data received - UID: $cardUid, PAN: $cardPan, Track2: ${cardData.track2Data}")
            
            currentCardDataState.value = cardData
            emvTagsState.value = cardData.emvTags
            progressState.value = 1.0f
            
            // Increment cards read counter
            cardsReadInSessionState.value = cardsReadInSessionState.value + 1
            val cardsCount = cardsReadInSessionState.value
            
            // Save to database with UID-based identification (until PAN sorting is implemented)
            cardProfileManager.saveCard(cardData)
            
            val isStealthMode = stealthModeState.value
            val isContinuousMode = continuousReadModeState.value
            
            // Display appropriate status message
            val panStatus = if (cardPan.isBlank()) "UID: $cardUid" else "PAN: $cardPan"
            val cardInfo = "${cardData.detectCardType().name} - $panStatus"
            
            if (isContinuousMode) {
                if (!isStealthMode) {
                    statusMessageState.value = "🔄 Cards read: $cardsCount | Latest: $cardInfo | Waiting for next card..."
                }
                Timber.d("$TAG Continuous mode: Card $cardsCount saved - $cardInfo")
                
                // In continuous mode, keep scanning for more cards
                // Reset progress for next card but keep NFC active
                progressState.value = 0.2f
                
            } else {
                // Single card mode - complete workflow and stop NFC
                statusMessageState.value = "✅ Workflow COMPLETE: $cardInfo saved to database"
                
                if (::nfcCardReader.isInitialized) {
                    nfcCardReader.stopReading()
                    nfcDetectionEnabledState.value = false
                    workflowCompleteState.value = true
                    Timber.d("$TAG Single card workflow complete - NFC detection DISABLED")
                }
                
                // Final status update
                statusMessageState.value = "✅ Transaction COMPLETE: $cardInfo | NFC stopped"
            }
            
            Timber.d("$TAG Card processing complete - Final result: $cardInfo, Session total: $cardsCount")
            
        } catch (e: Exception) {
            Timber.e(e, "$TAG Error processing card data")
            statusMessageState.value = "❌ Error processing card: ${e.message}"
            progressState.value = 0f
            
            // On error, stop NFC detection
            if (::nfcCardReader.isInitialized) {
                nfcCardReader.stopReading()
                nfcDetectionEnabledState.value = false
            }
        }
    }

    override fun onError(error: String) {
        statusMessageState.value = "Error: $error"
        progressState.value = 0f
        Timber.e("Card reading error: $error")
    }
}
