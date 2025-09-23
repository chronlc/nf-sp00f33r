# EMV Attack Module Architecture - Mag-Sp00f Project 🏗️

## Overview

The Mag-Sp00f Android HCE application uses a modular architecture for implementing various EMV attack techniques. This system allows for dynamic attack configuration, extensible module development, and real-time attack orchestration during EMV transactions.

---

## Core Architecture Components

### 1. EmvAttackModule Interface 

**File**: `android-app/src/main/java/com/mag_sp00f/app/emulation/EmvAttackModule.kt`

The base interface that all attack modules must implement:

```kotlin
interface EmvAttackModule {
    /**
     * Unique identifier for this attack module
     * Format: lowercase_with_underscores
     */
    fun getAttackId(): String
    
    /**
     * Human-readable description of the attack
     */
    fun getDescription(): String
    
    /**
     * Determines if this attack is applicable to the current EMV command/context
     * @param command The raw APDU command from the terminal
     * @param cardData The loaded card data (PAN, Track2, AIDs, etc.)
     * @return true if attack should be applied
     */
    fun isApplicable(command: ByteArray, cardData: Map<String, Any>): Boolean
    
    /**
     * Applies the attack to the EMV response
     * @param command The original APDU command
     * @param response The original response that would be sent
     * @param cardData The card data context
     * @return Modified response with attack applied
     */
    fun applyAttack(command: ByteArray, response: ByteArray, cardData: Map<String, Any>): ByteArray
    
    /**
     * Returns current status and statistics for this attack module
     */
    fun getAttackStatus(): Map<String, Any>
    
    /**
     * Configure attack parameters dynamically
     * @param config Map of configuration parameters
     */
    fun configure(config: Map<String, Any>)
}
```

### 2. EmvAttackEmulationManager

**File**: `android-app/src/main/java/com/mag_sp00f/app/emulation/EmvAttackEmulationManager.kt`

Central orchestration system that manages all attack modules:

```kotlin
class EmvAttackEmulationManager {
    private val attackModules = mutableMapOf<String, EmvAttackModule>()
    private val moduleConfigurations = mutableMapOf<String, Map<String, Any>>()
    private val attackStatistics = mutableMapOf<String, AttackStats>()
    
    /**
     * Register a new attack module
     */
    fun registerAttackModule(module: EmvAttackModule) {
        attackModules[module.getAttackId()] = module
        attackStatistics[module.getAttackId()] = AttackStats()
    }
    
    /**
     * Configure specific attack module
     */
    fun configureAttack(attackId: String, config: Map<String, Any>) {
        moduleConfigurations[attackId] = config
        attackModules[attackId]?.configure(config)
    }
    
    /**
     * Apply all applicable attacks to an EMV response
     */
    fun applyAttacks(command: ByteArray, response: ByteArray, cardData: Map<String, Any>): ByteArray {
        var modifiedResponse = response
        
        attackModules.values.forEach { module ->
            if (module.isApplicable(command, cardData)) {
                val beforeSize = modifiedResponse.size
                modifiedResponse = module.applyAttack(command, modifiedResponse, cardData)
                
                // Update statistics
                attackStatistics[module.getAttackId()]?.recordAttackApplied(
                    originalSize = beforeSize,
                    modifiedSize = modifiedResponse.size
                )
            }
        }
        
        return modifiedResponse
    }
    
    /**
     * Get comprehensive attack status report
     */
    fun getAttackReport(): Map<String, Any> {
        return mapOf(
            "registered_modules" to attackModules.keys.toList(),
            "module_statistics" to attackStatistics,
            "active_configurations" to moduleConfigurations
        )
    }
}
```

### 3. EmvCardDataLoader

**File**: `android-app/src/main/java/com/mag_sp00f/app/emulation/EmvCardDataLoader.kt`

Loads and manages card data profiles for attack context:

