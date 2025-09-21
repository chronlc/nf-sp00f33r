#!/usr/bin/env python3
"""
audit_codebase.py
Scans Android codebase for patching, placeholder code, duplicate logic, legacy/commented code, naming/style violations.
To be used by the agent for quality enforcement only. Never included in shipped app.
"""

import os
import re
from typing import List, Dict, Optional
from dataclasses import dataclass
from datetime import datetime


@dataclass
class CodeIssue:
    file_path: str
    line_number: int
    issue_type: str
    description: str
    severity: str


class CodebaseAuditor:
    PLACEHOLDER_PATTERNS = [
        r'TODO',
        r'FIXME',
        r'pass\s*$',
        r'NotImplementedError',
        r'placeholder',
        r'stub',
        r'dummy'
    ]
    
    LEGACY_PATTERNS = [
        r'/\*.*?old.*?\*/\s*$',  # Multi-line comment blocks with "old" marker
        r'//\s*(?:old|legacy|previous|deprecated)\b',  # Single line legacy markers
        r'@Deprecated',  # Java/Kotlin deprecation
        r'/\*.*?(?:previous implementation|old version|legacy code).*?\*/\s*$',  # Descriptive legacy markers
        r'//\s*(?:previous implementation|old version|legacy code)'  # Single line descriptive markers
    ]

    def __init__(self, workspace_root: str):
        self.workspace_root = workspace_root
        self.issues: List[CodeIssue] = []

    def scan_file(self, filepath: str) -> List[CodeIssue]:
        """Scan a single file for code quality issues."""
        issues = []
        
        with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
            lines = f.readlines()

        # Check for placeholders and stubs
        for idx, line in enumerate(lines, 1):
            for pattern in self.PLACEHOLDER_PATTERNS:
                if re.search(pattern, line, re.IGNORECASE):
                    issues.append(CodeIssue(
                        file_path=filepath,
                        line_number=idx,
                        issue_type="PLACEHOLDER",
                        description=f"Found placeholder/stub code: {line.strip()}",
                        severity="HIGH"
                    ))

        # Check for legacy/commented code
        comment_block = False
        comment_start = 0
        comment_content = []
        
        for idx, line in enumerate(lines, 1):
            # Check single-line legacy markers
            for pattern in self.LEGACY_PATTERNS:
                if re.search(pattern, line, re.IGNORECASE):
                    issues.append(CodeIssue(
                        file_path=filepath,
                        line_number=idx,
                        issue_type="LEGACY_CODE",
                        description=f"Legacy code marker found: {line.strip()}",
                        severity="MEDIUM"
                    ))
            
            # Track multi-line comments
            if '/*' in line:
                comment_block = True
                comment_start = idx
                comment_content = [line]
            elif comment_block:
                comment_content.append(line)
                if '*/' in line:
                    comment_block = False
                    comment_text = "".join(comment_content)
                    
                    # Check for legacy markers in multi-line comments
                    for pattern in self.LEGACY_PATTERNS:
                        if re.search(pattern, comment_text, re.IGNORECASE):
                            issues.append(CodeIssue(
                                file_path=filepath,
                                line_number=comment_start,
                                issue_type="LEGACY_CODE",
                                description=f"Legacy code block found ({idx-comment_start+1} lines)",
                                severity="MEDIUM"
                            ))
                            break

        # Check for potential duplicates, excluding data class equals/hashCode
        content = "".join(lines)
        
        # First find all data classes
        data_classes = set(re.findall(r'data class\s+(\w+)', content))
        
        # Then check for duplicates outside data class implementations
        functions = re.finditer(r'(public|private|protected)?\s*(fun|void|class)\s+(\w+)', content)
        seen_names = {}
        for match in functions:
            name = match.group(3)
            if name in ('equals', 'hashCode') and any(f'class {c}' in content for c in data_classes):
                continue  # Skip equals/hashCode in data classes
            if name in seen_names:
                issues.append(CodeIssue(
                    file_path=filepath,
                    line_number=len("".join(lines[:match.start()]).split("\n")),
                    issue_type="DUPLICATE",
                    description=f"Potential duplicate of {name}",
                    severity="HIGH"
                ))
            else:
                seen_names[name] = True

        return issues

    def audit_directory(self, directory: Optional[str] = None) -> List[CodeIssue]:
        """Recursively audit all relevant files in directory."""
        if directory is None:
            directory = self.workspace_root

        for root, _, files in os.walk(directory):
            for file in files:
                if file.endswith(('.java', '.kt', '.xml')):
                    path = os.path.join(root, file)
                    self.issues.extend(self.scan_file(path))

        return self.issues

    def generate_report(self) -> str:
        """Generate a markdown report of all issues found."""
        if not self.issues:
            return "# Code Audit Report\n\nNo issues found. ✅"

        report = [
            "# Code Audit Report",
            f"\nGenerated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
            "\n## Summary",
            f"- Total issues found: {len(self.issues)}",
            f"- High severity: {len([i for i in self.issues if i.severity == 'HIGH'])}",
            f"- Medium severity: {len([i for i in self.issues if i.severity == 'MEDIUM'])}",
            "\n## Detailed Findings\n"
        ]

        # Group by file
        issues_by_file: Dict[str, List[CodeIssue]] = {}
        for issue in self.issues:
            if issue.file_path not in issues_by_file:
                issues_by_file[issue.file_path] = []
            issues_by_file[issue.file_path].append(issue)

        # Generate detailed report
        for file_path, file_issues in issues_by_file.items():
            report.append(f"### {os.path.relpath(file_path, self.workspace_root)}")
            for issue in file_issues:
                report.append(
                    f"- [{issue.severity}] Line {issue.line_number}: {issue.description}"
                )
            report.append("")

        return "\n".join(report)


def main():
    workspace_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    android_app_dir = os.path.join(workspace_root, "android-app")
    
    print(f"Starting codebase audit in {android_app_dir}...")
    
    auditor = CodebaseAuditor(workspace_root)
    auditor.audit_directory(android_app_dir)
    
    report = auditor.generate_report()
    
    # Save report
    report_path = os.path.join(workspace_root, "docs", "audit_report.md")
    os.makedirs(os.path.dirname(report_path), exist_ok=True)
    with open(report_path, "w") as f:
        f.write(report)
    
    print(f"\nAudit complete. Report saved to {report_path}")
    if auditor.issues:
        print(f"\nFound {len(auditor.issues)} issue(s) that need attention!")
        return 1
    return 0


if __name__ == "__main__":
    exit(main())