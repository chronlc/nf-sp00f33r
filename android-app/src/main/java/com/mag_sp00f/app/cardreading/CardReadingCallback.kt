package com.mag_sp00f.app.cardreading

import com.mag_sp00f.app.data.EmvCardData

/**
 * Callback interface for NFC card reading operations
 */
interface CardReadingCallback {
    
    /**
     * Called when NFC reading starts
     */
    fun onReadingStarted()
    
    /**
     * Called when NFC reading stops
     */
    fun onReadingStopped()
    
    /**
     * Called when a card is successfully read
     */
    fun onCardRead(cardData: EmvCardData)
    
    /**
     * Called when an error occurs
     */
    fun onError(error: String)
}
