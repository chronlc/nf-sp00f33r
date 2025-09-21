package com.mag_sp00f.app.hardware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

/**
 * Android Bluetooth HC-06 Adapter for PN532
 * 
 * Connects to PN532 via Bluetooth HC-06 adapter directly from Android
 * HC-06 Specs: SSID "PN532", PIN "1234", MAC: 00:14:03:05:5C:CB
 * 
 * Following naming_scheme.md conventions
 */
class AndroidBluetoothHC06Adapter(
    private val context: Context,
    private val targetMacAddress: String = "00:14:03:05:5C:CB"
) : PN532Adapter {
    
    companion object {
        private const val TAG = "AndroidBluetoothHC06"
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
    
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var targetDevice: BluetoothDevice? = null
    private var isConnected = false
    
    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.tag(TAG).d("Connecting to PN532 HC-06 at $targetMacAddress")
            
            // Get Bluetooth adapter
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Timber.tag(TAG).e("Bluetooth not supported on this device")
                return@withContext false
            }
            
            if (!bluetoothAdapter!!.isEnabled) {
                Timber.tag(TAG).w("Bluetooth is disabled")
                return@withContext false
            }
            
            // Find target device
            targetDevice = bluetoothAdapter!!.getRemoteDevice(targetMacAddress)
            if (targetDevice == null) {
                Timber.tag(TAG).e("HC-06 device not found: $targetMacAddress")
                return@withContext false
            }
            
            // Create socket and connect
            bluetoothSocket = targetDevice!!.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothAdapter!!.cancelDiscovery() // Cancel discovery for better performance
            
            bluetoothSocket!!.connect()
            isConnected = true
            
            Timber.tag(TAG).i("Connected to PN532 HC-06: ${targetDevice!!.name}")
            true
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to connect to HC-06")
            disconnect()
            false
        }
    }
    
    override fun disconnect() {
        try {
            isConnected = false
            bluetoothSocket?.close()
            bluetoothSocket = null
            targetDevice = null
            Timber.tag(TAG).d("Disconnected from PN532 HC-06")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error disconnecting from HC-06")
        }
    }
    
    override suspend fun sendApduCommand(command: ByteArray): ByteArray? = withContext(Dispatchers.IO) {
        try {
            if (!isConnected || bluetoothSocket == null) {
                Timber.tag(TAG).w("Not connected to HC-06")
                return@withContext null
            }
            
            val socket = bluetoothSocket!!
            
            // Send PN532 frame with APDU
            val pn532Frame = buildPN532Frame(command)
            socket.outputStream.write(pn532Frame)
            socket.outputStream.flush()
            
            Timber.tag(TAG).d("TX: ${pn532Frame.toHexString()}")
            
            // Read response
            val response = readPN532Response(socket)
            if (response != null) {
                Timber.tag(TAG).d("RX: ${response.toHexString()}")
            }
            
            response
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "APDU transmission failed")
            null
        }
    }
    
    private fun buildPN532Frame(apdu: ByteArray): ByteArray {
        // PN532 InDataExchange frame for card emulation
        val header = byteArrayOf(
            0x00, 0x00, 0xFF.toByte(), // Preamble and start
            0x04, 0xFC.toByte(),        // Length and length checksum
            0xD4.toByte(), 0x40,        // TFI and InDataExchange command
            0x01                        // Target number
        )
        
        val frame = header + apdu
        val checksum = (0x100 - (frame.drop(3).sum() and 0xFF)).toByte()
        
        return frame + checksum + 0x00.toByte()
    }
    
    private fun readPN532Response(socket: BluetoothSocket): ByteArray? {
        return try {
            val inputStream = socket.inputStream
            val buffer = ByteArray(256)
            
            // Read with timeout
            var totalRead = 0
            val startTime = System.currentTimeMillis()
            
            while (totalRead < 6 && (System.currentTimeMillis() - startTime) < 5000) {
                if (inputStream.available() > 0) {
                    val bytesRead = inputStream.read(buffer, totalRead, buffer.size - totalRead)
                    if (bytesRead > 0) {
                        totalRead += bytesRead
                    }
                } else {
                    Thread.sleep(10)
                }
            }
            
            if (totalRead >= 6) {
                // Extract APDU response (skip PN532 headers)
                val responseLength = buffer[3].toInt() and 0xFF
                if (responseLength > 0 && totalRead >= responseLength + 6) {
                    buffer.copyOfRange(6, 6 + responseLength - 3)
                } else {
                    byteArrayOf(0x90.toByte(), 0x00.toByte()) // Success response
                }
            } else {
                null
            }
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to read PN532 response")
            null
        }
    }
    
    override fun isConnected(): Boolean = isConnected
    
    override fun getConnectionInfo(): String {
        return if (isConnected) {
            "Bluetooth HC-06: ${targetDevice?.name ?: "PN532"} ($targetMacAddress)"
        } else {
            "Bluetooth HC-06: Disconnected"
        }
    }
    
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }
}