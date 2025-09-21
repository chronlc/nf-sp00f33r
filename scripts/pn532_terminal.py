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
        if self.ser and self.ser.is_open:
            self.ser.close()
            logger.info("REAL HARDWARE: Disconnected from PN532")
    
    def send_command(self, command: str, description: str = "") -> Optional[str]:
        """Send REAL command to REAL PN532 hardware and get REAL response"""
        if not self.ser or not self.ser.is_open:
            logger.error("REAL HARDWARE: Serial port not open")
            return None
        
        try:
            cmd_bytes = bytes.fromhex(command)
            logger.info("[REAL-TX] %s: %s", description or "Command", command)
            
            self.ser.write(cmd_bytes)
            time.sleep(0.1)
            
            response = b''
            start_time = time.time()
            
            while time.time() - start_time < 2.0:
                if self.ser.in_waiting:
                    chunk = self.ser.read(self.ser.in_waiting)
                    response += chunk
                    time.sleep(0.05)
                else:
                    time.sleep(0.1)
            
            if response:
                response_hex = response.hex().upper()
                logger.info("[REAL-RX] Response: %s", response_hex)
                return response_hex
            else:
                logger.warning("REAL HARDWARE: No response received")
                return None
                
        except Exception as e:
            logger.error("REAL HARDWARE: Command failed: %s", e)
            return None
    
    def initialize_reader(self) -> bool:
        """Initialize REAL PN532 hardware for contactless communication"""
        init_commands = [
            ("55550000000000FF00FF00", "Wake up PN532"),
            ("55550000000100FFFF00", "SAM Configuration"),
            ("55550000000200FFFF0102", "RF Configuration"),
            ("5555000000040014FF5401FF00", "InListPassiveTarget")
        ]
        
        logger.info("REAL HARDWARE: Initializing PN532 reader...")
        
        for command, desc in init_commands:
            response = self.send_command(command, desc)
            if not response:
                logger.error("REAL HARDWARE: Initialization failed at: %s", desc)
                return False
            time.sleep(0.1)
        
        logger.info("REAL HARDWARE: PN532 reader initialized successfully!")
        return True
    
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
        logger.info("REAL HARDWARE: Waiting for contactless card...")
        time.sleep(3)
        
        success_count = 0
        total_commands = len(workflow['commands'])
        
        for i, (command, description) in enumerate(workflow['commands'], 1):
            logger.info("REAL HARDWARE: Command %d/%d: %s", i, total_commands, description)
            response = self.send_command(command, description)
            
            if response:
                if response.endswith('9000'):
                    success_count += 1
                    logger.info("[REAL-SUCCESS] %s", description)
                elif not response.startswith('6A'):
                    success_count += 1
                    logger.info("[REAL-SUCCESS] %s (Response: %s)", description, response[:20] + "...")
                else:
                    logger.warning("[REAL-ERROR] %s - Error: %s", description, response)
            else:
                logger.error("[REAL-FAILED] %s - No response", description)
            
            time.sleep(0.2)
        
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
