package com.mag_sp00f.app.hardware

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

/**
 * PN532 Manager - Dual Connectivity Support
 * 
 * Manages dual PN532 connectivity:
 * - USB Serial (/dev/ttyUSB0)  
 * - Bluetooth HC-06 (/dev/rfcomm1)
 * 
 * Features:
 * - Automatic connection detection
 * - Runtime switching between USB/Bluetooth
 * - Integration with OnePlus 11 HCE testing
 * - VISA MSD workflow automation
 */
class PN532Manager(private val context: Context) {
    
    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus
    
    private val _connectionMode = MutableLiveData<ConnectionMode>()
    val connectionMode: LiveData<ConnectionMode> = _connectionMode
    
    private var usbAdapter: AndroidUSBSerialAdapter? = null
    private var bluetoothAdapter: AndroidBluetoothHC06Adapter? = null
    private var activeAdapter: PN532Adapter? = null
    
    init {
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        _connectionMode.value = ConnectionMode.OFF
        initializeAdapters()
    }
    
    private fun initializeAdapters() {
        try {
            usbAdapter = AndroidUSBSerialAdapter(context)
            bluetoothAdapter = AndroidBluetoothHC06Adapter(context, "00:14:03:05:5C:CB")
            Timber.d("Android PN532 adapters initialized")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Android PN532 adapters")
        }
    }
    
    /**
     * Connect to PN532 using specified mode
     */
    suspend fun connect(mode: ConnectionMode): Boolean {
        return try {
            disconnect() // Disconnect any existing connection
            
            val adapter = when (mode) {
                ConnectionMode.USB -> {
                    Timber.d("Connecting to PN532 via Android USB OTG")
                    usbAdapter
                }
                ConnectionMode.BLUETOOTH -> {
                    Timber.d("Connecting to PN532 via Android Bluetooth HC-06")
                    bluetoothAdapter
                }
                ConnectionMode.OFF -> {
                    Timber.d("PN532 connection disabled")
                    null
                }
            }
            
            if (adapter != null && adapter.connect()) {
                activeAdapter = adapter
                _connectionMode.value = mode
                _connectionStatus.value = ConnectionStatus.CONNECTED
                Timber.i("PN532 connected via $mode")
                true
            } else {
                _connectionStatus.value = ConnectionStatus.ERROR
                Timber.w("Failed to connect PN532 via $mode")
                false
            }
            
        } catch (e: Exception) {
            _connectionStatus.value = ConnectionStatus.ERROR
            Timber.e(e, "PN532 connection error")
            false
        }
    }
    
    /**
     * Disconnect from PN532
     */
    fun disconnect() {
        try {
            activeAdapter?.disconnect()
            activeAdapter = null
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
            _connectionMode.value = ConnectionMode.OFF
            Timber.d("PN532 disconnected")
        } catch (e: Exception) {
            Timber.e(e, "Error disconnecting PN532")
        }
    }
    
    /**
     * Send APDU command to PN532 terminal
     */
    suspend fun sendApduCommand(command: ByteArray): ByteArray? {
        return try {
            val response = activeAdapter?.sendApduCommand(command)
            if (response != null) {
                Timber.d("APDU TX: ${command.toHexString()}")
                Timber.d("APDU RX: ${response.toHexString()}")
            } else {
                Timber.w("No APDU response received")
            }
            response
        } catch (e: Exception) {
            Timber.e(e, "APDU transmission error")
            null
        }
    }
    
    /**
     * Test VISA PPSE command for validation
     */
    suspend fun testVisaPpseFlow(): Boolean {
        val ppseCommand = "00A404000E325041592E5359532E444446303100".hexToByteArray()
        val response = sendApduCommand(ppseCommand)
        return response != null && response.size > 2 && 
               response[response.size - 2] == 0x90.toByte() && 
               response[response.size - 1] == 0x00.toByte()
    }
    
    /**
     * Get current connection info
     */
    fun getConnectionInfo(): String {
        return when (_connectionMode.value) {
            ConnectionMode.USB -> "Android USB OTG"
            ConnectionMode.BLUETOOTH -> "Android Bluetooth HC-06 (00:14:03:05:5C:CB)"
            ConnectionMode.OFF -> "Disconnected"
            else -> "Unknown"
        }
    }
    
    enum class ConnectionMode {
        USB, BLUETOOTH, OFF
    }
    
    enum class ConnectionStatus {
        DISCONNECTED, CONNECTING, CONNECTED, ERROR
    }
    
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }
    
    private fun String.hexToByteArray(): ByteArray {
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}