```kotlin
class EmvCardDataLoader {
    data class CardProfile(
        val profileName: String,
        val pan: String,
        val expiryDate: String,
        val track2Data: String,
        val supportedAids: List<String>,
        val cardCapabilities: Map<String, Boolean>,
        val cvmMethods: List<String>,
        val cryptogramData: Map<String, String>,
        val issuerData: Map<String, String>
    )
    
    /**
     * Load card profile from JSON file
     */
    fun loadCardProfile(profilePath: String): CardProfile {
        // Implementation loads from JSON or real card data
    }
    
    /**
     * Convert card profile to attack context map
     */
    fun toAttackContext(profile: CardProfile): Map<String, Any> {
        return mapOf(
            "pan" to profile.pan,
            "expiry" to profile.expiryDate,
            "track2" to profile.track2Data,
            "aids" to profile.supportedAids,
            "capabilities" to profile.cardCapabilities,
            "cvm_methods" to profile.cvmMethods,
            "cryptograms" to profile.cryptogramData,
            "issuer_data" to profile.issuerData
        )
    }
}
```

---

## Module Implementation Pattern

### Basic Attack Module Structure

Each attack module follows this implementation pattern:

```kotlin
class ExampleAttackModule : EmvAttackModule {
    private var configuration = mutableMapOf<String, Any>()
    private var attackCount = 0
    
    override fun getAttackId(): String = "example_attack"
    
    override fun getDescription(): String = "Example attack module for documentation"
    
    override fun isApplicable(command: ByteArray, cardData: Map<String, Any>): Boolean {
        // Check if this attack applies to the current command/context
        return when {
            isSelectPpseCommand(command) -> configuration["target_ppse"] == true
            isSelectAidCommand(command) -> configuration["target_aid"] == true
            isGpoCommand(command) -> configuration["target_gpo"] == true
            else -> false
        }
    }
    
    override fun applyAttack(command: ByteArray, response: ByteArray, cardData: Map<String, Any>): ByteArray {
        attackCount++
        
        return when {
            isSelectPpseCommand(command) -> manipulatePpseResponse(response)
            isSelectAidCommand(command) -> manipulateAidResponse(response)
            isGpoCommand(command) -> manipulateGpoResponse(response)
            else -> response
        }
    }
    
    override fun getAttackStatus(): Map<String, Any> {
        return mapOf(
            "attack_id" to getAttackId(),
            "attacks_applied" to attackCount,
            "configuration" to configuration,
            "last_activity" to System.currentTimeMillis()
        )
    }
    
    override fun configure(config: Map<String, Any>) {
        configuration.putAll(config)
    }
    
    // Helper methods for EMV command detection
    private fun isSelectPpseCommand(command: ByteArray): Boolean {
        return command.size >= 6 && 
               command[0] == 0x00.toByte() && 
               command[1] == 0xA4.toByte() &&
               command[2] == 0x04.toByte()
    }
    
    private fun isGpoCommand(command: ByteArray): Boolean {
        return command.size >= 4 &&
               command[0] == 0x80.toByte() &&
               command[1] == 0xA8.toByte()
    }
}
```

---

## Integration with HCE Service

### EnhancedHceService Integration

The attack system integrates with the Android HCE service:

```kotlin
class EnhancedHceService : HostApduService() {
    private lateinit var attackManager: EmvAttackEmulationManager
    private lateinit var cardDataLoader: EmvCardDataLoader
    private var currentCardData: Map<String, Any> = emptyMap()
    
    override fun onCreate() {
        super.onCreate()
        initializeAttackSystem()
    }
    
    private fun initializeAttackSystem() {
        attackManager = EmvAttackEmulationManager()
        cardDataLoader = EmvCardDataLoader()
        
        // Register all attack modules
        registerAttackModules()
        
        // Load default card profile
        loadCardProfile("default_test_card")
    }
    
    private fun registerAttackModules() {
        // Basic modules
        attackManager.registerAttackModule(PpseAidPoisoningModule())
        attackManager.registerAttackModule(AipForceOfflineModule())
        attackManager.registerAttackModule(Track2SpoofingModule())
        attackManager.registerAttackModule(CryptogramDowngradeModule())
        attackManager.registerAttackModule(CvmBypassModule())
        
        // Advanced modules  
        attackManager.registerAttackModule(AdvancedForceOfflineModule())
        attackManager.registerAttackModule(EnhancedCvmBypassModule())
        attackManager.registerAttackModule(AmountManipulationModule())
        attackManager.registerAttackModule(AdvancedCryptogramModule())
        attackManager.registerAttackModule(FailedCryptogramModule())
    }
    
    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        // Generate normal EMV response
        val normalResponse = processEmvCommand(commandApdu)
        
        // Apply attacks if configured
        val attackedResponse = attackManager.applyAttacks(commandApdu, normalResponse, currentCardData)
        
        // Log for analysis
        logApduExchange(commandApdu, normalResponse, attackedResponse)
        
        return attackedResponse
    }
}
```

