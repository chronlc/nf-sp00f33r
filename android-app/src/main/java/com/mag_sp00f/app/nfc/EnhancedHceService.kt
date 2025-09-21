package com.mag_sp00f.app.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import timber.log.Timber

class EnhancedHceService : HostApduService() {
    
    private lateinit var apduFlowHooks: ApduFlowHooks
    
    override fun onCreate() {
        super.onCreate()
        apduFlowHooks = ApduFlowHooks(this)
        Timber.d("EnhancedHceService created")
    }
    
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        return try {
            if (commandApdu == null) {
                Log.w(TAG, "Received null APDU command")
                return RESPONSE_UNKNOWN_COMMAND
            }
            
            val hexCommand = commandApdu.toHexString()
            Log.d(TAG, "RX APDU: $hexCommand")
            
            // Handle SELECT commands manually (INS = 0xA4) and READ RECORD (INS = 0xB2)
            val response = when {
                commandApdu.size >= 4 && (commandApdu[1].toInt() and 0xFF) == 0xA4 -> {
                    Log.d(TAG, "Processing SELECT command manually")
                    handleSelectCommand(commandApdu)
                }
                commandApdu.size >= 4 && (commandApdu[1].toInt() and 0xFF) == 0xB2 -> {
                    Log.d(TAG, "Processing READ RECORD command")
                    handleReadRecordCommand(commandApdu)
                }
                else -> {
                    Log.d(TAG, "Processing command via ApduFlowHooks")
                    apduFlowHooks.processCommand(commandApdu)
                }
            }
            
            val hexResponse = response.toHexString()
            Log.d(TAG, "TX APDU: $hexResponse")
            
            response
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing APDU command", e)
            RESPONSE_UNKNOWN_COMMAND
        }
    }
    
    private fun handleSelectCommand(commandApdu: ByteArray): ByteArray {
        return try {
            if (commandApdu.size < 5) {
                Log.w(TAG, "SELECT command too short")
                return RESPONSE_UNKNOWN_COMMAND
            }
            
            val lc = commandApdu[4].toInt() and 0xFF
            if (commandApdu.size < 5 + lc) {
                Log.w(TAG, "SELECT command AID data incomplete")
                return RESPONSE_UNKNOWN_COMMAND
            }
            
            val aidBytes = commandApdu.copyOfRange(5, 5 + lc)
            val aidHex = aidBytes.toHexString().uppercase()
            
            Log.d(TAG, "SELECT Command - AID: $aidHex")
            
            when (aidHex) {
                "325041592E5359532E4444463031" -> {
                    Log.i(TAG, "SELECT PPSE CONTACTLESS")
                    apduFlowHooks.handleSelectPpse()
                }
                "A0000000031010" -> {
                    Log.i(TAG, "SELECT VISA MSD AID")
                    apduFlowHooks.handleSelectAid(aidHex)
                }
                else -> {
                    Log.w(TAG, "SELECT unknown AID: $aidHex")
                    RESPONSE_FILE_NOT_FOUND
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling SELECT command", e)
            RESPONSE_UNKNOWN_COMMAND
        }
    }
    
    private fun handleReadRecordCommand(commandApdu: ByteArray): ByteArray {
        return try {
            if (commandApdu.size < 4) {
                Log.w(TAG, "READ RECORD command too short")
                return RESPONSE_UNKNOWN_COMMAND
            }
            
            val record = commandApdu[2].toInt() and 0xFF
            val sfi = (commandApdu[3].toInt() and 0xF8) shr 3
            
            Log.d(TAG, "READ RECORD Command - SFI: $sfi, Record: $record")
            
            // Forward to ApduFlowHooks for actual processing
            apduFlowHooks.handleReadRecord(sfi, record)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling READ RECORD command", e)
            RESPONSE_UNKNOWN_COMMAND
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
