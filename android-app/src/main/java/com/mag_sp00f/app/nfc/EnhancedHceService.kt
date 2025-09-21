package com.mag_sp00f.app.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import timber.log.Timber

/**
 * Enhanced Host Card Emulation Service with APDU Flow Hooks
 * 
 * Features:
 * - GPO/PPSE command interception
 * - Real-time APDU logging
 * - VISA MSD workflow emulation
 * - Configurable response generation
 * - Integration with PN532 terminal testing
 */
class EnhancedHceService : HostApduService() {
    
    private lateinit var apduFlowHooks: ApduFlowHooks
    
    override fun onCreate() {
        super.onCreate()
        apduFlowHooks = ApduFlowHooks(this)
        Timber.d("EnhancedHceService created")
    }
    
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray? {
        return try {
            if (commandApdu == null) {
                Log.w("EnhancedHceService", "Received null APDU command")
                Timber.w("Received null APDU command")
                return RESPONSE_UNKNOWN_COMMAND
            }
            
            val hexCommand = commandApdu.toHexString()
            Log.d("EnhancedHceService", "RX APDU: $hexCommand")
            Timber.d("RX APDU: $hexCommand")
            
            // Process through APDU flow hooks
            val response = apduFlowHooks.processCommand(commandApdu)
            
            val hexResponse = response.toHexString()
            Log.d("EnhancedHceService", "TX APDU: $hexResponse")
            Timber.d("TX APDU: $hexResponse")
            
            response
            
        } catch (e: Exception) {
            Log.e("EnhancedHceService", "Error processing APDU command", e)
            Timber.e(e, "Error processing APDU command")
            RESPONSE_GENERAL_ERROR
        }
    }
    
    override fun onDeactivated(reason: Int) {
        val reasonText = when (reason) {
            DEACTIVATION_LINK_LOSS -> "Link Loss"
            DEACTIVATION_DESELECTED -> "Deselected"
            else -> "Unknown ($reason)"
        }
        Timber.d("HCE service deactivated: $reasonText")
    }
    
    companion object {
        // Standard response codes
        private val RESPONSE_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val RESPONSE_UNKNOWN_COMMAND = byteArrayOf(0x6D.toByte(), 0x00.toByte())
        private val RESPONSE_GENERAL_ERROR = byteArrayOf(0x6F.toByte(), 0x00.toByte())
        private val RESPONSE_FILE_NOT_FOUND = byteArrayOf(0x6A.toByte(), 0x82.toByte())
    }
    
    /**
     * Extension function to convert ByteArray to hex string
     */
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }
}