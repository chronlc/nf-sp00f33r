package com.mag_sp00f.app.cardreading

import android.util.Log
import com.mag_sp00f.app.utils.Utils
import java.io.ByteArrayOutputStream

/**
 * PRODUCTION-GRADE EMV BER-TLV Parser
 * Critical: NO append mode corruption, clean write-mode generation
 * Dynamic PDOL parsing from live card, NO hardcoded emv.html values
 */
class EmvTlvParser {

    companion object {
        private const val TAG = "EmvTlvParser"
    }

    /**
     * TTQ Workflow Types for dynamic EMV processing
     */
    enum class TtqWorkflow {
        STANDARD,    // 27000000 - Most common
        ENHANCED,    // B7604000 - Enhanced features
        SIMPLIFIED,  // A0000000 - Basic workflow
        ADVANCED     // F0204000 - Full features
    }

    private var currentTtqWorkflow = TtqWorkflow.STANDARD

    /**
     * Set TTQ workflow for forcing different EMV paths
     */
    fun setTtqWorkflow(workflow: TtqWorkflow) {
        currentTtqWorkflow = workflow
        Log.d(TAG, "🎯 TTQ workflow set to: $workflow")
    }

    /**
     * Extract PDOL from SELECT AID response (NEVER from emv.html hardcoded values)
     */
    fun extractPdol(selectAidResponse: ByteArray): List<PdolTag>? {
        Log.d(TAG, "🔍 Extracting PDOL from live card SELECT AID response...")
        
        val pdolTags = mutableListOf<PdolTag>()
        val responseHex = Utils.bytesToHex(selectAidResponse)
        Log.d(TAG, "SELECT AID Response: $responseHex")

        try {
            // Look for PDOL tag (9F38)
            val pdolIndex = findTagInResponse(selectAidResponse, "9F38")
            if (pdolIndex == -1) {
                Log.w(TAG, "⚠️ No PDOL (9F38) found in SELECT AID response")
                return null
            }

            val pdolLength = selectAidResponse[pdolIndex + 2].toInt() and 0xFF
            val pdolData = selectAidResponse.copyOfRange(pdolIndex + 3, pdolIndex + 3 + pdolLength)
            
            Log.d(TAG, "📋 PDOL found - Length: $pdolLength bytes")
            Log.d(TAG, "📋 PDOL data: ${Utils.bytesToHex(pdolData)}")

            // Parse PDOL tag list from live card
            var offset = 0
            while (offset < pdolData.size) {
                val tagBytes: ByteArray
                val tagLength: Int

                // Parse tag (1 or 2 bytes)
                if ((pdolData[offset].toInt() and 0x1F) == 0x1F) {
                    // Two-byte tag
                    tagBytes = pdolData.copyOfRange(offset, offset + 2)
                    tagLength = pdolData[offset + 2].toInt() and 0xFF
                    offset += 3
                } else {
                    // One-byte tag
                    tagBytes = pdolData.copyOfRange(offset, offset + 1)
                    tagLength = pdolData[offset + 1].toInt() and 0xFF
                    offset += 2
                }

                val tagHex = Utils.bytesToHex(tagBytes)
                val pdolTag = PdolTag(tagHex, tagLength)
                pdolTags.add(pdolTag)
                
                Log.d(TAG, "📌 PDOL Tag: $tagHex, Length: $tagLength")
            }

            Log.d(TAG, "✅ Extracted ${pdolTags.size} PDOL tags from live card")
            return pdolTags

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error extracting PDOL", e)
            return null
        }
    }

    /**
     * Build terminal data from dynamic PDOL parsing
     */
    fun buildTerminalDataFromPdol(pdolTags: List<PdolTag>): ByteArray {
        Log.d(TAG, "🔧 Building terminal data from ${pdolTags.size} PDOL tags...")
        
        val terminalData = ByteArrayOutputStream()
        
        for (pdolTag in pdolTags) {
            val tagData = getStandardTerminalValue(pdolTag.tag, pdolTag.length)
            terminalData.write(tagData)
            
            Log.d(TAG, "🏷️  ${pdolTag.tag} (${pdolTag.length} bytes) = ${Utils.bytesToHex(tagData)}")
        }

        val result = terminalData.toByteArray()
        Log.d(TAG, "✅ Terminal data: ${Utils.bytesToHex(result)} (${result.size} bytes)")
        return result
    }

