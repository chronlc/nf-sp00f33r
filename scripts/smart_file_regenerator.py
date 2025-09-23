#!/usr/bin/env python3
"""
Smart File Regeneration Tool - mag-sp00f v31.337

DELETE → REGENERATE file tool per newrule.md specification to prevent:
- Duplicate imports/methods/functions
- File corruption loops  
- Append/patch failures
- Bad file structure

Implements intelligent file analysis, backup, and complete regeneration.
"""

import os
import sys
import shutil
import json
import re
import hashlib
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Tuple
from dataclasses import dataclass
import ast
import subprocess

@dataclass
class FileAnalysis:
    """Analysis results for a source file."""
    file_path: str
    language: str
    imports: List[str]
    classes: List[str]
    functions: List[str]
    variables: List[str]
    duplicates: List[str]
    corruption_signs: List[str]
    size_bytes: int
    line_count: int
    complexity_score: int

@dataclass
class RegenerationPlan:
    """Plan for file regeneration."""
    file_path: str
    backup_path: str
    analysis: FileAnalysis
    regeneration_needed: bool
    reason: str
    new_content: str
    confidence_score: float

class SmartFileRegenerator:
    """
    🛠️ Smart File Regeneration Tool - DELETE→REGENERATE Protocol
    
    Prevents file corruption by complete regeneration instead of appending.
    """
    
    def __init__(self, workspace_root: str):
        self.workspace_root = Path(workspace_root)
        self.backup_dir = self.workspace_root / ".regeneration_backups"
        self.log_file = self.workspace_root / "scripts" / "regeneration.log"
        self.config_file = self.workspace_root / "scripts" / "regeneration_config.json"
        
        # Create necessary directories
        self.backup_dir.mkdir(exist_ok=True)
        (self.workspace_root / "scripts").mkdir(exist_ok=True)
        
        # Load configuration
        self.config = self._load_config()
        
        # Initialize logging
        self._init_logging()
    
    def _load_config(self) -> dict:
        """Load regeneration configuration."""
        default_config = {
            "corruption_patterns": [
                r"import\s+.*\nimport\s+.*\1",  # Duplicate imports
                r"def\s+(\w+).*\ndef\s+\1",     # Duplicate functions
                r"class\s+(\w+).*\nclass\s+\1", # Duplicate classes
                r"package\s+.*\npackage\s+",    # Duplicate package declarations
                r"//\s*TODO.*\n.*//\s*TODO",   # Multiple TODOs indicating patches
            ],
            "auto_regenerate_threshold": 0.7,
            "backup_retention_days": 30,
            "max_file_size_mb": 10,
            "supported_languages": [".kt", ".java", ".py", ".xml", ".gradle"]
        }
        
        if self.config_file.exists():
            try:
                with open(self.config_file, 'r') as f:
                    config = json.load(f)
                    # Merge with defaults
                    for key, value in default_config.items():
                        if key not in config:
                            config[key] = value
                    return config
            except Exception as e:
                self._log(f"Config load error: {e}, using defaults")
        
        return default_config
    
    def _init_logging(self):
        """Initialize logging system."""
        self._log("🛠️ Smart File Regenerator v31.337 initialized")
        self._log(f"Workspace: {self.workspace_root}")
        self._log(f"Backup dir: {self.backup_dir}")
    
    def _log(self, message: str):
        """Log message with timestamp."""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        log_entry = f"[{timestamp}] {message}\n"
        
        with open(self.log_file, 'a') as f:
            f.write(log_entry)
        
        print(f"📝 {message}")
    
    def analyze_file(self, file_path: str) -> FileAnalysis:
        """Analyze file for corruption signs and structure."""
        file_path = Path(file_path)
        
        if not file_path.exists():
            raise FileNotFoundError(f"File not found: {file_path}")
        
        # Read file content
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
        except UnicodeDecodeError:
            with open(file_path, 'r', encoding='latin-1') as f:
                content = f.read()
        
        # Determine language
        language = self._detect_language(file_path)
        
        # Extract structure elements
        imports = self._extract_imports(content, language)
        classes = self._extract_classes(content, language) 
        functions = self._extract_functions(content, language)
        variables = self._extract_variables(content, language)
        
        # Detect duplicates and corruption
        duplicates = self._find_duplicates(content, imports, classes, functions)
        corruption_signs = self._detect_corruption(content)
        
        # Calculate metrics
        size_bytes = len(content.encode('utf-8'))
        line_count = len(content.splitlines())
        complexity_score = self._calculate_complexity(content, language)
        
        return FileAnalysis(
            file_path=str(file_path),
            language=language,
            imports=imports,
            classes=classes,
            functions=functions,
            variables=variables,
            duplicates=duplicates,
            corruption_signs=corruption_signs,
            size_bytes=size_bytes,
            line_count=line_count,
            complexity_score=complexity_score
        )
    
    def _detect_language(self, file_path: Path) -> str:
        """Detect programming language from file extension."""
        ext = file_path.suffix.lower()
        language_map = {
            '.kt': 'kotlin',
            '.java': 'java', 
            '.py': 'python',
            '.xml': 'xml',
            '.gradle': 'gradle'
        }
        return language_map.get(ext, 'unknown')
    
    def _extract_imports(self, content: str, language: str) -> List[str]:
        """Extract import statements."""
        imports = []
        
        if language in ['kotlin', 'java']:
            pattern = r'import\s+([a-zA-Z0-9_.]+)'
            imports = re.findall(pattern, content)
        elif language == 'python':
            pattern = r'(?:from\s+[\w.]+\s+)?import\s+([\w.,\s*]+)'
            imports = re.findall(pattern, content)
        
        return list(set(imports))  # Remove duplicates
    
    def _extract_classes(self, content: str, language: str) -> List[str]:
        """Extract class definitions."""
        classes = []
        
        if language in ['kotlin', 'java']:
            pattern = r'(?:class|interface|object)\s+(\w+)'
            classes = re.findall(pattern, content)
        elif language == 'python':
            pattern = r'class\s+(\w+)'
            classes = re.findall(pattern, content)
        
        return classes
    
    def _extract_functions(self, content: str, language: str) -> List[str]:
        """Extract function/method definitions."""
        functions = []
        
        if language == 'kotlin':
            pattern = r'fun\s+(\w+)'
            functions = re.findall(pattern, content)
        elif language == 'java':
            pattern = r'(?:public|private|protected)?\s*(?:static)?\s*\w+\s+(\w+)\s*\('
            functions = re.findall(pattern, content)
        elif language == 'python':
            pattern = r'def\s+(\w+)'
            functions = re.findall(pattern, content)
        
        return functions
    
    def _extract_variables(self, content: str, language: str) -> List[str]:
        """Extract variable declarations."""
        variables = []
        
        if language in ['kotlin', 'java']:
            pattern = r'(?:val|var|private|public)\s+(\w+)'
            variables = re.findall(pattern, content)
        elif language == 'python':
            pattern = r'^\s*(\w+)\s*='
            variables = re.findall(pattern, content, re.MULTILINE)
        
        return variables
    
    def _find_duplicates(self, content: str, imports: List[str], classes: List[str], functions: List[str]) -> List[str]:
        """Find duplicate elements in the file."""
        duplicates = []
        
        # Check for duplicate imports
        import_lines = [line.strip() for line in content.splitlines() if 'import' in line]
        if len(import_lines) != len(set(import_lines)):
            duplicates.append("duplicate_imports")
        
        # Check for duplicate classes
        if len(classes) != len(set(classes)):
            duplicates.append("duplicate_classes")
        
        # Check for duplicate functions
        if len(functions) != len(set(functions)):
            duplicates.append("duplicate_functions")
        
        return duplicates
    
    def _detect_corruption(self, content: str) -> List[str]:
        """Detect signs of file corruption."""
        corruption_signs = []
        
        for pattern in self.config["corruption_patterns"]:
            if re.search(pattern, content, re.MULTILINE | re.DOTALL):
                corruption_signs.append(f"pattern_match: {pattern[:30]}...")
        
        # Check for excessive empty lines
        empty_lines = len([line for line in content.splitlines() if not line.strip()])
        total_lines = len(content.splitlines())
        if total_lines > 0 and empty_lines / total_lines > 0.3:
            corruption_signs.append("excessive_empty_lines")
        
        # Check for malformed syntax patterns
        if '}}}}' in content or '{{{{' in content:
            corruption_signs.append("malformed_braces")
        
        if content.count('import') > 50:
            corruption_signs.append("excessive_imports")
        
        return corruption_signs
    
    def _calculate_complexity(self, content: str, language: str) -> int:
        """Calculate file complexity score."""
        complexity = 0
        
        # Basic complexity metrics
        complexity += len(content.splitlines()) // 10  # Line count factor
        complexity += content.count('{')  # Brace nesting
        complexity += content.count('if')  # Conditional complexity
        complexity += content.count('for')  # Loop complexity
        complexity += content.count('while')  # Loop complexity
        
        return min(complexity, 100)  # Cap at 100
    
    def should_regenerate(self, analysis: FileAnalysis) -> Tuple[bool, str, float]:
        """Determine if file should be regenerated."""
        score = 0.0
        reasons = []
        
        # Corruption signs
        if analysis.corruption_signs:
            score += 0.4
            reasons.append(f"corruption detected: {', '.join(analysis.corruption_signs)}")
        
        # Duplicates
        if analysis.duplicates:
            score += 0.3
            reasons.append(f"duplicates found: {', '.join(analysis.duplicates)}")
        
        # Excessive complexity
        if analysis.complexity_score > 50:
            score += 0.2
            reasons.append(f"high complexity: {analysis.complexity_score}")
        
        # Large file size
        if analysis.size_bytes > self.config["max_file_size_mb"] * 1024 * 1024:
            score += 0.1
            reasons.append("large file size")
        
        should_regen = score >= self.config["auto_regenerate_threshold"]
        reason = "; ".join(reasons) if reasons else "no issues detected"
        
        return should_regen, reason, score
    
    def create_backup(self, file_path: str) -> str:
        """Create backup of file before regeneration."""
        file_path = Path(file_path)
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        backup_name = f"{file_path.name}.backup_{timestamp}"
        backup_path = self.backup_dir / backup_name
        
        shutil.copy2(file_path, backup_path)
        self._log(f"📦 Backup created: {backup_path}")
        
        return str(backup_path)
    
    def regenerate_file(self, file_path: str, new_content: str) -> bool:
        """DELETE → REGENERATE file with new content."""
        try:
            file_path = Path(file_path)
            
            # Create backup first
            backup_path = self.create_backup(str(file_path))
            
            # DELETE the file
            if file_path.exists():
                file_path.unlink()
                self._log(f"🗑️ DELETED: {file_path}")
            
            # REGENERATE with new content
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            
            self._log(f"✅ REGENERATED: {file_path}")
            return True
            
        except Exception as e:
            self._log(f"❌ Regeneration failed: {e}")
            return False
    
    def clean_and_regenerate_content(self, analysis: FileAnalysis) -> str:
        """Generate clean content for file regeneration."""
        with open(analysis.file_path, 'r', encoding='utf-8') as f:
            original_content = f.read()
        
        # Start with original content
        clean_content = original_content
        
        # Remove duplicate imports
        if "duplicate_imports" in analysis.duplicates:
            clean_content = self._deduplicate_imports(clean_content, analysis.language)
        
        # Remove duplicate functions
        if "duplicate_functions" in analysis.duplicates:
            clean_content = self._deduplicate_functions(clean_content, analysis.language)
        
        # Clean excessive empty lines
        clean_content = re.sub(r'\n\s*\n\s*\n', '\n\n', clean_content)
        
        # Add header comment for regenerated files
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        header = f"// REGENERATED FILE - {timestamp} - Smart File Regenerator v31.337\n"
        
        if not clean_content.startswith("//") and analysis.language in ['kotlin', 'java']:
            clean_content = header + clean_content
        
        return clean_content
    
    def _deduplicate_imports(self, content: str, language: str) -> str:
        """Remove duplicate import statements."""
        lines = content.splitlines()
        seen_imports = set()
        clean_lines = []
        
        for line in lines:
            if 'import' in line and language in ['kotlin', 'java']:
                if line.strip() not in seen_imports:
                    seen_imports.add(line.strip())
                    clean_lines.append(line)
            else:
                clean_lines.append(line)
        
        return '\n'.join(clean_lines)
    
    def _deduplicate_functions(self, content: str, language: str) -> str:
        """Remove duplicate function definitions."""
        # This is a simplified implementation
        # In practice, you'd want more sophisticated AST-based deduplication
        lines = content.splitlines()
        seen_functions = set()
        clean_lines = []
        skip_lines = 0
        
        for i, line in enumerate(lines):
            if skip_lines > 0:
                skip_lines -= 1
                continue
                
            if language == 'kotlin' and 'fun ' in line:
                func_name = re.search(r'fun\s+(\w+)', line)
                if func_name and func_name.group(1) in seen_functions:
                    # Skip this function (simplified - just skip to next function)
                    skip_lines = 10  # Skip next 10 lines (rough estimate)
                    continue
                elif func_name:
                    seen_functions.add(func_name.group(1))
            
            clean_lines.append(line)
        
        return '\n'.join(clean_lines)
    
    def process_file(self, file_path: str, force: bool = False) -> RegenerationPlan:
        """Process a single file for potential regeneration."""
        self._log(f"🔍 Analyzing: {file_path}")
        
        # Analyze file
        analysis = self.analyze_file(file_path)
        
        # Determine if regeneration is needed
        should_regen, reason, confidence = self.should_regenerate(analysis)
        
        if force:
            should_regen = True
            reason = "forced regeneration"
            confidence = 1.0
        
        # Generate clean content if needed
        new_content = ""
        if should_regen:
            new_content = self.clean_and_regenerate_content(analysis)
        
        backup_path = ""
        if should_regen:
            backup_path = str(self.backup_dir / f"{Path(file_path).name}.backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}")
        
        plan = RegenerationPlan(
            file_path=file_path,
            backup_path=backup_path,
            analysis=analysis,
            regeneration_needed=should_regen,
            reason=reason,
            new_content=new_content,
            confidence_score=confidence
        )
        
        return plan
    
    def execute_regeneration_plan(self, plan: RegenerationPlan) -> bool:
        """Execute a regeneration plan."""
        if not plan.regeneration_needed:
            self._log(f"⏭️ Skipping: {plan.file_path} - {plan.reason}")
            return True
        
        self._log(f"🔄 Regenerating: {plan.file_path}")
        self._log(f"   Reason: {plan.reason}")
        self._log(f"   Confidence: {plan.confidence_score:.2f}")
        
        success = self.regenerate_file(plan.file_path, plan.new_content)
        return success
    
    def scan_project(self, pattern: str = "**/*.kt") -> List[str]:
        """Scan project for files matching pattern."""
        files = []
        for file_path in self.workspace_root.glob(pattern):
            if file_path.is_file() and file_path.suffix in self.config["supported_languages"]:
                files.append(str(file_path))
        return files
    
    def batch_regenerate(self, file_patterns: List[str], force: bool = False) -> Dict:
        """Process multiple files for regeneration."""
        results = {
            "processed": 0,
            "regenerated": 0,
            "skipped": 0,
            "errors": 0,
            "plans": []
        }
        
        # Collect all files
        all_files = []
        for pattern in file_patterns:
            all_files.extend(self.scan_project(pattern))
        
        all_files = list(set(all_files))  # Remove duplicates
        
        self._log(f"🎯 Processing {len(all_files)} files")
        
        for file_path in all_files:
            try:
                plan = self.process_file(file_path, force)
                results["plans"].append(plan)
                results["processed"] += 1
                
                if self.execute_regeneration_plan(plan):
                    if plan.regeneration_needed:
                        results["regenerated"] += 1
                    else:
                        results["skipped"] += 1
                else:
                    results["errors"] += 1
                    
            except Exception as e:
                self._log(f"❌ Error processing {file_path}: {e}")
                results["errors"] += 1
        
        # Summary
        self._log(f"📊 Batch complete:")
        self._log(f"   Processed: {results['processed']}")
        self._log(f"   Regenerated: {results['regenerated']}")
        self._log(f"   Skipped: {results['skipped']}")
        self._log(f"   Errors: {results['errors']}")
        
        return results

