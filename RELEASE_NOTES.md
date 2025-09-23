# nf-sp00f v1_alpha Release Notes

## Overview
First alpha release of nf-sp00f, a professional EMV security research tool designed for comprehensive card analysis and TTQ manipulation.

## Key Features
- **Brute Force TTQ Card Reader**: Dynamic Terminal Transaction Qualifiers manipulation for comprehensive EMV analysis
- **Six EMV Workflow Types**: Standard, Offline Forced, CVM Required, Issuer Auth, Enhanced Discovery, and Custom Research
- **Real-time APDU Logging**: Complete TX/RX monitoring with professional EMV tag analysis
- **Unmasked PAN Display**: Full card number visibility for legitimate security research
- **Hardware Support**: NFC (Android HCE), PN532 Bluetooth (HC-06), and PN532 USB direct connection

## Technical Specifications
- **Target SDK**: 33 (Android 13)
- **Minimum SDK**: 26 (Android 8.0+)
- **Architecture**: MVVM with Jetpack Compose
- **EMV Standards**: Complete BER-TLV parsing with 70+ standard tag support
- **Database**: SQLite with Room persistence for comprehensive card profile storage

## Installation Requirements
- Android 8.0 or higher
- NFC-enabled device
- Permissions: NFC, Storage, Location (for Bluetooth PN532)

## Security Notice
This application is designed exclusively for legitimate security research and educational purposes. Users must comply with all applicable laws and regulations. The developers do not support any malicious or illegal use.

## Known Limitations
- Alpha release focused on core functionality
- Advanced attack modules in development
- Professional validation tools planned for beta release

## Support
For legitimate security research inquiries and technical support, please use the GitHub issues system.
