package com.mag_sp00f.app.hardware

/**
 * PN532 Adapter Interface
 * 
 * Common interface for USB and Bluetooth PN532 connectivity
 * Following naming_scheme.md conventions
 */
interface PN532Adapter {
    suspend fun connect(): Boolean
    fun disconnect()
    suspend fun sendApduCommand(command: ByteArray): ByteArray?
    fun isConnected(): Boolean
    fun getConnectionInfo(): String
}