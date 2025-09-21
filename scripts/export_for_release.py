#!/usr/bin/env python3
"""
export_for_release.py
Prepares the project for release by removing development scripts, cleaning up resources,
validating the codebase, and generating a release checklist.
To be used by the agent for release preparation only. Never included in shipped app.

Features:
- Removes all Python scripts and development tools
- Validates the codebase for release readiness
- Generates release documentation
- Creates a release checklist
- Performs final security checks
"""

import os
import shutil
import json
import yaml
import subprocess
from typing import List, Dict, Optional
from dataclasses import dataclass
from datetime import datetime
import logging
from pathlib import Path


@dataclass
class ValidationIssue:
    """Represents a validation issue found during release prep."""
    file_path: str
    issue_type: str
    description: str
    severity: str


class ReleasePreparation:
    def __init__(self, workspace_root: str):
        self.workspace_root = workspace_root
        self.issues: List[ValidationIssue] = []
        
        # Create logs directory
        logs_dir = os.path.join(workspace_root, "logs")
        os.makedirs(logs_dir, exist_ok=True)
        
        # Setup logging
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler(os.path.join(logs_dir, "release_prep.log")),
                logging.StreamHandler()
            ]
        )
        self.logger = logging.getLogger(__name__)

    def validate_codebase(self) -> bool:
        """Run final validation on the codebase."""
        try:
            # Run audit_codebase.py
            audit_script = os.path.join(self.workspace_root, "scripts", "audit_codebase.py")
            if os.path.exists(audit_script):
                result = subprocess.run(
                    ["python3", audit_script],
                    capture_output=True,
                    text=True
                )
                if result.returncode != 0:
                    self.logger.error(f"Codebase audit failed:\n{result.stdout}\n{result.stderr}")
                    return False
            
            # Check for unauthorized file types
            for root, _, files in os.walk(self.workspace_root):
                if ".git" in root or "scripts" in root:
                    continue
                for file in files:
                    if file.endswith((".py", ".pyc", ".pyo", ".pyd")):
                        self.issues.append(ValidationIssue(
                            file_path=os.path.join(root, file),
                            issue_type="UNAUTHORIZED_FILE",
                            description="Python file found in release codebase",
                            severity="HIGH"
                        ))
            
            # Validate manifest consistency
            if not self._validate_manifest():
                return False
            
            return len(self.issues) == 0
            
        except Exception as e:
            self.logger.error(f"Error validating codebase: {e}")
            return False

    def _validate_manifest(self) -> bool:
        """Ensure project manifest matches actual codebase."""
        try:
            manifest_path = os.path.join(self.workspace_root, "project_manifest.yaml")
            if not os.path.exists(manifest_path):
                self.logger.error("project_manifest.yaml not found")
                return False
            
            with open(manifest_path, "r") as f:
                manifest = yaml.safe_load(f)
            
            # Validate android-app directory structure
            android_app = os.path.join(self.workspace_root, "android-app")
            required_dirs = ["src/main/java", "src/main/res"]
            for dir_path in required_dirs:
                full_path = os.path.join(android_app, dir_path)
                if not os.path.exists(full_path):
                    self.issues.append(ValidationIssue(
                        file_path=full_path,
                        issue_type="MISSING_DIRECTORY",
                        description=f"Required directory {dir_path} not found",
                        severity="HIGH"
                    ))
            
            return True
            
        except Exception as e:
            self.logger.error(f"Error validating manifest: {e}")
            return False

    def remove_development_files(self) -> bool:
        """Remove all development-only files and directories."""
        try:
            # Files/directories to remove
            to_remove = [
                "scripts",
                ".backups",
                "logs",
                "__pycache__",
                "*.pyc",
                "*.pyo",
                "*.pyd",
                ".pytest_cache"
            ]
            
            for pattern in to_remove:
                if "*" in pattern:
                    # Handle glob patterns
                    for root, _, files in os.walk(self.workspace_root):
                        for file in files:
                            if Path(file).match(pattern):
                                os.remove(os.path.join(root, file))
                else:
                    # Handle direct paths
                    path = os.path.join(self.workspace_root, pattern)
                    if os.path.exists(path):
                        if os.path.isdir(path):
                            shutil.rmtree(path)
                        else:
                            os.remove(path)
            
            self.logger.info("Removed development files and directories")
            return True
            
        except Exception as e:
            self.logger.error(f"Error removing development files: {e}")
            return False

    def generate_release_docs(self) -> bool:
        """Generate release documentation and checklist."""
        try:
            release_dir = os.path.join(self.workspace_root, "docs", "release")
            os.makedirs(release_dir, exist_ok=True)
            
            # Generate release notes
            notes_path = os.path.join(release_dir, "RELEASE_NOTES.md")
            with open(notes_path, "w") as f:
                f.write(self._generate_release_notes())
            
            # Generate checklist
            checklist_path = os.path.join(release_dir, "RELEASE_CHECKLIST.md")
            with open(checklist_path, "w") as f:
                f.write(self._generate_checklist())
            
            self.logger.info(f"Generated release documentation in {release_dir}")
            return True
            
        except Exception as e:
            self.logger.error(f"Error generating release docs: {e}")
            return False

    def _generate_release_notes(self) -> str:
        """Generate release notes from changelog and manifest."""
        try:
            notes = [
                "# Release Notes",
                f"\nGenerated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
                "\n## Project Overview"
            ]
            
            # Add manifest info
            manifest_path = os.path.join(self.workspace_root, "project_manifest.yaml")
            if os.path.exists(manifest_path):
                with open(manifest_path, "r") as f:
                    manifest = yaml.safe_load(f)
                notes.append("\n### Features")
                for section, items in manifest["manifest"].items():
                    notes.append(f"\n#### {section.title()}")
                    for item in items:
                        if isinstance(item, dict):
                            notes.append(f"- {item['name']}")
                            for feature in item.get('features', []):
                                notes.append(f"  - {feature}")
                        else:
                            notes.append(f"- {item}")
            
            # Add recent changelog entries
            changelog_path = os.path.join(self.workspace_root, "CHANGELOG.md")
            if os.path.exists(changelog_path):
                with open(changelog_path, "r") as f:
                    changelog = f.read()
                notes.extend([
                    "\n## Recent Changes",
                    changelog.split("## ")[1]  # Get the most recent entry
                ])
            
            return "\n".join(notes)
            
        except Exception as e:
            self.logger.error(f"Error generating release notes: {e}")
            return "Error generating release notes"

    def _generate_checklist(self) -> str:
        """Generate a release checklist."""
        checklist = [
            "# Release Checklist",
            f"\nGenerated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
            "\n## Pre-Release Validation",
            "- [ ] Codebase audit passed",
            "- [ ] No development files present",
            "- [ ] All documentation updated",
            "- [ ] Version numbers consistent",
            "- [ ] Release notes generated",
            "\n## Security Checks",
            "- [ ] No sensitive data in codebase",
            "- [ ] All debug/test modes disabled",
            "- [ ] Proper error handling verified",
            "- [ ] Security features tested",
            "\n## Compatibility",
            "- [ ] Tested on OnePlus 11 (Android 14+)",
            "- [ ] Tested on Samsung Galaxy S8+ (Android Pie)",
            "- [ ] Material2/AppCompat theme verified",
            "\n## Features",
            "- [ ] NFC/HCE functionality verified",
            "- [ ] Database operations tested",
            "- [ ] UI components validated",
            "- [ ] Navigation working correctly",
            "\n## Performance",
            "- [ ] Memory usage acceptable",
            "- [ ] Response times within limits",
            "- [ ] No memory leaks detected",
            "\n## Documentation",
            "- [ ] User documentation complete",
            "- [ ] API documentation updated",
            "- [ ] Release notes finalized",
            "- [ ] Known issues documented"
        ]
        
        # Add any validation issues found
        if self.issues:
            checklist.extend([
                "\n## Validation Issues",
                "The following issues must be resolved before release:"
            ])
            for issue in self.issues:
                checklist.append(
                    f"- [ ] {issue.severity}: {issue.description} in {issue.file_path}"
                )
        
        return "\n".join(checklist)


def main():
    workspace_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    
    print("Starting release preparation...")
    prep = ReleasePreparation(workspace_root)
    
    # Validate first
    if not prep.validate_codebase():
        print("\nValidation failed! Please fix the following issues:")
        for issue in prep.issues:
            print(f"\n{issue.severity}: {issue.description}")
            print(f"File: {issue.file_path}")
        return 1
    
    # Generate documentation
    if not prep.generate_release_docs():
        print("\nFailed to generate release documentation")
        return 1
    
    # Remove development files last
    if not prep.remove_development_files():
        print("\nFailed to remove development files")
        return 1
    
    print("\nRelease preparation complete!")
    print("\nPlease review the release checklist at:")
    print(f"docs/release/RELEASE_CHECKLIST.md")
    return 0


if __name__ == "__main__":
    exit(main())