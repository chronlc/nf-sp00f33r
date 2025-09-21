# mag-sp00f Uniform Naming Scheme
**Case-Sensitive Project-Wide Naming Conventions**  
*Generated: September 19, 2025*

## 🏴‍☠️ ELITE NAMING STANDARDS

### **PACKAGE STRUCTURE**
```
com.mag_sp00f.app
├── ui/                    # User Interface Components
├── hardware/             # PN532 & NFC Hardware
├── emulation/           # HCE & Emulation Logic  
├── data/                # Data Models & Storage
├── utils/               # Utility Classes
└── services/            # Background Services
```

---

## **KOTLIN CLASSES (PascalCase)**

### **Core Classes**
- `MainActivity`
- `EmulationFragment` 
- `PN532Manager`
- `EnhancedHceService`

### **Hardware Classes**
- `AndroidBluetoothHC06Adapter`
- `AndroidUSBSerialAdapter`
- `PN532ConnectionManager`
- `HardwareConfigManager`

### **Data Models**
- `EmulationConfig`
- `ApduCommand`
- `CardData`
- `EmvTransaction`
- `Track2Data`

### **ViewModels**
- `EmulationViewModel`
- `HardwareViewModel`
- `SettingsViewModel`
- `LogViewModel`

---

## **KOTLIN METHODS & VARIABLES (camelCase)**

### **Connection Methods**
- `connectToPN532()`
- `switchConnectionMode()`
- `getConnectionStatus()`
- `validateHardware()`

### **Emulation Methods**
- `startHceEmulation()`
- `processApduCommand()`
- `generateEmvResponse()`
- `handleGpoRequest()`
- `handlePpseRequest()`

### **Data Processing**
- `parseTrack2Data()`
- `extractPanNumber()`
- `validateExpiryDate()`
- `formatApduResponse()`

### **Variables**
- `connectionMode`
- `isEmulationActive`
- `currentApduCommand`
- `track2DataString`
- `panNumber`
- `expiryDate`

---

## **XML RESOURCES (snake_case)**

### **Layout Files**
- `activity_main.xml`
- `fragment_emulation.xml`
- `dialog_hardware_config.xml`
- `item_apdu_log.xml`

### **Drawable Resources**
- `mag_spoof_logo.png`
- `ic_nfc_enabled.xml`
- `ic_bluetooth_connected.xml`
- `ic_usb_connected.xml`
- `bg_gradient_elite.xml`

### **String Resources**
- `app_name`
- `emulation_status_active`
- `connection_mode_bluetooth`
- `connection_mode_usb`
- `error_hardware_not_found`

### **Color Resources**
- `color_elite_green`
- `color_hacker_red`
- `color_terminal_amber`
- `color_background_dark`

---

## **PYTHON BACKEND (snake_case)**

### **Script Files**
- `pn532_terminal.py`
- `pn532_live_terminal.py`
- `apdu_analyzer.py`
- `emv_flow_handler.py`

### **Class Names (PascalCase)**
- `PN532LiveTerminal`
- `ApduCommandProcessor`
- `EmvFlowAnalyzer`
- `HardwareManager`

### **Function Names (snake_case)**
- `connect_to_hardware()`
- `send_apdu_command()`
- `parse_emv_response()`
- `validate_track2_data()`

### **Variables (snake_case)**
- `connection_port`
- `apdu_response`
- `track2_data`
- `pan_number`

---

## **CONSTANTS (SCREAMING_SNAKE_CASE)**

### **Connection Constants**
```kotlin
const val PN532_BLUETOOTH_SSID = "PN532"
const val PN532_BLUETOOTH_PIN = "1234"
const val PN532_BLUETOOTH_MAC = "00:14:03:05:5C:CB"
const val USB_DEVICE_PATH = "/dev/ttyUSB0"
const val BLUETOOTH_DEVICE_PATH = "/dev/rfcomm1"
```

### **EMV Constants**
```kotlin
const val VISA_MSD_AID = "A000000003101000"
const val PPSE_NAME = "325041592E5359532E444446303100"
const val SELECT_PPSE_CMD = "00A404000E"
const val GPO_COMMAND = "80A80023"
```

### **Timeout Constants**
```kotlin
const val CONNECTION_TIMEOUT_MS = 5000
const val APDU_RESPONSE_TIMEOUT_MS = 3000
const val HARDWARE_INIT_DELAY_MS = 2000
```

---

## **INTERFACE NAMING**

### **Kotlin Interfaces**
- `PN532ConnectionInterface`
- `ApduProcessorInterface` 
- `EmulationStateListener`
- `HardwareStatusCallback`

### **Abstract Classes**
- `BaseHardwareAdapter`
- `BaseEmulationHandler`
- `BaseApduProcessor`

---

## **FILE NAMING CONVENTIONS**

### **Source Files**
- Kotlin: `ClassName.kt`
- XML Layouts: `type_name.xml`
- Python: `module_name.py`

### **Resource Files**
- Images: `prefix_description.png/svg`
- Icons: `ic_description.xml`
- Backgrounds: `bg_description.xml`

---

## **DATABASE NAMING**

### **Table Names (snake_case)**
- `apdu_logs`
- `emulation_sessions`
- `hardware_configs`
- `card_profiles`

### **Column Names (snake_case)**
- `session_id`
- `apdu_command`
- `response_data`
- `timestamp_ms`
- `connection_type`

---

## **LOGGING TAGS**

### **Android LogCat Tags**
- `"MAG_SPOOF_MAIN"`
- `"PN532_HARDWARE"`
- `"HCE_EMULATION"`
- `"APDU_PROCESSOR"`

### **Python Logging**
- `"pn532.terminal"`
- `"apdu.analyzer"`
- `"emv.processor"`
- `"hardware.manager"`

---

## **🔥 ELITE VALIDATION RULES**

1. **NO mixed case in same context**
2. **NO underscores in Kotlin class names**
3. **NO camelCase in XML resources**
4. **NO spaces in any identifiers**
5. **CONSISTENT prefixes for related items**
6. **DESCRIPTIVE but CONCISE naming**

---

## **⚡ ANTI-CORRUPTION MEASURES**

- **NEVER** mix naming conventions within same file
- **ALWAYS** use project-wide search before adding new names
- **VERIFY** case sensitivity on all imports/references  
- **VALIDATE** naming scheme compliance in code reviews
- **REGENERATE** files completely if naming corruption detected

---

**💀 REMEMBER: CASE SENSITIVITY SAVES LIVES! 💀**  
**🏴‍☠️ UNIFORM NAMING = NO LINK LOSS! 🏴‍☠️**