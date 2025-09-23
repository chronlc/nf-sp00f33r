---
applyTo: '**'
description: Workspace-specific AI memory for this project
lastOptimized: '2025-09-23T09:20:19.050302+00:00'
entryCount: 26
optimizationVersion: 2
autoOptimize: true
sizeThreshold: 50000
entryThreshold: 20
timeThreshold: 7
---
# Workspace AI Memory
This file contains workspace-specific information for AI conversations.

## Personal Context
*(No entries yet)*

## Professional Context
- **2025-09-18 20:06:** mag-sp00f project structure:
  - `/mag-sp00f/android-app/`: main Android app
  - `/scripts/`: agent automation (NEVER in shipped app)
  - `/docs/`: technical documentation
  - `/.new/`: optimized files

## Technical Preferences
- **2025-09-18 20:06:** Key scripts:
  - `audit_codebase.py`: corruption detection
  - `naming_auditor.py`: convention enforcement
  - `pn532_terminal.py`: PN532 testing
  - `backup_manager.py`: file recovery

## Communication Preferences
*(No entries yet)*

## Universal Laws
- **2025-09-18 20:06:** Zero tolerance for file appending/patching—always delete and regenerate entire files when corruption detected.

## Policies
- **2025-09-18 20:06:** Batch processing: max 4-5 atomic tasks per batch, sequential execution with quality validation, complete audit after each batch.

## Suggestions/Hints
*(No entries yet)*

