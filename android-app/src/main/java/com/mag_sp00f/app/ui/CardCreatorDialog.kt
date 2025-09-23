package com.mag_sp00f.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mag_sp00f.app.data.EmvCardData
import com.mag_sp00f.app.models.CardProfile

/**
 * NEWRULE.MD COMPLIANT: Real data only card creator - no sample data generation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCreatorDialog(
    onDismiss: () -> Unit,
    onCardCreated: (CardProfile) -> Unit
) {
    var pan by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    var track2Data by remember { mutableStateOf("") }
    var aip by remember { mutableStateOf("") }
    var afl by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Create Card Profile",
                        color = Color(0xFF00FF00),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF888888)
                        )
                    }
                }

                // Warning about real data
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A00))
                ) {
                    Text(
                        "⚠️ PRODUCTION MODE: Enter only real EMV card data from actual NFC reads. No simulation data allowed per newrule.md compliance.",
                        color = Color(0xFFFFAA00),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // PAN Input
                OutlinedTextField(
                    value = pan,
                    onValueChange = { if (it.length <= 19) pan = it.filter { char -> char.isDigit() } },
                    label = { Text("Primary Account Number (PAN)", color = Color(0xFF888888)) },
                    placeholder = { Text("Enter 16-19 digit PAN from real card") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF00),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Expiry Date Input
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { if (it.length <= 4) expiryDate = it.filter { char -> char.isDigit() } },
                    label = { Text("Expiry Date", color = Color(0xFF888888)) },
                    placeholder = { Text("MMYY from real card") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF00),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Cardholder Name Input
                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it.uppercase() },
                    label = { Text("Cardholder Name", color = Color(0xFF888888)) },
                    placeholder = { Text("Name from real card") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF00),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Track2 Data Input
                OutlinedTextField(
                    value = track2Data,
                    onValueChange = { track2Data = it.uppercase() },
                    label = { Text("Track2 Data (Optional)", color = Color(0xFF888888)) },
                    placeholder = { Text("Real Track2 from NFC read") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF00),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // AIP Input
                OutlinedTextField(
                    value = aip,
                    onValueChange = { aip = it.uppercase() },
                    label = { Text("Application Interchange Profile (Optional)", color = Color(0xFF888888)) },
                    placeholder = { Text("AIP hex from real card") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF00),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // AFL Input
                OutlinedTextField(
                    value = afl,
                    onValueChange = { afl = it.uppercase() },
                    label = { Text("Application File Locator (Optional)", color = Color(0xFF888888)) },
                    placeholder = { Text("AFL hex from real card") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00FF00),
                        unfocusedBorderColor = Color(0xFF444444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF888888)
                        )
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (pan.isNotEmpty() && pan.length >= 16) {
                                val emvCardData = EmvCardData(
                                    pan = pan,
                                    cardholderName = cardholderName.ifEmpty { null },
                                    expiryDate = expiryDate.ifEmpty { null },
                                    track2Data = track2Data.ifEmpty { null },
                                    applicationInterchangeProfile = aip.ifEmpty { null },
                                    applicationFileLocator = afl.ifEmpty { null },
                                    applicationLabel = "Manual Entry",
                                    emvTags = mutableMapOf(),
                                    availableAids = if (track2Data.isNotEmpty()) listOf("A0000000031010") else emptyList(),
                                    apduLog = mutableListOf()
                                )
                                
                                val cardProfile = CardProfile(emvCardData = emvCardData)
                                onCardCreated(cardProfile)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = pan.isNotEmpty() && pan.length >= 16,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF00),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("💾 Create Profile")
                    }
                }
                
                // Footer note
                Text(
                    "Note: Only real EMV data from actual card reads should be used. This tool is for legitimate security research only.",
                    color = Color(0xFF666666),
                    fontSize = 10.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
