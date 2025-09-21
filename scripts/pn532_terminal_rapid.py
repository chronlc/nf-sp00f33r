#!/usr/bin/env python3
"""
Ultra-Fast PN532 EMV Terminal - RFIDIOt Approach
Sub-4-second EMV transactions with immediate responses
"""
import serial
import time
import logging
import argparse
from typing import Optional, List

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    datefmt='%H:%M:%S'
)
logger = logging.getLogger(__name__)

class RapidPN532Terminal:
    """Ultra-fast PN532 EMV Terminal using RFIDIOt approach"""
    
    def __init__(self, port: str):
        self.port = port
        self.ser = None
        self.target_active = False
        
        # EMV workflows - optimized for speed
        self.WORKFLOWS = {
            1: {
                'name': 'VISA MSD Track2-from-GPO',
                'aid': 'A0000000031010',
                'description': 'Ultra-fast VISA MSD with Track2 extraction'
            }
        }
    
    def connect(self) -> bool:
        """Instant connection to PN532"""
        try:
            self.ser = serial.Serial(self.port, 115200, timeout=0.3)  # Balanced timeout
            logger.info("RAPID: Connected to PN532 on %s", self.port)
            return True
        except Exception as e:
            logger.error("RAPID: Connection failed - %s", e)
            return False
    
    def send_raw_command(self, data: bytes) -> bytes:
        """Send raw command with immediate response - RFIDIOt style"""
        try:
            self.ser.write(data)
            # Ultra-fast read with micro-delay
            time.sleep(0.05)  # Minimal delay for response
            response = self.ser.read(500)
            return response
        except Exception as e:
            logger.error("RAPID: Command failed - %s", e)
            return b''
    
    def initialize_rapid(self) -> bool:
        """Ultra-fast PN532 initialization - RFIDIOt approach"""
        logger.info("RAPID: Initializing PN532...")
        
        # Wake up + SAM Config + RF Enable - minimal necessary commands
        commands = [
            # Wake up - essential minimum
            b'\x55' * 10,
            # SAM Configuration  
            b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00',
            # RF Enable
            b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'
        ]
        
        # Send initialization commands with minimal delay
        for cmd in commands:
            self.send_raw_command(cmd)
        
        logger.info("RAPID: PN532 initialized")
        return True
    
    def detect_card_instant(self) -> bool:
        """Instant card detection - single attempt"""
        detect_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
        response = self.send_raw_command(detect_cmd)
        
        if len(response) > 15:
            logger.info("RAPID: Card detected instantly")
            self.target_active = True
            return True
        
        logger.error("RAPID: No card detected")
        return False
    
    def send_apdu_instant(self, apdu_hex: str, description: str = "") -> Optional[str]:
        """Instant APDU exchange - RFIDIOt style with zero delays"""
        if not self.target_active:
            logger.error("RAPID: No target active")
            return None
        
        try:
            # Build PN532 frame
            apdu_bytes = bytes.fromhex(apdu_hex)
            frame = bytearray([0x00, 0x00, 0xFF])
            
            data_len = len(apdu_bytes) + 3  # D4 40 01 + APDU
            frame.append(data_len)
            frame.append((~data_len + 1) & 0xFF)
            frame.extend([0xD4, 0x40, 0x01])
            frame.extend(apdu_bytes)
            
            # Checksum
            checksum = 0
            for b in frame[5:]:
                checksum += b
            frame.append((~checksum + 1) & 0xFF)
            frame.append(0x00)
            
            logger.info("[RAPID-TX] %s: %s", description, apdu_hex)
            
            # INSTANT SEND + RECEIVE - no delays!
            response = self.send_raw_command(frame)
            
            if response:
                # Extract APDU from PN532 response - optimized parsing
                for i in range(len(response) - 7):
                    if (response[i:i+3] == b'\x00\x00\xFF' and 
                        i+5 < len(response) and response[i+5] == 0xD5):
                        apdu_data = response[i+7:-2]  # Extract APDU
                        if apdu_data:
                            apdu_hex = apdu_data.hex().upper()
                            logger.info("[RAPID-RX] %s: %s", description, apdu_hex)
                            return apdu_hex
                        break
            
            logger.warning("RAPID: No valid response for %s", description)
            return None
            
        except Exception as e:
            logger.error("RAPID: APDU failed - %s", e)
            return None
    
    def parse_ppse_fast(self, ppse_hex: str) -> List[str]:
        """Fast PPSE parsing to extract AIDs"""
        try:
            ppse_bytes = bytes.fromhex(ppse_hex[:-4])  # Remove 9000
            aids = []
            
            # Fast AID extraction - look for tag 4F
            i = 0
            while i < len(ppse_bytes) - 2:
                if ppse_bytes[i] == 0x4F:  # AID tag
                    aid_len = ppse_bytes[i + 1]
                    if i + 2 + aid_len <= len(ppse_bytes):
                        aid = ppse_bytes[i + 2:i + 2 + aid_len].hex().upper()
                        aids.append(aid)
                        i += 2 + aid_len
                    else:
                        break
                else:
                    i += 1
            
            return aids
        except Exception:
            return []
    
    def execute_ultra_fast_emv(self) -> bool:
        """Ultra-fast EMV transaction - sub 4 seconds total"""
        start_time = time.time()
        logger.info("🚀 ULTRA-FAST EMV TRANSACTION STARTING...")
        
        # Phase 1: Instant initialization + detection
        if not self.initialize_rapid():
            return False
        
        if not self.detect_card_instant():
            return False
        
        # Phase 2: Rapid EMV sequence - no delays between commands
        
        # PPSE
        ppse_resp = self.send_apdu_instant("00a404000e325041592e5359532e444446303100", "SELECT PPSE")
        if not ppse_resp or not ppse_resp.endswith('9000'):
            logger.error("RAPID EMV: PPSE failed")
            return False
        
        # Parse AIDs instantly
        aids = self.parse_ppse_fast(ppse_resp)
        if not aids:
            logger.error("RAPID EMV: No AIDs found")
            return False
        
        logger.info("RAPID EMV: Found AIDs: %s", aids)
        
        # SELECT AID - immediate
        aid = aids[0]  # Use first AID
        aid_resp = self.send_apdu_instant(f"00a4040007{aid}00", f"SELECT AID {aid}")
        if not aid_resp or not aid_resp.endswith('9000'):
            logger.error("RAPID EMV: AID selection failed")
            return False
        
        # GPO - immediate  
        gpo_resp = self.send_apdu_instant("80a8000023832127000000000000001000000000000000097800000000000978230301003839303100", "GPO")
        if not gpo_resp or not gpo_resp.endswith('9000'):
            logger.error("RAPID EMV: GPO failed")
            return False
        
        # READ RECORD - immediate
        record_resp = self.send_apdu_instant("00B2011400", "READ RECORD")
        if record_resp and record_resp.endswith('9000'):
            logger.info("RAPID EMV: READ RECORD success")
        else:
            logger.warning("RAPID EMV: READ RECORD failed - continuing")
        
        # Calculate total time
        total_time = time.time() - start_time
        
        logger.info("🎯 ULTRA-FAST EMV COMPLETE!")
        logger.info("⚡ Total transaction time: %.2f seconds", total_time)
        
        if total_time < 4.0:
            logger.info("🏆 SUB-4-SECOND TARGET ACHIEVED!")
        else:
            logger.warning("⏰ Target time exceeded: %.2f seconds", total_time)
        
        return True
    
    def disconnect(self):
        """Clean disconnect"""
        if self.ser and self.ser.is_open:
            self.ser.close()
            logger.info("RAPID: Disconnected from PN532")

