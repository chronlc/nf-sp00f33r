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
import com.mag_sp00f.app.data.EmvCardData
import com.mag_sp00f.app.emulation.EmvAttackEmulationManager
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import timber.log.Timber

/**
 * Professional EMV Attack Emulation Fragment
 * Features: Real-time attack module control, card profile selection,
 * HCE service management, attack statistics, configuration management
 * Based on attack_module_architecture.md and emv_attack_reference.md
 */
@OptIn(ExperimentalMaterial3Api::class)
class EmulationFragment : Fragment() {

    private val attackManager = EmvAttackEmulationManager()
    private val cardProfileManager = CardProfileManager.getInstance()
    
    // Compose state holders for real-time attack control
    private val isHceActiveState = mutableStateOf(false)
    private val selectedCardProfileState = mutableStateOf<EmvCardData?>(null)
    private val activeAttacksState = mutableStateOf(setOf<String>())
    private val attackStatisticsState = mutableStateOf(mapOf<String, Map<String, Any>>())
    private val availableCardsState = mutableStateOf(listOf<EmvCardData>())
    private val showCardSelectorState = mutableStateOf(false)
    private val showAttackConfigState = mutableStateOf(false)
    private val selectedAttackForConfigState = mutableStateOf<String?>(null)

    companion object {
        private const val TAG = "EmulationFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Load available card profiles
        loadAvailableCards()
        
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    EmulationScreen()
                }
            }
        }
    }

    @Composable
    private fun EmulationScreen() {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        // Real-time state from attack management
        var isHceActive by isHceActiveState
        var selectedCardProfile by selectedCardProfileState
        var activeAttacks by activeAttacksState
        var attackStatistics by attackStatisticsState
        var availableCards by availableCardsState
        var showCardSelector by showCardSelectorState
        var showAttackConfig by showAttackConfigState
        var selectedAttackForConfig by selectedAttackForConfigState

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
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = Color(0xFF00BB22),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "EMV Attack Emulation",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00BB22)
                            )
                            if (isHceActive) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "ACTIVE",
                                    color = Color(0xFF00FF00),
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
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // HCE Control
                        Button(
                            onClick = {
                                isHceActive = !isHceActive
                                if (isHceActive) {
                                    startHceEmulation()
                                } else {
                                    stopHceEmulation()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isHceActive) Color(0xFFFF4444) else Color(0xFF00BB22),
                                contentColor = if (isHceActive) Color.White else Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = if (isHceActive) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHceActive) "STOP HCE" else "START HCE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Statistics Button
                        Button(
                            onClick = { 
                                updateAttackStatistics()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0066CC),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("STATS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                    // Card Profile Selection
                    CardProfileSelector(
                        selectedCard = selectedCardProfile,
                        availableCards = availableCards,
                        onCardSelected = { card ->
                            selectedCardProfile = card
                            updateAvailableAttacks(card)
                        },
                        onShowSelector = { showCardSelector = true }
                    )

                    // Attack Modules Control Panel
                    AttackModulesPanel(
                        selectedCard = selectedCardProfile,
                        activeAttacks = activeAttacks,
                        onAttackToggle = { attackId ->
                            val newActiveAttacks = activeAttacks.toMutableSet()
                            if (attackId in activeAttacks) {
                                newActiveAttacks.remove(attackId)
                            } else {
                                newActiveAttacks.add(attackId)
                            }
                            activeAttacks = newActiveAttacks
                            configureActiveAttacks(newActiveAttacks)
                        },
                        onAttackConfigure = { attackId ->
                            selectedAttackForConfig = attackId
                            showAttackConfig = true
                        }
                    )

                    // Real-time Attack Statistics
                    if (attackStatistics.isNotEmpty()) {
                        AttackStatisticsPanel(attackStatistics)
                    }

                    // HCE Service Status
                    HceServiceStatusPanel(isHceActive, selectedCardProfile, activeAttacks.size)
                }
            }
        }

        // Card Selector Dialog
        if (showCardSelector) {
            CardSelectorDialog(
                cards = availableCards,
                onCardSelected = { card ->
                    selectedCardProfile = card
                    showCardSelector = false
                    updateAvailableAttacks(card)
                },
                onDismiss = { showCardSelector = false }
            )
        }

        // Attack Configuration Dialog
        if (showAttackConfig && selectedAttackForConfig != null) {
            AttackConfigurationDialog(
                attackId = selectedAttackForConfig!!,
                onConfigSaved = { config ->
                    applyAttackConfiguration(selectedAttackForConfig!!, config)
                    showAttackConfig = false
                    selectedAttackForConfig = null
                },
                onDismiss = {
                    showAttackConfig = false
                    selectedAttackForConfig = null
                }
            )
        }
    }

    @Composable
    private fun CardProfileSelector(
        selectedCard: EmvCardData?,
        availableCards: List<EmvCardData>,
        onCardSelected: (EmvCardData) -> Unit,
        onShowSelector: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Target Card Profile",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (selectedCard != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PAN: ${selectedCard.getUnmaskedPan()}",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00AAFF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Brand: ${getCardBrand(selectedCard.pan)}",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF888888),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "EMV Tags: ${selectedCard.emvTags.size}",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF888888),
                                fontSize = 12.sp
                            )
                        }
                        
                        Button(
                            onClick = onShowSelector,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF333333),
                                contentColor = Color(0xFF00AAFF)
                            )
                        ) {
                            Text("CHANGE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onShowSelector,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BB22),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Filled.CreditCard, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SELECT CARD PROFILE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    @Composable
    private fun AttackModulesPanel(
        selectedCard: EmvCardData?,
        activeAttacks: Set<String>,
        onAttackToggle: (String) -> Unit,
        onAttackConfigure: (String) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Attack Modules",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (selectedCard != null) {
                    val availableAttacks = attackManager.getAvailableAttacks(selectedCard)
                    
                    availableAttacks.forEach { attackId ->
                        AttackModuleItem(
                            attackId = attackId,
                            isActive = attackId in activeAttacks,
                            attackInfo = attackManager.getAttackInfo(attackId),
                            onToggle = { onAttackToggle(attackId) },
                            onConfigure = { onAttackConfigure(attackId) }
                        )
                    }
                    
                    if (availableAttacks.isEmpty()) {
                        Text(
                            text = "No attacks available for selected card",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF888888),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Text(
                        text = "Select a card profile to view available attacks",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF888888),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    private fun AttackModuleItem(
        attackId: String,
        isActive: Boolean,
        attackInfo: Map<String, String>,
        onToggle: () -> Unit,
        onConfigure: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isActive) Color(0xFF00BB22).copy(alpha = 0.1f) 
                else Color(0xFF30363D).copy(alpha = 0.5f)
            )
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
                        text = attackInfo["name"] ?: attackId,
                        fontFamily = FontFamily.Monospace,
                        color = if (isActive) Color(0xFF00BB22) else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = attackInfo["description"] ?: "EMV attack module",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF888888),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Risk: ${attackInfo["risk_level"] ?: "UNKNOWN"}",
                        fontFamily = FontFamily.Monospace,
                        color = when (attackInfo["risk_level"]) {
                            "HIGH" -> Color(0xFFFF4444)
                            "MEDIUM" -> Color(0xFFFFAA00)
                            "LOW" -> Color(0xFF00BB22)
                            else -> Color(0xFF888888)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onConfigure,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Configure",
                            tint = Color(0xFF00AAFF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Switch(
                        checked = isActive,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00BB22),
                            checkedTrackColor = Color(0xFF00BB22).copy(alpha = 0.5f),
                            uncheckedThumbColor = Color(0xFF666666),
                            uncheckedTrackColor = Color(0xFF333333)
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun AttackStatisticsPanel(statistics: Map<String, Map<String, Any>>) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Attack Statistics",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BB22),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                statistics.forEach { (attackId, stats) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = attackId.replace("_", " ").uppercase(),
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00AAFF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(140.dp)
                        )
                        Text(
                            text = "Count: ${stats["attack_count"] ?: 0}",
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Rate: ${stats["success_rate"] ?: 0}%",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00FF88),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HceServiceStatusPanel(isActive: Boolean, selectedCard: EmvCardData?, activeAttacksCount: Int) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "HCE Service Status",
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
                    Column {
                        Text(
                            text = "Service: ${if (isActive) "ACTIVE" else "INACTIVE"}",
                            fontFamily = FontFamily.Monospace,
                            color = if (isActive) Color(0xFF00FF00) else Color(0xFF888888),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Card: ${if (selectedCard != null) "LOADED" else "NONE"}",
                            fontFamily = FontFamily.Monospace,
                            color = if (selectedCard != null) Color(0xFF00AAFF) else Color(0xFF888888),
                            fontSize = 12.sp
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Attacks: $activeAttacksCount",
                            fontFamily = FontFamily.Monospace,
                            color = if (activeAttacksCount > 0) Color(0xFFFFAA00) else Color(0xFF888888),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isActive && selectedCard != null && activeAttacksCount > 0) {
                            Text(
                                text = "EMULATION READY",
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00FF00),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog composables and helper functions would continue here...
    // Implementing card selector dialog, attack configuration dialog, etc.

    @Composable
    private fun CardSelectorDialog(
        cards: List<EmvCardData>,
        onCardSelected: (EmvCardData) -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "Select Card Profile",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    cards.forEach { card ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onCardSelected(card) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "PAN: ${card.getUnmaskedPan()}",
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF00AAFF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Brand: ${getCardBrand(card.pan)}",
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF888888),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    @Composable
    private fun AttackConfigurationDialog(
        attackId: String,
        onConfigSaved: (Map<String, Any>) -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "Configure $attackId",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Attack configuration options will be implemented based on specific module requirements.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Apply default configuration for now
                        onConfigSaved(emptyMap())
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    // Helper functions
    private fun loadAvailableCards() {
        val cards = cardProfileManager.getAllCardProfiles()
        availableCardsState.value = cards.map { cardProfile ->
            // Extract EmvCardData from CardProfile
            cardProfile.emvCardData
        }
    }

    private fun updateAvailableAttacks(card: EmvCardData?) {
        if (card != null) {
            val attacks = attackManager.getAvailableAttacks(card)
            Timber.d("$TAG Available attacks for card: $attacks")
        }
    }

    private fun configureActiveAttacks(attacks: Set<String>) {
        Timber.d("$TAG Configuring active attacks: $attacks")
        // Configuration logic will be implemented
    }

    private fun updateAttackStatistics() {
        // Update statistics from attack manager
        Timber.d("$TAG Updating attack statistics")
    }

    private fun startHceEmulation() {
        Timber.d("$TAG Starting HCE emulation service")
        // Start HCE service with current configuration
    }

    private fun stopHceEmulation() {
        Timber.d("$TAG Stopping HCE emulation service")
        // Stop HCE service
    }

    private fun applyAttackConfiguration(attackId: String, config: Map<String, Any>) {
        Timber.d("$TAG Applying configuration for $attackId: $config")
        // Apply configuration to specific attack module
    }

    private fun getCardBrand(pan: String?): String {
        return when {
            pan?.startsWith("4") == true -> "VISA"
            pan?.startsWith("5") == true -> "MASTERCARD"
            pan?.startsWith("3") == true -> "AMEX"
            pan?.startsWith("6") == true -> "DISCOVER"
            else -> "UNKNOWN"
        }
    }
}
