#!/usr/bin/env python3

import os
import re
from collections import defaultdict
from typing import List, Dict, Set, Tuple

class CodeQualityChecker:
    def __init__(self, root_dir: str):
        self.root_dir = root_dir
        self.issues = []
        
    def check_android_manifest(self, content: str) -> List[str]:
        """Check Android manifest for common issues."""
        issues = []
        
        # Check for essential NFC permissions and features
        required_elements = {
            '<uses-permission android:name="android.permission.NFC"': 'Missing NFC permission',
            '<uses-feature android:name="android.hardware.nfc"': 'Missing NFC feature declaration',
            '<uses-feature android:name="android.hardware.nfc.hce"': 'Missing HCE feature declaration',
            'android:permission="android.permission.BIND_NFC_SERVICE"': 'Missing NFC service binding permission'
        }
        
        for element, error in required_elements.items():
            if element not in content:
                issues.append(error)
                
        return issues

    def check_kotlin_conventions(self, content: str) -> List[str]:
        """Check Kotlin code conventions."""
        issues = []
        
        # Check naming conventions
        patterns = [
            (r'var [a-z]+[A-Z]', 'Variable names should use snake_case, not camelCase'),
            (r'fun [a-z]+[A-Z]', 'Function names should use snake_case, not camelCase'),
            (r'companion\s+object\s+\w+', 'Companion object should not be named unless needed'),
            (r'const\s+val\s+[a-z]', 'Constant names should be UPPER_CASE'),
        ]
        
        for pattern, msg in patterns:
            if re.search(pattern, content):
                issues.append(msg)
        
        # Check for unused imports
        import_pattern = r'import.*\n'
        imports = re.findall(import_pattern, content)
        for imp in imports:
            symbol = imp.split('.')[-1].strip()
            if symbol not in content[content.find('class'):]:
                issues.append(f"Potentially unused import: {imp.strip()}")
        
        return issues
        
    def check_android_resources(self, content: str) -> List[str]:
        """Check Android resource files."""
        issues = []
        
        if content.startswith('<?xml'):
            # Check for hardcoded strings in layouts
            if re.search(r'android:text="[^@]', content):
                issues.append("Avoid hardcoded strings in layouts, use @string resources")
            
            # Check for missing content descriptions
            if 'android:contentDescription=' not in content and '<ImageView' in content:
                issues.append("ImageView should have content description for accessibility")
                
            # Check for proper dimension usage
            if re.search(r'android:layout_[width|height|margin|padding]="[0-9]+px"', content):
                issues.append("Use dp instead of px for dimensions")
        
        return issues
        
    def check_nfc_specific_issues(self, content: str) -> List[str]:
        """Check for NFC-specific issues."""
        issues = []
        
        nfc_checks = [
            (r'NfcAdapter\?\.', 'Use lateinit for NfcAdapter and proper initialization checks'),
            (r'activity\s*=\s*activity', 'Avoid redundant activity assignments'),
            (r'Arrays\.equals\([^)]+\)', 'Use contentEquals for ByteArray comparison'),
            (r'@RequiresPermission\(\s*Manifest\.permission\.NFC\s*\)', 'Missing NFC permission annotation'),
            (r'HCE.*Service.*:.*Service', 'HCE service should extend HostApduService'),
            (r'fun\s+process.*APDU.*:\s*ByteArray', 'APDU processing methods should handle exceptions'),
        ]
        
        for pattern, msg in nfc_checks:
            if re.search(pattern, content):
                issues.append(msg)
        
        return issues

    def check_common_antipatterns(self, content: str) -> List[str]:
        """Check for common antipatterns."""
        issues = []
        
        antipatterns = [
            (r'Thread\(', 'Avoid using raw Thread, prefer coroutines'),
            (r'while\s*\(true\)', 'Avoid infinite loops'),
            (r'\.get\(\)\s*\{', 'Avoid using .get() with scoped functions, use safe call (?.)'),
            (r'@Suppress\(".*"\)', 'Avoid suppressing warnings without comments explaining why'),
            (r'Toast\.makeText\(', 'Consider using Snackbar instead of Toast for better UX'),
            (r'Log\.[vd]', 'Avoid using Log.v or Log.d in production code'),
            (r'synchronized\s*\(', 'Prefer coroutines over synchronized blocks'),
        ]
        
        for pattern, msg in antipatterns:
            if re.search(pattern, content):
                issues.append(msg)
        
        return issues

    def check_file(self, file_path: str) -> List[str]:
        """Check a single file for issues."""
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            lines = content.splitlines()
            
        file_issues = []
        
        # Apply appropriate checkers based on file type
        if file_path.endswith('AndroidManifest.xml'):
            file_issues.extend(self.check_android_manifest(content))
            
        if file_path.endswith('.kt'):
            # Track method definitions and their line numbers
            method_defs = defaultdict(list)
            current_class = None
            
            # Find class definition
            class_match = re.search(r'class\s+(\w+)', content)
            if class_match:
                current_class = class_match.group(1)
            
            # Find all method definitions
            for i, line in enumerate(lines, 1):
                method_match = re.search(r'\s*(?:fun|private fun|protected fun|public fun)\s+(\w+)', line)
                if method_match:
                    method_name = method_match.group(1)
                    method_defs[method_name].append(i)
            
            # Check for duplicate methods
            for method, line_nums in method_defs.items():
                if len(line_nums) > 1:
                    file_issues.append(f"Duplicate method '{method}' defined at lines {', '.join(map(str, line_nums))}")
                    
            file_issues.extend(self.check_kotlin_conventions(content))
            
            # Check for proper null safety with Elvis operator
            elvis_checks = re.findall(r'(\w+)\s*\?\:', content)
            for var in elvis_checks:
                if not re.search(fr'val\s+\w+\s*=\s*{var}\s*\?:', content):
                    file_issues.append(f"Consider using val assignment with Elvis operator for '{var}'")
            
            # Check for ByteArray equals/hashCode implementations
            if 'ByteArray' in content:
                if re.search(r'override\s+fun\s+equals.*ByteArray', content):
                    if not re.search(r'Arrays\.equals', content):
                        file_issues.append("ByteArray equals() should use Arrays.equals")
                if re.search(r'override\s+fun\s+hashCode.*ByteArray', content):
                    if not re.search(r'Arrays\.hashCode', content):
                        file_issues.append("ByteArray hashCode() should use Arrays.hashCode")
                        
            # Check NFC-specific issues
            if 'nfc' in file_path.lower():
                file_issues.extend(self.check_nfc_specific_issues(content))
                
        if '/res/' in file_path and file_path.endswith('.xml'):
            file_issues.extend(self.check_android_resources(content))
            
        file_issues.extend(self.check_common_antipatterns(content))
                
        return file_issues        # Track method definitions and their line numbers
        method_defs = defaultdict(list)
        current_class = None
        
        # Check for Kotlin-specific issues
        if file_path.endswith('.kt'):
            # Find class definition
            class_match = re.search(r'class\s+(\w+)', content)
            if class_match:
                current_class = class_match.group(1)
            
            # Find all method definitions
            for i, line in enumerate(lines, 1):
                # Method definition pattern
                method_match = re.search(r'\s*(?:fun|private fun|protected fun|public fun)\s+(\w+)', line)
                if method_match:
                    method_name = method_match.group(1)
                    method_defs[method_name].append(i)
            
            # Check for duplicate methods
            for method, line_nums in method_defs.items():
                if len(line_nums) > 1:
                    file_issues.append(f"Duplicate method '{method}' defined at lines {', '.join(map(str, line_nums))}")
            
            # Check for NFC-related type safety issues
            nfc_patterns = [
                (r'as\s+NfcManager\b', 'Unsafe cast to NfcManager - use as? for safe casting'),
                (r'as\s+NfcAdapter\b', 'Unsafe cast to NfcAdapter - use as? for safe casting'),
                (r'(\w+)\?\.(defaultAdapter|enableReaderMode|disableReaderMode)', 'Potential NPE with nullable NFC types'),
                (r'activity\s*=\s*activity', 'Double initialization of activity field'),
            ]
            
            for i, line in enumerate(lines, 1):
                for pattern, msg in nfc_patterns:
                    if re.search(pattern, line):
                        file_issues.append(f"Line {i}: {msg}")
                
            # Check for proper lateinit handling
            if 'lateinit' in content:
                lateinit_vars = re.findall(r'lateinit\s+var\s+(\w+)', content)
                for var in lateinit_vars:
                    if not re.search(fr'if\s*\(!::{var}\.isInitialized\)', content):
                        file_issues.append(f"Missing initialization check for lateinit var '{var}'")
            
            # Check for proper null safety with Elvis operator
            elvis_checks = re.findall(r'(\w+)\s*\?\:', content)
            for var in elvis_checks:
                if not re.search(fr'val\s+\w+\s*=\s*{var}\s*\?:', content):
                    file_issues.append(f"Consider using val assignment with Elvis operator for '{var}'")
            
            # Check for ByteArray equals/hashCode implementations
            if 'ByteArray' in content:
                if re.search(r'override\s+fun\s+equals.*ByteArray', content):
                    if not re.search(r'Arrays\.equals', content):
                        file_issues.append(f"ByteArray equals() should use Arrays.equals")
                if re.search(r'override\s+fun\s+hashCode.*ByteArray', content):
                    if not re.search(r'Arrays\.hashCode', content):
                        file_issues.append(f"ByteArray hashCode() should use Arrays.hashCode")
            
            # Check for consistent ByteArray handling
            byte_array_checks = [
                (r'\.toByteArray\(\)', 'Consider storing ByteArray directly instead of conversion'),
                (r'Arrays\.equals\([^)]+\)', 'Consider using contentEquals for ByteArray comparison'),
                (r'ByteArray\(\d+\)', 'Consider using ByteArray constructor with initializer')
            ]
            
            for pattern, msg in byte_array_checks:
                if re.search(pattern, content):
                    file_issues.append(msg)
            
            # Check NFC-specific naming conventions
            if 'nfc' in file_path.lower():
                nfc_names = {
                    'NfcAdapter': r'nfcAdapter',
                    'NfcManager': r'nfcManager',
                    'EmulationMode': r'emulationMode',
                }
                for type_name, var_pattern in nfc_names.items():
                    if type_name in content and not re.search(var_pattern, content):
                        file_issues.append(f"Inconsistent naming for {type_name} variables")
        
        return file_issues
    
    def scan_directory(self) -> List[str]:
        """Recursively scan directory for code quality issues."""
        for root, _, files in os.walk(self.root_dir):
            for file in files:
                if file.endswith(('.kt', '.java')):
                    file_path = os.path.join(root, file)
                    issues = self.check_file(file_path)
                    if issues:
                        rel_path = os.path.relpath(file_path, self.root_dir)
                        self.issues.append(f"\nIssues in {rel_path}:")
                        self.issues.extend([f"  - {issue}" for issue in issues])
        
        return self.issues

def main():
    # Get project root directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    
    print("Running code quality checks...")
    checker = CodeQualityChecker(project_root)
    issues = checker.scan_directory()
    
    if issues:
        print("\nFound the following issues:")
        for issue in issues:
            print(issue)
        print("\nPlease fix these issues to improve code quality.")
        exit(1)
    else:
        print("\nNo issues found. Code quality checks passed!")
        exit(0)

if __name__ == '__main__':
    main()