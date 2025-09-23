package com.mag_sp00f.app.ui

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mag_sp00f.app.data.EmvCardData
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

/**
 * Professional RecentCardsAdapter - Modern Android 14 Card Display
 * Shows recent EMV card reads with comprehensive data - CORRECTED DATA MODEL
 */
class RecentCardsAdapter(
    private var cards: List<EmvCardData>,
    private val onCardClick: (EmvCardData) -> Unit
) : RecyclerView.Adapter<RecentCardsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        // Create modern card layout programmatically
        val cardLayout = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                if (this is ViewGroup.MarginLayoutParams) {
                    setMargins(0, 8, 0, 8)
                }
            }
            setPadding(20, 16, 20, 16)
            setBackgroundColor(Color.WHITE)
            elevation = 4f
        }
        
        return CardViewHolder(cardLayout)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size
    
    fun updateCards(newCards: List<EmvCardData>) {
        cards = newCards
        notifyDataSetChanged()
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardLayout = itemView as LinearLayout
        
        fun bind(cardData: EmvCardData) {
            // Clear previous content
            cardLayout.removeAllViews()
            
            // Card brand and identifier - CORRECTED PROPERTIES
            val brandText = TextView(itemView.context).apply {
                text = "${cardData.getCardBrandDisplayName()} • ${getCardIdentifier(cardData)}"
                textSize = 18f
                setTextColor(0xFF212529.toInt())
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            cardLayout.addView(brandText)
            
            // PAN (unmasked) - CORRECTED PROPERTY
            val panText = TextView(itemView.context).apply {
                val panValue = if (cardData.pan != null) cardData.pan!! else "PAN Not Available"
                text = "PAN: $panValue"
                textSize = 14f
                setTextColor(0xFF495057.toInt())
                typeface = android.graphics.Typeface.MONOSPACE
                setPadding(0, 4, 0, 0)
            }
            cardLayout.addView(panText)
            
            // Cardholder name - CORRECTED PROPERTY
            if (cardData.cardholderName != null && cardData.cardholderName!!.isNotEmpty()) {
                val nameText = TextView(itemView.context).apply {
                    text = "Cardholder: ${cardData.cardholderName!!}"
                    textSize = 14f
                    setTextColor(0xFF495057.toInt())
                    setPadding(0, 2, 0, 0)
                }
                cardLayout.addView(nameText)
            }
            
            // Expiry date - CORRECTED PROPERTY
            if (cardData.expiryDate != null && cardData.expiryDate!!.isNotEmpty()) {
                val expiryText = TextView(itemView.context).apply {
                    text = "Expires: ${cardData.expiryDate!!}"
                    textSize = 14f
                    setTextColor(0xFF495057.toInt())
                    setPadding(0, 2, 0, 0)
                }
                cardLayout.addView(expiryText)
            }
            
            // EMV technical summary - CORRECTED PROPERTIES
            val techSummary = buildTechnicalSummary(cardData)
            if (techSummary.isNotEmpty()) {
                val techText = TextView(itemView.context).apply {
                    text = techSummary
                    textSize = 12f
                    setTextColor(0xFF6C757D.toInt())
                    setPadding(0, 8, 0, 0)
                }
                cardLayout.addView(techText)
            }
            
            // Timestamp - CORRECTED PROPERTY
            val timestampText = TextView(itemView.context).apply {
                val timeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                text = "Read: ${timeFormat.format(Date(cardData.readingTimestamp))}"
                textSize = 11f
                setTextColor(0xFF868E96.toInt())
                setPadding(0, 6, 0, 0)
            }
            cardLayout.addView(timestampText)
            
            // Click handler
            cardLayout.setOnClickListener { onCardClick(cardData) }
        }
        
        private fun getCardIdentifier(cardData: EmvCardData): String {
            return if (cardData.pan != null && cardData.pan!!.length >= 4) {
                "****${cardData.pan!!.takeLast(4)}"
            } else {
                "Unknown"
            }
        }
        
        private fun buildTechnicalSummary(cardData: EmvCardData): String {
            val details = mutableListOf<String>()
            
            // AIP status - CORRECTED PROPERTY
            if (cardData.applicationInterchangeProfile != null && cardData.applicationInterchangeProfile!!.isNotEmpty()) {
                details.add("AIP: ${cardData.applicationInterchangeProfile!!}")
            }
            
            // Available AIDs count - CORRECTED PROPERTY
            if (cardData.availableAids.isNotEmpty()) {
                details.add("${cardData.availableAids.size} AID(s)")
            }
            
            // EMV Tags count - CORRECTED PROPERTY
            if (cardData.emvTags.isNotEmpty()) {
                details.add("${cardData.emvTags.size} EMV tags")
            }
            
            // Track2 availability - CORRECTED PROPERTY
            if (cardData.track2Data != null && cardData.track2Data!!.isNotEmpty()) {
                details.add("Track2 data")
            }
            
            // Application cryptogram - CORRECTED PROPERTY
            if (cardData.applicationCryptogram != null && cardData.applicationCryptogram!!.isNotEmpty()) {
                details.add("Cryptogram available")
            }
            
            return if (details.isNotEmpty()) {
                "Technical: ${details.joinToString(" • ")}"
            } else {
                "Basic card data available"
            }
        }
    }
}
