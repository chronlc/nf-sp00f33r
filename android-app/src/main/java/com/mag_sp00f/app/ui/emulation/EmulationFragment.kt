package com.mag_sp00f.app.ui.emulation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.mag_sp00f.app.R
import com.mag_sp00f.app.databinding.FragmentEmulationBinding
import com.mag_sp00f.app.hardware.PN532Manager
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * EmulationFragment - Emulation Center UI
 * 
 * Following naming_scheme.md conventions
 */
class EmulationFragment : Fragment() {
    
    companion object {
        private const val TAG = "EmulationFragment"
    }
    
    private var _binding: FragmentEmulationBinding? = null
    private val binding get() = _binding!!
    
    private val emulationViewModel: EmulationViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmulationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
        setupClickListeners()
    }
    
    private fun setupUI() {
        // Set initial connection mode to OFF
        binding.toggleConnectionMode.check(R.id.btn_mode_off)
        
        // Initialize status messages
        appendStatusMessage("Emulation Center initialized")
        appendStatusMessage("Ready for connection")
    }
    
    private fun observeViewModel() {
        // Observe connection status
        emulationViewModel.connectionStatus.observe(viewLifecycleOwner) { status ->
            updateConnectionStatus(status)
        }
        
        // Observe connection mode
        emulationViewModel.connectionMode.observe(viewLifecycleOwner) { mode ->
            updateConnectionMode(mode)
        }
        
        // Observe emulation status
        emulationViewModel.emulationStatus.observe(viewLifecycleOwner) { status ->
            updateEmulationStatus(status)
        }
        
        // Observe error messages
        emulationViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                appendStatusMessage(message)
                if (message.contains("error", ignoreCase = true)) {
                    showErrorSnackbar(message)
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        // Connection mode toggle
        binding.toggleConnectionMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                handleConnectionModeChange(checkedId)
            }
        }
        
        // Emulation control buttons
        binding.btnStartEmulation.setOnClickListener {
            startEmulation()
        }
        
        binding.btnStopEmulation.setOnClickListener {
            stopEmulation()
        }
        
        // Test function buttons
        binding.btnTestConnection.setOnClickListener {
            testPN532Connection()
        }
        
        binding.btnTestVisaPpse.setOnClickListener {
            testVisaPpseFlow()
        }
    }
    
    private fun handleConnectionModeChange(checkedId: Int) {
        val mode = when (checkedId) {
            R.id.btn_mode_off -> PN532Manager.ConnectionMode.OFF
            R.id.btn_mode_usb -> PN532Manager.ConnectionMode.USB
            R.id.btn_mode_bluetooth -> PN532Manager.ConnectionMode.BLUETOOTH
            else -> PN532Manager.ConnectionMode.OFF
        }
        
        appendStatusMessage("Switching to ${mode.name} mode...")
        
        lifecycleScope.launch {
            emulationViewModel.setConnectionMode(mode)
        }
    }
    
    private fun startEmulation() {
        appendStatusMessage("Starting emulation...")
        
        lifecycleScope.launch {
            emulationViewModel.startEmulation()
        }
    }
    
    private fun stopEmulation() {
        appendStatusMessage("Stopping emulation...")
        
        lifecycleScope.launch {
            emulationViewModel.stopEmulation()
        }
    }
    
    private fun testPN532Connection() {
        appendStatusMessage("Testing PN532 connection...")
        
        lifecycleScope.launch {
            emulationViewModel.testPN532Connection()
        }
    }
    
    private fun testVisaPpseFlow() {
        appendStatusMessage("Testing VISA PPSE flow...")
        
        lifecycleScope.launch {
            emulationViewModel.testVisaPpseFlow()
        }
    }
    
    private fun updateConnectionStatus(status: PN532Manager.ConnectionStatus) {
        val statusText = when (status) {
            PN532Manager.ConnectionStatus.CONNECTED -> {
                binding.tvConnectionStatus.setTextColor(requireContext().getColor(R.color.primary))
                getString(R.string.connected)
            }
            PN532Manager.ConnectionStatus.CONNECTING -> {
                binding.tvConnectionStatus.setTextColor(requireContext().getColor(R.color.secondary))
                getString(R.string.connecting)
            }
            PN532Manager.ConnectionStatus.DISCONNECTED -> {
                binding.tvConnectionStatus.setTextColor(requireContext().getColor(R.color.error))
                getString(R.string.disconnected)
            }
            PN532Manager.ConnectionStatus.ERROR -> {
                binding.tvConnectionStatus.setTextColor(requireContext().getColor(R.color.error))
                getString(R.string.error)
            }
        }
        
        binding.tvConnectionStatus.text = statusText
        appendStatusMessage("Connection status: $statusText")
    }
    
    private fun updateConnectionMode(mode: PN532Manager.ConnectionMode) {
        val modeText = when (mode) {
            PN532Manager.ConnectionMode.OFF -> getString(R.string.mode_off)
            PN532Manager.ConnectionMode.USB -> getString(R.string.mode_usb)
            PN532Manager.ConnectionMode.BLUETOOTH -> getString(R.string.mode_bluetooth)
        }
        
        binding.tvConnectionMode.text = modeText
        
        // Update toggle button selection
        val buttonId = when (mode) {
            PN532Manager.ConnectionMode.OFF -> R.id.btn_mode_off
            PN532Manager.ConnectionMode.USB -> R.id.btn_mode_usb
            PN532Manager.ConnectionMode.BLUETOOTH -> R.id.btn_mode_bluetooth
        }
        
        if (binding.toggleConnectionMode.checkedButtonId != buttonId) {
            binding.toggleConnectionMode.check(buttonId)
        }
    }
    
    private fun updateEmulationStatus(status: EmulationStatus) {
        val statusText = when (status) {
            EmulationStatus.STOPPED -> {
                binding.btnStartEmulation.isEnabled = true
                binding.btnStopEmulation.isEnabled = false
                getString(R.string.stopped)
            }
            EmulationStatus.RUNNING -> {
                binding.btnStartEmulation.isEnabled = false
                binding.btnStopEmulation.isEnabled = true
                getString(R.string.running)
            }
            EmulationStatus.PAUSED -> {
                binding.btnStartEmulation.isEnabled = true
                binding.btnStopEmulation.isEnabled = true
                getString(R.string.paused)
            }
            EmulationStatus.ERROR -> {
                binding.btnStartEmulation.isEnabled = true
                binding.btnStopEmulation.isEnabled = true
                getString(R.string.error)
            }
        }
        
        binding.tvEmulationStatus.text = statusText
        appendStatusMessage("Emulation status: $statusText")
    }
    
    private fun appendStatusMessage(message: String) {
        val currentText = binding.tvStatusMessages.text.toString()
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        
        val newMessage = "[$timestamp] $message"
        val updatedText = if (currentText.isEmpty()) {
            newMessage
        } else {
            "$currentText\n$newMessage"
        }
        
        binding.tvStatusMessages.text = updatedText
        
        // Auto-scroll to bottom
        binding.tvStatusMessages.post {
            val scrollAmount = binding.tvStatusMessages.layout?.let { layout ->
                val line = layout.lineCount - 1
                if (line >= 0) layout.getLineTop(line) else 0
            } ?: 0
            
            binding.tvStatusMessages.scrollTo(0, scrollAmount)
        }
        
        Timber.tag(TAG).d("Status: $message")
    }
    
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Dismiss") { }
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}