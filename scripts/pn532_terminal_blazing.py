#!/usr/bin/env python3
"""
BLAZING-FAST PN532 EMV Terminal - Sub-2-second target
Ultimate optimization for sub-2-second EMV transactions
"""
import serial
import time
import logging
import argparse
from typing import Optional, List

# Minimal logging for speed
logging.basicConfig(level=logging.INFO, format='%(asctime)s [%(levelname)s] %(message)s', datefmt='%H:%M:%S')
logger = logging.getLogger(__name__)

class BlazingPN532Terminal:
    """Blazing-fast PN532 EMV Terminal - sub-2-second target"""
    
    def __init__(self, port: str):
        self.port = port
        self.ser = None
        
        # Pre-built frames for max speed
        self.WAKE_CMD = b'\x55' * 6  # Minimal wake
        self.SAM_CMD = b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00'
        self.RF_CMD = b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'
        self.DETECT_CMD = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
        
        # Pre-built APDU frames
        self.PPSE_FRAME = self._build_apdu_frame("00a404000e325041592e5359532e444446303100")
        self.VISA_AID_FRAME = self._build_apdu_frame("00a4040007A000000003101000")
        self.GPO_FRAME = self._build_apdu_frame("80a8000023832127000000000000001000000000000000097800000000000978230301003839303100")
        self.READ_FRAME = self._build_apdu_frame("00B2011400")
    
    def _build_apdu_frame(self, apdu_hex: str) -> bytes:
        """Pre-build APDU frames for instant sending"""
        apdu_bytes = bytes.fromhex(apdu_hex)
        frame = bytearray([0x00, 0x00, 0xFF])
        data_len = len(apdu_bytes) + 3
        frame.append(data_len)
        frame.append((~data_len + 1) & 0xFF)
        frame.extend([0xD4, 0x40, 0x01])
        frame.extend(apdu_bytes)
        checksum = sum(frame[5:]) & 0xFF
        frame.append((~checksum + 1) & 0xFF)
        frame.append(0x00)
        return bytes(frame)
    
    def connect(self) -> bool:
        """Lightning connect"""
        try:
            self.ser = serial.Serial(self.port, 115200, timeout=0.2, write_timeout=0.1)
            logger.info("BLAZING: Connected")
            return True
        except Exception as e:
            logger.error("BLAZING: Connect failed - %s", e)
            return False
    
    def send_instant(self, frame: bytes) -> bytes:
        """Instant send/receive - zero delays"""
        try:
            self.ser.write(frame)
            return self.ser.read(500)  # No sleep - pure speed
        except Exception:
            return b''
    
    def extract_apdu_fast(self, response: bytes) -> Optional[str]:
        """Ultra-fast APDU extraction - optimized parsing"""
        if len(response) < 10:
            return None
        
        # Fast scan for PN532 response pattern
        for i in range(len(response) - 8):
            if response[i:i+3] == b'\x00\x00\xFF' and i+5 < len(response) and response[i+5] == 0xD5:
                start = i + 7
                end = len(response) - 2
                if start < end and end <= len(response):
                    apdu_data = response[start:end]
                    if len(apdu_data) >= 2:  # At least SW1 SW2
                        return apdu_data.hex().upper()
        return None
    
    def parse_aids_instant(self, ppse_hex: str) -> List[str]:
        """Instant AID parsing"""
        try:
            data = bytes.fromhex(ppse_hex[:-4])
            aids = []
            i = 0
            while i < len(data) - 2:
                if data[i] == 0x4F and i + 1 < len(data):
                    length = data[i + 1]
                    if i + 2 + length <= len(data) and length > 0:
                        aids.append(data[i + 2:i + 2 + length].hex().upper())
                        i += 2 + length
                    else:
                        break
                else:
                    i += 1
            return aids
        except Exception:
            return []
    
    def init_blazing(self) -> bool:
        """Blazing initialization - batch all commands"""
        logger.info("BLAZING: Init...")
        
        # Send all init commands instantly
        self.send_instant(self.WAKE_CMD)
        self.send_instant(self.SAM_CMD) 
        self.send_instant(self.RF_CMD)
        
        logger.info("BLAZING: Ready")
        return True
    
    def detect_instant(self) -> bool:
        """Instant detection"""
        response = self.send_instant(self.DETECT_CMD)
        if len(response) > 15:
            logger.info("BLAZING: Card detected")
            return True
        logger.error("BLAZING: No card")
        return False
    
    def execute_blazing_emv(self) -> bool:
        """BLAZING EMV - sub-2-second target"""
        start = time.time()
        logger.info("🔥 BLAZING EMV STARTING...")
        
        # Phase 1: Init + detect
        if not self.init_blazing() or not self.detect_instant():
            return False
        
        # Phase 2: EMV sequence - pre-built frames, zero delays
        
        # PPSE
        logger.info("[BLAZING-TX] PPSE")
        resp = self.send_instant(self.PPSE_FRAME)
        ppse_hex = self.extract_apdu_fast(resp)
        if not ppse_hex or not ppse_hex.endswith('9000'):
            logger.error("BLAZING: PPSE failed")
            return False
        
        # Parse AIDs
        aids = self.parse_aids_instant(ppse_hex)
        if not aids:
            logger.error("BLAZING: No AIDs")
            return False
        logger.info("[BLAZING-RX] AIDs: %s", aids)
        
        # SELECT AID 
        logger.info("[BLAZING-TX] SELECT AID")
        resp = self.send_instant(self.VISA_AID_FRAME)
        aid_hex = self.extract_apdu_fast(resp)
        if not aid_hex or not aid_hex.endswith('9000'):
            logger.error("BLAZING: AID failed")
            return False
        
        # GPO
        logger.info("[BLAZING-TX] GPO")
        resp = self.send_instant(self.GPO_FRAME)
        gpo_hex = self.extract_apdu_fast(resp)
        if not gpo_hex or not gpo_hex.endswith('9000'):
            logger.error("BLAZING: GPO failed")
            return False
        
        # READ RECORD
        logger.info("[BLAZING-TX] READ RECORD")
        resp = self.send_instant(self.READ_FRAME)
        read_hex = self.extract_apdu_fast(resp)
        if read_hex and read_hex.endswith('9000'):
            logger.info("BLAZING: READ success")
        else:
            logger.warning("BLAZING: READ failed - continuing")
        
        # Results
        total = time.time() - start
        logger.info("🔥 BLAZING EMV COMPLETE!")
        logger.info("⚡ Total time: %.2f seconds", total)
        
        if total < 2.0:
            logger.info("🚀 SUB-2-SECOND ACHIEVED!")
        elif total < 2.5:
            logger.info("🎯 ULTRA-FAST TARGET!")
        else:
            logger.warning("⏰ Time: %.2f seconds", total)
        
        # Extract Track2 from GPO
        if gpo_hex and '57' in gpo_hex:
            idx = gpo_hex.find('57')
            if idx > 0 and idx + 4 < len(gpo_hex):
                track2_len = int(gpo_hex[idx+2:idx+4], 16)
                track2_data = gpo_hex[idx+4:idx+4+(track2_len*2)]
                logger.info("💳 Track2: %s", track2_data)
        
        return True
    
    def disconnect(self):
        """Clean disconnect"""
        if self.ser and self.ser.is_open:
            self.ser.close()

def main():
    parser = argparse.ArgumentParser(description='Blazing-Fast PN532 EMV Terminal')
    parser.add_argument('--port', default='/dev/rfcomm1', help='Serial port')
    args = parser.parse_args()
    
    logger.info("=" * 60)
    logger.info("🔥 BLAZING PN532 EMV TERMINAL")
    logger.info("🚀 TARGET: Sub-2-second EMV transactions")
    logger.info("🎯 Port: %s", args.port)
    logger.info("=" * 60)
    
    terminal = BlazingPN532Terminal(args.port)
    
    try:
        if not terminal.connect():
            return False
        
        logger.info("📱 PLACE DEVICE ON READER...")
        success = terminal.execute_blazing_emv()
        
        logger.info("=" * 60)
        if success:
            logger.info("✅ BLAZING EMV SUCCESS!")
        else:
            logger.error("❌ BLAZING EMV FAILED!")
        logger.info("=" * 60)
        
        return success
        
    except KeyboardInterrupt:
        logger.info("BLAZING: Interrupted")
        return False
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)