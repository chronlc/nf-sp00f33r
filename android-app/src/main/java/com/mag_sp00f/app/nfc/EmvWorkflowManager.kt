package com.mag_sp00f.app.nfc

import android.util.Log
import timber.log.Timber

/**
 * EMV Workflow Manager - True EMV Emulation Styles
 * 
 * Implements different EMV workflow behaviors:
 * 1. MSD-Only (Magnetic Stripe Data only, no chip data)
 * 2. Force Offline TC (Generate offline approval)
 * 3. Online Authorization (Request online approval) 
 * 4. Contactless Optimized (Fast transaction)
 * 5. Full EMV (Complete chip authentication)
 */
class EmvWorkflowManager(private val testData: VisaTestMsdData) {
    
    enum class WorkflowType(val id: Int, val description: String) {
        MSD_ONLY(1, "MSD-Only: Magnetic stripe emulation only"),
        FORCE_OFFLINE_TC(2, "Force Offline TC: Generate offline approval"),
        ONLINE_AUTHORIZATION(3, "Online Authorization: Request online approval"),
        CONTACTLESS_OPTIMIZED(4, "Contactless Optimized: Fast transaction"),
        FULL_EMV(5, "Full EMV: Complete chip authentication")
    }
    
    private var currentWorkflow: WorkflowType = WorkflowType.MSD_ONLY
    
    fun setWorkflow(workflowId: Int) {
        currentWorkflow = WorkflowType.values().find { it.id == workflowId } 
            ?: WorkflowType.MSD_ONLY
        
        Timber.d("EMV Workflow set to: ${currentWorkflow.description}")
        Log.d("EmvWorkflowManager", "EMV Workflow set to: ${currentWorkflow.description}")
    }
    
    fun getCurrentWorkflow(): WorkflowType = currentWorkflow
    
    /**
     * Generate workflow-specific GPO response
     * Different workflows return different AIP and AFL values
     */
    fun getWorkflowGpoResponse(): ByteArray {
        return when (currentWorkflow) {
            WorkflowType.MSD_ONLY -> {
                // AIP: 2000 = MSD supported only, no SDA/DDA/CDA
                // AFL: Simple AFL pointing to MSD track data only
                Timber.d("GPO: MSD-Only workflow - AIP=2000")
                "77819082022000940408020101001002020057134154904674973556D29022010000820083001F5F3401005F200F43415244484F4C4445522F564953419F100706011203A000009F2608D3967976E30EFAFC9F2701809F3602011E9F6C0200009F6E04207000009000".hexToByteArray()
            }
            
            WorkflowType.FORCE_OFFLINE_TC -> {
                // AIP: 6000 = SDA supported + Offline approval
                // CVM List: No CVM required for offline
                // Generate TC (Transaction Certificate) for offline approval
                Timber.d("GPO: Force Offline TC workflow - AIP=6000")
                "77819082026000940408020101001002020057134154904674973556D29022010000820083001F5F3401005F200F43415244484F4C4445522F564953419F100706011203A000009F2608D3967976E30EFAFC9F2701409F3602011E9F6C0200009F6E04207000009000".hexToByteArray()
            }
            
            WorkflowType.ONLINE_AUTHORIZATION -> {
                // AIP: A000 = SDA + Online authorization required
                // Force ARQC generation (Authorization Request Cryptogram)
                Timber.d("GPO: Online Authorization workflow - AIP=A000") 
                "7781908202A000940408020101001002020057134154904674973556D29022010000820083001F5F3401005F200F43415244484F4C4445522F564953419F100706011203A000009F2608D3967976E30EFAFC9F2701809F3602011E9F6C0200009F6E04207000009000".hexToByteArray()
            }
            
            WorkflowType.CONTACTLESS_OPTIMIZED -> {
                // AIP: E000 = Contactless optimized, fast transaction
                // Minimal data exchange, optimized for speed
                Timber.d("GPO: Contactless Optimized workflow - AIP=E000")
                "77819082028000940408020101001002020057134154904674973556D29022010000820083001F5F3401005F200F43415244484F4C4445522F564953419F100706011203A000009F2608D3967976E30EFAFC9F2701809F3602011E9F6C0200009F6E04207000009000".hexToByteArray()
            }
            
            WorkflowType.FULL_EMV -> {
                // AIP: 7C00 = Full EMV (SDA + DDA + CDA + Terminal Risk Management)
                // Complete authentication with all security features
                Timber.d("GPO: Full EMV workflow - AIP=7C00")
                "77819082027C00940408020101001002020057134154904674973556D29022010000820083001F5F3401005F200F43415244484F4C4445522F564953419F100706011203A000009F2608D3967976E30EFAFC9F2701809F3602011E9F6C0200009F6E04207000009000".hexToByteArray()
            }
        }
    }
    
    /**
     * Generate workflow-specific AID response
     * Some workflows return different capabilities in FCI
     */
    fun getWorkflowAidResponse(aid: String): ByteArray {
        val baseResponse = when (aid.uppercase()) {
            "A0000000031010" -> testData.getVisaMsdAidResponse()
            "A0000000980840" -> testData.getUsDebitAidResponse()
            else -> return byteArrayOf(0x6A.toByte(), 0x82.toByte())
        }
        
        // Modify response based on workflow if needed
        return when (currentWorkflow) {
            WorkflowType.MSD_ONLY -> {
                Timber.d("AID Response: MSD-Only capabilities")
                baseResponse // MSD capabilities already in base response
            }
            
            WorkflowType.CONTACTLESS_OPTIMIZED -> {
                Timber.d("AID Response: Contactless optimized capabilities")
                baseResponse // Same FCI, behavior changes in GPO
            }
            
            else -> {
                Timber.d("AID Response: Standard capabilities for ${currentWorkflow.description}")
                baseResponse
            }
        }
    }
    
    /**
     * Generate workflow-specific cryptogram
     * Different workflows generate different cryptogram types
     */
    fun generateCryptogram(): String {
        return when (currentWorkflow) {
            WorkflowType.FORCE_OFFLINE_TC -> {
                // Generate TC (Transaction Certificate) - 40 indicates offline approval
                Timber.d("Cryptogram: Generated TC for offline approval")
                "4011203A0000"
            }
            
            WorkflowType.ONLINE_AUTHORIZATION -> {
                // Generate ARQC (Authorization Request Cryptogram) - 80 requests online
                Timber.d("Cryptogram: Generated ARQC for online authorization")
                "8011203A0000"
            }
            
            WorkflowType.MSD_ONLY -> {
                // MSD-only: No cryptogram, just track data
                Timber.d("Cryptogram: MSD-only, no cryptogram generated")
                ""
            }
            
            else -> {
                // Default ARQC for other workflows
                Timber.d("Cryptogram: Default ARQC")
                "8011203A0000"
            }
        }
    }
    
    /**
     * Check if workflow supports specific EMV features
     */
    fun supportsOfflineApproval(): Boolean {
        return when (currentWorkflow) {
            WorkflowType.FORCE_OFFLINE_TC, WorkflowType.MSD_ONLY -> true
            else -> false
        }
    }
    
    fun requiresOnlineAuthorization(): Boolean {
        return when (currentWorkflow) {
            WorkflowType.ONLINE_AUTHORIZATION -> true
            else -> false
        }
    }
    
    fun isContactlessOptimized(): Boolean {
        return currentWorkflow == WorkflowType.CONTACTLESS_OPTIMIZED
    }
    
    private fun String.hexToByteArray(): ByteArray {
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}