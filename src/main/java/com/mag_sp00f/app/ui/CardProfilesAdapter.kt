package com.mag_sp00f.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mag_sp00f.app.R
import com.mag_sp00f.app.models.CardProfile
import timber.log.Timber

/**
 * PRODUCTION-GRADE Card Profiles RecyclerView Adapter
 * Displays card profiles with unmasked PAN for EMV analysis
 * NO SAFE CALL OPERATORS - Explicit null checks only per newrule.md
 */
class CardProfilesAdapter(
    private var cardProfiles: List<CardProfile> = emptyList(),
    private val onCardClick: (CardProfile) -> Unit = {},
    private val onCardLongClick: (CardProfile) -> Unit = {}
) : RecyclerView.Adapter<CardProfilesAdapter.CardProfileViewHolder>() {

    /**
     * ViewHolder for card profile items
     */
    class CardProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardBrandText: TextView = itemView.findViewById(R.id.textViewCardBrand)
        val maskedPanText: TextView = itemView.findViewById(R.id.textViewMaskedPan)
        val cardholderNameText: TextView = itemView.findViewById(R.id.textViewCardholderName)
        val applicationLabelText: TextView = itemView.findViewById(R.id.textViewApplicationLabel)
        val expiryDateText: TextView = itemView.findViewById(R.id.textViewExpiryDate)
        val createdDateText: TextView = itemView.findViewById(R.id.textViewCreatedDate)
        val apduCountText: TextView = itemView.findViewById(R.id.textViewApduCount)
        val attackVectorsText: TextView = itemView.findViewById(R.id.textViewAttackVectors)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_profile, parent, false)
        return CardProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardProfileViewHolder, position: Int) {
        val cardProfile = cardProfiles[position]
        
        try {
            bindCardProfile(holder, cardProfile)
            setupClickListeners(holder, cardProfile)
        } catch (e: Exception) {
            Timber.e(e, "Error binding card profile at position $position")
            bindErrorState(holder)
        }
    }

    /**
     * Bind card profile data to view holder
     */
    private fun bindCardProfile(holder: CardProfileViewHolder, cardProfile: CardProfile) {
        // Card brand
        val cardType = cardProfile.emvCardData.detectCardType()
        holder.cardBrandText.text = "💳 ${cardType.name}"

        // Unmasked PAN display - NO SAFE CALL OPERATOR
        val pan = cardProfile.emvCardData.pan
        if (pan != null && pan.isNotEmpty()) {
            holder.maskedPanText.text = "🔢 $pan"
        } else {
            holder.maskedPanText.text = "🔢 PAN: Not available"
        }

        // Cardholder name - NO SAFE CALL OPERATOR
        val cardholderName = cardProfile.emvCardData.cardholderName
        if (cardholderName != null && cardholderName.isNotEmpty()) {
            holder.cardholderNameText.text = "👤 $cardholderName"
        } else {
            holder.cardholderNameText.text = "👤 Cardholder: Not specified"
        }

        // Application label
        val applicationLabel = cardProfile.emvCardData.applicationLabel
        if (applicationLabel.isNotEmpty()) {
            holder.applicationLabelText.text = "🏷️ $applicationLabel"
        } else {
            holder.applicationLabelText.text = "🏷️ Application: Not specified"
        }

        // Expiry date - NO SAFE CALL OPERATOR
        val expiryDate = cardProfile.emvCardData.expiryDate
        if (expiryDate != null && expiryDate.isNotEmpty()) {
            holder.expiryDateText.text = "📅 $expiryDate"
        } else {
            holder.expiryDateText.text = "📅 Expiry: Not available"
        }

        // Created date
        holder.createdDateText.text = "📅 Created: ${formatTimestamp(cardProfile.createdAt.time)}"

        // APDU count
        val apduCount = cardProfile.apduLogs.size
        holder.apduCountText.text = "📝 $apduCount APDU Commands"

        // Attack vectors count
        val attackVectors = cardProfile.getAttackCompatibility()
        holder.attackVectorsText.text = "🎯 ${attackVectors.size} Attack Vectors"
    }

    /**
     * Setup click listeners for card profile item
     */
    private fun setupClickListeners(holder: CardProfileViewHolder, cardProfile: CardProfile) {
        holder.itemView.setOnClickListener {
            try {
                onCardClick(cardProfile)
                Timber.d("Card profile clicked: ${cardProfile.id}")
            } catch (e: Exception) {
                Timber.e(e, "Error handling card click")
            }
        }

        holder.itemView.setOnLongClickListener {
            try {
                onCardLongClick(cardProfile)
                Timber.d("Card profile long-clicked: ${cardProfile.id}")
                true
            } catch (e: Exception) {
                Timber.e(e, "Error handling card long click")
                false
            }
        }
    }

    /**
     * Bind error state to view holder
     */
    private fun bindErrorState(holder: CardProfileViewHolder) {
        holder.cardBrandText.text = "❌ Error loading card"
        holder.maskedPanText.text = "🔢 PAN: Error"
        holder.cardholderNameText.text = "👤 Cardholder: Error"
        holder.applicationLabelText.text = "🏷️ Application: Error"
        holder.expiryDateText.text = "�� Expiry: Error"
        holder.createdDateText.text = "📅 Created: Error"
        holder.apduCountText.text = "📝 APDU: Error"
        holder.attackVectorsText.text = "🎯 Attacks: Error"
    }

    /**
     * Format timestamp for display
     */
    private fun formatTimestamp(timestamp: Long): String {
        return try {
            val date = java.util.Date(timestamp)
            val format = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            format.format(date)
        } catch (e: Exception) {
            Timber.e(e, "Error formatting timestamp: $timestamp")
            "Unknown"
        }
    }

    /**
     * Update card profiles list
     */
    fun updateCardProfiles(newProfiles: List<CardProfile>) {
        val oldSize = cardProfiles.size
        cardProfiles = newProfiles.toList() // Create defensive copy
        
        // Notify adapter of changes
        when {
            oldSize == 0 -> {
                notifyItemRangeInserted(0, newProfiles.size)
            }
            newProfiles.isEmpty() -> {
                notifyItemRangeRemoved(0, oldSize)
            }
            else -> {
                notifyDataSetChanged() // Full refresh for complex changes
            }
        }
        
        Timber.d("Card profiles updated: ${newProfiles.size} items")
    }

    /**
     * Add single card profile
     */
    fun addCardProfile(cardProfile: CardProfile) {
        val newProfiles = cardProfiles.toMutableList()
        newProfiles.add(0, cardProfile) // Add to beginning
        
        cardProfiles = newProfiles
        notifyItemInserted(0)
        
        Timber.d("Card profile added: ${cardProfile.id}")
    }

    /**
     * Remove card profile
     */
    fun removeCardProfile(cardProfile: CardProfile) {
        val position = cardProfiles.indexOf(cardProfile)
        if (position >= 0) {
            val newProfiles = cardProfiles.toMutableList()
            newProfiles.removeAt(position)
            
            cardProfiles = newProfiles
            notifyItemRemoved(position)
            
            Timber.d("Card profile removed: ${cardProfile.id}")
        }
    }

    /**
     * Get card profile at position
     */
    fun getCardProfileAt(position: Int): CardProfile? {
        return if (position in 0 until cardProfiles.size) {
            cardProfiles[position]
        } else {
            null
        }
    }

    /**
     * Clear all card profiles
     */
    fun clearCardProfiles() {
        val oldSize = cardProfiles.size
        cardProfiles = emptyList()
        notifyItemRangeRemoved(0, oldSize)
        
        Timber.d("All card profiles cleared")
    }

    /**
     * Get current card profiles count
     */
    override fun getItemCount(): Int = cardProfiles.size

    /**
     * Filter card profiles by search query
     */
    fun filterCardProfiles(query: String, allProfiles: List<CardProfile>) {
        val filteredProfiles = if (query.isEmpty()) {
            allProfiles
        } else {
            allProfiles.filter { profile ->
                val pan = profile.emvCardData.pan
                val cardholderName = profile.emvCardData.cardholderName
                
                (pan != null && pan.contains(query, ignoreCase = true)) ||
                (cardholderName != null && cardholderName.contains(query, ignoreCase = true)) ||
                profile.emvCardData.applicationLabel.contains(query, ignoreCase = true)
            }
        }
        
        updateCardProfiles(filteredProfiles)
        Timber.d("Card profiles filtered: ${filteredProfiles.size} results for query '$query'")
    }

    /**
     * Sort card profiles by criteria
     */
    fun sortCardProfiles(sortBy: SortCriteria, ascending: Boolean = true) {
        val sortedProfiles = when (sortBy) {
            SortCriteria.CARD_TYPE -> {
                cardProfiles.sortedWith { a, b ->
                    val comparison = a.emvCardData.detectCardType().name.compareTo(b.emvCardData.detectCardType().name)
                    if (ascending) comparison else -comparison
                }
            }
            SortCriteria.PAN -> {
                cardProfiles.sortedWith { a, b ->
                    val panA = a.emvCardData.pan ?: ""
                    val panB = b.emvCardData.pan ?: ""
                    val comparison = panA.compareTo(panB)
                    if (ascending) comparison else -comparison
                }
            }
            SortCriteria.TIMESTAMP -> {
                cardProfiles.sortedWith { a, b ->
                    val comparison = a.createdAt.compareTo(b.createdAt)
                    if (ascending) comparison else -comparison
                }
            }
            SortCriteria.CARDHOLDER -> {
                cardProfiles.sortedWith { a, b ->
                    val nameA = a.emvCardData.cardholderName ?: ""
                    val nameB = b.emvCardData.cardholderName ?: ""
                    val comparison = nameA.compareTo(nameB, ignoreCase = true)
                    if (ascending) comparison else -comparison
                }
            }
        }
        
        updateCardProfiles(sortedProfiles)
        Timber.d("Card profiles sorted by ${sortBy.name}, ascending: $ascending")
    }

    /**
     * Sort criteria enum
     */
    enum class SortCriteria {
        CARD_TYPE,
        PAN,
        TIMESTAMP,
        CARDHOLDER
    }
}
