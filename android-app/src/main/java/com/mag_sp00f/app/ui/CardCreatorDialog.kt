package com.mag_sp00f.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mag_sp00f.app.data.EmvCardData
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCreatorDialog(
    onDismiss: () -> Unit,
    onCreate: (EmvCardData) -> Unit
) {
    var pan by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    var track2Data by remember { mutableStateOf("") }
    var serviceCode by remember { mutableStateOf("201") }
    var discretionaryData by remember { mutableStateOf("") }
    var applicationInterchangeProfile by remember { mutableStateOf("2000") }
    var applicationFileLocator by remember { mutableStateOf("10010301") }
    var cardType by remember { mutableStateOf("VISA") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "🆕 Create New EMV Card",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Card Type Selector
                Text(
                    text = "Card Type",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("VISA", "MasterCard", "AMEX", "Discover").forEach { type ->
                        FilterChip(
                            onClick = { cardType = type },
                            label = { Text(type) },
                            selected = cardType == type
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PAN Field
                OutlinedTextField(
                    value = pan,
                    onValueChange = { 
                        pan = it
                        // Auto-generate Track2 data when PAN changes
                        if (it.length >= 16 && expiryDate.isNotEmpty()) {
                            track2Data = "${it}D${expiryDate}${serviceCode}${discretionaryData}"
                        }
                    },
                    label = { Text("Card Number (PAN) *") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter 16-digit PAN") },
                    supportingText = { Text("16-19 digits") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Expiry Date
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { 
                        expiryDate = it
                        // Auto-generate Track2 data when expiry changes
                        if (pan.length >= 16 && it.isNotEmpty()) {
                            track2Data = "${pan}D${it}${serviceCode}${discretionaryData}"
                        }
                    },
                    label = { Text("Expiry Date (YYMM) *") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("MMYY format") },
                    supportingText = { Text("Format: YYMM") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cardholder Name
                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it },
                    label = { Text("Cardholder Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter cardholder name") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Auto-Generated Track2
                OutlinedTextField(
                    value = track2Data,
                    onValueChange = { track2Data = it },
                    label = { Text("Track2 Data (Auto-Generated)") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Auto-generated from PAN + Expiry") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Generate Sample Data Button
                Button(
                    onClick = {
                        when (cardType) {
                            "VISA" -> {
                                pan = "4154904674973556"
                                expiryDate = "2902"
                                cardholderName = "CARDHOLDER/VISA"
                                serviceCode = "201"
                                discretionaryData = "0000820083001F"
                                applicationInterchangeProfile = "2000"
                                applicationFileLocator = "10010301"
                            }
                            "MasterCard" -> {
                                pan = "5555555555554444"
                                expiryDate = "2902"
                                cardholderName = "CARDHOLDER/MC"
                                serviceCode = "201"
                                discretionaryData = "000082008300"
                                applicationInterchangeProfile = "2000"
                                applicationFileLocator = "10010301"
                            }
                            "AMEX" -> {
                                pan = "378282246310005"
                                expiryDate = "2902"
                                cardholderName = "CARDHOLDER/AMEX"
                                serviceCode = "201"      
                                discretionaryData = "000082008300"
                                applicationInterchangeProfile = "2000"
                                applicationFileLocator = "10010301"
                            }
                            "Discover" -> {
                                pan = "6011111111111117"
                                expiryDate = "2902"
                                cardholderName = "CARDHOLDER/DISC"
                                serviceCode = "201"
                                discretionaryData = "000082008300"
                                applicationInterchangeProfile = "2000"
                                applicationFileLocator = "10010301"
                            }
                        }
                        track2Data = "${pan}D${expiryDate}${serviceCode}${discretionaryData}"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎲 Generate Sample Data")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (pan.isNotEmpty() && expiryDate.isNotEmpty() && cardholderName.isNotEmpty()) {
                                // Create EmvCardData with nullable structure
                                val newCard = EmvCardData(
                                    pan = pan,
                                    expiryDate = expiryDate,
                                    cardholderName = cardholderName,
                                    track2Data = track2Data,
                                    serviceCode = serviceCode,
                                    discretionaryData = discretionaryData,
                                    applicationInterchangeProfile = applicationInterchangeProfile,
                                    applicationFileLocator = applicationFileLocator,
                                    applicationTransactionCounter = "1",
                                    readingTimestamp = System.currentTimeMillis()
                                )
                                onCreate(newCard)
                                Timber.d("🆕 New card created: ${pan.take(6)}...${pan.takeLast(4)}")
                            }
                        },
                        enabled = pan.isNotEmpty() && expiryDate.isNotEmpty() && cardholderName.isNotEmpty()
                    ) {
                        Text("Create Card")
                    }
                }
            }
        }
    }
}
