package com.mag_sp00f.app.hardware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import timber.log.Timber

/**
 * Android Bluetooth HC-06 adapter for PN532 communication
 * Per newrule.md: Production-grade implementation with HardwareAdapter interface
 */
class AndroidBluetoothHC06Adapter(
    private val context: Context,
    private val deviceAddress: String,
    private val deviceName: String
) : HardwareAdapter {
    
    companion object {
        private const val TAG = "AndroidBluetoothHC06Adapter"
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
    
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var isConnected = false
    
    override fun connect(): Boolean {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Timber.e("$TAG Bluetooth not supported")
                return false
            }
            
            if (!bluetoothAdapter!!.isEnabled) {
                Timber.e("$TAG Bluetooth not enabled")
                return false
            }
            
            // Find HC-06 device
            val pairedDevices = bluetoothAdapter!!.bondedDevices
            bluetoothDevice = pairedDevices.find { device ->
                device.name == deviceName || device.address == deviceAddress
            }
            
            if (bluetoothDevice == null) {
                Timber.e("$TAG HC-06 device not found: $deviceName ($deviceAddress)")
                return false
            }
            
            // Create socket and connect
            bluetoothSocket = bluetoothDevice!!.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothSocket!!.connect()
            
            inputStream = bluetoothSocket!!.inputStream
            outputStream = bluetoothSocket!!.outputStream
            
            isConnected = true
            Timber.d("$TAG Connected to HC-06: ${bluetoothDevice!!.name} (${bluetoothDevice!!.address})")
            return true
            
        } catch (e: IOException) {
            Timber.e("$TAG Connection failed: ${e.message}")
            disconnect()
            return false
        }
    }
    
    override fun disconnect() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Timber.e("$TAG Disconnect error: ${e.message}")
        } finally {
            inputStream = null
            outputStream = null
            bluetoothSocket = null
            isConnected = false
            Timber.d("$TAG Disconnected from HC-06")
        }
    }
    
    override fun sendCommand(command: ByteArray): ByteArray {
        if (!isConnected || outputStream == null || inputStream == null) {
            Timber.e("$TAG Not connected")
            return byteArrayOf()
        }
        
        return try {
            // Send command
            outputStream!!.write(command)
            outputStream!!.flush()
            
            // Wait for response
            Thread.sleep(50)
            
            // Read response with timeout
            val buffer = ByteArray(1024)
            val bytesRead = inputStream!!.read(buffer)
            
            if (bytesRead > 0) {
                val response = buffer.copyOf(bytesRead)
                Timber.d("$TAG Command sent: ${command.joinToString("") { "%02X".format(it) }}")
                Timber.d("$TAG Response: ${response.joinToString("") { "%02X".format(it) }}")
                response
            } else {
                Timber.w("$TAG No response received")
                byteArrayOf()
            }
            
        } catch (e: IOException) {
            Timber.e("$TAG Command failed: ${e.message}")
            byteArrayOf()
        } catch (e: InterruptedException) {
            Timber.e("$TAG Command interrupted: ${e.message}")
            byteArrayOf()
        }
    }
    
    override fun isConnected(): Boolean = isConnected
    
    override fun getDeviceInfo(): String {
        return if (isConnected && bluetoothDevice != null) {
            "HC-06 Bluetooth: ${bluetoothDevice!!.name} (${bluetoothDevice!!.address})"
        } else {
            "HC-06 Bluetooth: $deviceName ($deviceAddress) - Disconnected"
        }
    }
    
    override fun getConnectionType(): String = "BLUETOOTH_HC06"
}
