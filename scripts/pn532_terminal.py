#!/usr/bin/env python3
"""
PN532 Multi-Workflow Terminal v31.337
EMV Terminal with 5 Workflow Support
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
    """Multi-Workflow PN532 Terminal"""
    
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
    
    def __init__(self, port: str, simulation_mode: bool = False):
        """Initialize terminal"""
        self.port = port
        self.ser = None
        self.simulation_mode = simulation_mode
        
        logger.info("PN532 Terminal initialized on %s", port)
        if simulation_mode:
            logger.info("SIMULATION MODE ENABLED")
    
    def connect(self) -> bool:
        """Connect to PN532"""
        if self.simulation_mode:
            logger.info("[SIM] Mock connection established")
            return True
            
        try:
            self.ser = serial.Serial(
                port=self.port,
                baudrate=115200,
                timeout=2.0
            )
            
            if self.ser.in_waiting:
                self.ser.reset_input_buffer()
            
            logger.info("Connected to PN532 on %s", self.port)
            return True
            
        except Exception as e:
            logger.error("Connection failed: %s", e)
            return False
    
    def disconnect(self):
        """Disconnect from PN532"""
        if self.simulation_mode:
            logger.info("[SIM] Mock disconnection")
            return
            
        if self.ser and self.ser.is_open:
            self.ser.close()
            logger.info("Disconnected from PN532")
    
    def send_command(self, command: str, description: str = "") -> Optional[str]:
        """Send command and get response"""
        if self.simulation_mode:
            return self._simulate_response(command, description)
        
        if not self.ser or not self.ser.is_open:
            logger.error("Serial port not open")
            return None
        
        try:
            cmd_bytes = bytes.fromhex(command)
            logger.info("[TX] %s: %s", description or "Command", command)
            
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
                logger.info("[RX] Response: %s", response_hex)
                return response_hex
            else:
                logger.warning("No response received")
                return None
                
        except Exception as e:
            logger.error("Command failed: %s", e)
            return None
    
    def _simulate_response(self, command: str, description: str) -> str:
        """Simulate responses for testing"""
        mock_responses = {
            "00a404000e325041592e5359532e444446303100": "6F23840E325041592E5359532E4444463031A511BF0C0E6104A0000000031010950500001F00009000",
            "00a4040007a000000003101000": "6F3E8407A0000000031010A533502841564953414320434152440010564953414320444542495450104D4153544552434152442020202020208701019F38189F66049F02069F03069F1A0295055F2A029A039C019F37049000",
            "80a8000023832127000000000000001000000000000000097800000000000978230301003839303100": "770A82025C008407A00000000310105F34010157134154904674973556D2902201000082008300035F",
            "00B2011400": "70285F24032902285F25031406015F28020826570E4154904674973556D29022010000820083005A084154904674973556"
        }
        
        response = mock_responses.get(command, "9000")
        logger.info("[SIM] %s: %s -> %s", description, command, response)
        return response
    
    def execute_workflow(self, workflow_id: int) -> bool:
        """Execute specified EMV workflow"""
        if workflow_id not in self.WORKFLOWS:
            logger.error("Invalid workflow ID: %d", workflow_id)
            return False
        
        workflow = self.WORKFLOWS[workflow_id]
        logger.info("=" * 60)
        logger.info("EXECUTING WORKFLOW %d: %s", workflow_id, workflow['name'])
        logger.info("AID: %s", workflow['aid'])
        logger.info("=" * 60)
        
        logger.info("HOLD ANDROID DEVICE ON PN532 READER...")
        time.sleep(2)
        
        success_count = 0
        for command, description in workflow['commands']:
            response = self.send_command(command, description)
            if response and not response.endswith('6A82'):
                success_count += 1
                logger.info("[SUCCESS] %s", description)
            else:
                logger.warning("[PARTIAL] %s", description)
        
        workflow_success = success_count >= len(workflow['commands']) // 2
        
        if workflow_success:
            logger.info("[COMPLETE] WORKFLOW %d SUCCESSFUL!", workflow_id)
        else:
            logger.warning("[PARTIAL] Workflow completed with warnings")
        
        return workflow_success
    
    @classmethod
    def list_workflows(cls):
        """List available workflows"""
        print("\nAVAILABLE EMV WORKFLOWS")
        print("=" * 60)
        
        for workflow_id, workflow in cls.WORKFLOWS.items():
            print(f"Workflow {workflow_id}: {workflow['name']}")
            print(f"   AID: {workflow['aid']}")
            print(f"   Description: {workflow['description']}")
            print(f"   Commands: {len(workflow['commands'])}")
            print()

def main():
    """Main entry point"""
    parser = argparse.ArgumentParser(
        description="PN532 Multi-Workflow Terminal v31.337"
    )
    
    parser.add_argument('--port', '-p', default='/dev/rfcomm1',
                       help='Serial port (default: /dev/rfcomm1)')
    parser.add_argument('--workflow', '-w', type=int, default=1,
                       help='Workflow ID (1-5, default: 1)')
    parser.add_argument('--simulation', '-s', action='store_true',
                       help='Enable simulation mode')
    parser.add_argument('--list-workflows', '-l', action='store_true',
                       help='List all workflows')
    
    args = parser.parse_args()
    
    if args.list_workflows:
        PN532Terminal.list_workflows()
        return 0
    
    if args.workflow not in PN532Terminal.WORKFLOWS:
        print(f"Invalid workflow ID: {args.workflow}")
        return 1
    
    logger.info("=" * 60)
    logger.info("PN532 MULTI-WORKFLOW TERMINAL v31.337")
    logger.info("Port: %s | Workflow: %d | Simulation: %s", 
                args.port, args.workflow, args.simulation)
    logger.info("=" * 60)
    
    terminal = PN532Terminal(args.port, args.simulation)
    
    try:
        if not terminal.connect():
            logger.error("Failed to connect to PN532")
            return 1
        
        success = terminal.execute_workflow(args.workflow)
        
        if success:
            logger.info("[COMPLETE] EMV workflow executed successfully!")
            return 0
        else:
            logger.warning("[WARNING] Workflow completed with issues")
            return 1
            
    except KeyboardInterrupt:
        logger.info("Interrupted by user")
        return 1
    except Exception as e:
        logger.error("Unexpected error: %s", e)
        return 1
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    sys.exit(main())
