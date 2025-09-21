package com.mag_sp00f.app.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import timber.log.Timber

class EnhancedHceService : HostApduService() {
    
    // Direct access to test data - no complex routing
    private val testCardData = VisaTestMsdData()
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("EnhancedHceService created - Rapid EMV mode")
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
     * Direct APDU command processing for rapid EMV workflow
     * Based on reference implementation pattern - no delays, immediate response
     */
    private fun processCommandDirect(commandApdu: ByteArray): ByteArray {
        val hexCommand = commandApdu.toHexString().uppercase()
        
        return when {
            // SELECT PPSE (00A404000E325041592E5359532E4444463031)
            hexCommand.contains("325041592E5359532E4444463031") -> {
                Log.d(TAG, "SELECT PPSE - Immediate response")
                testCardData.getPpseResponse()
            }
            
            // SELECT VISA AID (A0000000031010)
            hexCommand.contains("A0000000031010") -> {
                Log.d(TAG, "SELECT VISA AID - Immediate response")
                testCardData.getVisaMsdAidResponse()
            }
            
            // SELECT US Debit AID (A0000000980840)
            hexCommand.contains("A0000000980840") -> {
                Log.i(TAG, "SELECT US DEBIT AID - Immediate response")
                testCardData.getUsDebitAidResponse()
            }
            
            // GPO (Get Processing Options)
            commandApdu.size >= 2 && commandApdu[0] == 0x80.toByte() && commandApdu[1] == 0xA8.toByte() -> {
                Log.i(TAG, "GPO - Immediate response")
                testCardData.getGpoResponse()
            }
            
            // READ RECORD
            commandApdu.size >= 4 && commandApdu[0] == 0x00.toByte() && commandApdu[1] == 0xB2.toByte() -> {
                val record = commandApdu[2].toInt() and 0xFF
                val sfi = (commandApdu[3].toInt() and 0xF8) shr 3
                Log.i(TAG, "READ RECORD SFI=$sfi Record=$record - Immediate response")
                testCardData.getReadRecordResponse(sfi, record)
            }
            
            else -> {
                Log.w(TAG, "Unknown command: $hexCommand")
                RESPONSE_UNKNOWN_COMMAND
            }
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
        private const val TAG = "EnhancedHceService"
        
        private val RESPONSE_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val RESPONSE_UNKNOWN_COMMAND = byteArrayOf(0x6D.toByte(), 0x00.toByte())
        private val RESPONSE_FILE_NOT_FOUND = byteArrayOf(0x6A.toByte(), 0x82.toByte())
    }
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02X".format(it) }
}
