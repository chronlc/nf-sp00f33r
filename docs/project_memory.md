# MAG-SPOOF PROJECT MEMORY LOG
## Critical Technical Knowledge Base
### Last Updated: 2025-09-20

---

## PN532 HARDWARE COMMUNICATION - SOLVED ISSUES

### 🔥 CRITICAL: PN532 Frame Parsing (SOLVED)
**Problem**: PN532 sends concatenated frames with empty frames first
**Solution**: Parser must skip empty frames, find data frame at offset 6 for APDU extraction
**Code Location**: `scripts/pn532_terminal.py` - `send_apdu()` method
**Status**: IMPLEMENTED AND WORKING

### 🔥 CRITICAL: Response Validation (SOLVED) 
**Problem**: 6A82 "File not found" was being treated as success
**Solution**: Only 9000 = success, ALL 6xxx responses = errors/failures
**Code Location**: `scripts/pn532_terminal.py` - workflow execution
**Status**: IMPLEMENTED AND WORKING

### 🔥 CRITICAL: Card Detection (SOLVED)
**Problem**: No card detection before APDU exchange
**Solution**: Use detection command `b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'` with retry logic
**Code Location**: `scripts/pn532_terminal.py` - `detect_card()` method
**Status**: IMPLEMENTED AND WORKING

---

## CURRENT STATUS - PN532 Multi-Workflow Terminal v31.337

### ✅ WORKING COMPONENTS:
1. **Real hardware communication** via /dev/rfcomm1 (Bluetooth HC-06)
2. **Card detection** - Successfully detects Android HCE device
3. **Frame parsing** - Correctly handles PN532 concatenated frames
4. **Response validation** - Proper error code handling
5. **Multi-workflow support** - 5 EMV workflows implemented

### ❌ CURRENT ISSUE - PROGRESS UPDATE:
- **Card connection lost after first APDU** - NOW IDENTIFIED: Connection drops after first successful APDU exchange
- **Target tracking implemented** - System now detects when card connection is lost
- **Root cause**: Android HCE or PN532 losing contactless connection after initial exchange
- **Next step**: Implement re-detection between APDUs or investigate HCE timeout settings

### LATEST TEST RESULTS (2025-09-20 20:35):
- ✅ Card detection working (target selected)
- ✅ SELECT PPSE: Gets proper 6A82 response  
- ❌ SELECT AID: "Short response" - card connection lost
- ✅ System correctly detects loss and prevents invalid APDU attempts

---

## EMV WORKFLOW STATUS

### Workflow 1: VISA MSD Track2-from-GPO (A0000000031010)
- ❌ SELECT PPSE: 6A82 (File not found) - Expected failure
- ❌ SELECT AID: Connection lost (truncated response)
- ❌ GPO: Connection lost
- ❌ READ RECORD: Connection lost

**Next Steps**: Debug why card connection is lost after first APDU

---

## ANDROID HCE STATUS

### Device: 10.0.0.45:5555 (OnePlus 11)
- ✅ **HCE Service Registered**: com.mag_sp00f.app.nfc.EnhancedHceService  
- ✅ **Default Payment Service**: mag_sp00f app
- ✅ **AIDs Registered**:
  - A0000000031010 (VISA MSD)
  - A0000000980840 (US Debit)
  - PPSE AIDs: 315041592E5359532E4444463031, 325041592E5359532E4444463031

---

## HARDWARE SETUP

### PN532 Connection:
- **Interface**: Bluetooth HC-06 adapter
- **Device**: /dev/rfcomm1 
- **HC-06 Specs**: SSID "PN532", PIN "1234", MAC 00:14:03:05:5C:CB
- **Status**: Connected and initialized successfully

### Commands That Work:
- Wake up: `b'\x55\x55\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'`
- Version: `b'\x00\x00\xFF\x02\xFE\xD4\x02\x2A\x00'`
- SAM Config: `b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00'`
- RF Enable: `b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'`
- Card Detect: `b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'`

---

## MEMORY SYNC ISSUES IDENTIFIED

### Problem: 
- AI agent keeps "forgetting" previously solved issues
- Re-implementing same solutions repeatedly
- Memory not being consistently referenced during troubleshooting

### Solution:
- This local memory file should be scanned FIRST before any PN532 troubleshooting
- Update this file with any new discoveries
- Reference this file in newrule.md for mandatory scanning

---

## TODO PRIORITY LIST

1. **IMMEDIATE**: Fix card connection loss after first APDU
2. **HIGH**: Test all 5 workflows with successful APDU chains  
3. **MEDIUM**: Optimize response timeouts and error handling
4. **LOW**: Add workflow switching and enhanced logging

---

## NEWRULE.MD COMPLIANCE STATUS

✅ **REAL DATA ONLY**: All simulation code removed
✅ **Multi-workflow terminal**: 5 workflows implemented  
✅ **PN532 dual mode**: Bluetooth /dev/rfcomm1 working
✅ **Android HCE testing**: Device connected and registered
❌ **Complete EMV flows**: Blocked by connection loss issue

Last Updated: 2025-09-20 20:32 UTC