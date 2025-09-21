package com.mag_sp00f.app.ui.emulation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mag_sp00f.app.hardware.PN532Manager
import com.mag_sp00f.app.python.PythonBackend
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Emulation ViewModel
 * 
 * Manages emulation state and PN532 connectivity
 * Following naming_scheme.md conventions
 */
class EmulationViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "EmulationViewModel"
    }
    
    private val pn532Manager = PN532Manager(application)
    private val pythonBackend = PythonBackend(application)
    
    private val _connectionStatus = MutableLiveData<PN532Manager.ConnectionStatus>()
    val connectionStatus: LiveData<PN532Manager.ConnectionStatus> = _connectionStatus
    
    private val _connectionMode = MutableLiveData<PN532Manager.ConnectionMode>()
    val connectionMode: LiveData<PN532Manager.ConnectionMode> = _connectionMode
    
    private val _emulationStatus = MutableLiveData<EmulationStatus>()
    val emulationStatus: LiveData<EmulationStatus> = _emulationStatus
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    init {
        // Initialize with default values
        _connectionStatus.value = PN532Manager.ConnectionStatus.DISCONNECTED
        _connectionMode.value = PN532Manager.ConnectionMode.OFF
        _emulationStatus.value = EmulationStatus.STOPPED
        _errorMessage.value = ""
        
        // Observe PN532Manager status
        observePN532Manager()
        
        // Initialize Python backend
        initializePythonBackend()
    }
    
    private fun observePN532Manager() {
        pn532Manager.connectionStatus.observeForever { status ->
            _connectionStatus.postValue(status)
        }
        
        pn532Manager.connectionMode.observeForever { mode ->
            _connectionMode.postValue(mode)
        }
    }
    
    private fun initializePythonBackend() {
        viewModelScope.launch {
            try {
                val initialized = pythonBackend.initializePythonEngine()
                if (!initialized) {
                    _errorMessage.postValue("Failed to initialize Python backend")
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error initializing Python backend")
                _errorMessage.postValue("Python backend error: ${e.message}")
            }
        }
    }
    
    /**
     * Set PN532 connection mode
     */
    suspend fun setConnectionMode(mode: PN532Manager.ConnectionMode) {
        try {
            Timber.tag(TAG).d("Setting connection mode: $mode")
            val connected = pn532Manager.connect(mode)
            
            if (!connected && mode != PN532Manager.ConnectionMode.OFF) {
                _errorMessage.postValue("Failed to connect PN532 in $mode mode")
            }
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error setting connection mode")
            _errorMessage.postValue("Connection error: ${e.message}")
        }
    }
    
    /**
     * Start emulation
     */
    suspend fun startEmulation() {
        try {
            if (_connectionStatus.value != PN532Manager.ConnectionStatus.CONNECTED) {
                _errorMessage.postValue("PN532 not connected. Please connect first.")
                return
            }
            
            Timber.tag(TAG).d("Starting emulation")
            _emulationStatus.postValue(EmulationStatus.RUNNING)
            
            // TODO: Implement actual emulation start logic
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error starting emulation")
            _emulationStatus.postValue(EmulationStatus.ERROR)
            _errorMessage.postValue("Emulation start error: ${e.message}")
        }
    }
    
    /**
     * Stop emulation
     */
    suspend fun stopEmulation() {
        try {
            Timber.tag(TAG).d("Stopping emulation")
            _emulationStatus.postValue(EmulationStatus.STOPPED)
            
            // TODO: Implement actual emulation stop logic
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error stopping emulation")
            _errorMessage.postValue("Emulation stop error: ${e.message}")
        }
    }
    
    /**
     * Test VISA PPSE flow
     */
    suspend fun testVisaPpseFlow() {
        try {
            if (_connectionStatus.value != PN532Manager.ConnectionStatus.CONNECTED) {
                _errorMessage.postValue("PN532 not connected")
                return
            }
            
            Timber.tag(TAG).d("Testing VISA PPSE flow")
            val result = pn532Manager.testVisaPpseFlow()
            
            if (result) {
                _errorMessage.postValue("VISA PPSE test: SUCCESS")
            } else {
                _errorMessage.postValue("VISA PPSE test: FAILED")
            }
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error testing VISA PPSE")
            _errorMessage.postValue("VISA PPSE test error: ${e.message}")
        }
    }
    
    /**
     * Test PN532 connection
     */
    suspend fun testPN532Connection() {
        try {
            val connectionInfo = pn532Manager.getConnectionInfo()
            _errorMessage.postValue("Connection: $connectionInfo")
            
            Timber.tag(TAG).d("PN532 connection test: $connectionInfo")
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error testing PN532 connection")
            _errorMessage.postValue("Connection test error: ${e.message}")
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        
        // Cleanup resources
        pn532Manager.disconnect()
        pythonBackend.cleanup()
        
        Timber.tag(TAG).d("EmulationViewModel cleared")
    }
}