#!/usr/bin/env python3
"""
EMV Terminal Workflow System - Based on emv.html flow data
Different EMV workflows implemented via terminal behavior variations

Workflow styles based on actual EMV flows:
1. MSD Track2 Quick Read (AIP=2000, minimal records)
2. Full EMV Flow (AIP=2000, complete AFL processing)
3. US Debit Contactless (AIP=0000, simplified)
4. High Value Transaction (extended verification)
5. Low Value Payment (VLP optimization)
"""
import serial
import time
import logging
import argparse
from typing import Optional, List, Dict

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    datefmt='%H:%M:%S'
)
logger = logging.getLogger(__name__)

class EmvWorkflowTerminal:
    """EMV Terminal with workflow-specific behavior patterns"""
    
    def __init__(self, port: str, workflow: int = 1):
        self.port = port
        self.ser = None
        self.target_active = False
        self.workflow = workflow
        
        # EMV Workflow definitions based on emv.html analysis
        self.WORKFLOWS = {
            1: {
                'name': 'MSD Track2 Quick Read',
                'description': 'VISA MSD with Track2 from GPO, minimal processing',
                'terminal_qualifiers': '27000000',  # Standard contactless
                'amount': '000000001000',  # $10.00
                'transaction_type': '00',  # Purchase
                'read_records': False,  # Skip READ RECORD for speed
                'target_aip': '2000'  # MSD supported
            },
            2: {
                'name': 'Full EMV Chip Flow',
                'description': 'Complete EMV processing with all records',
                'terminal_qualifiers': '27000000',
                'amount': '000000005000',  # $50.00 
                'transaction_type': '00',
                'read_records': True,  # Read all AFL records
                'target_aip': '2000'
            },
            3: {
                'name': 'US Debit Contactless',
                'description': 'US Debit AID with simplified flow',
                'terminal_qualifiers': 'B7604000',  # PIN required
                'amount': '000000002000',  # $20.00
                'transaction_type': '00',
                'read_records': False,
                'target_aip': '0000',  # Basic debit
                'preferred_aid': 'A0000000980840'  # US Debit AID
            },
            4: {
                'name': 'High Value Transaction',
                'description': 'High amount with extended verification',
                'terminal_qualifiers': 'F0204000',  # Online required
                'amount': '000000100000',  # $1000.00
                'transaction_type': '00',
                'read_records': True,
                'target_aip': 'A000'  # Online authorization
            },
            5: {
                'name': 'Low Value Payment (VLP)',
                'description': 'Fast low value payment, optimized',
                'terminal_qualifiers': 'A0000000',  # No CVM
                'amount': '000000000500',  # $5.00
                'transaction_type': '00',
                'read_records': False,
                'target_aip': '8000',  # Contactless only
                'optimize_speed': True
            }
        }
    
    def get_workflow_info(self) -> Dict:
        """Get current workflow configuration"""
        return self.WORKFLOWS.get(self.workflow, self.WORKFLOWS[1])
    
    def connect(self) -> bool:
        """Connect to PN532"""
        try:
            self.ser = serial.Serial(self.port, 115200, timeout=0.25)
            logger.info("EMV Terminal connected to %s", self.port)
            return True
        except Exception as e:
            logger.error("Connection failed - %s", e)
            return False
    
    def send_raw_command(self, data: bytes) -> bytes:
        """Send raw command with workflow-optimized timing"""
        try:
            self.ser.write(data)
            
            # Workflow-specific timing optimization
            workflow_info = self.get_workflow_info()
            if workflow_info.get('optimize_speed'):
                time.sleep(0.02)  # Ultra-fast for VLP
            else:
                time.sleep(0.04)  # Standard timing
                
            response = self.ser.read(500)
            return response
        except Exception as e:
            logger.error("Command failed - %s", e)
            return b''
    
    def initialize_terminal(self) -> bool:
        """Initialize PN532 for EMV processing"""
        logger.info("Initializing EMV terminal for workflow: %s", 
                   self.get_workflow_info()['name'])
        
        commands = [
            b'\x55' * 10,  # Wake up
            b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00',  # SAM Config
            b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'  # RF Enable
        ]
        
        for cmd in commands:
            self.send_raw_command(cmd)
        
        logger.info("EMV terminal initialized")
        return True
    
    def detect_card_emv(self) -> bool:
        """EMV card detection"""
        detect_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
        response = self.send_raw_command(detect_cmd)
        
        if len(response) > 15:
            logger.info("EMV card detected")
            self.target_active = True
            return True
        
        logger.error("No EMV card detected")
        return False
    
    def send_apdu_emv(self, apdu_hex: str, description: str = "") -> Optional[str]:
        """Send APDU with EMV-specific processing"""
        if not self.target_active:
            logger.error("No EMV target active")
            return None
        
        try:
            # Build PN532 frame for EMV APDU
            apdu_bytes = bytes.fromhex(apdu_hex)
            data_len = len(apdu_bytes) + 3
            
            frame = bytearray([0x00, 0x00, 0xFF, data_len, (~data_len + 1) & 0xFF, 0xD4, 0x40, 0x01])
            frame.extend(apdu_bytes)
            
            checksum = sum(frame[5:])
            frame.extend([(~checksum + 1) & 0xFF, 0x00])
            
            logger.info("[EMV-TX] %s: %s", description, apdu_hex)
            
            response = self.send_raw_command(frame)
            
            if response:
                # Parse EMV response
                for i in range(min(50, len(response) - 7)):
                    if (response[i:i+3] == b'\x00\x00\xFF' and 
                        i+5 < len(response) and response[i+5] == 0xD5):
                        apdu_data = response[i+7:-2]
                        if apdu_data:
                            apdu_hex_resp = apdu_data.hex().upper()
                            logger.info("[EMV-RX] %s: %s", description, apdu_hex_resp)
                            return apdu_hex_resp
                        break
            
            logger.warning("EMV: No response for %s", description)
            return None
            
        except Exception as e:
            logger.error("EMV APDU failed - %s", e)
            return None
    
    def build_gpo_command(self) -> str:
        """Build GPO command with workflow-specific PDOL data"""
        workflow_info = self.get_workflow_info()
        
        # PDOL data based on emv.html analysis
        # 9F66(4) + 9F02(6) + 9F03(6) + 9F1A(2) + 95(5) + 5F2A(2) + 9A(3) + 9C(1) + 9F37(4)
        pdol_data = (
            workflow_info['terminal_qualifiers'] +  # 9F66 Terminal Transaction Qualifiers
            workflow_info['amount'] +               # 9F02 Amount, Authorised
            '000000000000' +                        # 9F03 Amount, Other
            '0978' +                                # 9F1A Terminal Country Code (US)
            '0000000000' +                          # 95 Terminal Verification Results
            '0978' +                                # 5F2A Transaction Currency Code (USD)
            '230301' +                              # 9A Transaction Date
            workflow_info['transaction_type'] +     # 9C Transaction Type
            '38393031'                              # 9F37 Unpredictable Number
        )
        
        # GPO command: 80A8 + length + 83 + length + PDOL_data
        pdol_length = len(pdol_data) // 2
        gpo_command = f'80A8{pdol_length+2:04X}83{pdol_length:02X}{pdol_data}00'
        
        return gpo_command
    
    def execute_emv_workflow(self) -> bool:
        """Execute EMV workflow based on selected pattern"""
        start_time = time.time()
        workflow_info = self.get_workflow_info()
        
        logger.info("🚀 EMV WORKFLOW: %s", workflow_info['description'])
        logger.info("💰 Amount: $%.2f | TTQ: %s", 
                   int(workflow_info['amount']) / 100, 
                   workflow_info['terminal_qualifiers'])
        
        # Phase 1: Initialization
        if not self.initialize_terminal():
            return False
        
        if not self.detect_card_emv():
            return False
        
        # Phase 2: EMV Transaction Flow
        
        # SELECT PPSE
        ppse_resp = self.send_apdu_emv(
            "00a404000e325041592e5359532e444446303100", 
            "SELECT PPSE"
        )
        if not ppse_resp or not ppse_resp.endswith('9000'):
            logger.error("EMV: PPSE selection failed")
            return False
        
        # Parse AIDs from PPSE
        aids = self.parse_ppse_aids(ppse_resp)
        if not aids:
            logger.error("EMV: No AIDs found in PPSE")
            return False
        
        logger.info("EMV: Found AIDs: %s", aids)
        
        # Select AID based on workflow preference
        preferred_aid = workflow_info.get('preferred_aid')
        if preferred_aid and preferred_aid in aids:
            selected_aid = preferred_aid
            logger.info("EMV: Using preferred AID: %s", selected_aid)
        else:
            selected_aid = aids[0]
            logger.info("EMV: Using first AID: %s", selected_aid)
        
        # SELECT AID
        aid_resp = self.send_apdu_emv(
            f"00a4040007{selected_aid}00", 
            f"SELECT AID {selected_aid}"
        )
        if not aid_resp or not aid_resp.endswith('9000'):
            logger.error("EMV: AID selection failed")
            return False
        
        # GET PROCESSING OPTIONS with workflow-specific PDOL
        gpo_command = self.build_gpo_command()
        gpo_resp = self.send_apdu_emv(gpo_command, "GPO")
        if not gpo_resp or not gpo_resp.endswith('9000'):
            logger.error("EMV: GPO failed")
            return False
        
        # Extract AIP and validate against workflow expectation
        aip = self.extract_aip(gpo_resp)
        expected_aip = workflow_info.get('target_aip')
        if aip:
            logger.info("EMV: AIP received: %s (expected: %s)", aip, expected_aip)
        
        # READ RECORDs based on workflow configuration
        if workflow_info.get('read_records', False):
            afl = self.extract_afl(gpo_resp)
            if afl:
                self.process_afl_records(afl)
            else:
                logger.info("EMV: No AFL found, using Track2 from GPO")
        else:
            logger.info("EMV: Skipping READ RECORDs for fast transaction")
        
        # Extract Track2 data
        track2 = self.extract_track2(gpo_resp)
        if track2:
            pan, expiry = self.parse_track2(track2)
            logger.info("EMV: PAN: %s | Expiry: %s", pan, expiry)
        
        # Calculate transaction time
        total_time = time.time() - start_time
        
        logger.info("🎯 EMV WORKFLOW COMPLETE!")
        logger.info("⚡ Transaction time: %.2f seconds", total_time)
        logger.info("📊 Workflow: %s | AID: %s", workflow_info['name'], selected_aid)
        
        return True
    
    def parse_ppse_aids(self, ppse_hex: str) -> List[str]:
        """Extract AIDs from PPSE response"""
        try:
            ppse_bytes = bytes.fromhex(ppse_hex[:-4])
            aids = []
            
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
    
    def extract_aip(self, gpo_response: str) -> Optional[str]:
        """Extract AIP from GPO response"""
        try:
            # Look for tag 82 (AIP)
            data = bytes.fromhex(gpo_response[:-4])
            for i in range(len(data) - 3):
                if data[i] == 0x82 and data[i+1] == 0x02:
                    return data[i+2:i+4].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_afl(self, gpo_response: str) -> Optional[str]:
        """Extract AFL from GPO response"""
        try:
            # Look for tag 94 (AFL)
            data = bytes.fromhex(gpo_response[:-4])
            for i in range(len(data) - 2):
                if data[i] == 0x94:
                    afl_len = data[i+1]
                    if i + 2 + afl_len <= len(data):
                        return data[i+2:i+2+afl_len].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_track2(self, gpo_response: str) -> Optional[str]:
        """Extract Track2 from GPO response"""
        try:
            # Look for tag 57 (Track2)
            data = bytes.fromhex(gpo_response[:-4])
            for i in range(len(data) - 2):
                if data[i] == 0x57:
                    track2_len = data[i+1]
                    if i + 2 + track2_len <= len(data):
                        return data[i+2:i+2+track2_len].hex().upper()
            return None
        except Exception:
            return None
    
    def parse_track2(self, track2_hex: str) -> tuple:
        """Parse PAN and expiry from Track2 data"""
        try:
            # Find separator 'D'
            sep_pos = track2_hex.find('D')
            if sep_pos > 0:
                pan = track2_hex[:sep_pos]
                expiry = track2_hex[sep_pos+1:sep_pos+5]
                return pan, expiry
            return "", ""
        except Exception:
            return "", ""
    
    def process_afl_records(self, afl_hex: str):
        """Process AFL records if workflow requires it"""
        logger.info("EMV: Processing AFL records: %s", afl_hex)
        
        # Parse AFL entries (SFI, start record, end record, auth records)
        afl_bytes = bytes.fromhex(afl_hex)
        for i in range(0, len(afl_bytes), 4):
            if i + 3 < len(afl_bytes):
                sfi = (afl_bytes[i] & 0xF8) >> 3
                start_rec = afl_bytes[i + 1]
                end_rec = afl_bytes[i + 2]
                
                logger.info("EMV: Reading SFI %d, records %d-%d", sfi, start_rec, end_rec)
                
                # Read each record in the AFL entry
                for rec_num in range(start_rec, end_rec + 1):
                    read_cmd = f"00B2{rec_num:02X}{(sfi << 3) | 0x04:02X}00"
                    resp = self.send_apdu_emv(read_cmd, f"READ RECORD SFI{sfi} REC{rec_num}")
                    if resp and resp.endswith('9000'):
                        logger.info("EMV: Record read successful")
    
    def disconnect(self):
        """Clean disconnect"""
        if self.ser and self.ser.is_open:
            self.ser.close()
            logger.info("EMV Terminal disconnected")