    /**
     * Get standard EMV terminal values (NOT hardcoded transaction data)
     */
    private fun getStandardTerminalValue(tagHex: String, length: Int): ByteArray {
        return when (tagHex.uppercase()) {
            "9F02" -> buildAmountAuthorized(length)
            "9F03" -> buildAmountOther(length)
            "5F2A" -> buildTransactionCurrencyCode(length)
            "9F1A" -> buildTerminalCountryCode(length)
            "9A" -> buildTransactionDate(length)
            "9F21" -> buildTransactionTime(length)
            "9C" -> buildTransactionType(length)
            "9F33" -> buildTerminalCapabilities(length)
            "9F40" -> buildAdditionalTerminalCapabilities(length)
            "9F35" -> buildTerminalType(length)
            "9F1E" -> buildInterfaceDeviceSerialNumber(length)
            "95" -> buildTerminalVerificationResults(length)
            "9F34" -> buildCvmResults(length)
            "9F66" -> buildTtq(length)
            "9B" -> buildTransactionStatusInformation(length)
            "9F37" -> buildUnpredictableNumber(length)
            "9F41" -> buildTransactionSequenceCounter(length)
            "9F7A" -> buildApplicationVersionNumber(length)
            "9F36" -> buildApplicationTransactionCounter(length)
            else -> {
                Log.w(TAG, "⚠️ Unknown PDOL tag $tagHex, using zeros")
                ByteArray(length) { 0x00 }
            }
        }
    }

    private fun buildAmountAuthorized(length: Int): ByteArray {
        val amount = "000000001000" // Standard $10.00
        return Utils.hexStringToByteArray(amount.padStart(length * 2, '0'))
    }

    private fun buildAmountOther(length: Int): ByteArray {
        return ByteArray(length) { 0x00 }
    }

    private fun buildTransactionCurrencyCode(length: Int): ByteArray {
        return Utils.hexStringToByteArray("0840".padStart(length * 2, '0')) // USD
    }

    private fun buildTerminalCountryCode(length: Int): ByteArray {
        return Utils.hexStringToByteArray("0840".padStart(length * 2, '0')) // USA
    }

    private fun buildTransactionDate(length: Int): ByteArray {
        val cal = java.util.Calendar.getInstance()
        val year = (cal.get(java.util.Calendar.YEAR) % 100).toString().padStart(2, '0')
        val month = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
        val day = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val dateStr = year + month + day
        return Utils.hexStringToByteArray(dateStr.padStart(length * 2, '0'))
    }

    private fun buildTransactionTime(length: Int): ByteArray {
        val cal = java.util.Calendar.getInstance()
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val minute = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
        val second = cal.get(java.util.Calendar.SECOND).toString().padStart(2, '0')
        val timeStr = hour + minute + second
        return Utils.hexStringToByteArray(timeStr.padStart(length * 2, '0'))
    }

    private fun buildTransactionType(length: Int): ByteArray {
        return Utils.hexStringToByteArray("00".padStart(length * 2, '0')) // Goods/services
    }

    private fun buildTerminalCapabilities(length: Int): ByteArray {
        return Utils.hexStringToByteArray("E0E1C8".padStart(length * 2, '0'))
    }

    private fun buildAdditionalTerminalCapabilities(length: Int): ByteArray {
        return Utils.hexStringToByteArray("6000F0A001".padStart(length * 2, '0'))
    }

    private fun buildTerminalType(length: Int): ByteArray {
        return Utils.hexStringToByteArray("22".padStart(length * 2, '0')) // Attended online
    }

    private fun buildInterfaceDeviceSerialNumber(length: Int): ByteArray {
        return Utils.hexStringToByteArray("0000000000000000".padStart(length * 2, '0'))
    }

    private fun buildTerminalVerificationResults(length: Int): ByteArray {
        return ByteArray(length) { 0x00 }
    }