def main():
    """Main CLI interface."""
    if len(sys.argv) < 2:
        print("Usage: python smart_file_regenerator.py <command> [options]")
        print("Commands:")
        print("  analyze <file>     - Analyze single file")
        print("  regenerate <file>  - Regenerate single file")
        print("  batch <pattern>    - Batch process files")
        print("  scan              - Scan entire project")
        sys.exit(1)
    
    workspace_root = os.getcwd()
    regenerator = SmartFileRegenerator(workspace_root)
    
    command = sys.argv[1]
    
    if command == "analyze":
        if len(sys.argv) < 3:
            print("Usage: analyze <file_path>")
            sys.exit(1)
        
        file_path = sys.argv[2]
        analysis = regenerator.analyze_file(file_path)
        
        print(f"📊 Analysis: {analysis.file_path}")
        print(f"Language: {analysis.language}")
        print(f"Size: {analysis.size_bytes} bytes")
        print(f"Lines: {analysis.line_count}")
        print(f"Complexity: {analysis.complexity_score}")
        print(f"Imports: {len(analysis.imports)}")
        print(f"Classes: {len(analysis.classes)}")
        print(f"Functions: {len(analysis.functions)}")
        if analysis.duplicates:
            print(f"⚠️ Duplicates: {', '.join(analysis.duplicates)}")
        if analysis.corruption_signs:
            print(f"🚨 Corruption: {', '.join(analysis.corruption_signs)}")
    
    elif command == "regenerate":
        if len(sys.argv) < 3:
            print("Usage: regenerate <file_path>")
            sys.exit(1)
        
        file_path = sys.argv[2]
        force = "--force" in sys.argv
        
        plan = regenerator.process_file(file_path, force)
        regenerator.execute_regeneration_plan(plan)
    
    elif command == "batch":
        pattern = sys.argv[2] if len(sys.argv) > 2 else "**/*.kt"
        force = "--force" in sys.argv
        
        results = regenerator.batch_regenerate([pattern], force)
        print("✅ Batch processing complete!")
    
    elif command == "scan":
        regenerator.batch_regenerate(["**/*.kt", "**/*.java"], False)
    
    else:
        print(f"Unknown command: {command}")
        sys.exit(1)

if __name__ == "__main__":
    main()