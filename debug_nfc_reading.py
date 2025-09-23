#!/usr/bin/env python3
"""
🏴‍☠️ NFC Card Reading Debug Script v31.337 
Debug and test mag-sp00f NFC card reading functionality
"""

import subprocess
import time
import sys

def run_adb_command(command):
    """Execute ADB command and return output"""
    try:
        result = subprocess.run(command, shell=True, capture_output=True, text=True)
        return result.stdout.strip(), result.stderr.strip(), result.returncode
    except Exception as e:
        return "", str(e), 1

def check_nfc_status():
    """Check NFC service status on device"""
    print("🔍 Checking NFC status...")
    
    stdout, stderr, code = run_adb_command("adb shell dumpsys nfc | grep -E 'mState|enabled|Service State'")
    print(f"NFC Service Status:\n{stdout}")
    
    if "mState=on" in stdout:
        print("✅ NFC is enabled")
        return True
    else:
        print("❌ NFC is disabled or not available")
        return False

def check_app_permissions():
    """Check if app has NFC permissions"""
    print("\n🔍 Checking app NFC permissions...")
    
    stdout, stderr, code = run_adb_command("adb shell dumpsys package com.mag_sp00f.app | grep -A 5 -B 5 permission")
    print(f"App Permissions:\n{stdout}")

def check_hce_service():
    """Check HCE service registration"""
    print("\n🔍 Checking HCE service registration...")
    
    stdout, stderr, code = run_adb_command("adb shell dumpsys nfc | grep -A 10 -B 5 'mag_sp00f'")
    print(f"HCE Service Registration:\n{stdout}")

def simulate_nfc_card():
    """Simulate placing an NFC card"""
    print("\n📱 Simulating NFC card detection...")
    print("In a real scenario, you would:")
    print("1. Click START in the app")
    print("2. Place a real EMV card near the NFC antenna")
    print("3. Watch for APDU logs in the app")
    
    # Check if app is in reading mode
    stdout, stderr, code = run_adb_command("adb shell uiautomator dump /sdcard/ui_debug.xml && adb pull /sdcard/ui_debug.xml . >/dev/null 2>&1 && grep 'Ready to Read\\|Reading Active' ui_debug.xml")
    
    if "Reading Active" in stdout:
        print("✅ App is in reading mode - ready for card")
    elif "Ready to Read" in stdout:
        print("⚠️ App is ready but START button not pressed")
    else:
        print("❌ App reading status unknown")

def test_card_reading_workflow():
    """Test the complete card reading workflow"""
    print("\n🎯 Testing card reading workflow...")
    
    # Launch app
    print("1. Launching app...")
    run_adb_command("adb shell am start -n com.mag_sp00f.app/.ui.MainActivity")
    time.sleep(2)
    
    # Navigate to READ menu
    print("2. Navigating to READ menu...")
    run_adb_command("adb shell input tap 324 2191")
    time.sleep(1)
    
    # Check UI state
    print("3. Checking READ menu UI...")
    stdout, stderr, code = run_adb_command("adb shell uiautomator dump /sdcard/ui_test.xml && adb pull /sdcard/ui_test.xml . >/dev/null 2>&1 && grep -E 'START|Ready|Stats' ui_test.xml")
    
    if "START" in stdout:
        print("✅ READ menu loaded - START button available")
        
        # Click START button
        print("4. Clicking START button...")
        run_adb_command("adb shell input tap 280 748")
        time.sleep(1)
        
        print("5. App should now be ready for NFC card detection")
        print("   💳 Place a real EMV card near the phone's NFC antenna")
        print("   📡 Watch for APDU logs to appear in the LIVE APDU LOG section")
        
    else:
        print("❌ READ menu not loaded properly")

def main():
    """Main debug workflow"""
    print("🏴‍☠️ MAG-SP00F NFC CARD READING DEBUG v31.337 💀")
    print("=" * 60)
    
    # Check device connection
    stdout, stderr, code = run_adb_command("adb devices")
    if "device" not in stdout:
        print("❌ No Android device connected")
        return 1
    
    print("✅ Android device connected")
    
    # Run all diagnostic checks
    nfc_ok = check_nfc_status()
    check_app_permissions()
    check_hce_service()
    
    if nfc_ok:
        test_card_reading_workflow()
    else:
        print("\n❌ Cannot test card reading - NFC issues detected")
        
    print("\n🎯 DEBUG COMPLETE - Check results above")
    print("💡 For real testing: Use physical EMV card after clicking START")

if __name__ == "__main__":
    exit(main())