# INSTRUCTIONS - MAG-SP00F PROJECT (EFFICIENCY OPTIMIZED)

---

## 🎯 PROJECT IDENTITY & SCOPE

### Core Project Information
- **Project Name:** mag-sp00f
- **Package:** com.mag-sp00f.app
- **Platform:** Android 14+ with NFC/HCE capabilities
- **Purpose:** Advanced magstripe emulation and security analysis
- **Development Model:** Efficiency-first with zero tolerance for code corruption

---

## 🚨 CRITICAL ANTI-CORRUPTION RULES (ZERO TOLERANCE)

### File Integrity Requirements
- **NEVER append or patch code** to existing files
- **ALWAYS delete and regenerate** entire files when corruption detected
- **NO duplicate imports** throughout any file
- **NO repeated functions/classes** in same file
- **NO functions outside proper scope**
- **NO mixed naming conventions** within files

### Corruption Detection Triggers
When ANY of these patterns appear, immediately trigger file regeneration:
- Multiple import blocks scattered throughout file
- Duplicate function or class definitions
- Code appended after main logic blocks
- Inconsistent naming conventions (mixed snake_case/PascalCase)
- Functions defined outside class/module scope
- Repeated code blocks or logic sections

### File Regeneration Protocol
1. **Detect corruption** using automated scanning
2. **Delete corrupted file** completely
3. **Regenerate from scratch** with ALL requirements
4. **Validate syntax** thoroughly before writing
5. **Ensure production-grade quality** throughout
6. **Document regeneration** in CHANGELOG.md

---

## 📊 EFFICIENCY & PERFORMANCE REQUIREMENTS

### Operation Optimization
- **Batch related operations** to minimize tool calls
- **Use multi_replace_string_in_file** for multiple edits
- **Read large file sections** instead of piecemeal access
- **Minimize redundant validations** through caching
- **Track performance metrics** for continuous improvement

### Quality Gates
- **Zero placeholders** (no TODO, FIXME, stub code)
- **Production-ready code** at all times
- **Comprehensive error handling** with user-friendly messages
- **Clear documentation** for human maintainers
- **Consistent naming conventions** project-wide

### Time Efficiency Targets
- **Sub-2-minute batch completion** for standard tasks
- **First-pass audit success** rate >95%
- **Zero file regeneration** incidents per project
- **100% naming convention** compliance

---

## 🔄 BATCH PROCESSING RULES

### Batch Structure
- **Maximum 4-5 atomic tasks** per batch
- **Self-contained tasks** with clear completion criteria
- **Sequential processing** with quality validation between tasks
- **Complete audit** after each batch
- **Comprehensive summary** upon batch completion

### Batch Execution Protocol
1. **Load batch definition** from batches.yaml
2. **Pre-validate all target files** for corruption
3. **Execute tasks sequentially** with monitoring
4. **Post-validate outputs** for quality compliance
5. **Run complete audit suite** (audit, naming, lint)
6. **Mark batch COMPLETE** with timestamp
7. **Update Remember MCP** with progress and learnings

### Error Handling
- **Flag unclear tasks** as NEEDS INPUT
- **Skip problematic tasks** rather than getting stuck
- **Document all issues** for human review
- **Continue processing** other tasks when possible
- **Provide actionable recommendations** for resolution

---

## 🏗️ PROJECT STRUCTURE REQUIREMENTS

### Directory Organization
```
/mag-sp00f/
├── android-app/           # Main Android application
│   ├── src/main/java/     # Java/Kotlin source (PascalCase)
│   ├── src/main/res/      # Android resources (lowercase_underscore)
│   └── build.gradle       # Build configuration
├── scripts/               # Agent automation (snake_case)
│   ├── audit_codebase.py      # Code quality validation
│   ├── naming_auditor.py      # Naming convention enforcement
│   ├── lint_codebase.py       # Style and syntax validation
│   ├── pn532_terminal.py      # PN532 testing automation
│   └── backup_manager.py      # File backup and recovery
├── docs/                  # Documentation (lowercase)
├── .new/                  # Optimized documentation
└── [project files]       # LOADER.md, README.md, etc.
```

### Naming Convention Enforcement
- **Python files:** snake_case with descriptive names
- **Java/Kotlin classes:** PascalCase following Android conventions
- **Java/Kotlin methods:** camelCase with clear verb-noun structure
- **Android resources:** lowercase with underscores
- **Package names:** com.mag-sp00f.app structure

---

## 🔧 ANDROID APPLICATION REQUIREMENTS

### Architecture Components
- **MainActivity:** Material3 Bottom Navigation (not drawer) with mag-spoof.png background
- **Enhanced HCE Service:** Host Card Emulation with APDU flow hooks for GPO/PPSE
- **Dual PN532 Manager:** USB (/dev/ttyUSB0) and Bluetooth HC-06 connectivity
- **Python Backend Integration:** Embedded Python runtime for automation and testing
- **Real-time APDU Monitor:** Live debugging between Android HCE and PN532 terminal
- **Mini Fuzzer Engine:** APDU fuzzing for security testing and vulnerability analysis
- **Track2 Processor:** Enhanced magstripe data input with real-time validation
- **Response Data Generator:** Default and randomized APDU response generation

### Technical Specifications
- **Target SDK:** Android 14+ (API Level 34+)
- **Minimum SDK:** Android 8.0 (API Level 26)
- **Architecture:** MVVM with LiveData and ViewModel
- **UI Framework:** Material Design 3 with Bottom Navigation and dark/light theme support
- **NFC Technology:** Host Card Emulation (HCE) with ISO 14443 and APDU flow hooks
- **PN532 Connectivity:** Dual mode - USB Serial (/dev/ttyUSB0) and Bluetooth HC-06
- **Python Integration:** Embedded runtime with pyserial, bluetooth, and PN532 libraries
- **Security Testing:** Integrated mini fuzzer with APDU vulnerability analysis

