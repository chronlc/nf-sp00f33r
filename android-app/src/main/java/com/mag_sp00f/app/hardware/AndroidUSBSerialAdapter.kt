package com.mag_sp00f.app.hardware

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Android USB OTG Adapter for PN532
 * 
 * Connects to PN532 via USB OTG directly from Android device
 * Supports PN532 breakout boards with USB-to-Serial converters
 * 
 * Following naming_scheme.md conventions
 */
class AndroidUSBSerialAdapter(private val context: Context) : PN532Adapter {
    
    companion object {
        private const val TAG = "AndroidUSBSerial"
        
        // Common USB-to-Serial converter vendor/product IDs
        private val SUPPORTED_DEVICES = mapOf(
            0x0403 to listOf(0x6001, 0x6010, 0x6011), // FTDI
            0x067B to listOf(0x2303),                   // Prolific PL2303
            0x10C4 to listOf(0xEA60),                   // Silicon Labs CP210x
            0x1A86 to listOf(0x7523)                    // QinHeng CH340
        )
    }
    
    private var usbManager: UsbManager? = null
    private var usbDevice: UsbDevice? = null
    private var usbConnection: UsbDeviceConnection? = null
    private var isConnected = false
    
    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.tag(TAG).d("Scanning for PN532 USB devices")
            
            usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val deviceList = usbManager!!.deviceList
            
            // Find compatible USB-to-Serial device
            var targetDevice: UsbDevice? = null
            for ((deviceName, device) in deviceList) {
                val vendorId = device.vendorId
                val productId = device.productId
                
                Timber.tag(TAG).d("Found USB device: $deviceName (VID: ${vendorId.toString(16)}, PID: ${productId.toString(16)})")
                
                if (SUPPORTED_DEVICES.containsKey(vendorId) && 
                    SUPPORTED_DEVICES[vendorId]?.contains(productId) == true) {
                    targetDevice = device
                    Timber.tag(TAG).i("Compatible PN532 USB device found: $deviceName")
                    break
                }
            }
            
            if (targetDevice == null) {
                Timber.tag(TAG).w("No compatible PN532 USB devices found")
                return@withContext false
            }
            
            // Request permission and connect
            if (!usbManager!!.hasPermission(targetDevice)) {
                Timber.tag(TAG).w("USB permission not granted for device")
                return@withContext false
            }
            
            usbConnection = usbManager!!.openDevice(targetDevice)
            if (usbConnection == null) {
                Timber.tag(TAG).e("Failed to open USB device connection")
                return@withContext false
            }
            
            // Claim interface
            val usbInterface = targetDevice.getInterface(0)
            if (!usbConnection!!.claimInterface(usbInterface, true)) {
                Timber.tag(TAG).e("Failed to claim USB interface")
                usbConnection!!.close()
                return@withContext false
            }
            
            usbDevice = targetDevice
            isConnected = true
            
            // Initialize PN532 with basic configuration
            initializePN532()
            
            Timber.tag(TAG).i("Connected to PN532 via USB OTG")
            true
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to connect via USB OTG")
            disconnect()
            false
        }
    }
    
    override fun disconnect() {
        try {
            isConnected = false
            usbConnection?.close()
            usbConnection = null
            usbDevice = null
            Timber.tag(TAG).d("Disconnected from PN532 USB")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error disconnecting USB")
        }
    }
    
    override suspend fun sendApduCommand(command: ByteArray): ByteArray? = withContext(Dispatchers.IO) {
        try {
            if (!isConnected || usbConnection == null) {
                Timber.tag(TAG).w("USB not connected")
                return@withContext null
            }
            
            // Build PN532 InDataExchange frame
            val pn532Frame = buildPN532Frame(command)
            
            // Send via USB bulk transfer
            val endpoint = usbDevice!!.getInterface(0).getEndpoint(0) // OUT endpoint
            val bytesWritten = usbConnection!!.bulkTransfer(endpoint, pn532Frame, pn532Frame.size, 5000)
            
            if (bytesWritten != pn532Frame.size) {
                Timber.tag(TAG).w("USB write incomplete: $bytesWritten/${pn532Frame.size}")
                return@withContext null
            }
            
            Timber.tag(TAG).d("USB TX: ${pn532Frame.toHexString()}")
            
            // Read response
            val response = readUSBResponse()
            if (response != null) {
                Timber.tag(TAG).d("USB RX: ${response.toHexString()}")
            }
            
            response
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "USB APDU transmission failed")
            null
        }
    }
    
    private suspend fun initializePN532() = withContext(Dispatchers.IO) {
        try {
            // Send GetFirmwareVersion to wake up PN532
            val wakeupFrame = byteArrayOf(
                0x00, 0x00, 0xFF.toByte(), 0x02, 0xFE.toByte(),
                0xD4.toByte(), 0x02, 0x2A.toByte(), 0x00
            )
            
            val endpoint = usbDevice!!.getInterface(0).getEndpoint(0)
            usbConnection!!.bulkTransfer(endpoint, wakeupFrame, wakeupFrame.size, 1000)
            
            // Small delay for PN532 initialization
            kotlinx.coroutines.delay(100)
            
            Timber.tag(TAG).d("PN532 initialized via USB")
            
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "PN532 USB initialization warning")
        }
    }
    
    private fun buildPN532Frame(apdu: ByteArray): ByteArray {
        // PN532 InDataExchange frame
        val header = byteArrayOf(
            0x00, 0x00, 0xFF.toByte(),  // Preamble and start
            (apdu.size + 3).toByte(),   // Length
            (0x100 - (apdu.size + 3)).toByte(), // Length checksum
            0xD4.toByte(), 0x40,        // TFI and InDataExchange
            0x01                        // Target number
        )
        
        val frame = header + apdu
        val checksum = (0x100 - (frame.drop(5).sum() and 0xFF)).toByte()
        
        return frame + checksum + 0x00.toByte()
    }
    
    private fun readUSBResponse(): ByteArray? {
        return try {
            val buffer = ByteArray(256)
            val endpoint = usbDevice!!.getInterface(0).getEndpoint(1) // IN endpoint
            
            val bytesRead = usbConnection!!.bulkTransfer(endpoint, buffer, buffer.size, 5000)
            
            if (bytesRead > 6) {
                // Extract APDU response (skip PN532 headers)
                val responseLength = buffer[3].toInt() and 0xFF
                if (responseLength > 3) {
                    buffer.copyOfRange(7, 7 + responseLength - 3)
                } else {
                    byteArrayOf(0x90.toByte(), 0x00.toByte()) // Success
                }
            } else {
                Timber.tag(TAG).w("USB response too short: $bytesRead bytes")
                null
            }
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to read USB response")
            null
        }
    }
    
    override fun isConnected(): Boolean = isConnected
    
    override fun getConnectionInfo(): String {
        return if (isConnected && usbDevice != null) {
            "USB OTG: ${usbDevice!!.deviceName} (VID: ${usbDevice!!.vendorId.toString(16)}, PID: ${usbDevice!!.productId.toString(16)})"
        } else {
            "USB OTG: Disconnected"
        }
    }
    
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }
}