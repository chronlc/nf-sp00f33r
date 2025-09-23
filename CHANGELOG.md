# Changelog

All notable changes to nf-sp00f will be documented in this file.

## [v1_alpha] - 2025-09-23

### Added
- Initial release of nf-sp00f EMV security research tool
- Brute Force TTQ Card Reader with dynamic workflow manipulation
- Six EMV workflow types for comprehensive card analysis
- Real-time APDU transaction logging with TX/RX monitoring
- Unmasked PAN display for security research purposes
- BER-TLV dynamic parsing with professional EMV tag analysis
- Continuous read mode for automated card scanning
- Stealth mode for discrete research operations
- Persistent card database with comprehensive profile storage
- NFC (Android HCE) card emulation and reading support
- PN532 Bluetooth (HC-06) adapter integration
- PN532 USB direct connection support
- Professional Material3 UI design
- Complete EMV tag parsing (70+ standard tags)
- Dynamic PDOL and CDOL construction
- Application Interchange Profile (AIP) analysis
- Application File Locator (AFL) processing
- Track2 data extraction and comprehensive analysis

### Technical Details
- Target SDK: 33 (Android 13)
- Minimum SDK: 26 (Android 8.0)
- Build tools: Gradle 7.4+, Kotlin 1.7+
- Architecture: MVVM with Jetpack Compose
- Database: SQLite with Room persistence library
- NFC: Android HCE with ISO-DEP support
- External hardware: PN532 via Bluetooth and USB

### Security Features
- Real-time EMV workflow manipulation
- TTQ brute force capabilities
- Comprehensive card data extraction
- APDU command injection support
- Dynamic terminal capability modification
- Professional EMV attack vector analysis

### User Interface
- Professional dark theme with elite styling
- Intuitive navigation with bottom tab bar
- Real-time card reading feedback
- Comprehensive database management
- Advanced search and filtering options
- JSON export capabilities for analysis
