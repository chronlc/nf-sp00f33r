# Android HCE EMV Attack Emulation Architecture

## 🏗️ **MODULAR STRUCTURE OVERVIEW**

The Android HCE system now has a comprehensive modular architecture for handling different EMV emulation attacks based on card data from EMV files.

---

## 📁 **MODULE STRUCTURE**

### **Core Architecture**
```
com.mag_sp00f.app.emulation/
├── EmvAttackModule.kt           # Base interface for all attack modules
├── EmvCardData.kt               # EMV card data structure  
├── EmvAttackEmulationManager.kt # Main manager coordinating all attacks
├── EmvCardDataLoader.kt         # Load/save card data from JSON/files
└── modules/
    ├── PpseAidPoisoningModule.kt     # Attack 1: VISA→MasterCard AID
    ├── AipForceOfflineModule.kt      # Attack 2: Force offline approval
    ├── Track2SpoofingModule.kt       # Attack 3: PAN/Track2 spoofing
    └── CryptogramDowngradeModule.kt  # Attack 4&5: Crypto & CVM attacks
```

---

## 🎯 **ATTACK MODULE SYSTEM**

### **Module Interface**
Each attack module implements `EmvAttackModule`:
```kotlin
interface EmvAttackModule {
    val attackId: Int
    val attackName: String  
    val description: String
    val targetCommands: List<String>
    
    fun isApplicable(emvCardData: EmvCardData): Boolean
    fun applyAttack(command: String, originalResponse: ByteArray, emvCardData: EmvCardData): ByteArray
    fun getAttackStatus(): AttackStatus
}
```

### **Available Attack Modules**

1. **PPSE AID Poisoning Module** (ID: 1)
   - **Target**: SELECT PPSE, SELECT AID commands
   - **Action**: Changes VISA AID to MasterCard AID
   - **Applicability**: Cards with VISA AID (A0000000031010)
   - **Risk**: LOW - maintains valid EMV structure

2. **AIP Force Offline Module** (ID: 2)
   - **Target**: GET PROCESSING OPTIONS
   - **Action**: Modifies AIP to force offline approval (2000→2008)
   - **Applicability**: Cards supporting DDA
   - **Risk**: MEDIUM - suspicious offline approvals

3. **Track2 Spoofing Module** (ID: 3)
   - **Target**: GPO, READ RECORD commands
   - **Action**: Replaces PAN, expiry, cardholder data
   - **Profiles**: test_card, high_limit, business_card
   - **Applicability**: Cards with Track2 data
   - **Risk**: HIGH - backend validation likely to catch

4. **Cryptogram Downgrade Module** (ID: 4)
   - **Target**: GET PROCESSING OPTIONS  
   - **Action**: Changes ARQC to TC (80→40)
   - **Applicability**: Cards supporting ARQC/TC
   - **Risk**: HIGH - cryptogram mismatch detectable

5. **CVM Bypass Module** (ID: 5)
   - **Target**: GET PROCESSING OPTIONS
   - **Action**: Adds "No CVM Required" policy
   - **Applicability**: Cards with CVM methods
   - **Risk**: LOW - common for contactless

---

## 📊 **EMV CARD DATA STRUCTURE**

### **Comprehensive Card Information**
```kotlin
data class EmvCardData(
    // Card Identity
    val pan: String,
    val expiryDate: String,
    val cardholderName: String,
    val issuerCountry: String,
    
    // EMV Applications  
    val supportedAids: List<String>,
    val preferredAid: String,
    val applicationLabel: String,
    
    // Cryptographic Capabilities
    val aip: String,
    val supportedCryptograms: List<String>,
    val authenticationMethods: List<String>,
    
    // Transaction Controls
    val cvmMethods: List<String>,
    val transactionLimits: Map<String, Int>,
    val offlineCapable: Boolean,
    
    // Track Data
    val track2Data: String,
    val discretionaryData: String?,
    
    // EMV Tags & Capabilities
    val emvTags: Map<String, String>,
    val contactlessSupported: Boolean,
    val ddaSupported: Boolean,
    val cdaSupported: Boolean
)
```

