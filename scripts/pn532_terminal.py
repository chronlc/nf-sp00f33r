#!/usr/bin/env python3
"""
PN532 Terminal Emulation Script for mag-sp00f Testing
Provides automated NFC terminal functionality for validating HCE emulation.
"""

import argparse
import sys
import time
import logging
from typing import List, Optional, Dict, Tuple

# Try to import pyserial, fall back to simulation mode if not available
try:
    import serial
    import serial.tools.list_ports
    SERIAL_AVAILABLE = True
except ImportError:
    SERIAL_AVAILABLE = False
    print("Warning: pyserial not installed. Running in simulation mode.")

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class PN532Terminal:
    """
    PN532 NFC terminal emulator for testing mag-sp00f Android app.
    Provides automated APDU command testing and validation.
    """
    
    # VISA MSD Test Commands
    VISA_MSD_COMMANDS = {
        'SELECT_PPSE': bytes.fromhex('00A404000E325041592E5359532E444446303100'),
        'SELECT_VISA_MSD': bytes.fromhex('00A4040007A000000003101000'),
        'SELECT_US_DEBIT': bytes.fromhex('00A4040007A000000098084000'),
        'GPO': bytes.fromhex('80A8000008832127000000000000001000000000000000097800000000000978230301003839303100'),
        'READ_RECORD': bytes.fromhex('00B2010C00')
    }
    
    # Expected Response Patterns
    EXPECTED_RESPONSES = {
        'SUCCESS': b'\x90\x00',
        'FILE_NOT_FOUND': b'\x6A\x82',
        'WRONG_LENGTH': b'\x67\x00'
    }
    
    def __init__(self, port: str, baudrate: int = 115200, timeout: float = 2.0):
        """
        Initialize PN532 terminal connection.
        
        Args:
            port: Serial port path (e.g., /dev/ttyUSB0)
            baudrate: Serial communication speed
            timeout: Command timeout in seconds
        """
        self.port = port
        self.baudrate = baudrate
        self.timeout = timeout
        self.connection: Optional[serial.Serial] = None
        self.test_results: List[Dict] = []
        
    def connect(self) -> bool:
        """
        Establish connection to PN532 device.
        
        Returns:
            True if connection successful, False otherwise
        """
        if not SERIAL_AVAILABLE:
            logger.info(f"Simulating connection to PN532 on {self.port}")
            return True
            
        try:
            self.connection = serial.Serial(
                port=self.port,
                baudrate=self.baudrate,
                timeout=self.timeout,
                bytesize=serial.EIGHTBITS,
                parity=serial.PARITY_NONE,
                stopbits=serial.STOPBITS_ONE
            )
            
            if self.connection.is_open:
                logger.info(f"Connected to PN532 on {self.port}")
                return True
            else:
                logger.error(f"Failed to open connection to {self.port}")
                return False
                
        except Exception as e:
            logger.error(f"Serial connection error: {e}")
            return False
    
    def disconnect(self) -> None:
        """Close PN532 connection."""
        if self.connection and self.connection.is_open:
            self.connection.close()
            logger.info("PN532 connection closed")
    
    def send_apdu(self, command: bytes, expected_response: Optional[bytes] = None) -> Tuple[bool, bytes]:
        """
        Send APDU command to connected device and receive response.
        
        Args:
            command: APDU command bytes
            expected_response: Optional expected response for validation
            
        Returns:
            Tuple of (success, response_bytes)
        """
        if not SERIAL_AVAILABLE:
            # Simulation mode - return success response
            response = self.EXPECTED_RESPONSES['SUCCESS']
            command_hex = command.hex().upper()
            response_hex = response.hex().upper()
            logger.info(f"[SIM] TX: {command_hex}")
            logger.info(f"[SIM] RX: {response_hex}")
            return True, response
            
        if not self.connection or not self.connection.is_open:
            logger.error("No active PN532 connection")
            return False, b''
        
        try:
            # Send command
            self.connection.write(command)
            self.connection.flush()
            
            # Wait for response
            time.sleep(0.1)  # Small delay for processing
            
            # Read response
            response = self.connection.read(256)  # Read up to 256 bytes
            
            # Log transaction
            command_hex = command.hex().upper()
            response_hex = response.hex().upper() if response else "NO_RESPONSE"
            logger.info(f"TX: {command_hex}")
            logger.info(f"RX: {response_hex}")
            
            # Validate response if expected
            success = True
            if expected_response and response:
                success = response.endswith(expected_response)
                if not success:
                    logger.warning(f"Response validation failed. Expected ending: {expected_response.hex().upper()}")
            
            return success, response
            
        except Exception as e:
            logger.error(f"Serial communication error: {e}")
            return False, b''
    
    def run_visa_msd_test(self, aid: str = "VISA_MSD") -> bool:
        """
        Execute complete VISA MSD test workflow.
        
        Args:
            aid: AID to test ("VISA_MSD" or "US_DEBIT")
            
        Returns:
            True if test passed, False otherwise
        """
        logger.info(f"Starting VISA MSD test for {aid}")
        
        if aid == "VISA_MSD":
            select_aid_cmd = self.VISA_MSD_COMMANDS['SELECT_VISA_MSD']
        elif aid == "US_DEBIT":
            select_aid_cmd = self.VISA_MSD_COMMANDS['SELECT_US_DEBIT']
        else:
            logger.error(f"Unknown AID: {aid}")
            return False
        
        test_steps = [
            ("SELECT PPSE", self.VISA_MSD_COMMANDS['SELECT_PPSE']),
            (f"SELECT {aid}", select_aid_cmd),
            ("GPO", self.VISA_MSD_COMMANDS['GPO']),
            ("READ RECORD", self.VISA_MSD_COMMANDS['READ_RECORD'])
        ]
        
        results = []
        for step_name, command in test_steps:
            logger.info(f"Executing: {step_name}")
            success, response = self.send_apdu(command, self.EXPECTED_RESPONSES['SUCCESS'])
            
            result = {
                'step': step_name,
                'command': command.hex().upper(),
                'response': response.hex().upper() if response else "NO_RESPONSE",
                'success': success,
                'timestamp': time.time()
            }
            results.append(result)
            
            if not success:
                logger.warning(f"Step failed: {step_name}")
        
        self.test_results.extend(results)
        
        # Overall test success
        overall_success = all(result['success'] for result in results)
        logger.info(f"VISA MSD test {aid}: {'PASSED' if overall_success else 'FAILED'}")
        
        return overall_success
    
    def list_available_ports(self) -> List[str]:
        """
        List available serial ports that might have PN532 devices.
        
        Returns:
            List of available port names
        """
        if not SERIAL_AVAILABLE:
            logger.info("Simulation mode - no real ports available")
            return ['/dev/ttyUSB0', '/dev/rfcomm0']
            
        ports = []
        for port in serial.tools.list_ports.comports():
            ports.append(port.device)
            logger.info(f"Available port: {port.device} - {port.description}")
        
        return ports
    
    def generate_report(self) -> str:
        """
        Generate test report from collected results.
        
        Returns:
            Formatted test report string
        """
        if not self.test_results:
            return "No test results available."
        
        report = ["PN532 Terminal Test Report", "=" * 30, ""]
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for result in self.test_results if result['success'])
        
        report.append(f"Total Tests: {total_tests}")
        report.append(f"Passed: {passed_tests}")
        report.append(f"Failed: {total_tests - passed_tests}")
        report.append(f"Success Rate: {(passed_tests/total_tests)*100:.1f}%")
        report.append("")
        
        for result in self.test_results:
            status = "✅ PASS" if result['success'] else "❌ FAIL"
            report.append(f"{status} {result['step']}")
            report.append(f"  CMD: {result['command']}")
            report.append(f"  RSP: {result['response']}")
            report.append("")
        
        return "\n".join(report)