### Feature Implementation
- **Track2 Input Interface:** Secure input with real-time validation and MSD conversion
- **Dual PN532 Control:** Switchable connection modes in EMU menu (USB/Bluetooth/Off)
- **APDU Flow Hooks:** Real-time interception of GPO and PPSE commands with response generation
- **Real-time Terminal Testing:** Live debugging using PN532 connected to /dev/ttyUSB0
- **USA EMV Flow Validation:** Standard EMV workflow testing and validation
- **Mini Fuzzer Interface:** APDU fuzzing controls with security analysis results
- **Python Backend Integration:** Embedded automation scripts for PN532 control
- **Performance Metrics:** Response time analysis and EMV flow performance tracking

---

## 🧪 TESTING & VALIDATION REQUIREMENTS

### PN532 Terminal Integration
- **Hardware Support:** PN532 via /dev/ttyUSB0 or /dev/rfcomm0
- **Automated Testing:** Complete APDU workflow validation
- **Response Verification:** Against known-good VISA MSD patterns
- **Performance Analysis:** Response time and reliability metrics

### VISA MSD Test Workflows
#### Required AID Support
- **A0000000031010** (Visa MSD)
- **A0000000980840** (US Common Debit)

#### Workflow Validation Steps
1. **SELECT PPSE:** TX: 00A404000E325041592E5359532E444446303100
2. **SELECT AID:** TX: 00A4040007A000000003101000 (dynamic based on AID)
3. **GPO:** TX: 80A8000008832127000000000000001000000000000000097800000000000978230301003839303100
4. **READ RECORD:** TX: 00B2010C00

### Quality Validation Scripts
- **audit_codebase.py:** Detect corruption, placeholders, duplicates
- **naming_auditor.py:** Enforce naming conventions project-wide
- **lint_codebase.py:** Code style and syntax validation
- **pn532_terminal.py:** Hardware testing and APDU validation

---

## 💾 MEMORY & PERSISTENCE REQUIREMENTS

### Remember MCP Integration
- **Project Context Storage:** Coding standards, preferences, rules
- **Pattern Recognition:** Successful code patterns and architectures
- **Error Prevention:** Common mistakes and their solutions
- **Performance Tracking:** Efficiency metrics and improvement trends

### Data Management
- **Local Storage Only:** No external data transmission
- **Temporary Data:** Automatic cleanup after sessions
- **Secure Handling:** Proper memory management for sensitive data
- **Privacy Protection:** No user data exposure in logs

---

## 🛡️ SECURITY & PRIVACY REQUIREMENTS

### Code Security Standards
- **No arbitrary shell execution** without explicit approval
- **Agent scripts never shipped** with application
- **Comprehensive input validation** for all user data
- **Secure memory handling** for magstripe information
- **Privacy-first design** principles throughout

### Development Security
- **No secrets in code** or configuration files
- **Proper Android permissions** with minimal scope
- **Secure communication** protocols for NFC
- **Audit trail maintenance** for all operations

---

## 📝 DOCUMENTATION REQUIREMENTS

### Code Documentation Standards
- **File-level docstrings:** Purpose, usage, and key concepts
- **Function docstrings:** Parameters, returns, exceptions, examples
- **Inline comments:** Complex logic explanation for humans
- **Section headers:** Logical code block organization
- **Variable naming:** Self-documenting names with context

### Project Documentation
- **README.md:** Comprehensive project overview and setup
- **LOADER.md:** Agent operational instructions and rules
- **prompts.md:** Template prompts for common operations
- **CHANGELOG.md:** Detailed change tracking with timestamps

---

## 🚀 CONTINUOUS IMPROVEMENT REQUIREMENTS

### Performance Optimization
- **Efficiency Metrics:** Track and improve batch completion times
- **Quality Metrics:** Monitor and enhance first-pass success rates
- **Resource Optimization:** Minimize redundant operations
- **User Experience:** Focus on speed and reliability

### Learning Integration
- **Pattern Recognition:** Identify and store successful approaches
- **Error Prevention:** Learn from mistakes to prevent recurrence
- **Optimization Discovery:** Find and apply efficiency improvements
- **Knowledge Sharing:** Document insights for future development

---

## 🔄 WORKFLOW INTEGRATION REQUIREMENTS

### VSCode Integration
- **Tasks Configuration:** Automated build, run, test, and agent scripts
- **Launch Configuration:** Debug setups for Android and automation
- **Extension Requirements:** Android development and Python support
- **Workspace Settings:** Consistent formatting and validation rules

### Build System Integration
- **Gradle Configuration:** Optimized Android build with quality checks
- **Dependency Management:** Automatic resolution and updates
- **Code Quality Integration:** Automated linting and validation
- **Performance Monitoring:** Build time and resource optimization

---

## 📋 HANDOFF & RELEASE REQUIREMENTS

### Progress Reporting
- **Current Status:** Active batch and completion percentage
- **Quality Metrics:** Recent improvements and trends
- **Outstanding Issues:** NEEDS INPUT items with context
- **Next Actions:** Prioritized recommendations for continuation

### Release Preparation
- **Agent Script Removal:** Clean all automation from shipped code
- **Quality Validation:** Comprehensive final audit and validation
- **Documentation Verification:** Ensure all documentation is current
- **Security Review:** Final security and privacy compliance check

---

*These instructions are optimized for maximum efficiency and quality while maintaining zero tolerance for common failures that waste development time. All operations focus on getting clean, working results quickly while building institutional memory for continuous improvement.*