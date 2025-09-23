<div align="center">
  <img src="android-app/src/main/res/drawable/nfspoof3.png" alt="nf-sp00f Logo" width="200"/>
</div>

# nf-sp00f

Advanced NFC EMV Card Reader and Security Research Tool with TTQ Brute Force Capabilities

## Overview

nf-sp00f is a comprehensive Android application designed for EMV card security research and analysis. The application provides advanced NFC card reading capabilities, dynamic TTQ workflow manipulation, and comprehensive EMV data extraction for security researchers and professionals.

## Features

### Core Functionality
- **Brute Force TTQ Card Reader**: Dynamic Terminal Transaction Qualifiers manipulation
- **Multiple EMV Workflows**: Six different workflow types for comprehensive card analysis
- **Real-time APDU Logging**: Complete transaction monitoring with TX/RX analysis
- **Unmasked PAN Display**: Full card number visibility for research purposes

### Advanced Capabilities
- **BER-TLV Dynamic Parsing**: Professional EMV tag analysis and PDOL construction
- **Continuous Read Mode**: Automated card scanning for bulk analysis
- **Stealth Mode**: Silent operation for discrete research
- **Persistent Card Database**: Comprehensive card profile storage and management

### Hardware Support
- **NFC (Android HCE)**: Built-in NFC card emulation and reading
- **PN532 Bluetooth**: HC-06 adapter support for external readers
- **PN532 USB**: Direct USB connection for professional setups

## Technical Specifications

### EMV Workflows
1. **Standard Contactless**: Basic EMV transaction flow
2. **Offline Forced**: Force offline authentication
3. **CVM Required**: Cardholder verification method testing
4. **Issuer Authentication**: Enhanced issuer validation
5. **Enhanced Discovery**: Comprehensive AID discovery
6. **Custom Research**: User-defined workflow parameters

### Data Extraction
- Complete EMV tag parsing (70+ standard tags)
- Dynamic PDOL and CDOL construction
- Application Interchange Profile (AIP) analysis
- Application File Locator (AFL) processing
- Track2 data extraction and analysis

## Installation

### Requirements
- Android 8.0 (API 26) or higher
- NFC-enabled device
- Optional: PN532 NFC module for external operations

### Installation Steps
1. Download the latest release from the releases section
2. Enable installation from unknown sources
3. Install the APK file
4. Grant required permissions (NFC, Storage, Location)

## Usage

### Basic Card Reading
1. Open the application
2. Navigate to the "Read" tab
3. Select desired TTQ workflow from dropdown
4. Configure continuous read or stealth mode as needed
5. Tap "READ CARD" and present the target card

### Database Management
1. Navigate to the "Database" tab
2. View all captured card profiles
3. Search and filter cards by various criteria
4. Export data in JSON format for analysis

## Development

### Build Requirements
- Android Studio Arctic Fox or later
- Kotlin 1.7+
- Gradle 7.4+
- Target SDK 33
- Min SDK 26

### Build Instructions
```bash
git clone https://github.com/chronlc/nf-sp00f33r.git
cd nf-sp00f33r
./gradlew android-app:assembleDebug
```

## Security Notice

This application is designed for legitimate security research and educational purposes only. Users are responsible for complying with all applicable laws and regulations. The developers do not condone or support any malicious or illegal use of this software.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

nf-sp00f is provided "as is" without warranty of any kind. The developers are not liable for any damages or legal issues arising from the use of this software. Use at your own risk and ensure compliance with local laws and regulations.

## Contributing

Contributions are welcome for legitimate security research purposes. Please ensure all contributions maintain the professional and legal standards of the project.

## Version History

### v1_alpha
- Initial release with core functionality
- TTQ workflow manipulation
- Basic EMV data extraction
- NFC and PN532 hardware support
- Professional UI with Material3 design
