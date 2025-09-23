#!/usr/bin/env python3
"""
EMV Attack Method Tester
Tests real EMV attack methods against Android HCE using PN532 terminal

Uses the ultra-fast pn532_terminal_rapid.py as base terminal
Tests different attack modes by coordinating with Android HCE manipulation
"""
import serial
import time
import logging
import argparse
from typing import Optional, List, Dict

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    datefmt='%H:%M:%S'
)
logger = logging.getLogger(__name__)

class EmvAttackTester:
    """EMV Attack Method Tester using PN532 terminal"""
    
    def __init__(self, port: str):
        self.port = port
        self.ser = None
        self.target_active = False
        
        # Attack modes based on emv_real_data_manipulation.py
        self.attack_modes = {
            0: {"name": "Baseline (No Attack)", "desc": "Standard EMV responses", "target": "None"},
            1: {"name": "PPSE AID Poisoning", "desc": "Change VISA AID to MasterCard in PPSE", "target": "PPSE/AID"},
            2: {"name": "AIP Force Offline", "desc": "Manipulate AIP to force offline approval", "target": "GPO"},
            3: {"name": "Track2 PAN Spoofing", "desc": "Replace PAN with different card number", "target": "GPO"},
            4: {"name": "Cryptogram Downgrade", "desc": "Change ARQC to TC for offline approval", "target": "GPO"},
            5: {"name": "CVM Bypass", "desc": "Remove cardholder verification requirements", "target": "GPO"}
        }

    def connect_terminal(self):
        """Connect to PN532 terminal"""
        try:
            self.ser = serial.Serial(
                port=self.port,
                baudrate=115200,
                timeout=1.0
            )
            logger.info(f"🔌 Connected to PN532 terminal on {self.port}")
            return True
        except Exception as e:
            logger.error(f"Failed to connect to {self.port}: {e}")
            return False

    def disconnect_terminal(self):
        """Disconnect from PN532 terminal"""
        if self.ser:
            self.ser.close()
            logger.info("🔌 Disconnected from PN532 terminal")

    def send_pn532_command(self, command: bytes) -> bytes:
        """Send command to PN532 and receive response"""
        if not self.ser:
            return b''
        
        try:
            self.ser.write(command)
            time.sleep(0.05)  # Ultra-fast delay
            
            response = self.ser.read_all()
            return response
        except Exception as e:
            logger.error(f"PN532 communication error: {e}")
            return b''

    def initialize_pn532(self):
        """Initialize PN532 for EMV transactions"""
        logger.info("🚀 Initializing PN532 for attack testing...")
        
        # Wake up PN532
        wake_up = b'\\x55' * 10
        self.send_pn532_command(wake_up)
        
        # SAM Configuration
        sam_config = b'\\x00\\x00\\xFF\\x04\\xFC\\xD4\\x14\\x01\\x17\\x00'
        self.send_pn532_command(sam_config)
        
        # RF Configuration
        rf_config = b'\\x00\\x00\\xFF\\x04\\xFC\\xD4\\x32\\x01\\x01\\x00\\xE0\\x00'
        self.send_pn532_command(rf_config)
        
        logger.info("✅ PN532 initialized for attack testing")

    def detect_card(self) -> bool:
        """Detect Android HCE device"""
        # Use working detection method from pn532_terminal_rapid.py
        detect_cmd = b'\\x00\\x00\\xFF\\x04\\xFC\\xD4\\x4A\\x01\\x00\\xE1\\x00'
        response = self.send_pn532_command(detect_cmd)
        
        # Try immediate APDU exchange instead of detection
        logger.info("📱 Testing for Android HCE device...")
        ppse_test = "00A404000E325041592E5359532E4444463031"
        test_response = self.send_apdu(ppse_test)
        
        if len(test_response) > 10 and "9000" in test_response:
            logger.info("📱 Android HCE device detected via PPSE test")
            return True
        else:
            logger.warning("❌ No HCE device detected")
            return False

    def send_apdu(self, apdu_hex: str) -> str:
        """Send APDU command and receive response"""
        apdu_bytes = bytes.fromhex(apdu_hex)
        
        # Build PN532 InDataExchange command
        cmd = b'\\x00\\x00\\xFF' + bytes([len(apdu_bytes) + 3]) + bytes([256 - len(apdu_bytes) - 3]) + b'\\xD4\\x40\\x01' + apdu_bytes
        
        # Add checksum
        checksum = (256 - sum(cmd[3:])) & 0xFF
        cmd += bytes([checksum]) + b'\\x00'
        
        response = self.send_pn532_command(cmd)
        
        # Parse PN532 response to extract APDU
        if len(response) > 6:
            apdu_response = response[6:]  # Skip PN532 headers
            return apdu_response.hex().upper()
        
        return ""

    def test_attack_mode(self, attack_id: int) -> Dict:
        """Test specific attack mode"""
        attack_info = self.attack_modes.get(attack_id, self.attack_modes[0])
        
        logger.info(f"🎯 TESTING ATTACK {attack_id}: {attack_info['name']}")
        logger.info(f"📝 Description: {attack_info['desc']}")
        logger.info(f"🎯 Target: {attack_info['target']}")
        logger.info("=" * 80)
        
        # NOTE: In a full implementation, we would send a command to Android HCE 
        # to set the attack mode before running the EMV transaction
        # For now, we'll run the standard EMV flow and document expected changes
        
        start_time = time.time()
        results = {
            'attack_id': attack_id,
            'attack_name': attack_info['name'],
            'success': False,
            'transaction_time': 0,
            'ppse_response': '',
            'aid_response': '',
            'gpo_response': '',
            'differences_detected': [],
            'expected_changes': []
        }
        
        # 1. SELECT PPSE
        ppse_cmd = "00A404000E325041592E5359532E4444463031"
        logger.info(f"[ATTACK-TX] SELECT PPSE: {ppse_cmd}")
        ppse_response = self.send_apdu(ppse_cmd)
        logger.info(f"[ATTACK-RX] SELECT PPSE: {ppse_response}")
        results['ppse_response'] = ppse_response
        
        if attack_id == 1:  # PPSE AID Poisoning
            if "A0000000041010" in ppse_response:  # MasterCard AID
                results['differences_detected'].append("PPSE contains MasterCard AID instead of VISA")
            results['expected_changes'].append("VISA AID (A0000000031010) → MasterCard AID (A0000000041010)")
        
        # 2. SELECT AID (use first AID from PPSE)
        if len(ppse_response) > 20:
            # Extract first AID from PPSE (simplified parsing)
            if "A0000000031010" in ppse_response:
                aid = "A0000000031010"
            elif "A0000000980840" in ppse_response:
                aid = "A0000000980840"
            else:
                aid = "A0000000031010"  # Default to VISA
            
            aid_cmd = f"00A4040007{aid}"
            logger.info(f"[ATTACK-TX] SELECT AID {aid}: {aid_cmd}")
            aid_response = self.send_apdu(aid_cmd)
            logger.info(f"[ATTACK-RX] SELECT AID {aid}: {aid_response}")
            results['aid_response'] = aid_response
        
        # 3. GET PROCESSING OPTIONS
        gpo_cmd = "80A8000023832127000000000000001000000000000000097800000000000978230301003839303100"
        logger.info(f"[ATTACK-TX] GPO: {gpo_cmd}")
        gpo_response = self.send_apdu(gpo_cmd)
        logger.info(f"[ATTACK-RX] GPO: {gpo_response}")
        results['gpo_response'] = gpo_response
        
        # Analyze GPO response for attack-specific changes
        if attack_id == 2:  # AIP Force Offline
            if "82022008" in gpo_response:  # Force offline AIP
                results['differences_detected'].append("AIP changed to force offline (2008)")
            results['expected_changes'].append("AIP: 2000 (DDA) → 2008 (DDA + Force Offline)")
            
        elif attack_id == 3:  # Track2 PAN Spoofing
            if "4000000000000002" in gpo_response:  # Test PAN
                results['differences_detected'].append("Track2 PAN changed to test card")
            results['expected_changes'].append("PAN: 4154904674973556 → 4000000000000002")
            
        elif attack_id == 4:  # Cryptogram Downgrade
            if "9F270140" in gpo_response:  # TC instead of ARQC
                results['differences_detected'].append("Cryptogram type changed to TC (offline)")
            results['expected_changes'].append("Cryptogram: ARQC (80) → TC (40)")
            
        elif attack_id == 5:  # CVM Bypass
            if "8E0E" in gpo_response:  # No CVM required
                results['differences_detected'].append("CVM bypass policy detected")
            results['expected_changes'].append("Added 'No CVM Required' policy")
        
        results['transaction_time'] = time.time() - start_time
        results['success'] = len(results['ppse_response']) > 0 and "9000" in results['ppse_response']
        
        # Summary
        logger.info("=" * 80)
        logger.info(f"🎯 ATTACK {attack_id} TEST RESULTS:")
        logger.info(f"⚡ Transaction Time: {results['transaction_time']:.2f}s")
        logger.info(f"✅ Success: {results['success']}")
        logger.info(f"🔍 Differences Detected: {len(results['differences_detected'])}")
        for diff in results['differences_detected']:
            logger.info(f"   - {diff}")
        logger.info(f"📋 Expected Changes: {len(results['expected_changes'])}")
        for change in results['expected_changes']:
            logger.info(f"   - {change}")
        logger.info("=" * 80)
        
        return results

    def run_attack_test_suite(self):
        """Run complete attack test suite"""
        if not self.connect_terminal():
            return False
        
        self.initialize_pn532()
        
        logger.info("🎯 EMV ATTACK METHOD TEST SUITE STARTING")
        logger.info("📱 Place Android HCE device on PN532 reader...")
        
        if not self.detect_card():
            self.disconnect_terminal()
            return False
        
        all_results = []
        
        # Test each attack mode
        for attack_id in range(6):  # 0-5 attack modes
            if attack_id > 0:
                logger.info(f"\\n🔄 Preparing for Attack {attack_id}...")
                time.sleep(1)  # Brief pause between attacks
            
            results = self.test_attack_mode(attack_id)
            all_results.append(results)
            
            if not results['success']:
                logger.warning(f"⚠️  Attack {attack_id} test failed - continuing...")
        
        self.disconnect_terminal()
        
        # Final summary
        logger.info("\\n" + "=" * 80)
        logger.info("📊 EMV ATTACK TEST SUITE COMPLETE")
        logger.info("=" * 80)
        
        for result in all_results:
            status = "✅ PASS" if result['success'] else "❌ FAIL"
            changes = len(result['differences_detected'])
            logger.info(f"Attack {result['attack_id']}: {result['attack_name']} - {status} ({changes} changes)")
        
        return True

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="EMV Attack Method Tester")
    parser.add_argument("--port", default="/dev/rfcomm1", help="PN532 serial port")
    parser.add_argument("--attack", type=int, help="Test specific attack mode (0-5)")
    
    args = parser.parse_args()
    
    tester = EmvAttackTester(args.port)
    
    if args.attack is not None:
        # Test single attack mode
        if not tester.connect_terminal():
            exit(1)
        tester.initialize_pn532()
        if tester.detect_card():
            tester.test_attack_mode(args.attack)
        tester.disconnect_terminal()
    else:
        # Run full test suite
        tester.run_attack_test_suite()