---

## Configuration System

### Attack Configuration Format

Attack modules are configured using a hierarchical JSON structure:

```json
{
    "attack_profiles": {
        "stealth_bypass": {
            "description": "Low-detection bypass attacks",
            "modules": {
                "cvm_bypass": {
                    "enabled": true,
                    "bypass_pin": true,
                    "bypass_signature": false
                },
                "aip_force_offline": {
                    "enabled": true,
                    "force_offline": true,
                    "remove_cda": false
                }
            }
        },
        "aggressive_manipulation": {
            "description": "High-impact but detectable attacks",
            "modules": {
                "amount_manipulation": {
                    "enabled": true,
                    "target_amount": 100,
                    "spoofed_amount": 10000
                },
                "advanced_cryptogram": {
                    "enabled": true,
                    "use_precomputed": true,
                    "enable_replay": true
                }
            }
        }
    }
}
```

### Runtime Configuration API

```kotlin
class AttackConfigurationManager {
    fun loadAttackProfile(profileName: String) {
        val profile = loadProfileFromFile(profileName)
        profile.modules.forEach { (moduleId, config) ->
            if (config["enabled"] == true) {
                attackManager.configureAttack(moduleId, config)
            }
        }
    }
    
    fun createCustomProfile(modulesConfig: Map<String, Map<String, Any>>) {
        modulesConfig.forEach { (moduleId, config) ->
            attackManager.configureAttack(moduleId, config)
        }
    }
}
```

---

## Statistics and Monitoring

### Attack Statistics Tracking

```kotlin
data class AttackStats(
    var applicabilityChecks: Int = 0,
    var attacksApplied: Int = 0,
    var bytesModified: Int = 0,
    var lastApplied: Long = 0,
    var errorCount: Int = 0
) {
    fun recordAttackApplied(originalSize: Int, modifiedSize: Int) {
        attacksApplied++
        bytesModified += Math.abs(modifiedSize - originalSize)
        lastApplied = System.currentTimeMillis()
    }
    
    fun recordApplicabilityCheck() {
        applicabilityChecks++
    }
    
    fun recordError() {
        errorCount++
    }
}
```

### Real-Time Monitoring

```kotlin
class AttackMonitor {
    fun generateRealTimeReport(): Map<String, Any> {
        return mapOf(
            "active_attacks" to getActiveAttackCount(),
            "success_rate" to calculateSuccessRate(),
            "detection_risk" to assessDetectionRisk(),
            "performance_metrics" to getPerformanceMetrics()
        )
    }
    
    private fun assessDetectionRisk(): String {
        val riskFactors = listOf(
            "high_frequency_attacks",
            "unusual_response_patterns", 
            "terminal_validation_failures"
        )
        
        return when (calculateRiskScore(riskFactors)) {
            in 0..30 -> "LOW"
            in 31..70 -> "MEDIUM" 
            else -> "HIGH"
        }
    }
}
```

---

## Extension Points

### Adding New Attack Modules

1. **Create Module Class**
   ```kotlin
   class NewAttackModule : EmvAttackModule {
       // Implement interface methods
   }
   ```

