package com.mag_sp00f.app.emulation

import com.mag_sp00f.app.models.CardProfile
import timber.log.Timber

/**
 * EMV Attack Emulation Manager
 * Coordinates consolidated emulation profiles with data requirement validation
 * Based on g0d of r00t efficiency insight for consolidated attack profiles
 */
class EmvAttackEmulationManager {
    
    private val emulationProfiles = EmulationProfiles.getAllProfiles()
    private var currentCardProfile: CardProfile? = null
    private val executionStatistics = mutableMapOf<String, Int>()
    
    init {
        Timber.d("EmvAttackEmulationManager initialized with ${emulationProfiles.size} emulation profiles")
    }
    
    /**
     * Set the active card profile for emulation binding
     */
    fun setActiveCardProfile(profile: CardProfile) {
        currentCardProfile = profile
        Timber.i("Active card profile updated: ${profile.cardholderName ?: profile.applicationLabel ?: profile.detectCardType()}")
    }
    
    /**
     * Get available emulation profiles for current card profile
     */
    fun getAvailableProfiles(): List<EmulationProfile> {
        val cardData = currentCardProfile?.emvCardData
        
        return if (cardData != null) {
            emulationProfiles.mapNotNull { profile ->
                if (EmulationProfiles.hasRequiredData(profile, cardData)) {
                    profile
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }
    }
    
    /**
     * Execute specific emulation profile
     */
    fun executeProfile(profileType: String): Boolean {
        val profile = emulationProfiles.find { it.type == profileType }
        val cardData = currentCardProfile?.emvCardData
        
        if (profile == null) {
            Timber.e("Emulation profile not found: $profileType")
            return false
        }
        
        if (cardData == null) {
            Timber.e("No card profile set for emulation execution")
            return false
        }
        
        return try {
            if (EmulationProfiles.hasRequiredData(profile, cardData)) {
                EmulationProfiles.executeProfile(profile, cardData)
                
                // Update statistics
                executionStatistics[profileType] = (executionStatistics[profileType] ?: 0) + 1
                
                Timber.i("Emulation profile executed successfully: $profileType")
                true
            } else {
                Timber.w("Emulation profile lacks required data: $profileType")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Emulation profile execution failed: $profileType")
            false
        }
    }
    
    /**
     * Get data requirements for specific profile
     */
    fun getProfileRequirements(profileType: String): List<String> {
        val profile = emulationProfiles.find { it.type == profileType }
        return profile?.dataRequirements ?: emptyList()
    }
    
    /**
     * Check if profile can be executed with current card data
     */
    fun canExecuteProfile(profileType: String): Boolean {
        val profile = emulationProfiles.find { it.type == profileType }
        val cardData = currentCardProfile?.emvCardData
        
        return if (profile != null && cardData != null) {
            EmulationProfiles.hasRequiredData(profile, cardData)
        } else {
            false
        }
    }
    
    /**
     * Get missing data for specific profile
     */
    fun getMissingData(profileType: String): List<String> {
        val profile = emulationProfiles.find { it.type == profileType }
        val cardData = currentCardProfile?.emvCardData
        
        if (profile == null || cardData == null) {
            return emptyList()
        }
        
        return profile.dataRequirements.filter { requirement ->
            when (requirement) {
                "pan" -> cardData.pan.isNullOrBlank()
                "track2_data" -> cardData.track2Data.isNullOrBlank()
                "application_interchange_profile" -> cardData.applicationInterchangeProfile.isNullOrBlank()
                "supported_aids" -> cardData.availableAids.isEmpty()
                "cardholder_name" -> cardData.cardholderName.isNullOrBlank()
                "expiry_date" -> cardData.expiryDate.isNullOrBlank()
                "application_file_locator" -> cardData.applicationFileLocator.isNullOrBlank()
                "cryptogram_data" -> !cardData.emvTags.containsKey("9F27")
                "cvr_list" -> !cardData.emvTags.containsKey("8E")
                else -> false
            }
        }
    }
    
    /**
     * Get execution statistics
     */
    fun getExecutionStatistics(): Map<String, Int> = executionStatistics.toMap()
    
    /**
     * Reset all execution statistics
     */
    fun resetStatistics() {
        executionStatistics.clear()
        Timber.i("Execution statistics reset")
    }
    
    /**
     * Get comprehensive emulation status
     */
    fun getEmulationStatus(): Map<String, Any> {
        val totalProfiles = emulationProfiles.size
        val availableProfiles = getAvailableProfiles().size
        
        return mapOf(
            "total_profiles" to totalProfiles,
            "available_profiles" to availableProfiles,
            "card_loaded" to (currentCardProfile != null),
            "card_name" to (currentCardProfile?.cardholderName ?: currentCardProfile?.applicationLabel ?: currentCardProfile?.detectCardType() ?: "None"),
            "execution_statistics" to executionStatistics,
            "profile_requirements" to emulationProfiles.associate { 
                it.type to it.dataRequirements 
            }
        )
    }
}
