package com.mag_sp00f.app.cardreading

import com.mag_sp00f.app.models.CardProfile
import com.mag_sp00f.app.data.EmvCardData
import timber.log.Timber

/**
 * Manager for card profiles with full EMV data and APDU logs
 * Per newrule.md: Production-grade, real-data-only, no simulation
 */
class CardProfileManager {
    
    private val cardProfiles = mutableListOf<CardProfile>()
    
    /**
     * Save EMV card data as a new card profile
     */
    fun saveCard(cardData: EmvCardData) {
        val profile = CardProfile(
            emvCardData = cardData
        )
        cardProfiles.add(profile)
        Timber.d("💾 Card saved: ${cardData.cardholderName ?: "Unknown"}")
    }
    
    /**
     * Get recent cards
     */
    fun getRecentCards(): List<CardProfile> {
        return cardProfiles.takeLast(5)
    }
    
    /**
     * Get recent cards with limit
     */
    fun getRecentCards(limit: Int): List<EmvCardData> {
        return cardProfiles.takeLast(limit).map { it.emvCardData }
    }
    
    /**
     * Get all cards
     */
    fun getAllCards(): List<CardProfile> {
        return cardProfiles.toList()
    }
    
    /**
     * Save card profile
     */
    fun saveCardProfile(profile: CardProfile) {
        cardProfiles.add(profile)
    }
    
    /**
     * Get all card profiles
     */
    fun getAllCardProfiles(): List<CardProfile> {
        return cardProfiles.toList()
    }
    
    /**
     * Delete card profile by ID
     */
    fun deleteCardProfile(id: String) {
        cardProfiles.removeAll { it.id == id }
        Timber.d("🗑️ Card profile deleted: $id")
    }
}
