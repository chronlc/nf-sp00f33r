package com.mag_sp00f.app.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import timber.log.Timber

class EnhancedHceService : HostApduService() {
    
    // Direct access to APDU flow hooks with workflow support
    private val apduHooks = ApduFlowHooks(this)
    
    override fun onCreate() {
        super.onCreate()
        // Default to MSD-Only workflow
        apduHooks.setEmvWorkflow(1)
        Timber.d("EnhancedHceService created - EMV Workflow System enabled")
    }
    
    /**
     * Switch EMV workflow emulation style
     * @param workflowId: 1=MSD-Only, 2=Force Offline TC, 3=Online Auth, 4=Contactless, 5=Full EMV
     */
    fun switchWorkflow(workflowId: Int) {
        apduHooks.setEmvWorkflow(workflowId)
        Log.i(TAG, "EMV Workflow switched to ID: $workflowId")
    }
    
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        return try {
            if (commandApdu == null) {
                Log.w(TAG, "Received null APDU command")
                return RESPONSE_UNKNOWN_COMMAND
            }
            
            val hexCommand = commandApdu.toHexString()
            Log.d(TAG, "RX APDU: $hexCommand")
            
            // RAPID EMV RESPONSE - Direct processing without delays
            val response = processCommandDirect(commandApdu)
            
            val hexResponse = response.toHexString()
            Log.d(TAG, "TX APDU: $hexResponse")
            
            response
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing APDU command", e)
            RESPONSE_UNKNOWN_COMMAND
        }
    }
    
    /**
     * Direct APDU command processing using workflow-aware hooks
     * Each workflow emulates different EMV behavior patterns
     */
    private fun processCommandDirect(commandApdu: ByteArray): ByteArray {
        // Use workflow-aware APDU processing
        return apduHooks.processCommand(commandApdu)
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
        private const val TAG = "EnhancedHceService"
        
        private val RESPONSE_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val RESPONSE_UNKNOWN_COMMAND = byteArrayOf(0x6D.toByte(), 0x00.toByte())
        private val RESPONSE_FILE_NOT_FOUND = byteArrayOf(0x6A.toByte(), 0x82.toByte())
    }
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02X".format(it) }
}