    private fun buildCvmResults(length: Int): ByteArray {
        return Utils.hexStringToByteArray("000000".padStart(length * 2, '0'))
    }

    private fun buildTtq(length: Int): ByteArray {
        val ttqHex = when (currentTtqWorkflow) {
            TtqWorkflow.STANDARD -> "27000000"
            TtqWorkflow.ENHANCED -> "B7604000"
            TtqWorkflow.SIMPLIFIED -> "A0000000"
            TtqWorkflow.ADVANCED -> "F0204000"
        }
        return Utils.hexStringToByteArray(ttqHex.padStart(length * 2, '0'))
    }

    private fun buildTransactionStatusInformation(length: Int): ByteArray {
        return Utils.hexStringToByteArray("0000".padStart(length * 2, '0'))
    }

    private fun buildUnpredictableNumber(length: Int): ByteArray {
        val random = ByteArray(length)
        java.security.SecureRandom().nextBytes(random)
        return random
    }

    private fun buildTransactionSequenceCounter(length: Int): ByteArray {
        return Utils.hexStringToByteArray("00000001".padStart(length * 2, '0'))
    }

    private fun buildApplicationVersionNumber(length: Int): ByteArray {
        return Utils.hexStringToByteArray("01".padStart(length * 2, '0'))
    }

    private fun buildApplicationTransactionCounter(length: Int): ByteArray {
        return Utils.hexStringToByteArray("0001".padStart(length * 2, '0'))
    }

    /**
     * Build fallback terminal data when no PDOL found
     */
    fun buildFallbackTerminalData(): ByteArray {
        Log.w(TAG, "🔧 Building fallback terminal data (no PDOL)")
        
        val terminalData = ByteArrayOutputStream()
        terminalData.write(buildAmountAuthorized(6))
        terminalData.write(buildTransactionCurrencyCode(2))
        terminalData.write(buildTerminalCountryCode(2))
        terminalData.write(buildTransactionDate(3))
        terminalData.write(buildTransactionTime(3))
        terminalData.write(buildTtq(4))
        
        val result = terminalData.toByteArray()
        Log.d(TAG, "✅ Fallback data: ${Utils.bytesToHex(result)} (${result.size} bytes)")
        return result
    }

    /**
     * Parse EMV TLV data comprehensively
     */
    fun parseEmvTlvData(data: ByteArray): Map<String, String> {
        val tlvMap = mutableMapOf<String, String>()
        
        try {
            var offset = 0
            while (offset < data.size) {
                val tagStart = offset
                if ((data[offset].toInt() and 0x1F) == 0x1F) {
                    offset += 2 // Multi-byte tag
                } else {
                    offset += 1 // Single-byte tag
                }
                val tag = Utils.bytesToHex(data.copyOfRange(tagStart, offset))

                val lengthByte = data[offset].toInt() and 0xFF
                offset++
                
                val length: Int
                if ((lengthByte and 0x80) != 0) {
                    val lengthBytes = lengthByte and 0x7F
                    length = when (lengthBytes) {
                        1 -> data[offset].toInt() and 0xFF
                        2 -> ((data[offset].toInt() and 0xFF) shl 8) or (data[offset + 1].toInt() and 0xFF)
                        else -> 0
                    }
                    offset += lengthBytes
                } else {
                    length = lengthByte
                }

                if (length > 0 && offset + length <= data.size) {
                    val value = Utils.bytesToHex(data.copyOfRange(offset, offset + length))
                    tlvMap[tag] = value
                    offset += length
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error parsing EMV TLV data", e)
        }

        return tlvMap
    }

    /**
     * Helper to find tag in response data
     */
    private fun findTagInResponse(data: ByteArray, tagHex: String): Int {
        val tagBytes = Utils.hexStringToByteArray(tagHex)
        
        for (i in 0..data.size - tagBytes.size) {
            var match = true
            for (j in tagBytes.indices) {
                if (data[i + j] != tagBytes[j]) {
                    match = false
                    break
                }
            }
            if (match) return i
        }
        
        return -1
    }

    data class PdolTag(val tag: String, val length: Int)
}