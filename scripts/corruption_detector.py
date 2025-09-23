#!/usr/bin/env python3
"""
File Corruption Detection and Prevention Tool
Detects append-mode corruption in codegen agent output and provides fixes.
"""

import os
import re
import sys
from pathlib import Path
from typing import List, Tuple, Dict

class FileCorruptionDetector:
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.corruption_patterns = [
            r'Conflicting overloads.*defined in.*defined in',
            r'Expecting a top level declaration',
            r'Function declaration must have a name',
            r'Modifier.*is not applicable to.*local function',
            r'Duplicate.*declaration.*already defined',
        ]
        
    def detect_corrupted_files(self) -> Dict[str, List[str]]:
        """Scan for files showing corruption patterns"""
        corrupted = {}
        
        # Check Kotlin files
        for kt_file in self.project_root.rglob("*.kt"):
            issues = self.analyze_file(kt_file)
            if issues:
                corrupted[str(kt_file)] = issues
                
        return corrupted
    
    def analyze_file(self, filepath: Path) -> List[str]:
        """Analyze individual file for corruption signs"""
        issues = []
        
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
                lines = content.split('\n')
                
            # Check file size anomaly
            line_count = len(lines)
            if line_count > 1000 and 'Fragment' not in filepath.name:
                issues.append(f"Suspicious file size: {line_count} lines")
            
            # Check for duplicate functions
            function_names = re.findall(r'fun\s+(\w+)\s*\(', content)
            duplicates = [name for name in set(function_names) 
                         if function_names.count(name) > 1]
            if duplicates:
                issues.append(f"Duplicate functions: {duplicates}")
                
            # Check for malformed syntax patterns
            if re.search(r'}\s*package\s+', content):
                issues.append("Malformed package declaration (append corruption)")
                
            # Check for orphaned closing braces
            open_braces = content.count('{')
            close_braces = content.count('}')
            if abs(open_braces - close_braces) > 2:
                issues.append(f"Brace mismatch: {open_braces} open, {close_braces} close")
                
        except Exception as e:
            issues.append(f"Read error: {e}")
            
        return issues
    
    def generate_fix_script(self, corrupted_files: Dict[str, List[str]]) -> str:
        """Generate shell script to fix corrupted files"""
        script_lines = [
            "#!/bin/bash",
            "# Auto-generated corruption fix script",
            "# Run this to delete corrupted files for clean regeneration",
            "",
            "echo '🔧 Fixing file corruption detected by AI codegen agent...'",
            ""
        ]
        
        for filepath, issues in corrupted_files.items():
            script_lines.extend([
                f"# Issues in {filepath}:",
                f"# {' | '.join(issues)}",
                f"echo 'Deleting corrupted file: {filepath}'",
                f"rm -f '{filepath}'",
                ""
            ])
            
        script_lines.extend([
            "echo '✅ Corrupted files removed. Use atomic write pattern to regenerate:'",
            "echo 'cat > filename.kt << \\'EOF\\''",
            "echo '[complete file content]'", 
            "echo 'EOF'",
            ""
        ])
        
        return '\n'.join(script_lines)
    
    def create_atomic_write_template(self) -> str:
        """Create template for atomic file writing"""
        return '''#!/bin/bash
# Atomic File Write Template
# Use this pattern to prevent append-mode corruption

# Example usage:
create_kotlin_file() {
    local filepath="$1"
    local content="$2"
    
    cat > "$filepath" << 'EOF'
${content}
EOF
    
    echo "✅ Created clean file: $filepath ($(wc -l < "$filepath") lines)"
}

# Verify file was created correctly
verify_file() {
    local filepath="$1"
    local expected_lines="$2"
    
    local actual_lines=$(wc -l < "$filepath")
    if [ "$actual_lines" -gt $((expected_lines * 3)) ]; then
        echo "❌ File corruption detected: $actual_lines lines (expected ~$expected_lines)"
        return 1
    else
        echo "✅ File looks clean: $actual_lines lines"
        return 0
    fi
}
'''

def main():
    if len(sys.argv) != 2:
        print("Usage: python3 corruption_detector.py <project_root>")
        sys.exit(1)
        
    project_root = sys.argv[1]
    if not os.path.exists(project_root):
        print(f"Error: Project root {project_root} does not exist")
        sys.exit(1)
        
    detector = FileCorruptionDetector(project_root)
    
    print("🔍 Scanning for file corruption...")
    corrupted = detector.detect_corrupted_files()
    
    if not corrupted:
        print("✅ No file corruption detected")
        return
        
    print(f"❌ Found corruption in {len(corrupted)} files:")
    for filepath, issues in corrupted.items():
        print(f"  {filepath}:")
        for issue in issues:
            print(f"    - {issue}")
            
    # Generate fix script
    fix_script = detector.generate_fix_script(corrupted)
    fix_script_path = os.path.join(project_root, "fix_corruption.sh")
    
    with open(fix_script_path, 'w') as f:
        f.write(fix_script)
    os.chmod(fix_script_path, 0o755)
    
    # Generate atomic write template
    template = detector.create_atomic_write_template()
    template_path = os.path.join(project_root, "atomic_write_template.sh")
    
    with open(template_path, 'w') as f:
        f.write(template)
    os.chmod(template_path, 0o755)
    
    print(f"\n🔧 Generated fix script: {fix_script_path}")
    print(f"📋 Generated template: {template_path}")
    print("\nRun the fix script, then use atomic write pattern to regenerate files.")

if __name__ == "__main__":
    main()