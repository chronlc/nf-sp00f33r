package com.mag_sp00f.app.emulation

/**
 * EMV Attack Module Interface
 * Base interface for all EMV attack modules implementing specific attack vectors
 * Based on attack_module_architecture.md specifications
 */
interface EmvAttackModule {
    
    /**
     * Get unique attack identifier
     * @return Attack ID string (e.g., "ppse_aid_poisoning")
     */
    fun getAttackId(): String
    
    /**
     * Get human-readable attack description
     * @return Description of what this attack does
     */
    fun getDescription(): String
    
    /**
     * Check if this attack applies to the current command/context
     * @param command The APDU command being processed
     * @param cardData Current card data context
     * @return true if attack should be applied, false otherwise
     */
    fun isApplicable(command: ByteArray, cardData: Map<String, Any>): Boolean
    
    /**
     * Apply attack to the APDU response
     * @param command Original APDU command
     * @param response Original APDU response
     * @param cardData Current card data context
     * @return Modified APDU response with attack applied
     */
    fun applyAttack(command: ByteArray, response: ByteArray, cardData: Map<String, Any>): ByteArray
    
    /**
     * Get current attack status and statistics
     * @return Map containing attack status information
     */
    fun getAttackStatus(): Map<String, Any>
    
    /**
     * Configure attack parameters
     * @param config Configuration parameters map
     */
    fun configure(config: Map<String, Any>)
    
    /**
     * Reset attack statistics and state
     */
    fun reset()
}