2. **Register Module**
   ```kotlin
   attackManager.registerAttackModule(NewAttackModule())
   ```

3. **Add Configuration Schema**
   ```json
   "new_attack": {
       "enabled": true,
       "custom_param": "value"
   }
   ```

### Custom Attack Combinations

```kotlin
class AttackComboManager {
    fun createAttackCombo(name: String, modules: List<String>, sequencing: String) {
        val combo = AttackCombination(
            name = name,
            modules = modules,
            executionOrder = when(sequencing) {
                "parallel" -> ExecutionOrder.PARALLEL
                "sequential" -> ExecutionOrder.SEQUENTIAL
                else -> ExecutionOrder.CONDITIONAL
            }
        )
        registerCombo(combo)
    }
}
```

---

## Testing Architecture

### Unit Testing Framework

```kotlin
class AttackModuleTestSuite {
    @Test
    fun testPpseAidPoisoning() {
        val module = PpseAidPoisoningModule()
        val command = createSelectPpseCommand()
        val response = createNormalPpseResponse()
        val cardData = createTestCardData()
        
        // Configure attack
        module.configure(mapOf("target_aid" to "A0000000041010"))
        
        // Test applicability
        assertTrue(module.isApplicable(command, cardData))
        
        // Test attack application
        val attackedResponse = module.applyAttack(command, response, cardData)
        assertNotEquals(response, attackedResponse)
        
        // Verify attack result
        assertTrue(attackedResponse.containsAid("A0000000041010"))
    }
}
```

### Integration Testing

```kotlin
class EmvAttackIntegrationTest {
    @Test
    fun testMultipleAttackChaining() {
        val manager = EmvAttackEmulationManager()
        manager.registerAttackModule(PpseAidPoisoningModule())
        manager.registerAttackModule(Track2SpoofingModule())
        
        // Configure attack chain
        manager.configureAttack("ppse_aid_poisoning", mapOf("poison_type" to "visa_to_mastercard"))
        manager.configureAttack("track2_spoofing", mapOf("spoof_pan" to "5555555555554444"))
        
        // Test full EMV flow with attacks
        val emvFlow = createCompleteEmvFlow()
        val attackedFlow = manager.processEmvFlow(emvFlow)
        
        // Verify attack chain worked correctly
        assertAttackChainSuccess(attackedFlow)
    }
}
```

---

## Performance Considerations

### Optimization Strategies

1. **Lazy Loading**: Attack modules only loaded when needed
2. **Applicability Caching**: Cache applicability decisions for repeated commands  
3. **Response Pooling**: Reuse modified response objects
4. **Parallel Processing**: Apply independent attacks in parallel

### Memory Management

```kotlin
class AttackMemoryManager {
    private val responseCache = LRUCache<String, ByteArray>(100)
    private val applicabilityCache = LRUCache<String, Boolean>(500)
    
    fun getCachedResponse(commandHash: String): ByteArray? {
        return responseCache.get(commandHash)
    }
    
    fun cacheResponse(commandHash: String, response: ByteArray) {
        responseCache.put(commandHash, response)
    }
}
```

---

## Security Considerations

### Attack Stealth Features

1. **Response Timing**: Maintain normal response timing patterns
2. **Error Simulation**: Generate realistic error responses
3. **Terminal Validation**: Bypass common terminal validation checks
4. **Pattern Obfuscation**: Randomize attack application patterns

### Anti-Detection Mechanisms

```kotlin
class StealthManager {
    fun shouldApplyAttack(attackId: String, context: Map<String, Any>): Boolean {
        return when {
            isDetectionRiskHigh() -> false
            hasRecentlyAppliedSimilarAttack(attackId) -> false
            terminalValidationStrictness() > threshold -> false
            else -> true
        }
    }
    
    private fun isDetectionRiskHigh(): Boolean {
        // Analyze transaction patterns for detection risk
        return false
    }
}
```

---

*Architecture Documentation Version: 1.0 | Last Updated: September 21, 2025 | Mag-Sp00f Project* 🏗️