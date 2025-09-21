#!/usr/bin/env python3
"""
💀 PN532 DIRECT COMMUNICATION TEST 💀
🏴‍☠️ mag-sp00f Direct APDU Testing 🏴‍☠️
📞 FORCE NFC FIELD AND DIRECT APDU SEND 📞
"""

import serial
import time
import sys
import logging

# Elite logging setup
logging.basicConfig(
    level=logging.INFO,
    format='%(message)s',
    handlers=[logging.StreamHandler()]
)
logger = logging.getLogger(__name__)

class PN532DirectTest:
    def __init__(self, port):
        self.port = port
        self.connection = None
        self.connected = False

    def connect(self):
        """Connect and initialize PN532"""
        try:
            logger.info("💀 [DIRECT-TEST] Connecting to PN532...")
            self.connection = serial.Serial(
                port=self.port,
                baudrate=115200,
                timeout=1.0,
                parity=serial.PARITY_NONE,
                stopbits=serial.STOPBITS_ONE,
                bytesize=serial.EIGHTBITS
            )
            
            time.sleep(2)
            self.connection.reset_input_buffer()
            self.connection.reset_output_buffer()
            
            # Wake up and get version
            logger.info("⚡ [DIRECT] Wake up sequence...")
            wake_cmd = b'\x55\x55\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'
            self.connection.write(wake_cmd)
            time.sleep(0.5)
            
            # Get version
            version_cmd = b'\x00\x00\xFF\x02\xFE\xD4\x02\x2A\x00'
            self.connection.write(version_cmd)
            time.sleep(0.5)
            response = self.connection.read(20)
            logger.info(f"🔍 [VERSION] {response.hex() if response else 'None'}")
            
            # Configure SAM
            logger.info("🔧 [DIRECT] Configure SAM...")
            sam_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00'
            self.connection.write(sam_cmd)
            time.sleep(0.2)
            response = self.connection.read(10)
            logger.info(f"🔍 [SAM] {response.hex() if response else 'None'}")
            
            # Enable RF field with maximum power
            logger.info("📡 [DIRECT] MAXIMUM RF FIELD POWER...")
            rf_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'
            self.connection.write(rf_cmd)
            time.sleep(0.2)
            response = self.connection.read(10)
            logger.info(f"🔍 [RF] {response.hex() if response else 'None'}")
            
            logger.info("🔥 [DIRECT] PN532 READY FOR DIRECT TESTING!")
            self.connected = True
            return True
            
        except Exception as e:
            logger.error(f"❌ [ERROR] {e}")
            return False

    def force_card_detection(self):
        """Force active card detection with multiple methods"""
        logger.info("\n🎯 [DIRECT] === FORCE CARD DETECTION ===")
        
        methods = [
            # Method 1: InListPassiveTarget ISO14443A
            (b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00', "ISO14443A Passive"),
            
            # Method 2: InListPassiveTarget with different parameters
            (b'\x00\x00\xFF\x04\xFC\xD4\x4A\x02\x00\xE0\x00', "ISO14443A Passive (2 targets)"),
            
            # Method 3: InAutoPoll
            (b'\x00\x00\xFF\x05\xFB\xD4\x60\x0F\x01\x00\x9F\x00', "AutoPoll Mode"),
        ]
        
        for i, (cmd, desc) in enumerate(methods, 1):
            logger.info(f"\n📞 [METHOD-{i}] {desc}")
            logger.info(f"🔍 [CMD] {cmd.hex()}")
            
            for attempt in range(3):
                logger.info(f"⚡ [ATTEMPT] {attempt + 1}/3 - HOLD PHONE ON PN532 NOW!")
                
                self.connection.write(cmd)
                time.sleep(0.5)  # Longer wait
                response = self.connection.read(50)
                
                if response:
                    logger.info(f"🎯 [RESPONSE] {response.hex()}")
                    logger.info(f"📱 [LENGTH] {len(response)} bytes")
                    
                    # Analyze response
                    if len(response) >= 8:
                        if response[5] == 0xD5:  # Valid PN532 response
                            if response[6] == 0x4B:  # InListPassiveTarget response
                                if len(response) >= 8 and response[7] > 0:
                                    logger.info("🔥 [SUCCESS] CARD DETECTED!")
                                    return True
                                else:
                                    logger.info("📱 [INFO] No targets found")
                            elif response[6] == 0x61:  # AutoPoll response
                                logger.info("🔥 [SUCCESS] AutoPoll active!")
                                return True
                else:
                    logger.info("❌ [NO-RESPONSE] Command timeout")
                
                time.sleep(1)
        
        return False

    def send_direct_apdu(self):
        """Send direct APDU commands to try to trigger HCE"""
        logger.info("\n💀 [DIRECT] === SEND DIRECT APDUs ===")
        
        # Common payment APDUs
        apdus = [
            "00A404000E315041592E5359532E4444463031",  # SELECT PPSE
            "00A4040007A0000000031010",                 # SELECT VISA
            "00A4040007A0000000980840",                 # SELECT US Common Debit
        ]
        
        for i, apdu in enumerate(apdus, 1):
            logger.info(f"\n📞 [APDU-{i}] {apdu}")
            
            # Try to send APDU directly through InDataExchange
            apdu_bytes = bytes.fromhex(apdu)
            
            # Build InDataExchange command
            cmd_data = b'\xD4\x40\x01' + apdu_bytes
            cmd_len = len(cmd_data)
            cmd_lcs = 0xFF - cmd_len
            
            # Calculate DCS (Data Checksum)
            dcs = 0
            for b in cmd_data:
                dcs += b
            dcs = (0x100 - (dcs & 0xFF)) & 0xFF
            
            full_cmd = b'\x00\x00\xFF' + bytes([cmd_len]) + bytes([cmd_lcs]) + cmd_data + bytes([dcs]) + b'\x00'
            
            logger.info(f"🔍 [FULL-CMD] {full_cmd.hex()}")
            
            self.connection.write(full_cmd)
            time.sleep(1.0)
            response = self.connection.read(100)
            
            if response:
                logger.info(f"🎯 [APDU-RESPONSE] {response.hex()}")
            else:
                logger.info("❌ [NO-APDU-RESPONSE] APDU timeout")

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 pn532_direct_test.py /dev/rfcomm1")
        sys.exit(1)
    
    port = sys.argv[1]
    
    logger.info("============================================================")
    logger.info("💀 PN532 DIRECT TEST v31.337 💀")
    logger.info("🏴‍☠️ FORCE NFC COMMUNICATION 🏴‍☠️")
    logger.info("📞 HOLD PHONE DIRECTLY ON PN532! 📞")
    logger.info("============================================================")
    
    tester = PN532DirectTest(port)
    
    if tester.connect():
        logger.info("\n🔥 [READY] PN532 CONNECTED - TESTING NOW!")
        
        # Method 1: Force detection
        if tester.force_card_detection():
            logger.info("✅ [SUCCESS] Card detection worked!")
        else:
            logger.info("⚠️ [INFO] Card detection failed, trying direct APDUs...")
            
            # Method 2: Direct APDU attempts
            tester.send_direct_apdu()
    
    logger.info("\n🏴‍☠️ [DIRECT-TEST] Test complete!")

if __name__ == "__main__":
    main()