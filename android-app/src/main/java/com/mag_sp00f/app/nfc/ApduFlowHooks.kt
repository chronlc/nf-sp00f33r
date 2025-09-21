package com.mag_sp00f.app.nfc

import android.content.Context
import android.util.Log
import timber.log.Timber

/**
 * APDU Flow Hooks for GPO/PPSE Interception
 * 
 * This class implements hooks to intercept and process specific APDU commands:
 * - SELECT PPSE (00A404000E325041592E5359532E444446303100 - Contactless 2PAY)
 * - SELECT AID (A0000000031010 and A0000000980840)
 * - GET PROCESSING OPTIONS (GPO)
 * - READ RECORD commands
 * 
 * Uses VISA TEST MSD reference data from emv.html for validation
 */
class ApduFlowHooks(private val context: Context) {
    
    // VISA TEST MSD reference data from emv.html
    private val testCardData = VisaTestMsdData()
    
    // EMV Workflow Manager for different emulation styles
    private val workflowManager = EmvWorkflowManager(testCardData)
    
    /**
     * Set the EMV workflow emulation style
     * @param workflowId: 1=MSD-Only, 2=Force Offline TC, 3=Online Auth, 4=Contactless, 5=Full EMV
     */
    fun setEmvWorkflow(workflowId: Int) {
        workflowManager.setWorkflow(workflowId)
        Timber.d("EMV Workflow changed to: ${workflowManager.getCurrentWorkflow().description}")
    }
    
    fun processCommand(commandApdu: ByteArray): ByteArray {
        val command = commandApdu.toHexString()
        
        return when {
            // SELECT PPSE command
            isSelectPpse(commandApdu) -> {
                Timber.d("HOOK: SELECT PPSE intercepted")
                handleSelectPpse()
            }
            
            // SELECT AID commands
            isSelectAid(commandApdu) -> {
                val aid = extractAid(commandApdu)
                Timber.d("HOOK: SELECT AID intercepted: $aid")
                handleSelectAid(aid)
            }
            
            // GET PROCESSING OPTIONS (GPO)
            isGetProcessingOptions(commandApdu) -> {
                Timber.d("HOOK: GPO intercepted")
                handleGetProcessingOptions(commandApdu)
            }
            
            // READ RECORD commands
            isReadRecord(commandApdu) -> {
                val sfi = extractSfi(commandApdu)
                val record = extractRecord(commandApdu)
                Timber.d("HOOK: READ RECORD intercepted - SFI: $sfi, Record: $record")
                handleReadRecord(sfi, record)
            }
            
            else -> {
                Timber.d("HOOK: Unknown command, returning error")
                byteArrayOf(0x6D.toByte(), 0x00.toByte()) // Unknown command
            }
        }
    }
    
    private fun isSelectPpse(command: ByteArray): Boolean {
        // CONTACTLESS PPSE command: 2PAY.SYS.DDF01 (CLA=00)
        val contactlessPpseCommand = "00A404000E325041592E5359532E4444463031"
        // CONTACT PPSE command: 1PAY.SYS.DDF01 (CLA=00) 
        val contactPpseCommand = "00A404000E315041592E5359532E4444463031"
        
        val hexCommand = command.toHexString()
        val isContactless = hexCommand.equals(contactlessPpseCommand, ignoreCase = true)
        val isContact = hexCommand.equals(contactPpseCommand, ignoreCase = true)
        
        // Debug logging
        Log.d("ApduFlowHooks", "PPSE CHECK: Command=${hexCommand}")
        Log.d("ApduFlowHooks", "PPSE CHECK: Expected contactless=${contactlessPpseCommand}")
        Log.d("ApduFlowHooks", "PPSE CHECK: Expected contact=${contactPpseCommand}")
        Log.d("ApduFlowHooks", "PPSE CHECK: Is contactless match=${isContactless}")
        Log.d("ApduFlowHooks", "PPSE CHECK: Is contact match=${isContact}")
        Timber.d("PPSE CHECK: Command=${hexCommand}")
        Timber.d("PPSE CHECK: Expected contactless=${contactlessPpseCommand}")
        Timber.d("PPSE CHECK: Expected contact=${contactPpseCommand}")
        Timber.d("PPSE CHECK: Is contactless match=${isContactless}")
        Timber.d("PPSE CHECK: Is contact match=${isContact}")
        
        return isContactless || isContact
    }
    
    private fun isSelectAid(command: ByteArray): Boolean {
        val hex = command.toHexString()
        return hex.startsWith("00A40400", ignoreCase = true)
    }
    
    private fun isGetProcessingOptions(command: ByteArray): Boolean {
        return command.size > 0 && command[0] == 0x80.toByte() && command[1] == 0xA8.toByte()
    }
    
    private fun isReadRecord(command: ByteArray): Boolean {
        return command.size > 0 && command[0] == 0x00.toByte() && command[1] == 0xB2.toByte()
    }
    
    private fun extractAid(command: ByteArray): String {
        if (command.size < 5) return ""
        val aidLength = command[4].toInt() and 0xFF
        if (command.size < 5 + aidLength) return ""
        
        return command.sliceArray(5 until 5 + aidLength).toHexString()
    }
    
    private fun extractSfi(command: ByteArray): Int {
        return if (command.size > 3) (command[3].toInt() and 0xF8) shr 3 else 0
    }
    
    private fun extractRecord(command: ByteArray): Int {
        return if (command.size > 2) command[2].toInt() and 0xFF else 0
    }
    
    fun handleSelectPpse(): ByteArray {
        // Return VISA TEST PPSE response from emv.html
        return testCardData.getPpseResponse()
    }
    
    fun handleSelectAid(aid: String): ByteArray {
        Timber.d("SELECT AID: $aid using workflow: ${workflowManager.getCurrentWorkflow().description}")
        return workflowManager.getWorkflowAidResponse(aid)
    }
    
    private fun handleGetProcessingOptions(command: ByteArray): ByteArray {
        Timber.d("GPO: Using workflow: ${workflowManager.getCurrentWorkflow().description}")
        // Extract PDOL data and return workflow-specific GPO response
        return workflowManager.getWorkflowGpoResponse()
    }
    
    fun handleReadRecord(sfi: Int, record: Int): ByteArray {
        return testCardData.getReadRecordResponse(sfi, record)
    }
    
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }
}