def main():
    parser = argparse.ArgumentParser(description='Ultra-Fast PN532 EMV Terminal')
    parser.add_argument('--port', default='/dev/rfcomm1', help='Serial port (default: /dev/rfcomm1)')
    parser.add_argument('--workflow', type=int, default=1, help='Workflow ID (default: 1)')
    
    args = parser.parse_args()
    
    logger.info("=" * 70)
    logger.info("🚀 ULTRA-FAST PN532 EMV TERMINAL - RFIDIOt APPROACH")
    logger.info("⚡ TARGET: Sub-4-second EMV transactions")  
    logger.info("🎯 Port: %s | Workflow: %d", args.port, args.workflow)
    logger.info("=" * 70)
    
    terminal = RapidPN532Terminal(args.port)
    
    try:
        if not terminal.connect():
            return False
        
        logger.info("📱 PLACE ANDROID HCE DEVICE ON PN532 READER...")
        
        success = terminal.execute_ultra_fast_emv()
        
        logger.info("=" * 70)
        if success:
            logger.info("✅ ULTRA-FAST EMV TRANSACTION SUCCESSFUL!")
        else:
            logger.error("❌ ULTRA-FAST EMV TRANSACTION FAILED!")
        logger.info("=" * 70)
        
        return success
        
    except KeyboardInterrupt:
        logger.info("RAPID: Interrupted by user")
        return False
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)