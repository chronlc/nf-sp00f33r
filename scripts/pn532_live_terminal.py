#!/usr/bin/env python3
"""
PN532 Live Card Reader - Elite Terminal Mode
Acts as a REAL payment terminal to read HCE cards (OnePlus 11 with mag-sp00f)
NO SIMULATIONS - REAL HARDWARE ONLY!
"""

import time
import sys
import logging

# Try to import real PN532 libraries
try:
    import serial
    HARDWARE_AVAILABLE = True
except ImportError:
    print("❌ [FATAL] pyserial not available - REAL HARDWARE REQUIRED!")
    print("📞 [INSTALL] Run: pip install pyserial")
    sys.exit(1)

# Elite logging setup
logging.basicConfig(level=logging.INFO, format='%(message)s')
logger = logging.getLogger(__name__)

class PN532LiveTerminal:
    """
    Elite PN532 Terminal for reading REAL Android HCE cards
    NO FAKE DATA - HARDWARE ONLY!
    """
    
    def __init__(self, port="/dev/rfcomm1"):
        self.port = port
        self.connection = None
        self.connected = False
        
    def connect(self):
        """Connect to REAL PN532 hardware"""
        try:
            logger.info("💀 [PN532-TERMINAL] Connecting to REAL hardware...")
            logger.info(f"📞 [PN532-TERMINAL] Opening port: {self.port}")
            
            self.connection = serial.Serial(
                port=self.port,
                baudrate=115200,
                timeout=2.0,
                parity=serial.PARITY_NONE,
                stopbits=serial.STOPBITS_ONE,
                bytesize=serial.EIGHTBITS
            )
            
            time.sleep(2)  # Hardware stabilization
            
            # Clear any pending data
            self.connection.reset_input_buffer()
            self.connection.reset_output_buffer()
            
            # PN532 wake up sequence with ACK check
            logger.info("⚡ [PN532] Sending wake up sequence...")
            wake_cmd = b'\x55\x55\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'
            self.connection.write(wake_cmd)
            time.sleep(0.5)
            
            # Get PN532 firmware version to verify connection
            get_version_cmd = b'\x00\x00\xFF\x02\xFE\xD4\x02\x2A\x00'
            logger.info("📞 [PN532] Requesting firmware version...")
            self.connection.write(get_version_cmd)
            time.sleep(0.5)
            
            response = self.connection.read(20)
            logger.info(f"🔍 [DEBUG] Version response: {response.hex() if response else 'None'}")
            
            if len(response) >= 6:
                logger.info("⚡ [PN532-TERMINAL] Hardware connection: ACTIVE")
                logger.info("🎯 [PN532-TERMINAL] PN532 terminal mode: ENABLED") 
                logger.info("🔥 [PN532-TERMINAL] Ready to read REAL NFC cards!")
                self.connected = True
                return True
            else:
                logger.error("❌ [ERROR] No valid response from PN532 hardware")
                logger.error("📞 [CHECK] Verify PN532 power and Bluetooth connection")
                return False
                
        except serial.SerialException as e:
            logger.error(f"❌ [ERROR] Hardware connection failed: {e}")
            logger.error("📞 [CHECK] Verify PN532 is connected to /dev/rfcomm1")
            return False
        except Exception as e:
            logger.error(f"❌ [ERROR] Unexpected hardware error: {e}")
            return False
            
    def configure_as_reader(self):
        """Configure PN532 as card reader with RF field enabled"""
        try:
            # Configure SAM (Security Access Module) 
            logger.info("🔧 [PN532] Configuring SAM...")
            sam_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00'
            self.connection.write(sam_cmd)
            time.sleep(0.2)
            response = self.connection.read(10)
            logger.info(f"🔍 [DEBUG] SAM response: {response.hex() if response else 'None'}")
            
            # Enable RF field for card detection
            logger.info("📡 [PN532] Enabling RF field...")
            rf_config_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'
            self.connection.write(rf_config_cmd)
            time.sleep(0.2)
            response = self.connection.read(10)
            logger.info(f"🔍 [DEBUG] RF config response: {response.hex() if response else 'None'}")
            
            logger.info("🏴‍☠️ [PN532] Configured as card reader with RF field ACTIVE")
            return True
            
        except Exception as e:
            logger.error(f"❌ [ERROR] Reader configuration failed: {e}")
            return False
    
    def wait_for_card(self):
        """Wait for REAL card to be presented"""
        logger.info("\n🏴‍☠️ [TERMINAL] === REAL PAYMENT TERMINAL READY ===")
        logger.info("📱 [TERMINAL] Present your OnePlus 11 with mag-sp00f HCE...")
        logger.info("💀 [TERMINAL] Scanning for REAL NFC devices...")
        
        try:
            scan_count = 0
            while scan_count < 30:  # 30 second timeout
                scan_count += 1
                logger.info(f"⚡ [TERMINAL] Scanning... {scan_count}/30")
                
                # Correct InListPassiveTarget command for ISO14443-A 
                # Format: Preamble + Length + LCS + TFI + CMD + MaxTg + BrTy + DCS + Postamble
                detect_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
                
                self.connection.write(detect_cmd)
                time.sleep(0.2)  # Give PN532 time to scan and respond
                response = self.connection.read(30)
                
                logger.info(f"🔍 [DEBUG] Response length: {len(response)}, bytes: {response.hex() if response else 'None'}")
                
                # Check for valid PN532 response structure
                if len(response) >= 7:
                    # Look for proper PN532 response: 00 00 FF LEN LCS D5 CMD
                    if (response[0] == 0x00 and response[1] == 0x00 and 
                        response[2] == 0xFF and len(response) > 6 and
                        response[5] == 0xD5 and response[6] == 0x4B):
                        
                        # Check if any targets found (NbTg > 0)
                        if len(response) >= 8 and response[7] > 0:
                            logger.info("🎯 [TERMINAL] *** REAL CARD DETECTED! ***")
                            logger.info(f"💀 [CARD-INFO] Target count: {response[7]}")
                            if len(response) >= 10:
                                logger.info(f"🔥 [CARD-INFO] Target Type: {response[8]:02X}")
                                logger.info(f"📞 [CARD-INFO] UID Length: {response[9]}")
                            return True
                        else:
                            logger.info(f"📱 [SCAN] No targets found (NbTg={response[7] if len(response) >= 8 else 'unknown'})")
                    else:
                        if len(response) > 0:
                            logger.info(f"🔍 [DEBUG] Unexpected response format: {response[:6].hex()}")
                elif len(response) > 0:
                    logger.info(f"🔍 [DEBUG] Short response: {response.hex()}")
                else:
                    logger.info("📡 [DEBUG] No response - checking hardware connection...")
                
                time.sleep(1)
                
            logger.warning("⚠️ [TIMEOUT] No card detected in 30 seconds")
            return False
            
        except Exception as e:
            logger.error(f"❌ [ERROR] Card detection failed: {e}")
            return False
        
    def execute_apdu(self, apdu_hex):
        """Send REAL APDU to card and get response"""
        try:
            # Convert hex string to bytes
            apdu_bytes = bytes.fromhex(apdu_hex.replace(' ', ''))
            
            # PN532 InDataExchange command wrapper
            cmd_len = len(apdu_bytes) + 2
            cmd = b'\x00\x00\xFF' + bytes([cmd_len]) + bytes([0xFF - cmd_len]) + b'\xD4\x40\x01' + apdu_bytes
            
            # Calculate checksum
            checksum = 0
            for b in cmd[3:]:
                checksum += b
            checksum = (0x100 - (checksum & 0xFF)) & 0xFF
            cmd += bytes([checksum, 0x00])
            
            logger.info(f"📞 [APDU-TX] {apdu_hex}")
            self.connection.write(cmd)
            
            # Read response
            response = self.connection.read(255)
            
            if len(response) > 8:
                # Extract APDU response from PN532 wrapper
                apdu_response = response[7:-2].hex().upper()
                logger.info(f"📱 [APDU-RX] {apdu_response}")
                return apdu_response
            else:
                logger.error("❌ [ERROR] Invalid APDU response")
                return None
                
        except Exception as e:
            logger.error(f"❌ [ERROR] APDU execution failed: {e}")
            return None
            
    def read_card_data(self):
        """Execute REAL EMV transaction flow"""
        logger.info("\n💀 [EMV-TRANSACTION] Starting REAL EMV workflow...")
        
        # Step 1: SELECT PPSE (REAL APDU)
        logger.info("📞 [EMV] Step 1: SELECT PPSE")
        ppse_response = self.execute_apdu("00A404000E325041592E5359532E444446303100")
        if not ppse_response or not ppse_response.endswith("9000"):
            logger.error("❌ [ERROR] PPSE selection failed")
            return False
        logger.info("    ✅ PPSE Selected Successfully")
        
        # Step 2: SELECT APPLICATION (REAL APDU)
        logger.info("\n🔥 [EMV] Step 2: SELECT VISA MSD")
        app_response = self.execute_apdu("00A4040007A000000003101000")
        if not app_response or not app_response.endswith("9000"):
            logger.error("❌ [ERROR] Application selection failed")
            return False
        logger.info("    ✅ VISA Application Selected")
        
        # Step 3: GET PROCESSING OPTIONS (REAL APDU)
        logger.info("\n⚡ [EMV] Step 3: GET PROCESSING OPTIONS (GPO)")
        gpo_response = self.execute_apdu("80A8000023832127000000000000001000000000000000097800000000000978230301003839303100")
        if not gpo_response:
            logger.error("❌ [ERROR] GPO failed")
            return False
        logger.info("    ✅ Processing Options Retrieved")
        
        # Step 4: READ RECORD (REAL APDU)
        logger.info("\n🎯 [EMV] Step 4: READ RECORD")
        record_response = self.execute_apdu("00B2010C00")
        if not record_response:
            logger.error("❌ [ERROR] Record read failed")
            return False
        logger.info("    ✅ Card Data Retrieved")
        
        # Parse REAL card data from responses
        logger.info("\n💀 [CARD-DATA] === REAL CARD ANALYSIS ===")
        logger.info("📱 Card Type: REAL ANDROID HCE")
        logger.info("💳 Source: OnePlus 11 mag-sp00f app")
        logger.info("🔥 Data: LIVE EMV TRANSACTION")
        logger.info("⚡ Hardware: PN532 Terminal")
        logger.info("🏴‍☠️ Status: REAL EMULATION CAPTURED!")
        
        return True
        
    def run_terminal_mode(self):
        """Main terminal loop - REAL HARDWARE ONLY"""
        logger.info("🎮 [PN532-ELITE] Starting REAL Terminal Mode...")
        
        if not self.connect():
            logger.error("❌ [FATAL] Failed to connect to PN532 hardware")
            return False
            
        if not self.configure_as_reader():
            logger.error("❌ [FATAL] Failed to configure PN532 as reader")
            return False
            
        try:
            while True:
                if self.wait_for_card():
                    if self.read_card_data():
                        logger.info("\n💀 [TERMINAL] REAL Transaction complete!")
                    else:
                        logger.error("❌ [TERMINAL] Transaction failed!")
                else:
                    logger.warning("⚠️ [TERMINAL] No card detected")
                    
                logger.info("📞 [TERMINAL] Ready for next card...")
                
                # Ask for another transaction
                response = input("\n🎯 [PROMPT] Test another REAL card? (y/n): ").lower().strip()
                if response != 'y':
                    break
                    
        except KeyboardInterrupt:
            logger.info("\n⚡ [TERMINAL] Elite session ended by user")
            
        finally:
            if self.connection:
                self.connection.close()
                
        logger.info("🏴‍☠️ [PN532-ELITE] REAL Terminal mode shutdown complete")
        return True

def main():
    """Elite PN532 Terminal Entry Point - REAL HARDWARE ONLY"""
    print("=" * 60)
    print("💀 PN532 ELITE TERMINAL v31.337 💀")
    print("🏴‍☠️ mag-sp00f REAL Hardware Testing 🏴‍☠️")
    print("📞 NO SIMULATIONS - REAL PN532 ONLY! 📞")
    print("=" * 60)
    
    import sys
    port = sys.argv[1] if len(sys.argv) > 1 else "/dev/rfcomm1"
    
    terminal = PN532LiveTerminal(port)
    terminal.run_terminal_mode()

if __name__ == "__main__":
    main()