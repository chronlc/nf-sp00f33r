package com.mag_sp00f.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mag_sp00f.app.data.EmvCardData
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorDialog(
    card: EmvCardData,
    onDismiss: () -> Unit,
    onSave: (EmvCardData) -> Unit
) {
    var pan by remember { mutableStateOf(card.pan ?: "") }
    var expiryDate by remember { mutableStateOf(card.expiryDate ?: "") }
    var cardholderName by remember { mutableStateOf(card.cardholderName ?: "") }
    var track2Data by remember { mutableStateOf(card.track2Data ?: "") }
    var applicationInterchangeProfile by remember { mutableStateOf(card.applicationInterchangeProfile ?: "") }
    var applicationFileLocator by remember { mutableStateOf(card.applicationFileLocator ?: "") }

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
                    text = "💳 Edit EMV Card",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // PAN Field
                OutlinedTextField(
                    value = pan,
                    onValueChange = { pan = it },
                    label = { Text("Card Number (PAN)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("4154904674973556") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Expiry Date
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("2902") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cardholder Name
                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it },
                    label = { Text("Cardholder Name") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("CARDHOLDER/VISA") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Track2 Data
                OutlinedTextField(
                    value = track2Data,
                    onValueChange = { track2Data = it },
                    label = { Text("Track2 Data") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("4154904674973556D29022010000820083001F") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // AIP
                OutlinedTextField(
                    value = applicationInterchangeProfile,
                    onValueChange = { applicationInterchangeProfile = it },
                    label = { Text("Application Interchange Profile (AIP)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("2000") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // AFL
                OutlinedTextField(
                    value = applicationFileLocator,
                    onValueChange = { applicationFileLocator = it },
                    label = { Text("Application File Locator (AFL)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("10010301") }
                )

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
                            val updatedCard = card.copy(
                                pan = pan,
                                expiryDate = expiryDate,
                                cardholderName = cardholderName,
                                track2Data = track2Data,
                                applicationInterchangeProfile = applicationInterchangeProfile,
                                applicationFileLocator = applicationFileLocator,
                                readingTimestamp = System.currentTimeMillis()
                            )
                            onSave(updatedCard)
                            Timber.d("💾 Card saved: ${pan.take(6)}...${pan.takeLast(4)}")
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
