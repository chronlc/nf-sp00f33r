---
applyTo: '**'
description: Workspace-specific AI memory for this project
lastOptimized: '2025-09-19T03:06:17.219497+00:00'
entryCount: 5
optimizationVersion: 1
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
- **2025-09-18 20:06:** Project structure and workflow details as above.- **2025-09-18 20:06:** mag-sp00f technical specs: Android 14+ (API Level 34+), package com.mag-sp00f.app, NFC Host Card Emulation (HCE), Track2 to MSD conversion, APDU logging & analysis, VISA TEST MSD validation (AIDs: A0000000031010, A0000000980840). PN532 terminal testing via /dev/ttyUSB0 or /dev/rfcomm0. Material Design 3 UI, MVVM architecture, secure local-only storage. Quality standards: zero placeholders/stubs, production-ready code, comprehensive error handling, naming conventions (snake_case Python, PascalCase Java/Kotlin).
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