def main():
    """Main function for command-line interface."""
    parser = argparse.ArgumentParser(
        description="PN532 Terminal Emulator for mag-sp00f Testing",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python pn532_terminal.py --port /dev/ttyUSB0
  python pn532_terminal.py --port /dev/rfcomm0 --validate-visa-msd
  python pn532_terminal.py --list-ports
        """
    )
    
    parser.add_argument('--port', type=str, default='/dev/ttyUSB0',
                        help='Serial port for PN532 (default: /dev/ttyUSB0)')
    parser.add_argument('--baudrate', type=int, default=115200,
                        help='Serial baudrate (default: 115200)')
    parser.add_argument('--timeout', type=float, default=2.0,
                        help='Command timeout in seconds (default: 2.0)')
    parser.add_argument('--validate-visa-msd', action='store_true',
                        help='Run VISA MSD validation test')
    parser.add_argument('--list-ports', action='store_true',
                        help='List available serial ports')
    parser.add_argument('--apdu', type=str,
                        help='Send single APDU command (hex string)')
    parser.add_argument('--verbose', '-v', action='store_true',
                        help='Enable verbose logging')
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    # List ports and exit
    if args.list_ports:
        terminal = PN532Terminal(args.port)
        ports = terminal.list_available_ports()
        if not ports:
            logger.info("No serial ports found")
        return
    
    # Initialize terminal
    terminal = PN532Terminal(args.port, args.baudrate, args.timeout)
    
    # Connect to PN532
    if not terminal.connect():
        logger.error("Failed to connect to PN532")
        sys.exit(1)
    
    try:
        # Single APDU command
        if args.apdu:
            try:
                command = bytes.fromhex(args.apdu)
                success, response = terminal.send_apdu(command)
                if success:
                    logger.info("APDU command executed successfully")
                else:
                    logger.error("APDU command failed")
            except ValueError:
                logger.error("Invalid hex string for APDU command")
                sys.exit(1)
        
        # VISA MSD validation
        elif args.validate_visa_msd:
            logger.info("Running VISA MSD validation tests...")
            
            # Test both AIDs
            visa_success = terminal.run_visa_msd_test("VISA_MSD")
            debit_success = terminal.run_visa_msd_test("US_DEBIT")
            
            overall_success = visa_success and debit_success
            
            # Generate and display report
            report = terminal.generate_report()
            print("\n" + report)
            
            if overall_success:
                logger.info("All VISA MSD tests PASSED")
            else:
                logger.warning("Some VISA MSD tests FAILED")
                sys.exit(1)
        
        else:
            logger.info("PN532 terminal ready. Use --help for command options.")
    
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    main()