---

## 🔧 **INTEGRATION WITH HCE SERVICE**

### **Enhanced HCE Service Integration**
```kotlin
class EnhancedHceService : HostApduService() {
    private val attackManager = EmvAttackEmulationManager()
    private val cardDataLoader = EmvCardDataLoader()
    
    // New Methods:
    fun enableEmvAttack(attackId: Int): Boolean
    fun disableEmvAttack(attackId: Int)  
    fun loadEmvCard(cardType: String)
    
    // Attack Processing Pipeline:
    // 1. Process APDU with existing hooks
    // 2. Apply active attack modifications  
    // 3. Return modified response
}
```

### **Attack Processing Flow**
1. **APDU Received** → Determine command type
2. **Base Processing** → Use existing ApduFlowHooks  
3. **Attack Application** → Apply active attack modules
4. **Response Modification** → Return modified APDU response

---

## 💾 **CARD DATA SOURCES**

### **Built-in Test Cards**
- **Real Test Card**: From emv.html (VISA 4154...3556)
- **VISA Test Card**: Enhanced VISA test variant
- **MasterCard Test Card**: Complete MasterCard profile

### **JSON File Support** 
Load custom card data from JSON files:
```json
{
    "pan": "4154904674973556",
    "expiry_date": "2902", 
    "cardholder_name": "CARDHOLDER/VISA",
    "supported_aids": ["A0000000031010", "A0000000980840"],
    "aip": "2000",
    "track2_data": "4154904674973556D29022010000820083001F",
    "emv_tags": {
        "9F26": "D3967976E30EFAFC",
        "9F27": "80",
        "82": "2000"
    }
}
```

---

## 🎮 **USAGE EXAMPLES**

### **Enable Single Attack**
```kotlin
// Enable PPSE AID Poisoning
hceService.enableEmvAttack(1)
```

### **Load Different Card**  
```kotlin
// Load MasterCard test profile
hceService.loadEmvCard("mastercard")
```

### **Multiple Attack Combination**
```kotlin
// Enable AIP Force Offline + CVM Bypass
hceService.enableEmvAttack(2)  // AIP Force Offline
hceService.enableEmvAttack(5)  // CVM Bypass
```

### **Track2 Spoofing Profiles**
```kotlin
// Switch to high-limit spoofing profile
val track2Module = attackManager.getAttackModule(3) as Track2SpoofingModule
track2Module.setSpoofingProfile("high_limit")
```

---

## 🔍 **ATTACK APPLICABILITY LOGIC**

Each module automatically determines if it's applicable to the current card:

- **PPSE AID Poisoning**: Requires VISA AID in supported AIDs
- **AIP Force Offline**: Requires DDA support in card capabilities  
- **Track2 Spoofing**: Requires non-empty Track2 data
- **Cryptogram Downgrade**: Requires ARQC+TC support
- **CVM Bypass**: Requires configured CVM methods

---

## 📈 **MONITORING & STATUS**

### **Attack Status Tracking**
```kotlin
data class AttackStatus(
    val isActive: Boolean,
    val modificationsApplied: List<String>,
    val detectionRisk: DetectionRisk,
    val expectedImpact: String,
    val lastExecuted: Long
)
```

### **Emulation Summary**
Real-time status of:
- Card loaded (type, PAN, label)
- Total/applicable/active attacks
- Individual attack details and impacts

---

## 🚀 **READY FOR TESTING**

The modular architecture is now complete and ready for:

1. **Real-time Attack Switching** during EMV transactions
2. **Multiple Card Profile Support** via JSON files
3. **Complex Attack Combinations** with multiple modules
4. **Comprehensive Attack Monitoring** and status tracking
5. **Integration with PN532 Terminal** for live testing

**Next Steps**: Build Android APK and test attack modules against PN532 terminal! 💀⚡🎯