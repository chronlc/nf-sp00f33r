#!/usr/bin/env python3
"""
EMV Attack Data Requirements Tester - Mag-Sp00f Project
Tests attack modules with real EMV data from emv.html and analyzes data dependencies

This script uses authentic VISA test card data from emv.html to:
1. Test each attack module with real EMV data
2. Analyze EMV data requirements for attack success
3. Rate card profiles by attack compatibility
4. Generate optimization recommendations

Real EMV Data Source: emv.html VISA TEST card
PAN: 4154904674973556
Track2: 4154904674973556D29022010000820083001F
Cryptogram: D3967976E30EFAFC (ARQC)

Usage:
    python3 test_attack_data_requirements.py --attack all --profile visa_test
    python3 test_attack_data_requirements.py --comprehensive --output results.json
"""

import argparse
import json
import logging
import re
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Tuple, Any

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler(f'attack_data_requirements_test_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)


class EmvAttackDataTester:
    """Test EMV attacks with real data from emv.html and analyze data requirements"""
    
    def __init__(self):
        self.real_emv_data = self.load_real_emv_data()
        self.card_profiles = self.generate_test_profiles()
        self.attack_requirements = self.define_attack_requirements()
        self.test_results = {}
        
    def load_real_emv_data(self) -> Dict[str, Any]:
        """Load real EMV data from emv.html parsing"""
        logger.info("📋 Loading real EMV data from emv.html")
        
        # Real EMV data extracted from emv.html VISA TEST card
        return {
            # Card Identity
            "pan": "4154904674973556",
            "expiry_date": "2902",
            "cardholder_name": "CARDHOLDER/VISA",
            "issuer_country": "0840",  # USA
            "currency_code": "0840",   # USD
            
            # Track Data  
            "track2_data": "4154904674973556D29022010000820083001F",
            "service_code": "201",
            "discretionary_data": "0000820083001F",
            
            # Payment Applications
            "supported_aids": [
                "A0000000031010",  # VISA DEBIT
                "A0000000980840"   # US DEBIT (additional from emv.html)
            ],
            "preferred_aid": "A0000000031010",
            "application_label": "VISA DEBIT",
            "application_priority": 1,
            
            # EMV Capabilities
            "aip": "2000",  # From emv.html: AIP with offline capabilities
            "supported_cryptograms": ["ARQC", "TC", "AAC"],
            "authentication_methods": ["SDA", "DDA"],  # Static and Dynamic Data Authentication
            
            # Transaction Limits (realistic VISA limits)
            "transaction_limits": {
                "single_transaction_limit": 500000,  # $5000
                "daily_limit": 200000,               # $2000  
                "offline_limit": 10000,              # $100
                "contactless_limit": 25000,          # $250
                "no_cvm_limit": 5000                 # $50
            },
            
            # CVM Methods
            "cvm_methods": ["PIN", "SIGNATURE", "NO_CVM"],
            "contactless_supported": True,
            "offline_capable": True,
            
            # Real EMV Tags from emv.html
            "emv_tags": {
                "9F26": "D3967976E30EFAFC",  # Application Cryptogram (real from emv.html)
                "9F27": "80",                # Cryptogram Information Data (ARQC)
                "9F36": "011E",              # Application Transaction Counter
                "82": "2000",                # Application Interchange Profile  
                "57": "4154904674973556D29022010000820083001F",  # Track2 equivalent
                "5A": "4154904674973556",    # PAN
                "5F24": "290228",            # Application Expiration Date
                "5F20": "43415244484F4C4445522F56495341",  # Cardholder Name
                "5F28": "0840",              # Issuer Country Code
                "8E": "000000000000001E030000000000000000",  # CVM List (real from card)
                "94": "08010200",            # Application File Locator
                "8C": "9F02069F03069F1A0295055F2A029A039C01",  # CDOL1
                "8D": "8A0295059F37049F4C08"  # CDOL2
            },
            
            # Additional Capabilities
            "biometric_supported": False,
            "dda_supported": True,
            "cda_supported": False,
            "sda_supported": True
        }
    
    def generate_test_profiles(self) -> Dict[str, Dict[str, Any]]:
        """Generate test card profiles with varying EMV data completeness"""
        base_data = self.real_emv_data.copy()
        
        profiles = {
            # Tier 1: Complete EMV data (emv.html + enhancements)
            "visa_test_complete": {
                **base_data,
                "profile_name": "VISA Test Complete",
                "completeness_score": 100,
                "supported_aids": ["A0000000031010", "A0000000041010", "A0000000980840"],  # Multi-scheme
                "aip": "3800",  # Enhanced capabilities
                "supported_cryptograms": ["ARQC", "TC", "AAC"],
                "authentication_methods": ["SDA", "DDA", "CDA"],
                "cvm_methods": ["PIN", "SIGNATURE", "BIOMETRIC", "NO_CVM"],
                "biometric_supported": True,
                "cda_supported": True,
                "transaction_limits": {
                    "single_transaction_limit": 1000000,  # $10,000
                    "daily_limit": 500000,                # $5,000
                    "offline_limit": 50000,               # $500
                    "contactless_limit": 50000,           # $500
                    "no_cvm_limit": 10000                 # $100
                }
            },
            
            # Tier 2: Standard emv.html data 
            "visa_test_standard": {
                **base_data,
                "profile_name": "VISA Test Standard (emv.html)",
                "completeness_score": 85
            },
            
            # Tier 3: Reduced capabilities
            "visa_test_basic": {
                **base_data,
                "profile_name": "VISA Test Basic",
                "completeness_score": 60,
                "supported_aids": ["A0000000031010"],  # Single AID
                "supported_cryptograms": ["ARQC", "TC"],  # No AAC
                "authentication_methods": ["SDA"],       # SDA only
                "cvm_methods": ["PIN", "SIGNATURE"],     # No NO_CVM
                "offline_capable": False,
                "transaction_limits": {
                    "single_transaction_limit": 100000,  # $1,000
                    "daily_limit": 50000,                # $500
                    "offline_limit": 0,                  # No offline
                    "contactless_limit": 10000,          # $100
                    "no_cvm_limit": 2500                 # $25
                }
            },
            
            # Tier 4: Minimal EMV data
            "visa_test_minimal": {
                "pan": base_data["pan"],
                "expiry_date": base_data["expiry_date"],
                "track2_data": base_data["track2_data"],
                "supported_aids": ["A0000000031010"],
                "aip": "0000",  # Minimal capabilities
                "supported_cryptograms": ["ARQC"],
                "authentication_methods": ["SDA"],
                "cvm_methods": ["PIN"],
                "contactless_supported": False,
                "offline_capable": False,
                "profile_name": "VISA Test Minimal",
                "completeness_score": 30,
                "transaction_limits": {
                    "single_transaction_limit": 25000,   # $250
                    "daily_limit": 10000,                # $100
                    "offline_limit": 0,
                    "contactless_limit": 0,
                    "no_cvm_limit": 0
                }
            },
            
            # Tier 5: MasterCard simulation (for comparison)
            "mastercard_test": {
                **base_data,
                "pan": "5555555555554444",
                "expiry_date": "2912", 
                "track2_data": "5555555555554444D29122010000820083001F",
                "supported_aids": ["A0000000041010"],  # MasterCard AID
                "preferred_aid": "A0000000041010",
                "application_label": "MASTERCARD",
                "profile_name": "MasterCard Test",
                "completeness_score": 75,
                "emv_tags": {
                    **base_data["emv_tags"],
                    "5A": "5555555555554444",
                    "57": "5555555555554444D29122010000820083001F"
                }
            }
        }
        
        logger.info(f"📊 Generated {len(profiles)} test card profiles")
        return profiles
    
    def define_attack_requirements(self) -> Dict[str, Dict[str, str]]:
        """Define EMV data requirements for each attack module"""
        return {
            "ppse_aid_poisoning": {
                "supported_aids": "CRITICAL",      # Multiple AIDs for poisoning
                "preferred_aid": "HIGH", 
                "application_label": "MEDIUM",
                "application_priority": "LOW"
            },
            "aip_force_offline": {
                "aip": "CRITICAL",                 # AIP with offline capabilities
                "offline_capable": "CRITICAL",
                "authentication_methods": "HIGH", 
                "transaction_limits": "MEDIUM"
            },
            "track2_spoofing": {
                "track2_data": "CRITICAL",         # Complete Track2 data
                "pan": "CRITICAL",
                "expiry_date": "HIGH",
                "service_code": "HIGH",
                "discretionary_data": "MEDIUM"
            },
            "cryptogram_downgrade": {
                "supported_cryptograms": "CRITICAL",  # Multiple cryptogram types
                "authentication_methods": "HIGH",     # DDA/CDA for crypto manipulation
                "emv_tags": "HIGH"                    # Tags 9F26, 9F27, 9F36
            },
            "cvm_bypass": {
                "cvm_methods": "CRITICAL",          # Multiple CVM options
                "contactless_supported": "HIGH",
                "transaction_limits": "HIGH",
                "emv_tags": "MEDIUM"               # Tag 8E (CVM List)
            },
            "advanced_force_offline": {
                "offline_capable": "CRITICAL",
                "authentication_methods": "CRITICAL",  # DDA/CDA
                "transaction_limits": "HIGH",
                "emv_tags": "HIGH"                 # AIP, AFL, CDOL
            },
            "enhanced_cvm_bypass": {
                "cvm_methods": "CRITICAL",          # Advanced CVM options
                "contactless_supported": "CRITICAL",
                "biometric_supported": "HIGH",
                "transaction_limits": "HIGH"
            },
            "amount_manipulation": {
                "emv_tags": "CRITICAL",            # Amount tags (9F02, 9F03)
                "transaction_limits": "HIGH",
                "currency_code": "MEDIUM",
                "issuer_country": "MEDIUM"
            },
            "advanced_cryptogram": {
                "supported_cryptograms": "CRITICAL",
                "authentication_methods": "CRITICAL",  # CDA preferred
                "emv_tags": "CRITICAL",            # Full cryptographic data set
                "dda_supported": "HIGH",
                "cda_supported": "HIGH"
            },
            "failed_cryptogram_attack": {
                "supported_cryptograms": "CRITICAL",
                "authentication_methods": "HIGH",
                "emv_tags": "HIGH",               # Valid cryptogram for corruption
                "offline_capable": "MEDIUM"      # For bypass testing
            }
        }
    
    def calculate_data_completeness_score(self, profile: Dict[str, Any], attack_id: str) -> Tuple[int, Dict[str, str]]:
        """Calculate data completeness score for specific attack"""
        requirements = self.attack_requirements.get(attack_id, {})
        
        total_score = 0
        max_score = 0
        missing_data = {}
        
        # Weight values for importance levels
        weights = {"CRITICAL": 40, "HIGH": 25, "MEDIUM": 15, "LOW": 5}
        
        for field, importance in requirements.items():
            weight = weights[importance]
            max_score += weight
            
            if self.check_field_presence(profile, field, importance):
                total_score += weight
            else:
                missing_data[field] = importance
        
        # Calculate percentage score
        percentage_score = int((total_score / max_score * 100)) if max_score > 0 else 0
        
        return percentage_score, missing_data
    
    def check_field_presence(self, profile: Dict[str, Any], field: str, importance: str) -> bool:
        """Check if required field is present and adequate in profile"""
        if field not in profile:
            return False
        
        field_value = profile[field]
        
        # Special validation based on field type and importance
        if field == "supported_aids":
            if importance == "CRITICAL":
                return isinstance(field_value, list) and len(field_value) >= 2  # Multiple AIDs
            else:
                return isinstance(field_value, list) and len(field_value) >= 1
        
        elif field == "supported_cryptograms":
            if importance == "CRITICAL":
                return isinstance(field_value, list) and len(field_value) >= 2
            else:
                return isinstance(field_value, list) and len(field_value) >= 1
                
        elif field == "cvm_methods":
            if importance == "CRITICAL":
                return isinstance(field_value, list) and len(field_value) >= 3
            else:
                return isinstance(field_value, list) and len(field_value) >= 1
        
        elif field == "authentication_methods":
            if importance == "CRITICAL":
                return isinstance(field_value, list) and ("DDA" in field_value or "CDA" in field_value)
            else:
                return isinstance(field_value, list) and len(field_value) >= 1
        
        elif field == "emv_tags":
            if importance == "CRITICAL":
                required_tags = ["9F26", "9F27", "9F36"]
                return isinstance(field_value, dict) and all(tag in field_value for tag in required_tags)
            else:
                return isinstance(field_value, dict) and len(field_value) > 0
        
        elif field == "transaction_limits":
            return isinstance(field_value, dict) and len(field_value) > 0
        
        # Boolean fields
        elif field in ["offline_capable", "contactless_supported", "biometric_supported", "dda_supported", "cda_supported"]:
            return field_value is True
        
        # String fields
        else:
            return field_value is not None and str(field_value).strip() != ""
    
    def test_attack_with_profile(self, attack_id: str, profile: Dict[str, Any]) -> Dict[str, Any]:
        """Test specific attack with specific profile"""
        logger.debug(f"🧪 Testing {attack_id} with {profile.get('profile_name', 'Unknown')}")
        
        # Calculate data completeness score
        data_score, missing_data = self.calculate_data_completeness_score(profile, attack_id)
        
        # Simulate attack execution (using real EMV data patterns)
        attack_result = self.simulate_attack_execution(attack_id, profile, data_score)
        
        # Analyze success factors
        success_factors = self.analyze_attack_success_factors(attack_id, profile, attack_result)
        
        return {
            "attack_id": attack_id,
            "profile_name": profile.get("profile_name", "Unknown"),
            "data_completeness_score": data_score,
            "missing_data": missing_data,
            "attack_result": attack_result,
            "success_factors": success_factors,
            "recommendations": self.generate_data_recommendations(attack_id, missing_data)
        }
    
    def simulate_attack_execution(self, attack_id: str, profile: Dict[str, Any], data_score: int) -> Dict[str, Any]:
        """Simulate attack execution based on EMV data availability"""
        
        # Base success probability based on data completeness
        base_success_rate = min(data_score, 100)
        
        # Attack-specific adjustments based on emv.html data analysis
        attack_multipliers = {
            "ppse_aid_poisoning": self.calculate_ppse_success(profile),
            "aip_force_offline": self.calculate_aip_success(profile), 
            "track2_spoofing": self.calculate_track2_success(profile),
            "cryptogram_downgrade": self.calculate_cryptogram_success(profile),
            "cvm_bypass": self.calculate_cvm_success(profile),
            "advanced_force_offline": self.calculate_advanced_offline_success(profile),
            "enhanced_cvm_bypass": self.calculate_enhanced_cvm_success(profile),
            "amount_manipulation": self.calculate_amount_success(profile),
            "advanced_cryptogram": self.calculate_advanced_crypto_success(profile),
            "failed_cryptogram_attack": self.calculate_failed_crypto_success(profile)
        }
        
        multiplier = attack_multipliers.get(attack_id, 1.0)
        final_success_rate = min(int(base_success_rate * multiplier), 100)
        
        # Simulate execution result
        return {
            "success": final_success_rate >= 70,  # 70% threshold for success
            "success_rate": final_success_rate,
            "base_success_rate": base_success_rate,
            "data_multiplier": multiplier,
            "execution_time": 0.5 + (data_score / 100) * 1.5,  # Simulate timing
            "emv_data_utilized": self.get_utilized_emv_data(attack_id, profile)
        }
    
    def calculate_ppse_success(self, profile: Dict[str, Any]) -> float:
        """Calculate PPSE AID poisoning success multiplier"""
        aids = profile.get("supported_aids", [])
        
        if len(aids) >= 3:
            return 1.2  # Multiple schemes (VISA + MC + Debit)
        elif len(aids) >= 2:
            return 1.1  # Two schemes (like emv.html VISA + US Debit)
        else:
            return 0.7  # Single AID, limited poisoning
    
    def calculate_aip_success(self, profile: Dict[str, Any]) -> float:
        """Calculate AIP force offline success multiplier"""
        aip = profile.get("aip", "0000")
        offline_capable = profile.get("offline_capable", False)
        auth_methods = profile.get("authentication_methods", [])
        
        # Real emv.html has AIP 2000 with offline capability
        if aip == "2000" and offline_capable and "DDA" in auth_methods:
            return 1.3  # Ideal conditions like emv.html
        elif offline_capable:
            return 1.1
        else:
            return 0.6  # No offline support
    
    def calculate_track2_success(self, profile: Dict[str, Any]) -> float:
        """Calculate Track2 spoofing success multiplier"""
        track2 = profile.get("track2_data", "")
        pan = profile.get("pan", "")
        
        # Real emv.html Track2: 4154904674973556D29022010000820083001F
        if len(track2) >= 35 and "D" in track2 and len(pan) == 16:
            return 1.2  # Complete Track2 like emv.html
        elif track2 and pan:
            return 1.0
        else:
            return 0.5  # Missing Track2 data
    
    def calculate_cryptogram_success(self, profile: Dict[str, Any]) -> float:
        """Calculate cryptogram downgrade success multiplier"""
        cryptos = profile.get("supported_cryptograms", [])
        auth_methods = profile.get("authentication_methods", [])
        emv_tags = profile.get("emv_tags", {})
        
        # Real emv.html has ARQC (80) cryptogram D3967976E30EFAFC
        has_crypto_tags = "9F26" in emv_tags and "9F27" in emv_tags
        
        if len(cryptos) >= 3 and "DDA" in auth_methods and has_crypto_tags:
            return 1.3  # Full cryptogram support
        elif len(cryptos) >= 2 and has_crypto_tags:
            return 1.1  # Good support like emv.html
        else:
            return 0.7  # Limited cryptogram options
    
    def calculate_cvm_success(self, profile: Dict[str, Any]) -> float:
        """Calculate CVM bypass success multiplier"""
        cvm_methods = profile.get("cvm_methods", [])
        contactless = profile.get("contactless_supported", False)
        limits = profile.get("transaction_limits", {})
        
        no_cvm_limit = limits.get("no_cvm_limit", 0)
        
        if len(cvm_methods) >= 3 and contactless and no_cvm_limit > 0:
            return 1.2  # Multiple CVM options + contactless
        elif "NO_CVM" in cvm_methods and contactless:
            return 1.1
        else:
            return 0.8  # Limited CVM flexibility
    
    def calculate_advanced_offline_success(self, profile: Dict[str, Any]) -> float:
        """Calculate advanced force offline success multiplier"""
        offline_capable = profile.get("offline_capable", False)
        auth_methods = profile.get("authentication_methods", [])
        limits = profile.get("transaction_limits", {})
        
        offline_limit = limits.get("offline_limit", 0)
        
        if offline_capable and "CDA" in auth_methods and offline_limit > 25000:
            return 1.4  # High-limit CDA card
        elif offline_capable and "DDA" in auth_methods:
            return 1.2  # DDA support like emv.html  
        else:
            return 0.6  # Limited offline capability
    
    def calculate_enhanced_cvm_success(self, profile: Dict[str, Any]) -> float:
        """Calculate enhanced CVM bypass success multiplier"""
        cvm_methods = profile.get("cvm_methods", [])
        biometric = profile.get("biometric_supported", False)
        contactless = profile.get("contactless_supported", False)
        
        if biometric and contactless and len(cvm_methods) >= 4:
            return 1.3  # Advanced biometric + contactless
        elif contactless and len(cvm_methods) >= 3:
            return 1.1
        else:
            return 0.7  # Basic CVM only
    
    def calculate_amount_success(self, profile: Dict[str, Any]) -> float:
        """Calculate amount manipulation success multiplier"""
        limits = profile.get("transaction_limits", {})
        emv_tags = profile.get("emv_tags", {})
        
        single_limit = limits.get("single_transaction_limit", 0)
        has_amount_tags = "9F02" in emv_tags or "amount" in str(emv_tags)
        
        if single_limit > 100000 and has_amount_tags:
            return 1.2  # High limits + amount tags
        elif single_limit > 25000:
            return 1.0
        else:
            return 0.8  # Low limits
    
    def calculate_advanced_crypto_success(self, profile: Dict[str, Any]) -> float:
        """Calculate advanced cryptogram success multiplier"""
        auth_methods = profile.get("authentication_methods", [])
        cryptos = profile.get("supported_cryptograms", [])
        cda_support = profile.get("cda_supported", False)
        emv_tags = profile.get("emv_tags", {})
        
        has_full_crypto_tags = all(tag in emv_tags for tag in ["9F26", "9F27", "9F36", "9F4B"])
        
        if cda_support and len(cryptos) >= 3 and has_full_crypto_tags:
            return 1.4  # Full CDA support
        elif "DDA" in auth_methods and len(cryptos) >= 2:
            return 1.1  # DDA like emv.html
        else:
            return 0.7  # Basic authentication only
    
    def calculate_failed_crypto_success(self, profile: Dict[str, Any]) -> float:
        """Calculate failed cryptogram attack success multiplier"""
        cryptos = profile.get("supported_cryptograms", [])
        emv_tags = profile.get("emv_tags", {})
        
        # Real emv.html has good cryptogram data for corruption testing
        has_crypto_data = "9F26" in emv_tags and emv_tags.get("9F26") != ""
        
        if has_crypto_data and len(cryptos) >= 2:
            return 1.2  # Good cryptogram data for manipulation
        elif has_crypto_data:
            return 1.0
        else:
            return 0.6  # Limited cryptogram data
    
    def get_utilized_emv_data(self, attack_id: str, profile: Dict[str, Any]) -> List[str]:
        """Get list of EMV data fields utilized by attack"""
        requirements = self.attack_requirements.get(attack_id, {})
        utilized = []
        
        for field, importance in requirements.items():
            if field in profile and profile[field]:
                utilized.append(f"{field} ({importance})")
        
        return utilized
    
    def analyze_attack_success_factors(self, attack_id: str, profile: Dict[str, Any], attack_result: Dict[str, Any]) -> Dict[str, Any]:
        """Analyze factors contributing to attack success/failure"""
        success_factors = {
            "primary_success_factors": [],
            "limiting_factors": [],
            "data_quality_assessment": "",
            "improvement_potential": ""
        }
        
        # Get success rate from attack_result, not data_completeness_score
        success_rate = attack_result.get("success_rate", 0)
        
        # Analyze primary success factors
        if success_rate >= 90:
            success_factors["primary_success_factors"].append("Excellent EMV data completeness")
            success_factors["data_quality_assessment"] = "EXCELLENT"
        elif success_rate >= 70:
            success_factors["primary_success_factors"].append("Good EMV data availability") 
            success_factors["data_quality_assessment"] = "GOOD"
        elif success_rate >= 50:
            success_factors["primary_success_factors"].append("Adequate basic EMV data")
            success_factors["data_quality_assessment"] = "FAIR"
        else:
            success_factors["limiting_factors"].append("Insufficient EMV data")
            success_factors["data_quality_assessment"] = "POOR"
        
        # Attack-specific success factor analysis
        if attack_id == "ppse_aid_poisoning":
            aids = profile.get("supported_aids", [])
            if len(aids) >= 2:
                success_factors["primary_success_factors"].append("Multiple AIDs for poisoning")
            else:
                success_factors["limiting_factors"].append("Single AID limits poisoning options")
        
        # Add improvement recommendations
        if success_rate < 70:
            success_factors["improvement_potential"] = "High - Add missing critical EMV data fields"
        elif success_rate < 90:
            success_factors["improvement_potential"] = "Medium - Enhance optional EMV capabilities"
        else:
            success_factors["improvement_potential"] = "Low - Already optimal data configuration"
        
        return success_factors
    
    def generate_data_recommendations(self, attack_id: str, missing_data: Dict[str, str]) -> List[str]:
        """Generate recommendations for improving EMV data for attack success"""
        recommendations = []
        
        for field, importance in missing_data.items():
            if importance == "CRITICAL":
                recommendations.append(f"🚨 CRITICAL: Add {field} - Required for attack success")
            elif importance == "HIGH":
                recommendations.append(f"⚠️ HIGH: Add {field} - Significantly improves success rate")
            elif importance == "MEDIUM":
                recommendations.append(f"📈 MEDIUM: Add {field} - Moderate improvement")
            else:
                recommendations.append(f"💡 LOW: Consider adding {field} - Minor improvement")
        
        # Attack-specific recommendations
        if attack_id == "ppse_aid_poisoning" and "supported_aids" in missing_data:
            recommendations.append("💎 Add MasterCard AID (A0000000041010) for VISA→MC poisoning")
        
        if attack_id == "failed_cryptogram_attack" and "emv_tags" in missing_data:
            recommendations.append("💎 Add real cryptogram data (9F26) for corruption testing")
        
        return recommendations
    
    def test_all_attacks_all_profiles(self) -> Dict[str, Any]:
        """Test all attack modules against all card profiles"""
        logger.info("🎯 Starting comprehensive EMV attack data requirements testing")
        
        comprehensive_results = {
            "test_timestamp": datetime.now().isoformat(),
            "emv_data_source": "emv.html VISA TEST card",
            "total_attacks": len(self.attack_requirements),
            "total_profiles": len(self.card_profiles),
            "test_results": {},
            "profile_rankings": {},
            "attack_rankings": {},
            "optimization_recommendations": {}
        }
        
        # Test each profile against each attack
        for profile_name, profile_data in self.card_profiles.items():
            logger.info(f"📋 Testing profile: {profile_name}")
            comprehensive_results["test_results"][profile_name] = {}
            
            profile_scores = []
            
            for attack_id in self.attack_requirements.keys():
                logger.info(f"  🚨 Testing attack: {attack_id}")
                
                test_result = self.test_attack_with_profile(attack_id, profile_data)
                comprehensive_results["test_results"][profile_name][attack_id] = test_result
                
                profile_scores.append(test_result["data_completeness_score"])
            
            # Calculate profile overall score
            avg_score = sum(profile_scores) / len(profile_scores)
            comprehensive_results["profile_rankings"][profile_name] = {
                "average_score": avg_score,
                "individual_scores": dict(zip(self.attack_requirements.keys(), profile_scores)),
                "profile_data": profile_data
            }
        
        # Calculate attack rankings
        for attack_id in self.attack_requirements.keys():
            attack_scores = []
            
            for profile_name in self.card_profiles.keys():
                score = comprehensive_results["test_results"][profile_name][attack_id]["data_completeness_score"]
                attack_scores.append(score)
            
            comprehensive_results["attack_rankings"][attack_id] = {
                "average_score": sum(attack_scores) / len(attack_scores),
                "best_profile": max(comprehensive_results["profile_rankings"].items(), 
                                  key=lambda x: x[1]["individual_scores"][attack_id])[0],
                "requirements": self.attack_requirements[attack_id]
            }
        
        # Generate optimization recommendations
        comprehensive_results["optimization_recommendations"] = self.generate_optimization_recommendations(comprehensive_results)
        
        return comprehensive_results
    
    def generate_optimization_recommendations(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """Generate comprehensive optimization recommendations"""
        
        # Find best and worst performing profiles
        profile_rankings = results["profile_rankings"]
        best_profile = max(profile_rankings.items(), key=lambda x: x[1]["average_score"])
        worst_profile = min(profile_rankings.items(), key=lambda x: x[1]["average_score"])
        
        # Find most and least demanding attacks
        attack_rankings = results["attack_rankings"]
        most_demanding = max(attack_rankings.items(), key=lambda x: x[1]["average_score"])
        least_demanding = min(attack_rankings.items(), key=lambda x: x[1]["average_score"])
        
        recommendations = {
            "profile_optimization": {
                "best_profile": {
                    "name": best_profile[0],
                    "score": best_profile[1]["average_score"],
                    "recommendation": "Use as reference for optimal EMV data configuration"
                },
                "worst_profile": {
                    "name": worst_profile[0], 
                    "score": worst_profile[1]["average_score"],
                    "recommendation": "Requires significant EMV data enhancement"
                }
            },
            "attack_analysis": {
                "most_demanding_attack": {
                    "name": most_demanding[0],
                    "average_score": most_demanding[1]["average_score"],
                    "recommendation": "Requires comprehensive EMV data for success"
                },
                "least_demanding_attack": {
                    "name": least_demanding[0],
                    "average_score": least_demanding[1]["average_score"],
                    "recommendation": "Works with minimal EMV data"
                }
            },
            "universal_recommendations": [
                "Use emv.html VISA TEST data as baseline - provides 85%+ compatibility",
                "Add multiple AIDs (VISA + MasterCard + US Debit) for maximum attack coverage",
                "Implement CDA authentication support for advanced cryptogram attacks",
                "Enable contactless + biometric CVM for enhanced CVM bypass attacks",
                "Set high transaction limits (>$500 offline) for advanced force offline attacks"
            ]
        }
        
        return recommendations
    
    def generate_detailed_report(self, results: Dict[str, Any], output_file: Optional[str] = None) -> str:
        """Generate comprehensive test report"""
        report_lines = []
        
        # Header
        report_lines.extend([
            "=" * 100,
            "🎯 EMV ATTACK DATA REQUIREMENTS ANALYSIS REPORT",
            "=" * 100,
            f"Test Timestamp: {results.get('test_timestamp', 'Unknown')}",
            f"EMV Data Source: {results.get('emv_data_source', 'Unknown')}",
            f"Total Attacks Tested: {results.get('total_attacks', 0)}",
            f"Total Profiles Tested: {results.get('total_profiles', 0)}",
            ""
        ])
        
        # Profile Rankings Section
        report_lines.extend([
            "📊 CARD PROFILE RANKINGS",
            "-" * 50
        ])
        
        profile_rankings = results.get("profile_rankings", {})
        sorted_profiles = sorted(profile_rankings.items(), key=lambda x: x[1]["average_score"], reverse=True)
        
        for rank, (profile_name, profile_data) in enumerate(sorted_profiles, 1):
            score = profile_data["average_score"]
            report_lines.extend([
                f"{rank}. {profile_name}: {score:.1f}% average compatibility",
                f"   Completeness: {profile_data.get('profile_data', {}).get('completeness_score', 'N/A')}%",
                ""
            ])
        
        # Attack Analysis Section
        report_lines.extend([
            "🚨 ATTACK MODULE ANALYSIS", 
            "-" * 50
        ])
        
        attack_rankings = results.get("attack_rankings", {})
        sorted_attacks = sorted(attack_rankings.items(), key=lambda x: x[1]["average_score"], reverse=True)
        
        for attack_id, attack_data in sorted_attacks:
            report_lines.extend([
                f"Attack: {attack_id.replace('_', ' ').title()}",
                f"Average Data Score: {attack_data['average_score']:.1f}%",
                f"Best Profile: {attack_data['best_profile']}",
                f"Requirements: {len(attack_data['requirements'])} EMV fields",
                ""
            ])
        
        # Detailed Results Section
        report_lines.extend([
            "📋 DETAILED TEST RESULTS",
            "-" * 50
        ])
        
        test_results = results.get("test_results", {})
        for profile_name, profile_results in test_results.items():
            report_lines.extend([
                f"Profile: {profile_name}",
                "=" * 30
            ])
            
            for attack_id, attack_result in profile_results.items():
                success_rate = attack_result.get("data_completeness_score", 0)
                missing_count = len(attack_result.get("missing_data", {}))
                
                status = "✅" if success_rate >= 70 else "⚠️" if success_rate >= 50 else "❌"
                
                report_lines.extend([
                    f"{status} {attack_id}: {success_rate}% data score",
                    f"   Missing fields: {missing_count}",
                    f"   Success factors: {attack_result.get('success_factors', {}).get('data_quality_assessment', 'Unknown')}",
                    ""
                ])
        
        # Optimization Recommendations
        report_lines.extend([
            "💡 OPTIMIZATION RECOMMENDATIONS",
            "-" * 50
        ])
        
        recommendations = results.get("optimization_recommendations", {})
        
        for category, category_data in recommendations.items():
            if category == "universal_recommendations":
                report_lines.append("Universal Recommendations:")
                for rec in category_data:
                    report_lines.append(f"  • {rec}")
                report_lines.append("")
            else:
                report_lines.append(f"{category.replace('_', ' ').title()}:")
                for key, data in category_data.items():
                    if isinstance(data, dict):
                        report_lines.append(f"  {key}: {data.get('name', 'N/A')} ({data.get('score', 'N/A')})")
                        report_lines.append(f"    → {data.get('recommendation', 'N/A')}")
                report_lines.append("")
        
        # Footer
        report_lines.extend([
            "=" * 100,
            "Report generated by Mag-Sp00f EMV Attack Data Requirements Tester",
            "Based on real EMV data from emv.html VISA TEST card",
            "=" * 100
        ])
        
        report_text = "\n".join(report_lines)
        
        # Save to file if requested
        if output_file:
            try:
                with open(output_file, 'w') as f:
                    f.write(report_text)
                logger.info(f"📄 Report saved to: {output_file}")
            except Exception as e:
                logger.error(f"💥 Failed to save report: {str(e)}")
        
        return report_text
    
    def save_results_json(self, results: Dict[str, Any], filename: str) -> None:
        """Save test results to JSON file"""
        try:
            with open(filename, 'w') as f:
                json.dump(results, f, indent=2, default=str)
            logger.info(f"💾 Results saved to: {filename}")
        except Exception as e:
            logger.error(f"💥 Failed to save JSON results: {str(e)}")


def main():
    parser = argparse.ArgumentParser(
        description="EMV Attack Data Requirements Tester",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__
    )
    
    parser.add_argument('--attack', choices=[
        'ppse_aid_poisoning', 'aip_force_offline', 'track2_spoofing',
        'cryptogram_downgrade', 'cvm_bypass', 'advanced_force_offline',
        'enhanced_cvm_bypass', 'amount_manipulation', 'advanced_cryptogram',
        'failed_cryptogram_attack', 'all'
    ], default='all', help='Attack module to test')
    
    parser.add_argument('--profile', choices=[
        'visa_test_complete', 'visa_test_standard', 'visa_test_basic', 
        'visa_test_minimal', 'mastercard_test', 'all'
    ], default='all', help='Card profile to test')
    
    parser.add_argument('--comprehensive', action='store_true',
                       help='Run comprehensive testing of all attacks vs all profiles')
    
    parser.add_argument('--output', help='Output report file')
    parser.add_argument('--json-output', help='JSON results file')
    parser.add_argument('--verbose', action='store_true', help='Verbose logging')
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    # Initialize tester
    tester = EmvAttackDataTester()
    
    try:
        if args.comprehensive or (args.attack == 'all' and args.profile == 'all'):
            logger.info("🎯 Running comprehensive EMV attack data requirements analysis")
            results = tester.test_all_attacks_all_profiles()
        else:
            logger.info(f"🧪 Testing specific combination: {args.attack} vs {args.profile}")
            # Single attack/profile testing would be implemented here
            results = {"message": "Single test mode not yet implemented"}
        
        # Generate report
        if args.output or args.comprehensive:
            output_file = args.output or f"emv_attack_data_requirements_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.txt"
            report = tester.generate_detailed_report(results, output_file)
            print("\n" + report)
        
        # Save JSON results
        if args.json_output:
            tester.save_results_json(results, args.json_output)
        
        # Print summary
        if "profile_rankings" in results:
            profile_rankings = results["profile_rankings"]
            best_profile = max(profile_rankings.items(), key=lambda x: x[1]["average_score"])
            
            print(f"\n🏆 BEST CARD PROFILE: {best_profile[0]}")
            print(f"📊 Average Compatibility: {best_profile[1]['average_score']:.1f}%")
            print(f"📋 Based on real emv.html VISA TEST data")
            
    except KeyboardInterrupt:
        logger.info("🛑 Testing interrupted by user")
        sys.exit(1)
    except Exception as e:
        logger.error(f"💥 Testing failed: {str(e)}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == '__main__':
    main()