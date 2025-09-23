# Advanced EMV Attack Methods Research
## Extended Attack Vectors for Android HCE Emulation

Based on EMV specifications, real-world vulnerabilities, and security research.

---

## 🔥 **ADVANCED ATTACK CATEGORIES**

### **1. OFFLINE TRANSACTION MANIPULATION**

#### **Force Offline Approval (Enhanced)**
- **Basic Method**: AIP bit manipulation (2000→2008)
- **Advanced Methods**:
  - Terminal Verification Results (TVR) manipulation
  - Terminal Action Analysis (TAA) forcing
  - Issuer Action Code (IAC) bypass
  - Application Usage Control (AUC) modification

#### **Offline Data Authentication Bypass**
- **SDA Downgrade**: Force Static Data Authentication
- **DDA Weakness Exploit**: Manipulate Dynamic Data Authentication
- **CDA Bypass**: Combined Data Authentication circumvention
- **Certificate Chain Breaking**: Invalid certificate acceptance

### **2. CARDHOLDER VERIFICATION METHOD (CVM) ATTACKS**

#### **CVM List Manipulation**
- **No CVM Required**: Remove all verification requirements
- **PIN Bypass**: Alter PIN verification flags
- **Signature Bypass**: Remove signature requirements  
- **CVM Limit Manipulation**: Change amount limits for verification
- **Online PIN Downgrade**: Force offline PIN or no PIN

#### **CVM Rule Priority Attack**
- Reorder CVM list to prefer weaker methods
- Change CVM rule conditions and limits
- Manipulate terminal CVM capabilities

### **3. TRANSACTION AMOUNT MANIPULATION**

#### **Amount Substitution Attacks**
- **Authorized Amount**: Modify amount in APDU
- **Other Amount**: Cashback/tip manipulation
- **Currency Code**: Exchange rate exploitation
- **Amount Binary**: Modify binary amount representation

#### **Transaction Type Manipulation**
- **Purchase→Refund**: Change transaction type
- **Cash Advance Limits**: Bypass cash advance restrictions
- **Contactless Limits**: Exceed contactless transaction limits

### **4. APPLICATION SELECTION ATTACKS**

#### **AID Priority Manipulation**
- **Application Priority Indicator**: Change selection priority
- **PSE Directory**: Modify payment system environment
- **Application Selection**: Force specific application selection
- **Kernel Selection**: Contactless kernel manipulation

#### **Multi-Application Card Attacks**
- **Application Switching**: Switch between applications mid-transaction
- **Cross-Application Data**: Use data from different applications
- **Application Blocking**: Prevent access to secure applications

### **5. CRYPTOGRAPHIC ATTACKS**

#### **Cryptogram Manipulation**
- **ARQC→TC**: Authorization Request→Transaction Certificate
- **ARQC→AAC**: Force application authentication cryptogram
- **TC Generation**: Generate fake transaction certificates
- **Cryptogram Replay**: Replay previous cryptograms

#### **Authentication Data Manipulation**
- **Application Transaction Counter (ATC)**: Replay/modify counter
- **Unpredictable Number**: Predictable number generation
- **Terminal Challenge**: Weak challenge generation
- **Dynamic Signature**: Bypass signature verification

### **6. DATA INTEGRITY ATTACKS**

#### **TLV Structure Manipulation**
- **Length Field Attacks**: Buffer overflow via length manipulation
- **Tag Confusion**: Use wrong tags for data elements
- **Primitive/Constructed**: Manipulate TLV encoding
- **Indefinite Length**: Exploit indefinite length encoding

#### **Data Element Spoofing**
- **Terminal Capabilities**: Modify terminal capability flags
- **Terminal Type**: Change terminal type designation
- **Merchant Category Code**: Change merchant classification
- **Terminal Country Code**: Geographic restriction bypass

### **7. CONTACTLESS-SPECIFIC ATTACKS**

#### **Proximity Payment Attacks**
- **Transaction Limit Bypass**: Exceed contactless limits
- **Kernel Identifier**: Force specific contactless kernel
- **CVM Limit Override**: Bypass contactless CVM limits
- **Multiple Tap**: Multiple transaction execution

#### **NFC Communication Attacks**
- **ISO14443 Layer**: Low-level communication manipulation
- **Anti-Collision**: Manipulate card selection process
- **Frame Timing**: Exploit timing-based vulnerabilities
- **Power Analysis**: Side-channel attack simulation

### **8. TERMINAL CAPABILITY ATTACKS**

#### **Terminal Verification Results (TVR) Manipulation**
- **Offline Processing**: Force offline transaction processing
- **PIN Entry**: Manipulate PIN entry capabilities
- **Risk Management**: Bypass risk management checks
- **Issuer Authentication**: Skip issuer authentication

#### **Terminal Action Analysis (TAA)**
- **Approval Code**: Force transaction approval
- **Decline Code**: Prevent transaction decline
- **Online Processing**: Skip online processing requirements
- **Additional Processing**: Bypass additional verifications

---

## 🛠️ **IMPLEMENTATION PRIORITIES**

### **HIGH PRIORITY ATTACKS** (Add to modules)

1. **Advanced Force Offline**
   - TVR manipulation
   - IAC bypass
   - Multiple offline forcing techniques

2. **Enhanced CVM Bypass**
   - CVM list reordering
   - PIN limit manipulation
   - Online PIN downgrade

3. **Transaction Amount Attacks**
   - Amount substitution
   - Currency manipulation
   - Transaction type changes

4. **Cryptogram Advanced Attacks**
   - ATC manipulation
   - Cryptogram replay
   - Authentication bypass

### **MEDIUM PRIORITY ATTACKS**

5. **Data Authentication Bypass**
   - SDA/DDA/CDA manipulation
   - Certificate chain attacks

6. **Application Selection Attacks**
   - AID priority manipulation
   - Multi-application exploits

### **RESEARCH/FUTURE ATTACKS**

7. **TLV Structure Attacks**
   - Length field exploitation
   - Tag confusion attacks

8. **Contactless-Specific Exploits**
   - Kernel-specific vulnerabilities
   - NFC layer attacks

---

## 📊 **ATTACK EFFECTIVENESS MATRIX**

| Attack Type | Detection Risk | Implementation Complexity | Real-World Impact |
|-------------|----------------|---------------------------|-------------------|
| Force Offline (Basic) | Medium | Low | High |
| Force Offline (Advanced) | Low | Medium | Critical |
| CVM Bypass | Low | Low | High |
| CVM List Manipulation | Medium | Medium | Critical |
| Amount Substitution | High | Low | Critical |
| Currency Manipulation | Medium | Low | High |
| Cryptogram Downgrade | High | Medium | Critical |
| ATC Manipulation | Medium | High | High |
| Authentication Bypass | High | High | Critical |
| Transaction Type Change | Medium | Medium | High |

---

## 🔬 **RESEARCH SOURCES**

- EMV 4.4 Book 1-4 Specifications
- PCI Security Standards Council documentation
- Academic papers on EMV vulnerabilities
- Real-world EMV attack case studies
- Contactless payment security research
- NFC communication protocol analysis

---

## 🎯 **NEXT IMPLEMENTATION STEPS**

1. **Expand Force Offline Module** with TVR/IAC manipulation
2. **Enhanced CVM Module** with list manipulation and limits
3. **New Amount Manipulation Module** for transaction amounts
4. **Advanced Cryptogram Module** with ATC and replay attacks
5. **Transaction Type Module** for purchase/refund manipulation
6. **Authentication Bypass Module** for SDA/DDA/CDA attacks

Each module will follow the existing architecture with EMV card data applicability checking and modular attack application.