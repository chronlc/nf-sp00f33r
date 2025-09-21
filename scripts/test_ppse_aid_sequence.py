#!/usr/bin/env python3
"""
Test PPSE → SELECT AID sequence to debug EMV state machine
"""
import serial
import time
import logging
from datetime import datetime

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    datefmt='%H:%M:%S'
)

def send_apdu(ser, command_bytes, description="Command"):
    """Send APDU command and get response"""
    try:
        # Send command
        hex_cmd = command_bytes.hex().upper()
        logging.info(f"[TX] {description}: {hex_cmd}")
        
        ser.write(command_bytes)
        time.sleep(1.5)  # Wait for response
        
        # Read response
        response = ser.read_all()
        if len(response) > 0:
            hex_resp = response.hex().upper()
            logging.info(f"[RX] Raw response: {hex_resp}")
            
            # Extract APDU from PN532 frame
            if len(response) > 10:
                # Skip empty frames, find data frame
                for i in range(len(response) - 10):
                    if (response[i:i+4] == b'\x00\x00\xFF\x00' or 
                        (len(response) > i+5 and response[i] == 0x00 and response[i+1] == 0x00 and response[i+2] == 0xFF and response[i+3] > 0x00)):
                        frame_len = response[i+3]
                        if frame_len > 0 and len(response) >= i+6+frame_len:
                            apdu_data = response[i+6:i+6+frame_len-2]  # -2 for checksum
                            if len(apdu_data) > 0:
                                hex_apdu = apdu_data.hex().upper()
                                logging.info(f"[RX] APDU response: {hex_apdu}")
                                if hex_apdu.endswith('9000'):
                                    logging.info(f"[SUCCESS] {description}")
                                    return True, apdu_data
                                else:
                                    logging.warning(f"[ERROR] {description} - Error: {hex_apdu[-4:]}")
                                    return False, apdu_data
            
            logging.warning(f"[FAILED] {description} - No valid APDU response")
            return False, None
        else:
            logging.warning(f"[FAILED] {description} - No response")
            return False, None
            
    except Exception as e:
        logging.error(f"[ERROR] {description} failed: {e}")
        return False, None

def main():
    logging.info("=" * 50)
    logging.info("PPSE → SELECT AID Sequence Test")
    logging.info("=" * 50)
    
    # Connect to PN532
    try:
        ser = serial.Serial('/dev/rfcomm1', 115200, timeout=2)
        logging.info("Connected to PN532 on /dev/rfcomm1")
    except Exception as e:
        logging.error(f"Failed to connect to PN532: {e}")
        return
    
    try:
        # Initialize PN532
        logging.info("Initializing PN532...")
        
        # Wake up
        ser.write(b'\x55' * 16)
        time.sleep(0.1)
        
        # Get version
        ser.write(b'\x00\x00\xFF\x02\xFE\xD4\x02\x2A\x00')
        time.sleep(0.5)
        response = ser.read_all()
        logging.info(f"Version response: {response.hex()}")
        
        # SAM Configuration
        ser.write(b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00')
        time.sleep(0.5)
        ser.read_all()
        
        # RF Configuration
        ser.write(b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00')
        time.sleep(0.5)
        ser.read_all()
        
        logging.info("PN532 initialized successfully!")
        logging.info("PLACE ANDROID HCE DEVICE ON PN532 READER...")
        
        # Detect card
        logging.info("Detecting contactless card...")
        for attempt in range(10):
            logging.info(f"Detection attempt {attempt+1}/10 - Hold phone on PN532!")
            
            ser.write(b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00')
            time.sleep(2.0)
            response = ser.read_all()
            
            if len(response) > 15:
                logging.info(f"Raw detection response: {response.hex()}")
                logging.info("Contactless card detected successfully!")
                break
        else:
            logging.error("Failed to detect contactless card")
            return
        
        # Test PPSE → SELECT AID sequence
        logging.info("=" * 50)
        logging.info("TESTING PPSE → SELECT AID SEQUENCE")
        logging.info("=" * 50)
        
        # 1. SELECT PPSE
        ppse_cmd = bytes.fromhex('00A404000E325041592E5359532E4444463031')
        ppse_frame = b'\x00\x00\xFF' + bytes([len(ppse_cmd) + 1]) + bytes([0xFF - len(ppse_cmd) - 1]) + b'\xD4\x40\x01' + ppse_cmd
        checksum = (sum(ppse_frame[4:]) & 0xFF)
        ppse_frame += bytes([checksum ^ 0xFF]) + b'\x00'
        
        success1, _ = send_apdu(ser, ppse_frame, "SELECT PPSE")
        
        if success1:
            # Short delay before next command
            time.sleep(0.5)
            
            # 2. SELECT AID (VISA MSD)
            aid_cmd = bytes.fromhex('00A4040007A0000000031010')
            aid_frame = b'\x00\x00\xFF' + bytes([len(aid_cmd) + 1]) + bytes([0xFF - len(aid_cmd) - 1]) + b'\xD4\x40\x01' + aid_cmd
            checksum = (sum(aid_frame[4:]) & 0xFF)
            aid_frame += bytes([checksum ^ 0xFF]) + b'\x00'
            
            success2, _ = send_apdu(ser, aid_frame, "SELECT AID")
            
            if success1 and success2:
                logging.info("=" * 50)
                logging.info("🎯 SUCCESS: PPSE → SELECT AID sequence working!")
                logging.info("=" * 50)
            else:
                logging.warning("PPSE worked but SELECT AID failed")
        else:
            logging.error("PPSE failed - cannot test SELECT AID")
        
    except Exception as e:
        logging.error(f"Test failed: {e}")
    finally:
        ser.close()
        logging.info("Disconnected from PN532")

if __name__ == "__main__":
    main()