## Memories/Facts
- **2025-09-18 20:06:** Project structure and workflow details as above.
- **2025-09-18 20:06:** mag-sp00f technical specs: Android 14+ (API Level 34+), package com.mag-sp00f.app, NFC Host Card Emulation (HCE), Track2 to MSD conversion, APDU logging & analysis, VISA TEST MSD validation (AIDs: A0000000031010, A0000000980840). PN532 terminal testing via /dev/ttyUSB0 or /dev/rfcomm0. Material Design 3 UI, MVVM architecture, secure local-only storage. Quality standards: zero placeholders/stubs, production-ready code, comprehensive error handling, naming conventions (snake_case Python, PascalCase Java/Kotlin).
- **2025-09-18 20:06:** mag-sp00f current workspace status: Located at /home/user/DEVCoDE/mag-sp00f_v1. Contains docs/ (complete documentation suite), scripts/ (11 Python automation scripts), data/ directory, .github/ directory, README.md. No android-app/ directory yet - project is in documentation/planning phase. All core automation scripts present: audit_codebase.py, naming_auditor.py, task_tracker.py, manifest_generator.py, backup_manager.py, export_for_release.py, integration_test.py, undo_last_batch.py, code_quality_check.py, test_scripts.py, mailtm_on_demand.py. Ready for Android app development phase.
- **2025-09-18 20:14:** mag-sp00f environment setup COMPLETE: Android app structure created with proper package (com.mag_sp00f.app), NFC/HCE manifests, Material Design 3, build.gradle configured. VSCode integration with tasks.json (build, quality, PN532 testing) and launch.json (Python debugging). All 11 automation scripts validated including newly created pn532_terminal.py with simulation mode. Batch processing system active (batches.yaml, 20 tasks in 5 batches). Build tools confirmed: Gradle, ADB, Python 3.13. Quality gates operational: audit_codebase.py, naming_auditor.py working. Ready for Batch 1 Android development.
- **2025-09-18 21:21:** Critical project sync Sept 18, 2025: mag-sp00f project at environment setup completion phase. Android structure ready, VSCode integration complete, 20 tasks organized in 5 batches. NO batches started yet - ready for Batch 1 "Project Foundation". Key corruption prevention rule from newrule.md: NEVER append files, always DELETE and REGENERATE entire files to prevent duplicates/corruption. Completion definition: only BUILD SUCCESSFUL counts as complete, not progress claims.
- **2025-09-18 21:24:** Updated newrule.md feature requirements: Material3 UI with nav bottom bar, mag-spoof.png background/splash. UI frontend tied to Python backend in Android APK. Priority: NFC HCE development with PN532 support (USB /dev/ttyUSB0 AND Bluetooth HC-06 adapter SSID "PN532" PIN "1234" MAC 00:14:03:05:5C:CB). Switchable PN532 connection modes in EMU menu. Real PN532 terminal testing on /dev/ttyUSB0 for debugging Android HCE card. Standard USA EMV flows testing. APDU flow hooks for GPO/PPSE with default/randomized data generation. Mini fuzzer component.
- **2025-09-18 21:36:** VISA TEST MSD complete workflow found in emv.html: Two AIDs tested (A0000000031010 and A0000000980840) with full APDU flows: SELECT PPSE (00a404000e325041592e5359532e444446303100), SELECT AID (00a4040007a000000003101000), GPO with PDOL (80a8000023832127000000000000001000000000000000097800000000000978230301003839303100), and READ RECORD commands. Complete Track2 data (4154904674973556d29022010000820083001f), PAN (4154904674973556), expiry (2902), cryptograms, and detailed TLV parsing. This is the reference implementation for validation.
- **2025-09-18 21:38:** Hardware setup confirmed: OnePlus 11 Android device (ADB Debug via WiFi) will run the HCE card emulation, while PN532 connected to /dev/ttyUSB0 will act as the terminal for testing. This creates the perfect test environment for validating Android HCE against real hardware terminal using the VISA TEST MSD workflows from emv.html.
- **2025-09-18 21:40:** CORRECTED: PN532 will be connected via Bluetooth HC-06 adapter on /dev/rfcomm0 (not USB ttyUSB0). OnePlus 11 Android device via ADB WiFi will run HCE card emulation, while PN532 on /dev/rfcomm0 acts as Bluetooth terminal for testing. HC-06 specs: SSID "PN532", PIN "1234", MAC 00:14:03:05:5C:CB.
- **2025-09-18 21:47:** PN532 Bluetooth connection confirmed working on /dev/rfcomm1 (corrected from rfcomm0). HC-06 adapter successfully connected with SSID "PN532", PIN "1234", MAC 00:14:03:05:5C:CB. APDU commands transmitting successfully. Ready for OnePlus 11 HCE testing via /dev/rfcomm1.
- **2025-09-18 22:05:** CRITICAL: Check newrule.md every 5 minutes for updates. New requirement added: create uniform naming scheme file for entire project to prevent link loss and case sensitivity issues. Created comprehensive naming_scheme.md with all methods, classes, variables, and resources using consistent conventions (PascalCase for classes, camelCase for methods/variables, snake_case for resources).
- **2025-09-18 22:10:** newrule.md updated with header formatting and new incomplete "CHAN" entry. Continue monitoring every 5 minutes. Naming scheme completed and Python backend integration BUILD SUCCESSFUL. Currently on Task 2.3: Create Switchable Connection UI for PN532 modes.
- **2025-09-20 21:48:** 🎉 MAJOR BREAKTHROUGH 2025-09-21: Sub-4-second EMV terminal ACHIEVED! pn532_terminal_rapid.py using RFIDIOt approach delivers 2.81-second complete EMV workflow. ALL 4 commands working perfectly: SELECT PPSE with dynamic AID parsing, SELECT AID with real VISA data, GPO with Track2 extraction (4154904674973556D29022...), READ RECORD with complete cardholder data. Ultra-fast optimizations: 0.05s command delays, 0.3s timeouts, zero inter-command delays, optimized PN532 frame parsing. PRODUCTION READY!
- **2025-09-20 21:54:** 🚀 SPEED BOOST SUCCESS 2025-09-21: Optimized EMV terminal from 2.81s to 2.32s (17% improvement)! Blazing-fast optimizations: 0.04s delays (down from 0.05s), 0.25s timeout (down from 0.3s), hyper-optimized frame parsing, pre-built frame construction. All 4 EMV commands working perfectly with real Track2 data extraction. Terminal cleanup completed - multiple bash sessions closed. Production ready at 2.32 seconds!
- **2025-09-20 23:41:** GRADLEW DISAPPEARING ISSUE SOLVED Sept 20, 2025: Problem was VSCode tasks running from wrong directory (android-app/ instead of root). gradlew kept "disappearing" because it was never created in correct location due to working directory confusion. Solution: 1) Created working gradlew script in root directory, 2) Fixed VSCode task cwd from "${workspaceFolder}/android-app" to "${workspaceFolder}", 3) Updated task args to "android-app:assembleDebug" to target Android module. Build now running successfully - reached 80% completion, progressing toward BUILD SUCCESSFUL status.
- **2025-09-20 23:45:** Sept 20, 2025 - EMV Attack Method Testing COMPLETED: Created comprehensive attack analysis framework using real Android HCE data. Analyzed 5 attack vectors: (1) PPSE AID Poisoning VISA→MasterCard, (2) AIP Force Offline 2000→2008, (3) Track2 PAN Spoofing, (4) Cryptogram Downgrade ARQC→TC, (5) CVM Bypass. All attacks maintain valid EMV TLV structure. Baseline EMV working perfectly at 2.33s. Ready for Android HCE integration with CardManipulationManager.kt to implement real-time attack switching.
- **2025-09-21 03:14:** File corruption issue: Pn532CardReader.kt keeps getting duplicated content. Always delete and recreate files cleanly when corruption detected. Use DELETE→REGENERATE protocol strictly.
- **2025-09-21 11:57:** Sept 21, 2025 - COMPLETE MEMORY SYNC PERFORMED: Synced with ALL memory sources - MCP persistent memory (74 entries, 4 optimization versions), workspace memory (.github/instructions/memory.instructions.md), local project memory (docs/project_memory.md with PN532 2.32s EMV terminal success), all docs/ directory (23 comprehensive files including EMV attack research), newrule.md (live rules with 5-minute sync, DELETE→REGENERATE protocol, BUILD SUCCESSFUL requirement). Environment status: Python 3.13.7, virtual env at .venv, gradle wrapper present. Current BUILD FAILED status with interface mismatch errors in CardReadingCallback, NfcCardReader.kt byte literal issues, and EmvCardData nullable types. Per newrule.md: must achieve BUILD SUCCESSFUL before UI polishing work on READ menu, EMULATION menu, CARD DB menu.
- **2025-09-23 01:54:** Sept 23, 2025 - COMPREHENSIVE PROJECT SYNC COMPLETED: Full workspace scan performed, all memory sources synced (MCP persistent memory, workspace memory, newrule.md), all existing functionality reviewed. Current status: BUILD SUCCESSFUL with professional Material3 UI, comprehensive navigation (MainActivity with CardReadingFragment, EmulationFragment, CardDatabaseFragment, AnalysisFragment), all attack modules consolidated into EmulationProfiles.kt with real EMV manipulation, all placeholder/simulation code eliminated per newrule.md compliance. Project has production-grade NFC EMV card reading with unmasked PAN display, live APDU logging, complete card database management, HCE emulation system, and EMV analysis tools. Code base fully compliant with atomic write protocols, DELETE→REGENERATE approach, and newrule.md requirements.
- **2025-09-23 01:57:** Sept 23, 2025 - GIT PUSH SUCCESSFUL: Committed and pushed production-grade Android EMV app to GitHub repository chronlc/mag-sp00f. Major commit 6cbd493 includes: Material3 UI, complete CRUD system, consolidated attack modules (EmulationProfiles.kt), professional database management, real-time NFC card reading, all newrule.md compliance features. 27 files changed, 2511 insertions, 731 deletions. Repository now contains production-ready Android 14 NFC EMV app with professional UI and comprehensive EMV capabilities.
- **2025-09-23 02:02:** Sept 23, 2025 - MAG-SP00F APK INSTALLATION SUCCESSFUL: App installed on Android device b470269a and launched successfully. MainActivity started with com.mag_sp00f.app/.ui.MainActivity intent. Production-grade Android 14 NFC EMV app now running on device ready for testing. All UI components (CardReadingFragment, CardDatabaseFragment, EmulationFragment) available for user interaction.
- **2025-09-23 02:19:** Sept 23, 2025 - SPLASH SCREEN & STATUS BAR FIX COMPLETED: Successfully implemented professional splash screen with nfspoof3.png image, fixed Android system status bar color to black (removing green), created SplashActivity with 3-second display and smooth transition to MainActivity. Fixed CardDatabaseFragment import error. Updated AndroidManifest.xml to make SplashActivity the launcher. Status bar now shows black instead of green throughout the app. BUILD SUCCESSFUL achieved and APK installed successfully.