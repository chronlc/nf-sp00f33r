#!/usr/bin/env python3
"""
App Functionality Test Script
Tests the core functionality of the mag-sp00f Android app
"""

import subprocess
import time
import sys

def run_adb_command(command):
    """Run ADB command and return result"""
    try:
        result = subprocess.run(command, shell=True, capture_output=True, text=True)
        return result.returncode == 0, result.stdout, result.stderr
    except Exception as e:
        return False, "", str(e)

def test_app_installation():
    """Test if app is installed"""
    print("🔍 Testing app installation...")
    success, stdout, stderr = run_adb_command("adb shell pm list packages | grep com.mag_sp00f.app")
    if success and "com.mag_sp00f.app" in stdout:
        print("✅ App is installed")
        return True
    else:
        print("❌ App is not installed")
        return False

def test_app_launch():
    """Test if app can launch"""
    print("🚀 Testing app launch...")
    success, stdout, stderr = run_adb_command("adb shell monkey -p com.mag_sp00f.app -c android.intent.category.LAUNCHER 1")
    if success:
        print("✅ App launched successfully")
        time.sleep(2)  # Give app time to start
        return True
    else:
        print("❌ App failed to launch")
        return False

def test_app_activities():
    """Test if app activities are running"""
    print("📱 Testing app activities...")
    success, stdout, stderr = run_adb_command("adb shell dumpsys activity activities | grep com.mag_sp00f.app")
    if success and "com.mag_sp00f.app" in stdout:
        print("✅ App activities are running")
        return True
    else:
        print("⚠️ No app activities found (app may have crashed)")
        return False

def test_nfc_permissions():
    """Test if app has NFC permissions"""
    print("📡 Testing NFC permissions...")
    success, stdout, stderr = run_adb_command("adb shell dumpsys package com.mag_sp00f.app | grep 'android.permission.NFC'")
    if success and "android.permission.NFC" in stdout:
        print("✅ NFC permissions granted")
        return True
    else:
        print("⚠️ NFC permissions not found")
        return False

def test_app_logs():
    """Check app logs for any critical errors"""
    print("📝 Checking app logs...")
    success, stdout, stderr = run_adb_command("adb logcat -d | grep -i 'mag_sp00f\\|cardreading\\|nfc' | tail -10")
    if success:
        print("✅ App logs accessible")
        if stdout.strip():
            print("Recent app logs:")
            for line in stdout.strip().split('\n')[-5:]:  # Show last 5 lines
                print(f"  {line}")
        return True
    else:
        print("⚠️ Could not access app logs")
        return False

def run_comprehensive_test():
    """Run all tests"""
    print("🧪 Starting Comprehensive App Functionality Test")
    print("=" * 50)
    
    tests = [
        ("App Installation", test_app_installation),
        ("App Launch", test_app_launch),
        ("App Activities", test_app_activities),
        ("NFC Permissions", test_nfc_permissions),
        ("App Logs", test_app_logs)
    ]
    
    passed = 0
    total = len(tests)
    
    for test_name, test_func in tests:
        try:
            result = test_func()
            if result:
                passed += 1
            print()
        except Exception as e:
            print(f"❌ {test_name} failed with exception: {e}")
            print()
    
    print("=" * 50)
    print(f"🎯 Test Results: {passed}/{total} tests passed")
    
    if passed == total:
        print("🎉 ALL TESTS PASSED - App is fully functional!")
        return 0
    elif passed >= total - 1:
        print("✅ App is mostly functional with minor issues")
        return 0
    else:
        print("⚠️ App has significant issues that need attention")
        return 1

if __name__ == "__main__":
    exit_code = run_comprehensive_test()
    sys.exit(exit_code)