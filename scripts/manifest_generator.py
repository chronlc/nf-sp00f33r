#!/usr/bin/env python3
"""
manifest_generator.py
Generates and updates project manifest, batches, and changelog files from project requirements.
To be used by the agent for project organization only. Never included in shipped app.
"""

import os
import re
import yaml
from typing import Dict, List, Optional
from dataclasses import dataclass
from datetime import datetime


@dataclass
class Feature:
    name: str
    description: str
    status: str = "not_started"
    batch_id: Optional[int] = None


@dataclass
class Batch:
    id: int
    name: str
    tasks: List[str]
    status: str = "not_started"


class ManifestGenerator:
    def __init__(self, workspace_root: str):
        self.workspace_root = workspace_root
        self.manifest_path = os.path.join(workspace_root, "project_manifest.yaml")
        self.batches_path = os.path.join(workspace_root, "batches.yaml")
        self.changelog_path = os.path.join(workspace_root, "CHANGELOG.md")

    def parse_readme(self, readme_path: str) -> Dict:
        """Parse README.md to extract features and requirements."""
        features = {
            "android": [],
            "setup": [],
            "testing": []
        }
        
        current_section = None
        
        with open(readme_path, "r") as f:
            content = f.read()
            
        # Extract features from sections
        sections = re.split(r'\n##+ ', content)
        for section in sections:
            if not section.strip():
                continue
                
            lines = section.split('\n')
            title = lines[0].strip()
            
            if "FEATURE" in title.upper():
                current_section = "android"
            elif "SETUP" in title.upper():
                current_section = "setup"
            elif "TEST" in title.upper():
                current_section = "testing"
            
            if current_section:
                # Extract bullet points
                features[current_section].extend(
                    line.strip('- ').strip()
                    for line in lines
                    if line.strip().startswith('- ')
                )
        
        return features

    def generate_manifest(self, features: Dict) -> None:
        """Generate project_manifest.yaml from parsed features."""
        manifest = {"manifest": features}
        
        with open(self.manifest_path, "w") as f:
            yaml.safe_dump(manifest, f, sort_keys=False, allow_unicode=True)

    def generate_batches(self, features: Dict) -> None:
        """Generate batches.yaml from features."""
        batches = []
        batch_id = 1
        
        # Setup batch
        if features.get("setup"):
            batches.append(Batch(
                id=batch_id,
                name="Project Setup",
                tasks=features["setup"][:4]  # Max 4 tasks per batch
            ))
            batch_id += 1
        
        # Android feature batches
        android_features = features.get("android", [])
        for i in range(0, len(android_features), 4):
            batches.append(Batch(
                id=batch_id,
                name=f"Android Features Batch {batch_id}",
                tasks=android_features[i:i+4]
            ))
            batch_id += 1
        
        # Testing batch
        if features.get("testing"):
            batches.append(Batch(
                id=batch_id,
                name="Testing & Validation",
                tasks=features["testing"]
            ))
        
        # Write to file
        with open(self.batches_path, "w") as f:
            yaml.safe_dump(
                {"batches": [vars(b) for b in batches]},
                f,
                sort_keys=False,
                allow_unicode=True
            )

    def update_changelog(self, message: str) -> None:
        """Add a new entry to CHANGELOG.md."""
        today = datetime.now().strftime("%Y-%m-%d")
        
        try:
            with open(self.changelog_path, "r") as f:
                content = f.read()
        except FileNotFoundError:
            content = "# Changelog\n\n"
        
        # Check if we already have an entry for today
        if f"## [{today}]" in content:
            # Add to existing entry
            content = content.replace(
                f"## [{today}]",
                f"## [{today}]\n\n{message}"
            )
        else:
            # Create new entry
            unreleased_section = "## [Unreleased]"
            if unreleased_section in content:
                content = content.replace(
                    unreleased_section,
                    f"{unreleased_section}\n\n## [{today}]\n\n{message}"
                )
            else:
                content += f"\n## [{today}]\n\n{message}\n"
        
        with open(self.changelog_path, "w") as f:
            f.write(content)

def main():
    workspace_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    readme_path = os.path.join(workspace_root, "README.md")
    
    if not os.path.exists(readme_path):
        print("ERROR: README.md not found!")
        return 1
    
    print("Generating project files from README.md...")
    
    generator = ManifestGenerator(workspace_root)
    
    # Parse and generate
    features = generator.parse_readme(readme_path)
    generator.generate_manifest(features)
    generator.generate_batches(features)
    
    # Update changelog
    generator.update_changelog(
        "- Updated project manifest and batches from README.md\n"
        "- Regenerated project structure\n"
    )
    
    print("Done! Generated/updated:")
    print(f"- {generator.manifest_path}")
    print(f"- {generator.batches_path}")
    print(f"- {generator.changelog_path}")
    
    return 0


if __name__ == "__main__":
    exit(main())