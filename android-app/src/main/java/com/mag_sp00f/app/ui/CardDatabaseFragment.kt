package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.models.CardProfile
import com.mag_sp00f.app.data.EmvCardData
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Card Database Management Fragment
 * Complete EMV card profile management with search, CRUD operations, and modern UI
 * Full production functionality for professional EMV analysis
 */
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
        cardManager = CardProfileManager()
        
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
    var selectedSortOption by remember { mutableStateOf("Date Created") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<CardProfile?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<CardProfile?>(null) }
    var showImportExportDialog by remember { mutableStateOf(false) }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    
    // Load cards on startup
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                cardProfiles = cardManager.getAllCardProfiles()
                isLoading = false
                Timber.d("Database: Loaded ${cardProfiles.size} card profiles")
            } catch (e: Exception) {
                Timber.e(e, "Database: Failed to load card profiles")
                isLoading = false
            }
        }
    }
    
    // Filter and sort cards
    val filteredAndSortedCards = remember(cardProfiles, searchQuery, selectedSortOption) {
        val filtered = if (searchQuery.isBlank()) {
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
        
        when (selectedSortOption) {
            "Date Created" -> filtered.sortedByDescending { it.createdAt }
            "Card Type" -> filtered.sortedBy { it.emvCardData.detectCardType().name }
            "PAN" -> filtered.sortedBy { it.emvCardData.pan ?: "" }
            "Cardholder" -> filtered.sortedBy { it.emvCardData.cardholderName ?: "" }
            else -> filtered
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    text = "Card Database",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "EMV Profile Management System",
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
                    StatsCard("Total Cards", cardProfiles.size.toString())
                    StatsCard("Filtered", filteredAndSortedCards.size.toString())
                    StatsCard("Ready", cardProfiles.count { it.isEmulationReady() }.toString())
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search and Controls Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search cards...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Sort and Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sort Dropdown
                    var sortExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = sortExpanded,
                        onExpandedChange = { sortExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "Sort: $selectedSortOption",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            listOf("Date Created", "Card Type", "PAN", "Cardholder").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedSortOption = option
                                        sortExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Action Buttons Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Import/Export Button
                        OutlinedButton(
                            onClick = { showImportExportDialog = true }
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Backup")
                        }
                        
                        // Bulk Delete Button (only show if cards exist)
                        if (cardProfiles.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { showBulkDeleteConfirm = true },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                )
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear All")
                            }
                        }
                        
                        // Add Card Button
                        FloatingActionButton(
                            onClick = { showCreateDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Card")
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cards List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading EMV profiles...")
                }
            }
        } else if (filteredAndSortedCards.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (cardProfiles.isEmpty()) "No Cards in Database" else "No Matching Cards",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (cardProfiles.isEmpty()) 
                            "Start by reading your first EMV card!" 
                        else 
                            "Try adjusting your search query",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn {
                items(filteredAndSortedCards) { profile ->
                    CardProfileItem(
                        profile = profile,
                        onEdit = { showEditDialog = it },
                        onDelete = { showDeleteConfirm = it }
                    )
                }
            }
        }
    }
    
    // Dialogs
    if (showCreateDialog) {
        CardCreatorDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { emvCardData ->
                scope.launch {
                    try {
                        val newProfile = CardProfile(emvCardData = emvCardData)
                        cardManager.saveCardProfile(newProfile)
                        cardProfiles = cardManager.getAllCardProfiles()
                        showCreateDialog = false
                        Timber.d("Database: Created new card profile: ${emvCardData.pan}")
                    } catch (e: Exception) {
                        Timber.e(e, "Database: Failed to create card profile")
                    }
                }
            }
        )
    }
    
    // Import/Export Dialog
    if (showImportExportDialog) {
        CardImportExportDialog(
            cardManager = cardManager,
            onDismiss = { showImportExportDialog = false },
            onComplete = { message ->
                scope.launch {
                    cardProfiles = cardManager.getAllCardProfiles()
                    showImportExportDialog = false
                    Timber.d("Database: Import/Export completed - $message")
                }
            }
        )
    }
    
    // Bulk Delete Confirmation
    if (showBulkDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteConfirm = false },
            title = { Text("⚠️ Clear All Card Profiles") },
            text = { 
                Text("This will permanently delete ALL ${cardProfiles.size} card profiles from your database.\n\nThis action cannot be undone. Consider exporting a backup first.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            try {
                                cardProfiles.forEach { profile ->
                                    cardManager.deleteCardProfile(profile.id)
                                }
                                cardProfiles = cardManager.getAllCardProfiles()
                                showBulkDeleteConfirm = false
                                Timber.d("Database: Bulk delete completed")
                            } catch (e: Exception) {
                                Timber.e(e, "Database: Failed to bulk delete profiles")
                            }
                        }
                    }
                ) {
                    Text("DELETE ALL", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteConfirm = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
    
    showEditDialog?.let { profile ->
        CardEditorDialog(
            card = profile.emvCardData,
            onDismiss = { showEditDialog = null },
            onSave = { updatedCard ->
                scope.launch {
                    try {
                        val updatedProfile = profile.copy(emvCardData = updatedCard)
                        cardManager.saveCardProfile(updatedProfile)
                        cardProfiles = cardManager.getAllCardProfiles()
                        showEditDialog = null
                        Timber.d("Database: Updated card profile: ${updatedProfile.id}")
                    } catch (e: Exception) {
                        Timber.e(e, "Database: Failed to update card profile")
                    }
                }
            }
        )
    }
    
    showDeleteConfirm?.let { profile ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Card Profile") },
            text = { 
                Text("Are you sure you want to delete this EMV card profile?\n\n${profile.emvCardData.getMaskedPan()}\n${profile.emvCardData.cardholderName ?: "Unknown Cardholder"}")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            try {
                                cardManager.deleteCardProfile(profile.id)
                                cardProfiles = cardManager.getAllCardProfiles()
                                showDeleteConfirm = null
                                Timber.d("Database: Deleted card profile: ${profile.id}")
                            } catch (e: Exception) {
                                Timber.e(e, "Database: Failed to delete card profile")
                            }
                        }
                    }
                ) {
                    Text("DELETE", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun StatsCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(60.dp),
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
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardProfileItem(
    profile: CardProfile,
    onEdit: (CardProfile) -> Unit,
    onDelete: (CardProfile) -> Unit
) {
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
                        text = "${profile.emvCardData.getMaskedPan()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                
                Row {
                    IconButton(onClick = { onEdit(profile) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { onDelete(profile) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Details Row
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
                
                Text(
                    text = "${profile.apduLogs.size} APDU Commands | ${profile.getAttackCompatibility().size} Test Vectors",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Text(
                    text = "Created: ${profile.createdTimestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status Badge
            val statusColor = if (profile.isEmulationReady()) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            statusColor.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (profile.isEmulationReady()) "READY FOR TESTING" else "NEEDS MORE DATA",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = statusColor
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = profile.getAttackRiskLevel(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
