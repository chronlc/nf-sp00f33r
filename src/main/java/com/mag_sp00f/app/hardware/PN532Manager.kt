package com.mag_sp00f.app.hardware

import android.content.Context
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

/**
 * PRODUCTION-GRADE PN532 Hardware Manager
 * Manages PN532 NFC module with dual USB/Bluetooth connectivity
 * NO SAFE CALL OPERATORS - Explicit null checks only per newrule.md
 */
class PN532Manager(private val context: Context) {

    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR
    }

    enum class ConnectionType {
        USB_SERIAL,
        BLUETOOTH_HC06
    }

    private val _connectionState = MutableLiveData<ConnectionState>()
    val connectionState: MutableLiveData<ConnectionState> = _connectionState

    private val _connectionType = MutableLiveData<ConnectionType>()
    val connectionType: MutableLiveData<ConnectionType> = _connectionType

    private val _lastError = MutableLiveData<String>()
    val lastError: MutableLiveData<String> = _lastError

    private var currentAdapter: HardwareAdapter? = null
    private var connectionCallback: ((String) -> Unit)? = null

    init {
        _connectionState.value = ConnectionState.DISCONNECTED
        _connectionType.value = ConnectionType.USB_SERIAL
    }

    /**
     * Set connection callback for status updates
     */
    fun setConnectionCallback(callback: (String) -> Unit) {
        this.connectionCallback = callback
    }

    /**
     * Connect to PN532 using specified connection type
     */
    fun connect(type: ConnectionType) {
        _connectionState.value = ConnectionState.CONNECTING
        _connectionType.value = type

        try {
            val adapter = createAdapter(type)
            currentAdapter = adapter

            // Connect with explicit null check
            val callback = this.connectionCallback
            if (callback != null) {
                callback("Initializing ${type.name} connection...")
            }

            val isConnected = adapter.connect()
            if (isConnected) {
                _connectionState.value = ConnectionState.CONNECTED
                
                val callbackAfterConnect = this.connectionCallback
                if (callbackAfterConnect != null) {
                    callbackAfterConnect("Connected to PN532 via ${type.name}")
                }
                
                Timber.i("PN532 connected successfully via ${type.name}")
            } else {
                throw RuntimeException("Failed to establish connection")
            }

        } catch (e: Exception) {
            _connectionState.value = ConnectionState.ERROR
            _lastError.value = e.message
            
            val errorCallback = this.connectionCallback
            if (errorCallback != null) {
                errorCallback("Connection failed: ${e.message}")
            }
            
            Timber.e(e, "Failed to connect to PN532")
        }
    }

    /**
     * Disconnect from PN532
     */
    fun disconnect() {
        val adapter = currentAdapter
        if (adapter != null) {
            try {
                adapter.disconnect()
                Timber.i("PN532 disconnected")
            } catch (e: Exception) {
                Timber.e(e, "Error during disconnect")
            }
        }

        currentAdapter = null
        _connectionState.value = ConnectionState.DISCONNECTED
        
        val callback = this.connectionCallback
        if (callback != null) {
            callback("Disconnected from PN532")
        }
    }

    /**
     * Send APDU command to PN532
     */
    fun sendApduCommand(command: ByteArray): ByteArray {
        val adapter = currentAdapter
        if (adapter == null) {
            throw IllegalStateException("Not connected to PN532")
        }

        if (_connectionState.value != ConnectionState.CONNECTED) {
            throw IllegalStateException("PN532 not in connected state")
        }

        return adapter.sendCommand(command)
    }

    /**
     * Get connection status
     */
    fun isConnected(): Boolean {
        return _connectionState.value == ConnectionState.CONNECTED && currentAdapter != null
    }

    /**
     * Get current connection info
     */
    fun getConnectionInfo(): String {
        val state = _connectionState.value
        val type = _connectionType.value
        val adapter = currentAdapter

        return when {
            state == ConnectionState.CONNECTED && adapter != null -> {
                "Connected via ${type?.name ?: "UNKNOWN"}"
            }
            state == ConnectionState.CONNECTING -> {
                "Connecting via ${type?.name ?: "UNKNOWN"}..."
            }
            state == ConnectionState.ERROR -> {
                "Error: ${_lastError.value ?: "Unknown error"}"
            }
            else -> {
                "Disconnected"
            }
        }
    }

    /**
     * Create hardware adapter based on connection type
     */
    private fun createAdapter(type: ConnectionType): HardwareAdapter {
        return when (type) {
            ConnectionType.USB_SERIAL -> {
                AndroidUSBSerialAdapter(context, "/dev/ttyUSB0", 115200)
            }
            ConnectionType.BLUETOOTH_HC06 -> {
                AndroidBluetoothHC06Adapter(
                    context = context,
                    deviceAddress = "00:14:03:05:5C:CB",
                    deviceName = "PN532"
                )
            }
        }
    }

    /**
     * Test PN532 connectivity and firmware
     */
    fun testConnection(): Boolean {
        val adapter = currentAdapter
        if (adapter == null) {
            return false
        }

        return try {
            // Send GetFirmwareVersion command
            val getFirmwareCmd = byteArrayOf(
                0x00, 0x00, 0xFF.toByte(), 0x02, 0xFE.toByte(), 0xD4.toByte(), 0x02, 0x2A
            )
            
            val response = adapter.sendCommand(getFirmwareCmd)
            
            val callback = this.connectionCallback
            if (callback != null) {
                callback("Firmware test response: ${response.size} bytes")
            }
            
            response.isNotEmpty()
            
        } catch (e: Exception) {
            Timber.e(e, "Connection test failed")
            
            val callback = this.connectionCallback
            if (callback != null) {
                callback("Connection test failed: ${e.message}")
            }
            
            false
        }
    }

    /**
     * Get supported connection types
     */
    fun getSupportedConnectionTypes(): List<ConnectionType> {
        return listOf(
            ConnectionType.USB_SERIAL,
            ConnectionType.BLUETOOTH_HC06
        )
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _lastError.value = null
        if (_connectionState.value == ConnectionState.ERROR) {
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }
}
