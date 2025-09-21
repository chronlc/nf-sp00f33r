#!/usr/bin/env python3
"""
💀 PN532 EMV TRANSACTION FLOW 💀
🏴‍☠️ mag-sp00f REAL EMV Communication 🏴‍☠️
📞 PPSE → SELECT AID → GPO → READ RECORDS 📞
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

class PN532EMVFlow:
    def __init__(self, port):
        self.port = port
        self.connection = None
        self.connected = False
        self.target_selected = False

    def connect_and_init(self):
        """Connect and initialize PN532"""
        try:
            logger.info("💀 [EMV-FLOW] Connecting to PN532...")
            self.connection = serial.Serial(
                port=self.port,
                baudrate=115200,
                timeout=2.0,
                parity=serial.PARITY_NONE,
                stopbits=serial.STOPBITS_ONE,
                bytesize=serial.EIGHTBITS
            )
            
            time.sleep(2)
            self.connection.reset_input_buffer()
            self.connection.reset_output_buffer()
            
            # Wake up
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
            sam_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00'
            self.connection.write(sam_cmd)
            time.sleep(0.2)
            response = self.connection.read(10)
            
            # Enable RF field
            rf_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'
            self.connection.write(rf_cmd)
            time.sleep(0.2)
            response = self.connection.read(10)
            
            logger.info("🔥 [EMV-FLOW] PN532 READY FOR EMV!")
            self.connected = True
            return True
            
        except Exception as e:
            logger.error(f"❌ [ERROR] {e}")
            return False

    def detect_and_select_target(self):
        """Detect card and select target for communication"""
        logger.info("\n🎯 [EMV] === CARD DETECTION AND SELECTION ===")
        
        for attempt in range(10):
            logger.info(f"⚡ [DETECT] Attempt {attempt + 1}/10 - PHONE IS ON PN532!")
            
            # Use the WORKING detection command from direct test
            detect_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
            self.connection.write(detect_cmd)
            time.sleep(0.5)
            response = self.connection.read(50)
            
            logger.info(f"🔍 [RAW] Response: {response.hex() if response else 'None'}")
            
            if response and len(response) >= 8:
                logger.info(f"🔍 [PARSE] Full response length: {len(response)}")
                
                # Sometimes we get concatenated frames, find the actual data frame
                data_start = 0
                for i in range(len(response) - 7):
                    if (response[i] == 0x00 and response[i+1] == 0x00 and 
                        response[i+2] == 0xFF and i+5 < len(response) and
                        response[i+5] == 0xD5 and response[i+6] == 0x4B):
                        data_start = i
                        logger.info(f"🔍 [PARSE] Found data frame at offset {data_start}")
                        break
                
                if data_start < len(response) - 7:
                    # Extract the data frame
                    frame = response[data_start:]
                    logger.info(f"🔍 [PARSE] Data frame: {frame[:15].hex()}...")
                    
                    if (len(frame) >= 7 and
                        frame[0] == 0x00 and frame[1] == 0x00 and 
                        frame[2] == 0xFF and 
                        frame[5] == 0xD5 and frame[6] == 0x4B):
                        
                        logger.info("🎯 [PARSE] Valid PN532 InListPassiveTarget response!")
                        
                        # Check if any targets found (NbTg > 0)
                        if len(frame) >= 8 and frame[7] > 0:
                            logger.info("🔥 [DETECT] *** CARD DETECTED! ***")
                            logger.info(f"💀 [CARD] Targets: {frame[7]}")
                            
                            if len(frame) >= 10:
                                target_type = frame[8] if len(frame) > 8 else 0
                                uid_len = frame[9] if len(frame) > 9 else 0
                                logger.info(f"📞 [CARD] Type: {target_type:02X}")
                                logger.info(f"🔍 [CARD] UID Length: {uid_len}")
                                
                                if uid_len > 0 and len(frame) >= 10 + uid_len:
                                    uid = frame[10:10+uid_len]
                                    logger.info(f"🔥 [CARD] UID: {uid.hex().upper()}")
                            
                            logger.info("📞 [EMV] Target selected for communication!")
                            self.target_selected = True
                            return True
                        else:
                            logger.info(f"📱 [SCAN] No targets (NbTg={frame[7] if len(frame) >= 8 else '?'})")
                    else:
                        logger.info(f"🔍 [DEBUG] Invalid PN532 response structure after parsing")
                else:
                    logger.info(f"🔍 [DEBUG] No valid data frame found in response")
            else:
                logger.info("❌ [DEBUG] No response or too short")
            
            time.sleep(0.5)
        
        logger.error("❌ [ERROR] No card detected after 10 attempts!")
        return False

    def send_apdu(self, apdu_hex, description):
        """Send APDU and get response"""
        logger.info(f"\n📞 [APDU] {description}")
        logger.info(f"🔍 [TX] {apdu_hex}")
        
        if not self.target_selected:
            logger.error("❌ [ERROR] No target selected!")
            return None
        
        try:
            # Convert hex to bytes
            apdu_bytes = bytes.fromhex(apdu_hex.replace(' ', ''))
            
            # Build InDataExchange command (0x40)
            # Format: D4 40 Tg APDU_DATA
            cmd_data = b'\xD4\x40\x01' + apdu_bytes  # Tg=01 (target 1)
            cmd_len = len(cmd_data)
            cmd_lcs = (0x100 - cmd_len) & 0xFF
            
            # Calculate checksum
            checksum = 0
            for b in cmd_data:
                checksum += b
            checksum = (0x100 - (checksum & 0xFF)) & 0xFF
            
            # Complete command
            full_cmd = (b'\x00\x00\xFF' + 
                       bytes([cmd_len]) + 
                       bytes([cmd_lcs]) + 
                       cmd_data + 
                       bytes([checksum]) + 
                       b'\x00')
            
            logger.info(f"🔍 [CMD] {full_cmd.hex()}")
            
            # Send command
            self.connection.write(full_cmd)
            time.sleep(1.0)  # Wait for response
            
            # Read response
            response = self.connection.read(300)
            
            if response and len(response) > 0:
                logger.info(f"🎯 [RX-RAW] {response.hex()}")
                
                # Parse PN532 concatenated frames - skip empty frames first
                # Pattern: [00 00 FF 00 FF] (empty) + [00 00 00 FF LEN LCS D5 41 STATUS APDU_DATA DCS 00]
                apdu_data = None
                
                # Look for the actual data frame after empty frames
                for i in range(len(response) - 10):
                    if (response[i:i+3] == b'\x00\x00\xFF' and 
                        i + 6 < len(response) and
                        response[i+5] == 0xD5 and response[i+6] == 0x41):
                        
                        # Found InDataExchange response at offset i
                        logger.info(f"🔍 [PARSE] Found data frame at offset {i}")
                        
                        if i + 8 < len(response):
                            status = response[i+7]
                            if status == 0x00:  # Success
                                # Extract APDU data (skip PN532 wrapper and checksum)
                                start_idx = i + 8
                                end_idx = len(response) - 2  # Remove DCS and postamble
                                
                                if start_idx < end_idx:
                                    apdu_data = response[start_idx:end_idx]
                                    apdu_hex = apdu_data.hex().upper()
                                    logger.info(f"📱 [RX] {apdu_hex}")
                                    
                                    # Parse status words
                                    if len(apdu_data) >= 2:
                                        sw1 = apdu_data[-2]
                                        sw2 = apdu_data[-1]
                                        logger.info(f"🔥 [STATUS] SW1={sw1:02X} SW2={sw2:02X}")
                                        
                                        if sw1 == 0x90 and sw2 == 0x00:
                                            logger.info("✅ [SUCCESS] Command successful!")
                                        elif sw1 == 0x61:
                                            logger.info(f"📋 [INFO] More data available: {sw2} bytes")
                                        elif sw1 == 0x6A and sw2 == 0x82:
                                            logger.info("⚠️ [INFO] File not found")
                                        else:
                                            logger.info(f"⚠️ [INFO] Status: {sw1:02X}{sw2:02X}")
                                    
                                    return apdu_hex
                            else:
                                logger.error(f"❌ [ERROR] InDataExchange status: {status:02X}")
                        break
                
                if apdu_data is None:
                    logger.error(f"❌ [ERROR] Could not parse PN532 response")
            else:
                logger.error("❌ [ERROR] No APDU response")
            
            return None
            
        except Exception as e:
            logger.error(f"❌ [ERROR] APDU failed: {e}")
            return None

    def run_emv_flow(self):
        """Run complete EMV transaction flow"""
        logger.info("\n🏴‍☠️ [EMV] === REAL EMV TRANSACTION FLOW ===")
        
        # Step 1: SELECT PPSE (CONTACTLESS)
        ppse_response = self.send_apdu(
            "00A404000E325041592E5359532E4444463031", 
            "SELECT PPSE CONTACTLESS (2PAY.SYS.DDF01)"
        )
        
        if ppse_response:
            logger.info("🎯 [EMV] PPSE selection successful!")
            
            # Step 2: SELECT VISA MSD
            visa_response = self.send_apdu(
                "00A4040007A0000000031010",
                "SELECT VISA MSD AID"
            )
            
            if visa_response:
                logger.info("🔥 [EMV] VISA AID selection successful!")
                
                # Step 3: GET PROCESSING OPTIONS
                gpo_response = self.send_apdu(
                    "80A8000023832127000000000000001000000000000000097800000000000978230301003839303100",
                    "GET PROCESSING OPTIONS (GPO)"
                )
                
                if gpo_response:
                    logger.info("💀 [EMV] GPO successful!")
                    
                    # Step 4: READ RECORD
                    read_response = self.send_apdu(
                        "00B2011400",
                        "READ RECORD (SFI=2, Record=1)"
                    )
                    
                    if read_response:
                        logger.info("📞 [EMV] READ RECORD successful!")
                        logger.info("🏴‍☠️ [SUCCESS] COMPLETE EMV FLOW EXECUTED!")
                        return True
        
        logger.error("❌ [EMV] Transaction flow failed")
        return False

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 pn532_emv_flow.py /dev/rfcomm1")
        sys.exit(1)
    
    port = sys.argv[1]
    
    logger.info("============================================================")
    logger.info("💀 PN532 EMV FLOW v31.337 💀")
    logger.info("🏴‍☠️ REAL EMV TRANSACTION TESTING 🏴‍☠️")
    logger.info("📞 HOLD PHONE ON PN532 FOR FULL EMV! 📞")
    logger.info("============================================================")
    
    emv = PN532EMVFlow(port)
    
    if emv.connect_and_init():
        if emv.detect_and_select_target():
            emv.run_emv_flow()
    
    logger.info("\n🏴‍☠️ [EMV-FLOW] Transaction complete!")

if __name__ == "__main__":
    main()