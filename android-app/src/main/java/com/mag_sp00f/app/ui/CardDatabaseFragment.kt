package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import timber.log.Timber

class CardDatabaseFragment : Fragment() {
    
    private lateinit var cardManager: CardProfileManager
    
    companion object {
        fun newInstance() = CardDatabaseFragment()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cardManager = CardProfileManager.getInstance()
        
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    CardDatabaseScreen(cardManager = cardManager)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDatabaseScreen(cardManager: CardProfileManager) {
    var cardProfiles by remember { mutableStateOf<List<CardProfile>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showImportExportDialog by remember { mutableStateOf(false) }
    
    // Load cards on startup
    LaunchedEffect(Unit) {
        cardProfiles = cardManager.getAllCardProfiles()
        cardManager.addListener {
            cardProfiles = cardManager.getAllCardProfiles()
        }
    }
    
    // Filter cards
    val filteredCards = remember(cardProfiles, searchQuery) {
        if (searchQuery.isBlank()) {
            cardProfiles
        } else {
            cardProfiles.filter { profile ->
                val pan = profile.emvCardData.pan ?: ""
                val cardholderName = profile.emvCardData.cardholderName ?: ""
                val applicationLabel = profile.emvCardData.applicationLabel
                
                pan.contains(searchQuery, ignoreCase = true) ||
                cardholderName.contains(searchQuery, ignoreCase = true) ||
                applicationLabel.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Compact Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Card Database",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "EMV Profile Management",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Compact Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompactStatsCard("Cards", cardProfiles.size.toString())
                    CompactStatsCard("Filtered", filteredCards.size.toString())
                    CompactStatsCard("Ready", cardProfiles.count { it.isEmulationReady() }.toString())
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Compact Search
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
            IconButton(onClick = { showImportExportDialog = true }) {
                Icon(Icons.Default.ImportExport, contentDescription = "Import/Export")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Cards List
        if (filteredCards.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isBlank()) "No cards in database" else "No cards match search",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn {
                items(filteredCards) { profile ->
                    EnhancedCardProfileItem(profile = profile)
                }
            }
        }
    }
    
    // Dialog for adding new cards
    if (showAddDialog) {
        CardCreatorDialog(
            onDismiss = { showAddDialog = false },
            onCardCreated = { cardProfile ->
                cardManager.saveCardProfile(cardProfile)
                cardProfiles = cardManager.getAllCardProfiles()
                showAddDialog = false
            }
        )
    }
    
    // Dialog for import/export functionality
    if (showImportExportDialog) {
        ImportExportDialog(
            cardManager = cardManager,
            onDismiss = { showImportExportDialog = false },
            onDataChanged = {
                cardProfiles = cardManager.getAllCardProfiles()
            }
        )
    }
}

@Composable
fun CompactStatsCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(45.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun EnhancedCardProfileItem(profile: CardProfile) {
    var expandedApduLog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${profile.emvCardData.detectCardType().name}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${profile.emvCardData.getUnmaskedPan()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                
                Row {
                    IconButton(onClick = { expandedApduLog = !expandedApduLog }) {
                        Icon(
                            if (expandedApduLog) Icons.Default.ExpandLess else Icons.Default.ExpandMore, 
                            contentDescription = "Toggle APDU Log",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Enhanced Details
            Column {
                val cardholderName = profile.emvCardData.cardholderName
                if (cardholderName != null && cardholderName.isNotEmpty()) {
                    Text(
                        text = "Cardholder: $cardholderName",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                val expiryDate = profile.emvCardData.expiryDate
                if (expiryDate != null && expiryDate.isNotEmpty()) {
                    Text(
                        text = "Expires: $expiryDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // EMV Technical Details
                profile.emvCardData.applicationLabel.let { label ->
                    if (label.isNotEmpty()) {
                        Text(
                            text = "App: $label",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                profile.emvCardData.applicationInterchangeProfile?.let { aip ->
                    Text(
                        text = "AIP: $aip",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                profile.emvCardData.track2Data?.let { track2 ->
                    Text(
                        text = "Track2: ${track2.take(20)}${if (track2.length > 20) "..." else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = "${profile.apduLogs.size} APDU Commands | ${profile.getAttackCompatibility().size} Attack Vectors",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Created: ${profile.createdTimestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            // Expandable APDU Log Section
            if (expandedApduLog && profile.apduLogs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "APDU Transaction Log (${profile.apduLogs.size} commands)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Show recent APDU commands (limit to 10 for UI performance)
                        profile.apduLogs.take(10).forEach { apdu ->
                            Text(
                                text = "→ ${apdu.command.take(40)}${if (apdu.command.length > 40) "..." else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "← ${apdu.response.take(40)}${if (apdu.response.length > 40) "..." else ""} (${apdu.statusWord})",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF2196F3)
                            )
                            if (apdu.description.isNotEmpty()) {
                                Text(
                                    text = "  ${apdu.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                        
                        if (profile.apduLogs.size > 10) {
                            Text(
                                text = "... and ${profile.apduLogs.size - 10} more commands",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImportExportDialog(
    cardManager: CardProfileManager,
    onDismiss: () -> Unit,
    onDataChanged: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Import/Export Cards",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column {
                Text(
                    "Card Database Management",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Total Cards: ${cardManager.getAllCardProfiles().size}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        // Export functionality
                        val exportData = cardManager.exportToJson()
                        Timber.d("Exported ${cardManager.getAllCardProfiles().size} cards")
                        onDismiss()
                    }
                ) {
                    Text("EXPORT")
                }
                TextButton(
                    onClick = {
                        // Clear all cards functionality
                        cardManager.clearAllProfiles()
                        onDataChanged()
                        onDismiss()
                    }
                ) {
                    Text("CLEAR ALL")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
