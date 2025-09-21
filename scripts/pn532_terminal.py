#!/usr/bin/env python3
"""
PN532 Multi-Workflow Terminal v31.337
REAL HARDWARE ONLY - EMV Terminal with 5 Workflow Support
NO SIMULATION - REAL DATA TESTING ONLY
"""

import serial
import time
import logging
import sys
import argparse
from typing import Optional

# Logging setup
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    datefmt='%H:%M:%S'
)
logger = logging.getLogger(__name__)

class PN532Terminal:
    """REAL HARDWARE Multi-Workflow PN532 Terminal"""
    
    WORKFLOWS = {
        1: {
            "name": "VISA MSD Track2-from-GPO",
            "description": "Standard VISA MSD with Track2 from GPO response",
            "aid": "A0000000031010",
            "commands": [
                ("00a404000e325041592e5359532e444446303100", "SELECT PPSE"),
                ("00a4040007a000000003101000", "SELECT AID"),
                ("80a8000023832127000000000000001000000000000000097800000000000978230301003839303100", "GPO with PDOL"),
                ("00B2011400", "READ RECORD (SFI=2, Record=1)")
            ]
        },
        2: {
            "name": "US Debit Full Transaction", 
            "description": "Full US debit card transaction flow",
            "aid": "A0000000980840",
            "commands": [
                ("00a404000e325041592e5359532e444446303100", "SELECT PPSE"),
                ("00a4040007a000000098084000", "SELECT AID"),
                ("80a8000023832127000000000000001000000000000000097800000000000978230301003839303100", "GPO with PDOL"),
                ("00B2011400", "READ RECORD (SFI=2, Record=1)"),
                ("00B2021400", "READ RECORD (SFI=2, Record=2)")
            ]
        },
        3: {
            "name": "MasterCard MSD Profile",
            "description": "MasterCard Magnetic Stripe Data profile", 
            "aid": "A0000000041010",
            "commands": [
                ("00a404000e325041592e5359532e444446303100", "SELECT PPSE"),
                ("00a4040007a000000004101000", "SELECT AID"),
                ("80a8000023832127000000000000001000000000000000097800000000000978230301003839303100", "GPO with PDOL"),
                ("00B2011400", "READ RECORD (SFI=2, Record=1)")
            ]
        },
        4: {
            "name": "Discover Contactless",
            "description": "Discover Network contactless transaction",
            "aid": "A0000001523010", 
            "commands": [
                ("00a404000e325041592e5359532e444446303100", "SELECT PPSE"),
                ("00a4040007a000000152301000", "SELECT AID"),
                ("80a8000023832127000000000000001000000000000000097800000000000978230301003839303100", "GPO with PDOL"),
                ("00B2011400", "READ RECORD (SFI=2, Record=1)")
            ]
        },
        5: {
            "name": "AMEX ExpressPay",
            "description": "American Express ExpressPay contactless",
            "aid": "A0000000250100",
            "commands": [
                ("00a404000e325041592e5359532e444446303100", "SELECT PPSE"),
                ("00a4040007a000000025010000", "SELECT AID"),
                ("80a8000023832127000000000000001000000000000000097800000000000978230301003839303100", "GPO with PDOL"),
                ("00B2011400", "READ RECORD (SFI=2, Record=1)")
            ]
        }
    }
    
    def __init__(self, port: str):
        """Initialize REAL HARDWARE terminal"""
        self.port = port
        self.ser = None
        self.target_selected = False  # Track if card target is selected
        
        logger.info("PN532 REAL HARDWARE Terminal initialized on %s", port)
        logger.info("REAL DATA ONLY - NO SIMULATION MODE")
    
    def connect(self) -> bool:
        """Connect to REAL PN532 hardware"""
        try:
            self.ser = serial.Serial(
                port=self.port,
                baudrate=115200,
                timeout=2.0,
                bytesize=serial.EIGHTBITS,
                parity=serial.PARITY_NONE,
                stopbits=serial.STOPBITS_ONE
            )
            
            if self.ser.in_waiting:
                self.ser.reset_input_buffer()
            
            logger.info("REAL HARDWARE: Connected to PN532 on %s", self.port)
            return True
            
        except Exception as e:
            logger.error("REAL HARDWARE: Connection failed: %s", e)
            return False
    
    def disconnect(self):
        """Disconnect from REAL PN532 hardware"""
        self.target_selected = False  # Reset target selection
        if self.ser and self.ser.is_open:
            self.ser.close()
            logger.info("REAL HARDWARE: Disconnected from PN532")
    
    def send_apdu(self, apdu_hex: str, description: str = "") -> Optional[str]:
        """Send APDU command to card via PN532 hardware"""
        if not self.ser or not self.ser.is_open:
            logger.error("REAL HARDWARE: Serial port not open")
            return None
        
        if not self.target_selected:
            logger.error("REAL HARDWARE: No target selected! Card detection required first.")
            return None
        
        try:
            # Convert APDU hex to bytes
            apdu_bytes = bytes.fromhex(apdu_hex)
            apdu_len = len(apdu_bytes)
            
            # Build PN532 InDataExchange command frame
            # Format: 00 00 FF LEN CS D4 40 01 [APDU] CS 00
            frame = bytearray()
            frame.extend([0x00, 0x00, 0xFF])  # Preamble and start code
            
            data_len = apdu_len + 2  # D4 40 01 + APDU
            frame.append(data_len + 1)  # Length including command
            frame.append((~(data_len + 1) + 1) & 0xFF)  # Length checksum
            
            frame.extend([0xD4, 0x40, 0x01])  # InDataExchange command
            frame.extend(apdu_bytes)  # APDU data
            
            # Calculate data checksum
            checksum = 0
            for b in frame[5:]:  # Skip preamble, start, length, length_cs
                checksum += b
            frame.append((~checksum + 1) & 0xFF)
            frame.append(0x00)  # Postamble
            
            logger.info("[REAL-TX] %s: %s", description or "APDU", apdu_hex)
            
            # Send command
            self.ser.write(frame)
            time.sleep(1.0)  # Critical timing: match working script (was 0.5)
            
            # Read response with proper timing for EMV
            response = self.ser.read(300)  # Match working script buffer size
            if response:
                response_hex = response.hex().upper()
                logger.info("[REAL-RX] Raw response: %s", response_hex)
                
                # PN532 sends concatenated frames with empty frames first
                # Skip empty frames, find data frame at offset 6 for APDU extraction
                data_start = 0
                for i in range(len(response) - 7):
                    if (response[i] == 0x00 and response[i+1] == 0x00 and 
                        response[i+2] == 0xFF and i+5 < len(response) and
                        response[i+5] == 0xD5 and response[i+6] == 0x41):
                        data_start = i + 6  # Found data frame, APDU starts after offset 6
                        break
                
                if data_start > 0:
                    # Extract APDU response from data frame
                    apdu_start = data_start + 2  # Skip D5 41 status bytes
                    apdu_data = response[apdu_start:-2]  # Remove checksum and postamble
                    if apdu_data:
                        apdu_hex = apdu_data.hex().upper()
                        logger.info("[REAL-RX] APDU response: %s", apdu_hex)
                        return apdu_hex
                else:
                    # Check if this is a communication error indicating lost card
                    if len(response) <= 6:
                        logger.warning("REAL HARDWARE: Possible card connection lost - short response")
                        self.target_selected = False  # Reset target selection
                    else:
                        logger.warning("REAL HARDWARE: No valid data frame found in response")
                
            logger.warning("REAL HARDWARE: No valid APDU response received")
            return None
                
        except Exception as e:
            logger.error("REAL HARDWARE: APDU command failed: %s", e)
            return None
    
    def initialize_reader(self) -> bool:
        """Initialize REAL PN532 hardware for contactless communication"""
        logger.info("REAL HARDWARE: Initializing PN532 reader...")
        
        try:
            # Reset buffers
            if self.ser.in_waiting:
                self.ser.reset_input_buffer()
            self.ser.reset_output_buffer()
            time.sleep(0.5)
            
            # Wake up command (proven working sequence from pn532_emv_flow.py)
            wake_cmd = b'\x55\x55\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'
            self.ser.write(wake_cmd)
            time.sleep(0.5)
            
            # Get version
            version_cmd = b'\x00\x00\xFF\x02\xFE\xD4\x02\x2A\x00'
            self.ser.write(version_cmd)
            time.sleep(0.5)
            response = self.ser.read(20)
            logger.info("REAL HARDWARE: Version response: %s", response.hex() if response else 'None')
            
            # Configure SAM
            sam_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00'
            self.ser.write(sam_cmd)
            time.sleep(0.2)
            response = self.ser.read(10)
            
            # Enable RF field
            rf_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'
            self.ser.write(rf_cmd)
            time.sleep(0.2)
            response = self.ser.read(10)
            
            logger.info("REAL HARDWARE: PN532 reader initialized successfully!")
            return True
            
        except Exception as e:
            logger.error("REAL HARDWARE: Initialization failed: %s", e)
            return False
    
    def detect_card(self) -> bool:
        """Detect and select contactless card"""
        logger.info("REAL HARDWARE: Detecting contactless card...")
        
        for attempt in range(10):
            logger.info("REAL HARDWARE: Detection attempt %d/10 - Hold phone on PN532!", attempt + 1)
            
            # Use the working detection command
            detect_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
            self.ser.write(detect_cmd)
            time.sleep(0.5)
            response = self.ser.read(50)
            
            if response and len(response) >= 8:
                logger.info("REAL HARDWARE: Raw detection response: %s", response.hex().upper())
                
                # PN532 sends concatenated frames, find the actual data frame
                data_start = 0
                for i in range(len(response) - 7):
                    if (response[i] == 0x00 and response[i+1] == 0x00 and 
                        response[i+2] == 0xFF and i+5 < len(response) and
                        response[i+5] == 0xD5 and response[i+6] == 0x4B):
                        data_start = i + 6  # Found data frame
                        break
                
                if data_start > 0 and len(response) > data_start + 5:
                    # Check if card was detected (status byte after D5 4B)
                    if response[data_start + 2] == 0x01:  # Number of targets found
                        logger.info("REAL HARDWARE: Contactless card detected successfully!")
                        self.target_selected = True  # Mark target as selected
                        return True
                    else:
                        logger.debug("REAL HARDWARE: No card detected in this attempt")
            
            time.sleep(0.5)
        
        logger.error("REAL HARDWARE: Failed to detect contactless card after 10 attempts")
        return False
    
    def execute_workflow(self, workflow_id: int) -> bool:
        """Execute specified EMV workflow on REAL hardware"""
        if workflow_id not in self.WORKFLOWS:
            logger.error("Invalid workflow ID: %d", workflow_id)
            return False
        
        workflow = self.WORKFLOWS[workflow_id]
        logger.info("=" * 70)
        logger.info("REAL HARDWARE: EXECUTING WORKFLOW %d: %s", workflow_id, workflow['name'])
        logger.info("REAL HARDWARE: AID: %s", workflow['aid'])
        logger.info("REAL HARDWARE: Description: %s", workflow['description'])
        logger.info("=" * 70)
        
        if not self.initialize_reader():
            logger.error("REAL HARDWARE: Reader initialization failed")
            return False
        
        logger.info("REAL HARDWARE: PLACE ANDROID HCE DEVICE ON PN532 READER...")
        
        # Detect and select target before sending APDUs
        if not self.detect_card():
            logger.error("REAL HARDWARE: Card detection failed")
            return False
        
        success_count = 0
        total_commands = len(workflow['commands'])
        
        for i, (command, description) in enumerate(workflow['commands'], 1):
            logger.info("REAL HARDWARE: Command %d/%d: %s", i, total_commands, description)
            
            # Check if target is still selected, re-detect if needed
            if not self.target_selected:
                logger.warning("REAL HARDWARE: Target lost, attempting re-detection...")
                if not self.detect_card():
                    logger.error("REAL HARDWARE: Failed to re-detect card, aborting workflow")
                    break
            
            response = self.send_apdu(command, description)
            
            if response:
                if response.endswith('9000'):
                    success_count += 1
                    logger.info("[REAL-SUCCESS] %s", description)
                else:
                    # All 6xxx responses are error codes (6A82=File not found, etc.)
                    logger.warning("[REAL-ERROR] %s - Error: %s", description, response)
            else:
                logger.error("[REAL-FAILED] %s - No response", description)
            
            time.sleep(1.0)  # EMV timing: proper delay between commands
        
        workflow_success = success_count >= (total_commands // 2)
        
        if workflow_success:
            logger.info("=" * 70)
            logger.info("REAL HARDWARE: [COMPLETE] WORKFLOW %d EXECUTED SUCCESSFULLY!", workflow_id)
            logger.info("REAL HARDWARE: Commands successful: %d/%d", success_count, total_commands)
            logger.info("=" * 70)
        else:
            logger.warning("=" * 70)
            logger.warning("REAL HARDWARE: [PARTIAL] Workflow completed with issues")
            logger.warning("REAL HARDWARE: Commands successful: %d/%d", success_count, total_commands)
            logger.warning("=" * 70)
        
        return workflow_success
    
    @classmethod
    def list_workflows(cls):
        """List all available REAL HARDWARE workflows"""
        print("\nREAL HARDWARE EMV WORKFLOWS")
        print("=" * 70)
        
        for workflow_id, workflow in cls.WORKFLOWS.items():
            print(f"Workflow {workflow_id}: {workflow['name']}")
            print(f"   AID: {workflow['aid']}")
            print(f"   Description: {workflow['description']}")
            print(f"   Commands: {len(workflow['commands'])}")
            print()

def main():
    """Main entry point for REAL HARDWARE terminal"""
    parser = argparse.ArgumentParser(
        description="PN532 Multi-Workflow Terminal v31.337 - REAL HARDWARE ONLY",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
REAL HARDWARE EXAMPLES:
  python3 pn532_terminal.py --port /dev/rfcomm1 --workflow 1
  python3 pn532_terminal.py --list-workflows
  python3 pn532_terminal.py --port /dev/ttyUSB0 --workflow 2
  
REAL DATA TESTING with Android HCE - NO SIMULATION
        """
    )
    
    parser.add_argument('--port', '-p', default='/dev/rfcomm1',
                       help='Serial port for REAL PN532 hardware (default: /dev/rfcomm1)')
    parser.add_argument('--workflow', '-w', type=int, default=1,
                       help='Workflow ID (1-5, default: 1)')
    parser.add_argument('--list-workflows', '-l', action='store_true',
                       help='List all available workflows')
    parser.add_argument('--verbose', '-v', action='store_true',
                       help='Enable verbose logging')
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    if args.list_workflows:
        PN532Terminal.list_workflows()
        return 0
    
    if args.workflow not in PN532Terminal.WORKFLOWS:
        print(f"Invalid workflow ID: {args.workflow}")
        print("Use --list-workflows to see available options")
        return 1
    
    logger.info("=" * 70)
    logger.info("PN532 MULTI-WORKFLOW TERMINAL v31.337")
    logger.info("REAL HARDWARE ONLY - NO SIMULATION")
    logger.info("Port: %s | Workflow: %d", args.port, args.workflow)
    logger.info("=" * 70)
    
    terminal = PN532Terminal(args.port)
    
    try:
        if not terminal.connect():
            logger.error("REAL HARDWARE: Failed to connect to PN532")
            logger.error("REAL HARDWARE: Check connection to %s", args.port)
            return 1
        
        success = terminal.execute_workflow(args.workflow)
        
        if success:
            logger.info("REAL HARDWARE: [COMPLETE] EMV workflow executed successfully!")
            return 0
        else:
            logger.warning("REAL HARDWARE: [WARNING] Workflow completed with issues")
            return 1
            
    except KeyboardInterrupt:
        logger.info("REAL HARDWARE: Interrupted by user")
        return 1
    except Exception as e:
        logger.error("REAL HARDWARE: Unexpected error: %s", e)
        return 1
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    sys.exit(main())
