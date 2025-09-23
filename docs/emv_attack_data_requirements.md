# EMV Attack Data Requirements & Success Rating Guide - Mag-Sp00f Project 🎯

## Table of Contents
1. [EMV Data Requirements by Attack](#emv-data-requirements-by-attack)
2. [Card Profile Success Ratings](#card-profile-success-ratings)
3. [Attack Testing Framework](#attack-testing-framework)
4. [Comprehensive Test Results](#comprehensive-test-results)

---

## EMV Data Requirements by Attack 📋

### 1. PPSE AID Poisoning Module
**Attack ID**: `ppse_aid_poisoning`
**Target Commands**: SELECT PPSE

#### Required EMV Data
```json
{
    "supported_aids": ["A0000000031010", "A0000000041010"],  // MANDATORY
    "preferred_aid": "A0000000031010",                       // MANDATORY
    "application_label": "VISA DEBIT",                       // OPTIONAL
    "application_priority": 1                                // OPTIONAL
}
```

#### Data Impact on Success
- **Critical**: Multiple supported AIDs (VISA + MasterCard)
- **High**: Application priority indicators
- **Medium**: Application labels for legitimacy
- **Low**: Issuer country codes

#### Success Factors
✅ **BEST**: Cards supporting both VISA (A0000000031010) and MasterCard (A0000000041010) AIDs  
✅ **GOOD**: Cards with multiple payment applications  
⚠️ **FAIR**: Single AID cards (limited poisoning options)  
❌ **POOR**: Proprietary AID-only cards

---

### 2. AIP Force Offline Module  
**Attack ID**: `aip_force_offline`
**Target Commands**: GET PROCESSING OPTIONS

#### Required EMV Data
```json
{
    "aip": "2000",                                          // MANDATORY
    "offline_capable": true,                                // MANDATORY  
    "authentication_methods": ["SDA", "DDA"],               // MANDATORY
    "transaction_limits": {"offline_limit": 5000}           // RECOMMENDED
}
```

#### Data Impact on Success
- **Critical**: AIP with offline capabilities (bit 4 set)
- **High**: SDA/DDA authentication support
- **Medium**: Transaction limits configuration
- **Low**: Terminal capabilities matching

#### Success Factors
✅ **BEST**: Cards with AIP offline bit + DDA support  
✅ **GOOD**: SDA-capable cards with offline limits  
⚠️ **FAIR**: Online-only cards (limited offline forcing)  
❌ **POOR**: Contactless-only cards without offline support

---

### 3. Track2 Spoofing Module
**Attack ID**: `track2_spoofing`  
**Target Commands**: GET PROCESSING OPTIONS, READ RECORD

#### Required EMV Data
```json
{
    "pan": "4154904674973556",                              // MANDATORY
    "expiry_date": "2902",                                  // MANDATORY
    "track2_data": "4154904674973556D29022010000820083001F", // MANDATORY
    "service_code": "201",                                  // MANDATORY
    "discretionary_data": "0000820083001F"                  // RECOMMENDED
}
```

#### Data Impact on Success
- **Critical**: Valid Track2 structure with separators
- **High**: Luhn-valid PAN for legitimacy
- **Medium**: Realistic service codes (201, 101, etc.)
- **Low**: Discretionary data authenticity

#### Success Factors
✅ **BEST**: Cards with complete Track2 in multiple records  
✅ **GOOD**: Single Track2 with valid structure  
⚠️ **FAIR**: Track2 equivalent data only  
❌ **POOR**: Cards without magnetic stripe equivalent

---

### 4. Cryptogram Downgrade Module
**Attack ID**: `cryptogram_downgrade`
**Target Commands**: GET PROCESSING OPTIONS, GENERATE AC

#### Required EMV Data  
```json
{
    "supported_cryptograms": ["ARQC", "TC", "AAC"],         // MANDATORY
    "authentication_methods": ["DDA", "CDA"],               // MANDATORY
    "emv_tags": {
        "9F26": "D3967976E30EFAFC",                        // Application Cryptogram
        "9F27": "80",                                       // Cryptogram Info Data
        "9F36": "011E"                                      // ATC
    }
}
```

#### Data Impact on Success
- **Critical**: Multiple cryptogram type support
- **High**: Valid cryptogram patterns in EMV tags
- **Medium**: Application Transaction Counter (ATC)
- **Low**: Issuer authentication data

#### Success Factors
✅ **BEST**: Cards supporting ARQC→TC conversion with DDA  
✅ **GOOD**: CDA-capable cards with cryptogram flexibility  
⚠️ **FAIR**: SDA-only cards (limited cryptogram manipulation)  
❌ **POOR**: Proprietary authentication cards

---

### 5. CVM Bypass Module
**Attack ID**: `cvm_bypass`
**Target Commands**: GET PROCESSING OPTIONS

#### Required EMV Data
```json
{
    "cvm_methods": ["PIN", "SIGNATURE", "NO_CVM"],          // MANDATORY
    "transaction_limits": {                                 // MANDATORY
        "cvm_limit": 5000,
        "no_cvm_limit": 2500
    },
    "contactless_supported": true,                          // RECOMMENDED
    "emv_tags": {
        "8E": "000000000000001E030000000000000000"          // CVM List
    }
}
```

#### Data Impact on Success  
- **Critical**: Multiple CVM methods supported
- **High**: Contactless transaction capability
- **Medium**: Realistic CVM limits configuration
- **Low**: International usage indicators

#### Success Factors
✅ **BEST**: Contactless cards with flexible CVM lists  
✅ **GOOD**: Multi-CVM cards (PIN + Signature + No CVM)  
⚠️ **FAIR**: PIN-only cards with contactless  
❌ **POOR**: Signature-only or fixed CVM cards

---

### 6. Advanced Force Offline Module
**Attack ID**: `advanced_force_offline`
**Target Commands**: GET PROCESSING OPTIONS, GENERATE AC

#### Required EMV Data
```json
{
    "offline_capable": true,                                // MANDATORY
    "authentication_methods": ["SDA", "DDA", "CDA"],        // MANDATORY
    "transaction_limits": {                                 // MANDATORY
        "offline_limit": 10000,
        "floor_limit": 5000
    },
    "emv_tags": {
        "82": "2000",                                       // AIP
        "94": "08010200",                                   // AFL
        "8C": "9F02069F03069F1A0295055F2A029A039C01"       // CDOL1
    }
}
```

#### Success Factors
✅ **BEST**: High-limit cards with DDA/CDA + offline capability  
✅ **GOOD**: EMV contact cards with flexible authentication  
⚠️ **FAIR**: Basic EMV cards with SDA only  
❌ **POOR**: Online-mandatory or proprietary cards

---

### 7. Enhanced CVM Bypass Module  
**Attack ID**: `enhanced_cvm_bypass`
**Target Commands**: GET PROCESSING OPTIONS

#### Required EMV Data
```json
{
    "cvm_methods": ["PIN", "SIGNATURE", "BIOMETRIC", "NO_CVM"], // MANDATORY
    "contactless_supported": true,                              // MANDATORY
    "biometric_supported": true,                                // RECOMMENDED
    "transaction_limits": {                                     // MANDATORY
        "contactless_limit": 10000,
        "no_cvm_limit": 5000
    }
}
```

#### Success Factors
✅ **BEST**: Modern biometric cards with multiple CVM options  
✅ **GOOD**: Contactless cards with high transaction limits  
⚠️ **FAIR**: Basic contactless without biometric support  
❌ **POOR**: Contact-only cards with fixed CVM

---

### 8. Amount Manipulation Module
**Attack ID**: `amount_manipulation`  
**Target Commands**: GET PROCESSING OPTIONS

#### Required EMV Data
```json
{
    "emv_tags": {
        "9F02": "000000001000",                             // Amount Authorized
        "9F03": "000000000000",                             // Amount Other
        "5F2A": "0840",                                     // Currency Code
        "9F1A": "0840"                                      // Terminal Country Code
    },
    "transaction_limits": {                                 // MANDATORY
        "single_transaction_limit": 50000,
        "daily_limit": 200000
    }
}
```

#### Success Factors
✅ **BEST**: High-limit cards with multiple currency support  
✅ **GOOD**: International cards with flexible amount handling  
⚠️ **FAIR**: Domestic cards with moderate limits  
❌ **POOR**: Low-limit or restricted amount cards

---

### 9. Advanced Cryptogram Module
**Attack ID**: `advanced_cryptogram`
**Target Commands**: GET PROCESSING OPTIONS, GENERATE AC

#### Required EMV Data
```json
{
    "supported_cryptograms": ["ARQC", "TC", "AAC"],         // MANDATORY
    "authentication_methods": ["DDA", "CDA"],               // MANDATORY
    "emv_tags": {                                          // MANDATORY
        "9F26": "D3967976E30EFAFC",                        // Application Cryptogram  
        "9F27": "80",                                       // Cryptogram Info Data
        "9F36": "011E",                                     // ATC
        "9F4B": "16A29B7D8EFCD866..."                      // Signed Dynamic Application Data
    }
}
```

#### Success Factors  
✅ **BEST**: CDA-capable cards with complete cryptographic data  
✅ **GOOD**: DDA cards with dynamic authentication  
⚠️ **FAIR**: SDA cards with static authentication  
❌ **POOR**: Legacy cards without cryptographic support

---

### 10. Failed Cryptogram Attack Module
**Attack ID**: `failed_cryptogram_attack`
**Target Commands**: GET PROCESSING OPTIONS, GENERATE AC

#### Required EMV Data
```json
{
    "supported_cryptograms": ["ARQC", "TC", "AAC"],         // MANDATORY
    "authentication_methods": ["SDA", "DDA", "CDA"],        // MANDATORY
    "emv_tags": {
        "9F26": "D3967976E30EFAFC",                        // Valid cryptogram for corruption
        "9F27": "80"                                        // Cryptogram type for manipulation  
    },
    "offline_capable": true                                 // RECOMMENDED
}
```

#### Success Factors
✅ **BEST**: Cards with weak terminal validation (high bypass potential)  
✅ **GOOD**: Cards with multiple cryptogram types  
⚠️ **FAIR**: Standard EMV cards with basic validation  
❌ **POOR**: Cards with strict cryptographic enforcement

---

## Card Profile Success Ratings 🏆

### VISA Test Card Profile (Primary)
**File**: `visa_test_card.json`
**Overall Rating**: ⭐⭐⭐⭐⭐ **EXCELLENT**

#### EMV Data Coverage
```json
{
    "pan": "4154904674973556",
    "expiry_date": "2902", 
    "track2_data": "4154904674973556D29022010000820083001F",
    "supported_aids": ["A0000000031010", "A0000000980840"],
    "aip": "2000",
    "supported_cryptograms": ["ARQC", "TC", "AAC"],
    "authentication_methods": ["SDA", "DDA"],
    "cvm_methods": ["PIN", "SIGNATURE", "NO_CVM"],
    "offline_capable": true,
    "contactless_supported": true
}
```

#### Attack Success Predictions
| Attack Module | Success Rating | Reason |
|---------------|----------------|---------|
| PPSE AID Poisoning | ⭐⭐⭐⭐⭐ | Multiple AIDs (VISA + US Debit) |
| AIP Force Offline | ⭐⭐⭐⭐⭐ | AIP 2000 with offline capability |
| Track2 Spoofing | ⭐⭐⭐⭐⭐ | Complete Track2 data |
| Cryptogram Downgrade | ⭐⭐⭐⭐ | ARQC→TC support, missing CDA |
| CVM Bypass | ⭐⭐⭐⭐⭐ | Multiple CVM methods + contactless |
| Advanced Force Offline | ⭐⭐⭐⭐ | Good offline support, needs higher limits |
| Enhanced CVM Bypass | ⭐⭐⭐ | Basic contactless, no biometric |
| Amount Manipulation | ⭐⭐⭐⭐ | Standard limits, single currency |
| Advanced Cryptogram | ⭐⭐⭐ | SDA/DDA only, missing CDA |
| Failed Cryptogram | ⭐⭐⭐⭐⭐ | Complete cryptogram data for manipulation |

---

### MasterCard Test Profile (Secondary)  
**File**: `mastercard_test_card.json`
**Overall Rating**: ⭐⭐⭐⭐ **GOOD**

#### EMV Data Coverage
```json
{
    "pan": "5555555555554444",
    "expiry_date": "2912",
    "supported_aids": ["A0000000041010"],
    "aip": "1800", 
    "supported_cryptograms": ["ARQC", "TC"],
    "authentication_methods": ["SDA"],
    "cvm_methods": ["PIN", "SIGNATURE"],
    "offline_capable": true
}
```

#### Attack Success Predictions
| Attack Module | Success Rating | Reason |
|---------------|----------------|---------|
| PPSE AID Poisoning | ⭐⭐⭐ | Single AID, limited poisoning |
| AIP Force Offline | ⭐⭐⭐⭐ | Good offline support |
| Track2 Spoofing | ⭐⭐⭐⭐ | Standard Track2 structure |
| Cryptogram Downgrade | ⭐⭐⭐ | ARQC→TC only, missing AAC |
| CVM Bypass | ⭐⭐⭐ | Basic CVM, no contactless |
| Advanced Force Offline | ⭐⭐⭐ | Limited authentication methods |
| Enhanced CVM Bypass | ⭐⭐ | No contactless/biometric |
| Amount Manipulation | ⭐⭐⭐ | Basic amount handling |
| Advanced Cryptogram | ⭐⭐ | SDA only, limited crypto |
| Failed Cryptogram | ⭐⭐⭐ | Basic cryptogram support |

---

### US Debit Card Profile (Tertiary)
**File**: `us_debit_card.json`  
**Overall Rating**: ⭐⭐⭐ **FAIR**

#### EMV Data Coverage
```json
{
    "pan": "4000000000000002",
    "expiry_date": "2512",
    "supported_aids": ["A0000000980840"],
    "aip": "0000",
    "supported_cryptograms": ["ARQC"],
    "authentication_methods": ["SDA"],
    "cvm_methods": ["PIN"],
    "offline_capable": false
}
```

#### Attack Success Predictions  
| Attack Module | Success Rating | Reason |
|---------------|----------------|---------|
| PPSE AID Poisoning | ⭐⭐ | Single debit AID |
| AIP Force Offline | ⭐⭐ | No offline capability |
| Track2 Spoofing | ⭐⭐⭐ | Basic Track2 |
| Cryptogram Downgrade | ⭐ | ARQC only |
| CVM Bypass | ⭐ | PIN-only |
| Advanced Force Offline | ⭐ | No offline support |
| Enhanced CVM Bypass | ⭐ | No advanced CVM |
| Amount Manipulation | ⭐⭐ | Debit limits |
| Advanced Cryptogram | ⭐ | Minimal crypto |
| Failed Cryptogram | ⭐⭐ | Basic support |

---

## Attack Testing Framework 🧪

### Test Methodology

#### Phase 1: EMV Data Validation
```python
def validate_emv_data_for_attack(card_profile, attack_module):
    """Validate if card has required EMV data for attack success"""
    requirements = ATTACK_REQUIREMENTS[attack_module.attack_id]
    
    missing_data = []
    for field, importance in requirements.items():
        if field not in card_profile or not card_profile[field]:
            missing_data.append((field, importance))
    
    return calculate_success_probability(missing_data)
```

#### Phase 2: Attack Execution
```python  
def execute_attack_with_data_analysis(attack_module, card_profile):
    """Execute attack and analyze data dependencies"""
    
    # Pre-attack data validation
    data_score = validate_emv_data_for_attack(card_profile, attack_module)
    
    # Execute attack
    attack_result = attack_module.execute(card_profile)
    
    # Post-attack analysis
    success_factors = analyze_success_factors(attack_result, card_profile)
    
    return {
        'data_score': data_score,
        'attack_result': attack_result, 
        'success_factors': success_factors,
        'recommendations': generate_data_recommendations(attack_module, card_profile)
    }
```

### Comprehensive Test Script

#### `test_attack_data_requirements.py`
```python
#!/usr/bin/env python3
"""
EMV Attack Data Requirements Tester
Tests each attack module with different card profiles and analyzes data dependencies
"""

class AttackDataRequirementsTester:
    def __init__(self):
        self.card_profiles = self.load_card_profiles()
        self.attack_modules = self.load_attack_modules()
        
    def test_all_combinations(self):
        """Test all attack modules against all card profiles"""
        results = {}
        
        for profile_name, profile_data in self.card_profiles.items():
            results[profile_name] = {}
            
            for attack_name, attack_module in self.attack_modules.items():
                print(f"🧪 Testing {attack_name} with {profile_name}")
                
                test_result = self.test_attack_with_profile(
                    attack_module, profile_data
                )
                
                results[profile_name][attack_name] = test_result
                
        return results
    
    def generate_data_optimization_report(self, results):
        """Generate report on optimal EMV data for each attack"""
        
        optimization_report = {
            'attack_rankings': {},
            'profile_rankings': {},
            'data_recommendations': {}
        }
        
        # Rank attacks by data requirements
        for attack_name in self.attack_modules.keys():
            attack_scores = []
            
            for profile_name in self.card_profiles.keys():
                score = results[profile_name][attack_name]['data_score']
                attack_scores.append(score)
            
            optimization_report['attack_rankings'][attack_name] = {
                'average_score': sum(attack_scores) / len(attack_scores),
                'best_profile': max(results.items(), 
                    key=lambda x: x[1][attack_name]['data_score'])[0],
                'data_requirements': ATTACK_REQUIREMENTS[attack_name]
            }
            
        return optimization_report
```

---

## Comprehensive Test Results 📊

### Test Execution Plan

#### Batch 1: Basic Attack Modules (1-5)
```bash
# Test PPSE AID Poisoning with all profiles
python3 scripts/test_attack_data_requirements.py --attack ppse_aid_poisoning --profiles all

# Test AIP Force Offline 
python3 scripts/test_attack_data_requirements.py --attack aip_force_offline --profiles all

# Test Track2 Spoofing
python3 scripts/test_attack_data_requirements.py --attack track2_spoofing --profiles all

# Test Cryptogram Downgrade  
python3 scripts/test_attack_data_requirements.py --attack cryptogram_downgrade --profiles all

# Test CVM Bypass
python3 scripts/test_attack_data_requirements.py --attack cvm_bypass --profiles all
```

#### Batch 2: Advanced Attack Modules (6-10)
```bash
# Test Advanced Force Offline
python3 scripts/test_attack_data_requirements.py --attack advanced_force_offline --profiles all

# Test Enhanced CVM Bypass
python3 scripts/test_attack_data_requirements.py --attack enhanced_cvm_bypass --profiles all

# Test Amount Manipulation
python3 scripts/test_attack_data_requirements.py --attack amount_manipulation --profiles all

# Test Advanced Cryptogram
python3 scripts/test_attack_data_requirements.py --attack advanced_cryptogram --profiles all

# Test Failed Cryptogram
python3 scripts/test_attack_data_requirements.py --attack failed_cryptogram_attack --profiles all
```

#### Comprehensive Analysis
```bash
# Run complete test suite
python3 scripts/test_attack_data_requirements.py --comprehensive --output comprehensive_results.json

# Generate optimization report
python3 scripts/generate_attack_optimization_report.py --input comprehensive_results.json --output optimization_report.html
```

---

## Data Optimization Recommendations 💡

### High-Priority EMV Data Fields

#### Critical Success Factors (Required for 80%+ attacks)
1. **supported_aids** - Multiple payment applications
2. **aip** - Application Interchange Profile with offline bits
3. **track2_data** - Complete magnetic stripe equivalent
4. **supported_cryptograms** - ARQC, TC, AAC support
5. **cvm_methods** - Multiple cardholder verification methods

#### High-Value Data Fields (Required for 60%+ attacks)
1. **authentication_methods** - DDA/CDA capability
2. **offline_capable** - Offline transaction support
3. **contactless_supported** - Contactless transaction capability
4. **transaction_limits** - Flexible limit configuration
5. **emv_tags** - Complete EMV tag set

### Card Profile Optimization Strategy

#### Tier 1: Universal Attack Card (10/10 attack compatibility)
```json
{
    "pan": "4154904674973556",
    "supported_aids": ["A0000000031010", "A0000000041010", "A0000000980840"],
    "aip": "3800",  // Enhanced offline + contactless capabilities
    "supported_cryptograms": ["ARQC", "TC", "AAC"],
    "authentication_methods": ["SDA", "DDA", "CDA"],
    "cvm_methods": ["PIN", "SIGNATURE", "BIOMETRIC", "NO_CVM"],
    "offline_capable": true,
    "contactless_supported": true,
    "biometric_supported": true,
    "transaction_limits": {
        "offline_limit": 50000,
        "contactless_limit": 25000,
        "no_cvm_limit": 10000
    }
}
```

#### Tier 2: Standard Attack Card (8/10 attack compatibility)
```json
{
    "pan": "4154904674973556", 
    "supported_aids": ["A0000000031010", "A0000000041010"],
    "aip": "2000",
    "supported_cryptograms": ["ARQC", "TC"],
    "authentication_methods": ["SDA", "DDA"],
    "cvm_methods": ["PIN", "SIGNATURE", "NO_CVM"],
    "offline_capable": true,
    "contactless_supported": true
}
```

#### Tier 3: Basic Attack Card (5/10 attack compatibility)
```json
{
    "pan": "4154904674973556",
    "supported_aids": ["A0000000031010"],
    "aip": "2000", 
    "supported_cryptograms": ["ARQC"],
    "authentication_methods": ["SDA"],
    "cvm_methods": ["PIN"],
    "offline_capable": false
}
```

---

*EMV Attack Data Requirements Guide | Version 1.0 | September 21, 2025 | Mag-Sp00f Project* 🎯