def main():
    parser = argparse.ArgumentParser(description='EMV Terminal Workflow System')
    parser.add_argument('--port', default='/dev/rfcomm1', help='Serial port (default: /dev/rfcomm1)')
    parser.add_argument('--workflow', type=int, default=1, choices=[1,2,3,4,5], 
                       help='EMV Workflow: 1=MSD Quick, 2=Full EMV, 3=US Debit, 4=High Value, 5=VLP (default: 1)')
    parser.add_argument('--list-workflows', action='store_true', help='List available workflows')
    
    args = parser.parse_args()
    
    if args.list_workflows:
        terminal = EmvWorkflowTerminal(args.port, 1)
        print("Available EMV Workflows:")
        print("=" * 80)
        for wid, workflow in terminal.WORKFLOWS.items():
            print(f"{wid}. {workflow['name']}")
            print(f"   {workflow['description']}")
            print(f"   Amount: ${int(workflow['amount']) / 100:.2f} | TTQ: {workflow['terminal_qualifiers']}")
            if workflow.get('preferred_aid'):
                print(f"   Preferred AID: {workflow['preferred_aid']}")
            print()
        return
    
    workflow_info = EmvWorkflowTerminal(args.port, args.workflow).get_workflow_info()
    
    logger.info("=" * 80)
    logger.info("🏦 EMV TERMINAL WORKFLOW SYSTEM")
    logger.info("📡 Port: %s | Workflow: %d", args.port, args.workflow)
    logger.info("🎯 %s", workflow_info['description'])
    logger.info("💰 Amount: $%.2f | TTQ: %s", 
               int(workflow_info['amount']) / 100, 
               workflow_info['terminal_qualifiers'])
    logger.info("=" * 80)
    
    terminal = EmvWorkflowTerminal(args.port, args.workflow)
    
    try:
        if not terminal.connect():
            return False
        
        logger.info("📱 PLACE EMV CARD ON PN532 READER...")
        
        success = terminal.execute_emv_workflow()
        
        logger.info("=" * 80)
        if success:
            logger.info("✅ EMV WORKFLOW SUCCESSFUL!")
        else:
            logger.error("❌ EMV WORKFLOW FAILED!")
        logger.info("=" * 80)
        
        return success
        
    except KeyboardInterrupt:
        logger.info("EMV: Interrupted by user")
        return False
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)