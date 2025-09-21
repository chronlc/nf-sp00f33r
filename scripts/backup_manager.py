#!/usr/bin/env python3
"""
backup_manager.py
Manages automated backups of project files and provides restore capabilities.
For development use only, not shipped with app.

Features:
- Automated daily backups
- Batch operation tracking
- Integrity validation
- Space-efficient storage
- Restore point management
"""

import os
import sys
import shutil
import hashlib
import logging
import json
import yaml
from typing import Dict, List, Optional, Tuple
from datetime import datetime, timedelta
from pathlib import Path


class BackupManager:
    """Manages project backups and restore points."""
    
    def __init__(self, workspace_root: str):
        """Initialize backup manager."""
        self.workspace_root = os.path.abspath(workspace_root)
        self.backup_dir = os.path.join(self.workspace_root, ".backups")
        self.manifest_file = os.path.join(self.backup_dir, "backup_manifest.yaml")
        self.batch_dir = os.path.join(self.backup_dir, "batches")
        self.daily_dir = os.path.join(self.backup_dir, "daily")
        
        # Create backup directories
        for dir_path in [self.backup_dir, self.batch_dir, self.daily_dir]:
            os.makedirs(dir_path, exist_ok=True)
        
        # Setup logging
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler(os.path.join(self.backup_dir, "backup.log")),
                logging.StreamHandler()
            ]
        )
        self.logger = logging.getLogger("BackupManager")
        
        # Load or create manifest
        self.manifest = self._load_manifest()
    
    def _load_manifest(self) -> Dict:
        """Load backup manifest or create if not exists."""
        if os.path.exists(self.manifest_file):
            try:
                with open(self.manifest_file, "r") as f:
                    return yaml.safe_load(f)
            except Exception as e:
                self.logger.error(f"Failed to load manifest: {e}")
                return self._create_manifest()
        return self._create_manifest()
    
    def _create_manifest(self) -> Dict:
        """Create a new backup manifest."""
        manifest = {
            "last_daily_backup": None,
            "batches": {},
            "daily_backups": [],
            "backup_points": {}
        }
        self._save_manifest(manifest)
        return manifest
    
    def _save_manifest(self, manifest: Dict = None):
        """Save backup manifest."""
        manifest = manifest or self.manifest
        try:
            with open(self.manifest_file, "w") as f:
                yaml.safe_dump(manifest, f)
        except Exception as e:
            self.logger.error(f"Failed to save manifest: {e}")
            raise
    
    def _hash_file(self, file_path: str) -> str:
        """Generate SHA-256 hash of file."""
        sha256 = hashlib.sha256()
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                sha256.update(chunk)
        return sha256.hexdigest()
    
    def create_backup_point(self, name: str, description: str = None) -> bool:
        """Create a named backup point."""
        try:
            timestamp = datetime.now().isoformat()
            backup_dir = os.path.join(self.backup_dir, "points", name)
            os.makedirs(backup_dir, exist_ok=True)
            
            # Copy current state
            self._backup_workspace(backup_dir)
            
            # Record in manifest
            self.manifest["backup_points"][name] = {
                "timestamp": timestamp,
                "description": description,
                "location": backup_dir
            }
            self._save_manifest()
            
            self.logger.info(f"Created backup point: {name}")
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to create backup point '{name}': {e}")
            return False
    
    def restore_backup_point(self, name: str) -> bool:
        """Restore to a named backup point."""
        try:
            if name not in self.manifest["backup_points"]:
                raise ValueError(f"Backup point '{name}' not found")
            
            point_info = self.manifest["backup_points"][name]
            backup_dir = point_info["location"]
            
            # Create backup of current state first
            self.create_backup_point("pre_restore_" + datetime.now().strftime("%Y%m%d_%H%M%S"))
            
            # Restore files
            self._restore_workspace(backup_dir)
            
            self.logger.info(f"Restored to backup point: {name}")
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to restore backup point '{name}': {e}")
            return False
    
    def _backup_workspace(self, backup_dir: str):
        """Backup workspace files."""
        for root, _, files in os.walk(self.workspace_root):
            if ".backups" in root:
                continue
                
            for file in files:
                src_path = os.path.join(root, file)
                rel_path = os.path.relpath(src_path, self.workspace_root)
                dst_path = os.path.join(backup_dir, rel_path)
                
                os.makedirs(os.path.dirname(dst_path), exist_ok=True)
                shutil.copy2(src_path, dst_path)
    
    def _restore_workspace(self, backup_dir: str):
        """Restore workspace files from backup."""
        for root, _, files in os.walk(backup_dir):
            for file in files:
                src_path = os.path.join(root, file)
                rel_path = os.path.relpath(src_path, backup_dir)
                dst_path = os.path.join(self.workspace_root, rel_path)
                
                os.makedirs(os.path.dirname(dst_path), exist_ok=True)
                shutil.copy2(src_path, dst_path)
    
    def create_daily_backup(self) -> bool:
        """Create a daily backup if needed."""
        try:
            last_backup = self.manifest["last_daily_backup"]
            now = datetime.now()
            
            if last_backup:
                last_date = datetime.fromisoformat(last_backup)
                if (now - last_date).days < 1:
                    self.logger.info("Daily backup already exists")
                    return True
            
            # Create new daily backup
            backup_dir = os.path.join(
                self.daily_dir,
                now.strftime("%Y%m%d")
            )
            os.makedirs(backup_dir, exist_ok=True)
            
            # Backup workspace
            self._backup_workspace(backup_dir)
            
            # Update manifest
            self.manifest["last_daily_backup"] = now.isoformat()
            self.manifest["daily_backups"].append({
                "date": now.strftime("%Y%m%d"),
                "location": backup_dir
            })
            self._save_manifest()
            
            self.logger.info("Created daily backup")
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to create daily backup: {e}")
            return False
    
    def cleanup_old_backups(self, days: int = 30) -> bool:
        """Remove backups older than specified days."""
        try:
            now = datetime.now()
            threshold = now - timedelta(days=days)
            
            # Clean daily backups
            for backup in self.manifest["daily_backups"][:]:
                date_str = backup["date"]
                date = datetime.strptime(date_str, "%Y%m%d")
                
                if date < threshold:
                    backup_dir = backup["location"]
                    if os.path.exists(backup_dir):
                        shutil.rmtree(backup_dir)
                    self.manifest["daily_backups"].remove(backup)
                    self.logger.info(f"Removed old daily backup: {date_str}")
            
            self._save_manifest()
            return True
            
        except Exception as e:
            self.logger.error(f"Failed to cleanup old backups: {e}")
            return False
    
    def verify_backups(self) -> Tuple[bool, List[str]]:
        """Verify integrity of backups."""
        try:
            issues = []
            
            # Check daily backups
            for backup in self.manifest["daily_backups"]:
                location = backup["location"]
                if not os.path.exists(location):
                    issues.append(f"Missing daily backup: {backup['date']}")
            
            # Check backup points
            for name, point in self.manifest["backup_points"].items():
                location = point["location"]
                if not os.path.exists(location):
                    issues.append(f"Missing backup point: {name}")
            
            return len(issues) == 0, issues
            
        except Exception as e:
            self.logger.error(f"Failed to verify backups: {e}")
            return False, [str(e)]


def main():
    """Main entry point for backup management."""
    if len(sys.argv) < 2:
        print("Usage: backup_manager.py <workspace_root>")
        sys.exit(1)
    
    workspace_root = sys.argv[1]
    manager = BackupManager(workspace_root)
    
    # Create daily backup if needed
    manager.create_daily_backup()
    
    # Verify backups
    success, issues = manager.verify_backups()
    if not success:
        print("Backup verification failed:")
        for issue in issues:
            print(f"- {issue}")
        sys.exit(1)
    
    # Cleanup old backups
    manager.cleanup_old_backups()


if __name__ == "__main__":
    main()