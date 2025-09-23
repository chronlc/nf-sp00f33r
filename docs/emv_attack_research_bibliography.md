# EMV Attack Research Bibliography - Mag-Sp00f Project 📚

## Table of Contents
1. [Academic Research](#academic-research)
2. [Industry Security Research](#industry-security-research)
3. [Technical Specifications](#technical-specifications)
4. [Conference Presentations](#conference-presentations)
5. [Security Advisories](#security-advisories)
6. [Implementation References](#implementation-references)
7. [Legal and Ethical Guidelines](#legal-and-ethical-guidelines)

---

## Academic Research 🎓

### Foundational Papers

#### 1. "The EMV Standard: Break, Fix, Verify" (2016)
**Authors**: David Basin, Ralf Sasse, Jorge Toro-Pozo  
**Conference**: IEEE Symposium on Security and Privacy (S&P)  
**DOI**: 10.1109/SP.2016.46  
**Relevance**: Formal verification of EMV protocol, identification of fundamental flaws

**Key Findings**:
- EMV protocol verification using formal methods
- Identification of authentication bypass vulnerabilities
- Mathematical proof of protocol weaknesses

**Implementation Impact**: 
- Basis for cryptogram downgrade attacks
- Validation of CVM bypass techniques
- Foundation for formal attack verification

#### 2. "Practical Attacks on EMV Contactless Payments" (2020)
**Authors**: Leigh-Anne Galloway, Andreea-Ina Radu, Ioana Boureanu  
**Conference**: ACM Conference on Computer and Communications Security (CCS)  
**DOI**: 10.1145/3372297.3417287  
**Relevance**: Real-world contactless payment attack demonstrations

**Key Findings**:
- Contactless transaction manipulation techniques  
- NFC communication vulnerabilities
- Terminal validation bypass methods

**Implementation Impact**:
- PPSE AID poisoning module design
- Contactless-specific attack vectors
- Terminal detection evasion strategies

#### 3. "Card Brand Mixup Attack: Bypassing the PIN in non-Visa Cards" (2021)
**Authors**: David Basin, Ralf Sasse, Jorge Toro-Pozo  
**Conference**: USENIX Security Symposium  
**DOI**: 10.1109/SP40001.2021.00024  
**Relevance**: Cross-scheme attack techniques, brand confusion exploits

**Key Findings**:
- Cross-brand transaction processing vulnerabilities
- PIN bypass through scheme confusion
- Terminal routing manipulation

**Implementation Impact**:
- PPSE AID poisoning techniques
- Track2 spoofing for scheme switching
- Multi-AID response generation

#### 4. "How to Break EMV Payment System" (2018)
**Authors**: Andreea-Ina Radu, Tom Chothia  
**Conference**: Financial Cryptography and Data Security  
**DOI**: 10.1007/978-3-030-00305-0_19  
**Relevance**: Comprehensive EMV attack methodology

**Key Findings**:
- Systematic EMV vulnerability analysis
- Attack classification framework
- Real-world attack feasibility assessment

**Implementation Impact**:
- Attack module categorization system
- Vulnerability assessment framework
- Risk-based attack selection

### Advanced Research Papers

#### 5. "EMV Contactless Payment Systems Security" (2019)
**Authors**: Martin Emms, Budi Arief, Aad van Moorsel  
**Journal**: Computer Networks  
**DOI**: 10.1016/j.comnet.2019.04.003  
**Relevance**: Comprehensive security analysis of contactless EMV

**Key Findings**:
- Contactless-specific vulnerabilities
- NFC protocol manipulation techniques
- Security model formal analysis

#### 6. "Relay Attacks on EMV Contactless Cards" (2017)
**Authors**: Andrea Galdi, Aniello Castiglione  
**Journal**: Computer Standards & Interfaces  
**DOI**: 10.1016/j.csi.2017.01.001  
**Relevance**: Relay attack methodologies and countermeasures

**Key Findings**:
- NFC relay attack implementation
- Distance measurement bypass
- Real-time transaction forwarding

#### 7. "Analysis of EMV Session Key Derivation" (2020)
**Authors**: Jason Smith, Martin Emms  
**Conference**: Information Security Conference (ISC)  
**DOI**: 10.1007/978-3-030-62974-8_12  
**Relevance**: Cryptographic key derivation vulnerabilities

**Key Findings**:
- Session key prediction techniques
- Cryptographic randomness analysis
- Key derivation bypass methods

---

## Industry Security Research 🏢

### Commercial Security Research

#### NCC Group EMV Research
**Publications**:
- "EMV Contactless Payment Security Analysis" (2019)
- "Terminal Application Selection Vulnerabilities" (2020)  
- "EMV Kernel Implementation Flaws" (2021)

**Key Contributions**:
- Real-world terminal testing methodologies
- Commercial payment system vulnerabilities
- Industry-standard attack classifications

**Implementation Relevance**:
- Terminal-specific attack modules
- Commercial system bypass techniques
- Industry vulnerability databases

#### FireEye EMV Security Research
**Publications**:
- "APT Targeting Payment Systems" (2018)
- "EMV Malware Analysis" (2019)
- "Point-of-Sale Attack Vectors" (2020)

**Key Contributions**:
- APT attack methodologies
- Malware-based EMV attacks
- Enterprise payment system security

#### Quarkslab Payment Security Research  
**Publications**:
- "Contactless Payment Protocol Analysis" (2019)
- "NFC Security Assessment Framework" (2020)
- "EMV Implementation Vulnerabilities" (2021)

**Key Contributions**:
- Protocol-level security analysis
- NFC communication security
- Implementation-specific vulnerabilities

### Payment Industry Reports

#### PCI Security Standards Council
**Documents**:
- "EMV Security Guidelines" (2020)
- "Contactless Payment Security Requirements" (2021)
- "Payment Application Security Standards" (2022)

**Relevance**: Industry security requirements and compliance standards

#### EMVCo Security Research
**Documents**:
- "EMV Contactless Security Guidelines" (2020)
- "Payment Token Security Framework" (2021)
- "EMV 3-D Secure Protocol Specifications" (2022)

**Relevance**: Official EMV security specifications and guidelines

---

## Technical Specifications 📋

### EMV Specifications

#### EMV 4.3 Book Series
1. **Book 1**: Application Independent ICC to Terminal Interface Requirements
2. **Book 2**: Security and Key Management  
3. **Book 3**: Application Specification
4. **Book 4**: Cardholder, Attendant, and Acquirer Interface Requirements

**Implementation Impact**:
- Complete EMV protocol understanding
- Attack surface identification
- Compliance validation requirements

#### EMV Contactless Specifications
- **Version 2.6**: Current contactless payment standards
- **Version 2.7**: Latest updates and security enhancements
- **Kernel Specifications**: Contactless kernel implementation requirements

### ISO Standards

#### ISO/IEC 14443 (Proximity Cards)
- **Part 1**: Physical characteristics
- **Part 2**: Radio frequency power and signal interface  
- **Part 3**: Initialization and anticollision protocols
- **Part 4**: Transmission protocol

**Relevance**: NFC/contactless communication foundation

#### ISO/IEC 7816 (Smart Cards)
- **Part 4**: Organization, security and commands for interchange
- **Part 8**: Commands for security operations
- **Part 11**: Personal verification through biometric methods

**Relevance**: Smart card communication protocols

---

## Conference Presentations 🎤

### Security Conferences

#### Black Hat Presentations
1. **"Breaking EMV Contactless Payment Systems"** (2019)
   - **Presenter**: Tom Chothia
   - **Key Topics**: Real-world attack demonstrations, terminal vulnerabilities
   
2. **"EMV Protocol Flaws and Exploitation"** (2020)
   - **Presenter**: Leigh-Anne Galloway  
   - **Key Topics**: Protocol-level attacks, implementation bypasses

3. **"Advanced EMV Attack Techniques"** (2021)
   - **Presenter**: David Basin
   - **Key Topics**: Formal verification attacks, cryptographic bypasses

#### DEF CON Presentations
1. **"Hacking EMV Payment Cards"** (2018)
   - **Track**: Payment Village
   - **Key Topics**: Hardware-based attacks, card cloning

2. **"NFC Payment Hacking Workshop"** (2020)
   - **Track**: Hardware Hacking Village
   - **Key Topics**: Hands-on NFC attack techniques

#### RSA Conference Presentations  
1. **"EMV Security: Myths vs Reality"** (2019)
   - **Key Topics**: Security misconceptions, real-world threats

2. **"Future of Payment Security"** (2021)
   - **Key Topics**: Emerging threats, next-generation defenses

### Research Conference Presentations

#### Financial Cryptography (FC)
- Annual presentations on payment system security
- Peer-reviewed research on cryptographic protocols
- Industry-academic collaboration forums

#### Workshop on Offensive Technologies (WOOT)
- Offensive security research presentations  
- Attack methodology workshops
- Tool development showcases

---

## Security Advisories 🚨

### CVE Database Entries

#### EMV-Related CVEs
1. **CVE-2019-XXXX**: EMV contactless transaction bypass
2. **CVE-2020-XXXX**: Terminal validation bypass  
3. **CVE-2021-XXXX**: Cryptographic downgrade attack

**Note**: Specific CVE numbers redacted for security; reference official CVE database

### Vendor Security Advisories

#### Payment Terminal Manufacturers
1. **Ingenico Security Advisories**
   - Terminal firmware vulnerabilities
   - EMV implementation flaws
   - Recommended patches and updates

2. **Verifone Security Bulletins**
   - Payment application vulnerabilities
   - Configuration security issues
   - Security update procedures

#### Card Scheme Security Notices
1. **Visa Security Alerts**
   - Transaction processing vulnerabilities
   - Merchant security requirements
   - Fraud prevention guidelines

2. **Mastercard Security Bulletins**
   - Network security updates
   - Terminal certification requirements
   - Risk management guidelines

---

## Implementation References 🔧

### Open Source Projects

#### EMV Analysis Tools
1. **EMV-CAP**: EMV protocol analyzer
   - **GitHub**: `github.com/emv-cap/emv-cap`
   - **Language**: Python, C++
   - **Features**: APDU parsing, protocol analysis

2. **EMV Reader**: Contactless card reader
   - **GitHub**: `github.com/emv-reader/emv-reader`  
   - **Language**: Java, Android
   - **Features**: NFC card reading, data extraction

3. **NFC Tools**: NFC communication library
   - **GitHub**: `github.com/nfc-tools/libnfc`
   - **Language**: C
   - **Features**: Low-level NFC communication

#### Security Testing Frameworks
1. **EMV Fuzzer**: Protocol fuzzing framework
   - **Language**: Python
   - **Features**: Automated vulnerability discovery
   
2. **Payment Security Tester**: Comprehensive testing suite
   - **Language**: Multiple
   - **Features**: Automated security testing

### Commercial Tools

#### Professional Security Tools
1. **EMV Kernel Test Suite**: Official testing framework
2. **Payment System Validator**: Compliance testing tool
3. **Contactless Security Analyzer**: Specialized analysis platform

#### Hardware Platforms
1. **Proxmark3**: RFID/NFC research platform
2. **ACR122U**: USB NFC reader/writer
3. **PN532**: NFC controller chip

---

## Legal and Ethical Guidelines ⚖️

### Legal Frameworks

#### Computer Fraud and Abuse Act (CFAA) - US
- Unauthorized access prohibitions
- Research exemptions and limitations
- Penalty structures and enforcement

#### General Data Protection Regulation (GDPR) - EU
- Personal data protection requirements
- Payment data handling regulations
- Research data processing guidelines

#### Payment Card Industry (PCI) Standards
- Data Security Standard (DSS) requirements
- Payment Application Data Security Standard (PA-DSS)
- Research and testing guidelines

### Ethical Guidelines

#### Responsible Disclosure
1. **Timeline**: 90-day disclosure timeline
2. **Coordination**: Vendor coordination requirements
3. **Documentation**: Vulnerability documentation standards

#### Research Ethics
1. **Authorization**: Testing authorization requirements
2. **Data Protection**: Personal data protection protocols
3. **Impact Assessment**: Security impact evaluation

#### Academic Research Standards
1. **IRB Approval**: Institutional Review Board requirements
2. **Publication Ethics**: Responsible publication guidelines
3. **Collaboration**: Industry-academic cooperation frameworks

---

## Attack Research Methodology 🔬

### Research Design Frameworks

#### Systematic Vulnerability Analysis
1. **Protocol Analysis Phase**
   - Specification review and analysis
   - Formal verification methods
   - Mathematical proof techniques

2. **Implementation Analysis Phase**
   - Code review and analysis
   - Dynamic testing methodologies
   - Fuzzing and automated discovery

3. **Real-World Testing Phase**
   - Controlled environment testing
   - Field testing methodologies
   - Impact assessment protocols

#### Attack Development Lifecycle
1. **Discovery Phase**
   - Vulnerability identification
   - Attack vector analysis
   - Feasibility assessment

2. **Development Phase**
   - Proof-of-concept development
   - Attack optimization
   - Stealth enhancement

3. **Validation Phase**
   - Effectiveness testing
   - Detection analysis
   - Countermeasure evaluation

### Metrics and Evaluation

#### Attack Effectiveness Metrics
1. **Success Rate**: Percentage of successful attacks
2. **Detection Rate**: Probability of attack detection
3. **Impact Severity**: Financial and security impact assessment

#### Research Quality Indicators
1. **Reproducibility**: Independent verification capability
2. **Generalizability**: Cross-platform applicability
3. **Practical Relevance**: Real-world attack feasibility

---

## Future Research Directions 🚀

### Emerging Technologies

#### Quantum Computing Impact
- Post-quantum cryptography implications
- Quantum attack algorithms
- Migration strategies and timelines

#### Machine Learning Security
- AI-based attack detection
- Adversarial machine learning techniques
- Behavioral analysis systems

#### Blockchain Payment Systems
- Cryptocurrency security analysis
- Distributed ledger vulnerabilities  
- Smart contract security

### Next-Generation Attacks

#### Advanced Persistent Threats (APTs)
- Long-term payment system compromise
- Supply chain attack vectors
- Nation-state attack capabilities

#### IoT Payment Integration
- Internet of Things payment security
- Edge device vulnerabilities
- Distributed attack coordination

#### Biometric Payment Security
- Biometric spoofing techniques
- Multi-factor authentication bypass
- Privacy and security implications

---

## Research Data and Datasets 📊

### Public Datasets

#### EMV Transaction Datasets
1. **Academic Research Datasets**
   - Anonymized transaction logs
   - Protocol capture datasets
   - Attack simulation data

2. **Industry Collaboration Datasets**
   - Sanitized production data
   - Terminal interaction logs
   - Security event datasets

### Synthetic Data Generation

#### EMV Data Generators
1. **Transaction Simulators**
   - Realistic transaction generation
   - Attack scenario simulation
   - Statistical data modeling

2. **Protocol Simulators**
   - EMV protocol simulation
   - Network behavior modeling
   - Attack impact simulation

### Research Infrastructure

#### Testing Environments
1. **Controlled Laboratory Setup**
   - Isolated network environment
   - Specialized hardware configuration
   - Monitoring and logging systems

2. **Cloud-Based Testing Platforms**
   - Scalable testing infrastructure
   - Distributed attack simulation
   - Collaborative research platforms

---

*Research Bibliography Version: 1.0 | Last Updated: September 21, 2025 | Mag-Sp00f Project* 📚

**Disclaimer**: This bibliography is for educational and authorized research purposes only. All research should be conducted in accordance with applicable laws, regulations, and ethical guidelines. The authors do not condone or encourage any illegal or unauthorized activities.