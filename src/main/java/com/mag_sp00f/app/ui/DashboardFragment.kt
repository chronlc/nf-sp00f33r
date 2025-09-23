package com.mag_sp00f.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.mag_sp00f.app.R
import com.mag_sp00f.app.cardreading.CardProfileManager
import com.mag_sp00f.app.data.EmvCardData
import com.mag_sp00f.app.ui.theme.MagSp00fTheme
import timber.log.Timber

/**
 * PRODUCTION-GRADE Dashboard Fragment
 * Fixed: Fragment navigation, no more UI overlays
 */
class DashboardFragment : Fragment() {
    
    private val cardManager = CardProfileManager.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("📊 DashboardFragment onCreateView")
        
        return ComposeView(requireContext()).apply {
            setContent {
                MagSp00fTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF0A0A0A)
                    ) {
                        DashboardContent()
                    }
                }
            }
        }
    }
    
    @Composable
    private fun DashboardContent() {
        val recentCards = remember { cardManager.getRecentCards(5) }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Background Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nfspoof3),
                        contentDescription = "NF-Spoof Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                    )
                    
                    // Welcome Text
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "nf-sp00f",
                            color = Color(0xFF006600),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Professional NFC EMV Analysis",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Statistics Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        title = "Total Cards",
                        value = "${cardManager.getAllCardProfiles().size}",
                        icon = Icons.Default.CreditCard,
                        modifier = Modifier.weight(1f)
                    )
                    ActionCard(
                        title = "Active Sessions",
                        value = "0",
                        icon = Icons.Default.Wifi,
                        modifier = Modifier.weight(1f)
                    )
                    ActionCard(
                        title = "Success Rate",
                        value = "100%",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Cards Section
            item {
                Text(
                    "Recent Cards",
                    color = Color(0xFF00FF00),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                if (recentCards.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.CreditCard,
                                    contentDescription = "No Cards",
                                    tint = Color(0xFF666666),
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    "No cards yet",
                                    color = Color(0xFF666666),
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Use the Read tab to scan your first card",
                                    color = Color(0xFF888888),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentCards) { cardData ->
                            RecentCardItem(cardData = cardData)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ActionCard(
        title: String,
        value: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null
    ) {
        Card(
            modifier = modifier
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color(0xFF00FF00),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    value,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    title,
                    color = Color(0xFF888888),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    private fun RecentCardItem(cardData: EmvCardData) {
        Card(
            modifier = Modifier
                .width(280.dp)
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            cardData.cardholderName ?: "Unknown",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            cardData.getMaskedPan(),
                            color = Color(0xFF00FF00),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = "Card",
                        tint = Color(0xFF00FF00),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "${cardData.apduLog.size} APDUs",
                        color = Color(0xFF888888),
                        fontSize = 10.sp
                    )
                    Text(
                        cardData.applicationLabel,
                        color = Color(0xFF888888),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
