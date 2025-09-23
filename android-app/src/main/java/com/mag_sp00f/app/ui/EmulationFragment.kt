package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.models.CardProfile
import com.mag_sp00f.app.hardware.PN532Manager
import com.mag_sp00f.app.emulation.EmvAttackEmulationManager
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import kotlinx.coroutines.launch
import timber.log.Timber

class EmulationFragment : Fragment() {
    
    private lateinit var cardManager: CardProfileManager
    private lateinit var pn532Manager: PN532Manager
    private lateinit var attackManager: EmvAttackEmulationManager
    
    companion object {
        fun newInstance() = EmulationFragment()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cardManager = CardProfileManager()
        pn532Manager = PN532Manager(requireContext())
        attackManager = EmvAttackEmulationManager()
        
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    EmulationScreen(
                        cardManager = cardManager,
                        pn532Manager = pn532Manager,
                        attackManager = attackManager
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmulationScreen(
    cardManager: CardProfileManager,
    pn532Manager: PN532Manager,
    attackManager: EmvAttackEmulationManager
) {
    var cardProfiles by remember { mutableStateOf<List<CardProfile>>(emptyList()) }
    var selectedProfile by remember { mutableStateOf<CardProfile?>(null) }
    var selectedEmulationProfile by remember { mutableStateOf("PPSE_AID_POISONING") }
    var connectionType by remember { mutableStateOf("HCE") }
    var isEmulating by remember { mutableStateOf(false) }
    var emulationStatus by remember { mutableStateOf("OFFLINE") }
    var connectionStatus by remember { mutableStateOf("Disconnected") }
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Load card profiles
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                cardProfiles = cardManager.getAllCardProfiles()
                Timber.d("Loaded ${cardProfiles.size} card profiles for emulation")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load card profiles")
            }
        }
    }
    
    // Update attack manager when profile changes
    LaunchedEffect(selectedProfile) {
        selectedProfile?.let { profile ->
            attackManager.setActiveCardProfile(profile)
            Timber.d("Set active card profile: ${profile.cardholderName ?: profile.applicationLabel ?: profile.detectCardType()}")
        }
    }
    
    // Get available profiles
    val availableProfiles = attackManager.getAvailableProfiles()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "EMV Emulation System",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Advanced EMV attack emulation with consolidated profile system",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Status Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Emulation Status",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = emulationStatus,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (isEmulating) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Connection",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = connectionStatus,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Available Profiles",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${availableProfiles.size}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            // Card Profile Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Card Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    if (cardProfiles.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedProfile?.cardholderName ?: selectedProfile?.applicationLabel ?: selectedProfile?.detectCardType() ?: "Select a card profile...",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Card Profile") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                cardProfiles.forEach { profile ->
                                    DropdownMenuItem(
                                        text = { Text(profile.cardholderName ?: profile.applicationLabel ?: profile.detectCardType()) },
                                        onClick = {
                                            selectedProfile = profile
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No card profiles available. Create a profile in the Card Database first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Emulation Profile Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Emulation Profile Selection",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (availableProfiles.isNotEmpty()) {
                        availableProfiles.forEach { profile ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedEmulationProfile == profile.type,
                                        onClick = { selectedEmulationProfile = profile.type },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedEmulationProfile == profile.type,
                                    onClick = { selectedEmulationProfile = profile.type }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = profile.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = "Requirements: ${profile.dataRequirements.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = if (selectedProfile == null) {
                                "Select a card profile to see available emulation profiles."
                            } else {
                                "No emulation profiles available for this card. Required data may be missing."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Connection Type Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Connection Type",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            onClick = { connectionType = "HCE" },
                            label = { Text("Android HCE") },
                            selected = connectionType == "HCE",
                            leadingIcon = if (connectionType == "HCE") {
                                { Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                        
                        FilterChip(
                            onClick = { connectionType = "PN532_BT" },
                            label = { Text("PN532 Bluetooth") },
                            selected = connectionType == "PN532_BT",
                            leadingIcon = if (connectionType == "PN532_BT") {
                                { Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                        
                        FilterChip(
                            onClick = { connectionType = "PN532_USB" },
                            label = { Text("PN532 USB") },
                            selected = connectionType == "PN532_USB",
                            leadingIcon = if (connectionType == "PN532_USB") {
                                { Icon(Icons.Default.Usb, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }
            
            // Control Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                if (!isEmulating) {
                                    isEmulating = true
                                    emulationStatus = "ACTIVE"
                                    
                                    Timber.d("Starting $selectedEmulationProfile emulation via $connectionType")
                                    
                                    val success = attackManager.executeProfile(selectedEmulationProfile)
                                    
                                    if (success) {
                                        connectionStatus = "Connected ($connectionType)"
                                        Timber.i("Emulation started successfully")
                                    } else {
                                        isEmulating = false
                                        emulationStatus = "FAILED"
                                        Timber.e("Failed to start emulation")
                                    }
                                } else {
                                    isEmulating = false
                                    emulationStatus = "OFFLINE"
                                    connectionStatus = "Disconnected"
                                    Timber.i("Emulation stopped")
                                }
                            } catch (e: Exception) {
                                isEmulating = false
                                emulationStatus = "ERROR"
                                Timber.e(e, "Emulation error")
                            }
                        }
                    },
                    enabled = selectedProfile != null && availableProfiles.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isEmulating) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEmulating) "Stop Emulation" else "Start Emulation")
                }
                
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            attackManager.resetStatistics()
                            emulationStatus = "OFFLINE"
                            connectionStatus = "Disconnected"
                            isEmulating = false
                            Timber.d("Emulation reset")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
            }
            
            // Statistics
            if (attackManager.getExecutionStatistics().isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Execution Statistics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        attackManager.getExecutionStatistics().forEach { (profileType, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = profileType.replace("_", " "),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
