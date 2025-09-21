#!/usr/bin/env python3
"""
naming_auditor.py
Validates naming conventions across the codebase for consistency and best practices.
To be used by the agent for code quality enforcement only. Never included in shipped app.
"""

import os
import re
from typing import List, Dict, Optional
from dataclasses import dataclass


@dataclass
class NamingIssue:
    file_path: str
    line_number: int
    name: str
    expected_pattern: str
    actual_pattern: str
    suggestion: str


class NamingAuditor:
    KOTLIN_CLASS_PATTERN = r'^[A-Z][a-zA-Z0-9]*$'
    KOTLIN_FUNCTION_PATTERN = r'^[a-z][a-zA-Z0-9]*$'
    KOTLIN_VARIABLE_PATTERN = r'^[a-z][a-zA-Z0-9]*$'
    KOTLIN_CONST_PATTERN = r'^[A-Z][A-Z0-9_]*$'
    
    PYTHON_FUNCTION_PATTERN = r'^[a-z_][a-z0-9_]*$'
    PYTHON_VARIABLE_PATTERN = r'^[a-z_][a-z0-9_]*$'
    PYTHON_CONST_PATTERN = r'^[A-Z][A-Z0-9_]*$'
    
    def __init__(self, workspace_root: str):
        self.workspace_root = workspace_root
        self.issues: List[NamingIssue] = []

    def check_kotlin_file(self, filepath: str) -> List[NamingIssue]:
        """Check naming conventions in Kotlin files."""
        issues = []
        
        with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
            lines = content.split("\n")
        
        # Check class names
        for match in re.finditer(r'\bclass\s+(\w+)', content):
            name = match.group(1)
            if not re.match(self.KOTLIN_CLASS_PATTERN, name):
                line_no = len(content[:match.start()].split("\n"))
                issues.append(NamingIssue(
                    file_path=filepath,
                    line_number=line_no,
                    name=name,
                    expected_pattern="PascalCase",
                    actual_pattern="unknown",
                    suggestion=f"Rename class to follow PascalCase (e.g., {name.title()})"
                ))
        
        # Check function names
        for match in re.finditer(r'\bfun\s+(\w+)', content):
            name = match.group(1)
            if not re.match(self.KOTLIN_FUNCTION_PATTERN, name):
                line_no = len(content[:match.start()].split("\n"))
                issues.append(NamingIssue(
                    file_path=filepath,
                    line_number=line_no,
                    name=name,
                    expected_pattern="camelCase",
                    actual_pattern="unknown",
                    suggestion=f"Rename function to follow camelCase"
                ))
        
        # Check variable names
        for match in re.finditer(r'\bvar\s+(\w+)|\bval\s+(\w+)', content):
            name = match.group(1) or match.group(2)
            if name.isupper():  # Likely a constant
                if not re.match(self.KOTLIN_CONST_PATTERN, name):
                    line_no = len(content[:match.start()].split("\n"))
                    issues.append(NamingIssue(
                        file_path=filepath,
                        line_number=line_no,
                        name=name,
                        expected_pattern="UPPER_SNAKE_CASE",
                        actual_pattern="unknown",
                        suggestion=f"Rename constant to follow UPPER_SNAKE_CASE"
                    ))
            else:
                if not re.match(self.KOTLIN_VARIABLE_PATTERN, name):
                    line_no = len(content[:match.start()].split("\n"))
                    issues.append(NamingIssue(
                        file_path=filepath,
                        line_number=line_no,
                        name=name,
                        expected_pattern="camelCase",
                        actual_pattern="unknown",
                        suggestion=f"Rename variable to follow camelCase"
                    ))
        
        return issues

    def check_python_file(self, filepath: str) -> List[NamingIssue]:
        """Check naming conventions in Python files."""
        issues = []
        
        with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
            lines = content.split("\n")
        
        # Check function names
        for match in re.finditer(r'\bdef\s+(\w+)', content):
            name = match.group(1)
            if not re.match(self.PYTHON_FUNCTION_PATTERN, name):
                line_no = len(content[:match.start()].split("\n"))
                issues.append(NamingIssue(
                    file_path=filepath,
                    line_number=line_no,
                    name=name,
                    expected_pattern="snake_case",
                    actual_pattern="unknown",
                    suggestion=f"Rename function to follow snake_case"
                ))
        
        # Check variable assignments
        for match in re.finditer(r'^(\w+)\s*=', content, re.MULTILINE):
            name = match.group(1)
            if name.isupper():  # Likely a constant
                if not re.match(self.PYTHON_CONST_PATTERN, name):
                    line_no = len(content[:match.start()].split("\n"))
                    issues.append(NamingIssue(
                        file_path=filepath,
                        line_number=line_no,
                        name=name,
                        expected_pattern="UPPER_SNAKE_CASE",
                        actual_pattern="unknown",
                        suggestion=f"Rename constant to follow UPPER_SNAKE_CASE"
                    ))
            else:
                if not re.match(self.PYTHON_VARIABLE_PATTERN, name):
                    line_no = len(content[:match.start()].split("\n"))
                    issues.append(NamingIssue(
                        file_path=filepath,
                        line_number=line_no,
                        name=name,
                        expected_pattern="snake_case",
                        actual_pattern="unknown",
                        suggestion=f"Rename variable to follow snake_case"
                    ))
        
        return issues

    def audit_directory(self, directory: Optional[str] = None) -> List[NamingIssue]:
        """Recursively audit all relevant files in directory."""
        if directory is None:
            directory = self.workspace_root
        
        for root, _, files in os.walk(directory):
            for file in files:
                path = os.path.join(root, file)
                if file.endswith('.kt'):
                    self.issues.extend(self.check_kotlin_file(path))
                elif file.endswith('.py'):
                    self.issues.extend(self.check_python_file(path))
        
        return self.issues

    def generate_report(self) -> str:
        """Generate a markdown report of all naming issues found."""
        if not self.issues:
            return "# Naming Convention Audit Report\n\nNo issues found. ✅"
        
        report = [
            "# Naming Convention Audit Report",
            f"\nTotal issues found: {len(self.issues)}\n"
        ]
        
        # Group by file
        issues_by_file: Dict[str, List[NamingIssue]] = {}
        for issue in self.issues:
            if issue.file_path not in issues_by_file:
                issues_by_file[issue.file_path] = []
            issues_by_file[issue.file_path].append(issue)
        
        # Generate report
        for file_path, file_issues in issues_by_file.items():
            report.append(f"\n## {os.path.relpath(file_path, self.workspace_root)}")
            for issue in file_issues:
                report.append(
                    f"\n- Line {issue.line_number}: `{issue.name}`"
                    f"\n  - Expected: {issue.expected_pattern}"
                    f"\n  - Suggestion: {issue.suggestion}"
                )
        
        return "\n".join(report)


def main():
    workspace_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    
    print("Starting naming convention audit...")
    
    auditor = NamingAuditor(workspace_root)
    auditor.audit_directory()
    
    report = auditor.generate_report()
    
    # Save report
    report_path = os.path.join(workspace_root, "docs", "naming_audit.md")
    os.makedirs(os.path.dirname(report_path), exist_ok=True)
    with open(report_path, "w") as f:
        f.write(report)
    
    print(f"\nAudit complete. Report saved to {report_path}")
    if auditor.issues:
        print(f"\nFound {len(auditor.issues)} naming convention issue(s)!")
        return 1
    return 0


if __name__ == "__main__":
    exit(main())