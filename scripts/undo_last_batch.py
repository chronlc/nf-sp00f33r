#!/usr/bin/env python3
"""
undo_last_batch.py
Provides rollback functionality for the last batch of changes. Creates and manages
backups before destructive operations and allows reverting to previous states.
To be used by the agent for safety and recovery only. Never included in shipped app.

Features:
- Creates timestamped backups before file modifications
- Stores backup metadata in JSON format
- Allows rollback of entire batches or individual files
- Maintains a backup history for multiple undos
"""

import os
import json
import shutil
from typing import Dict, List, Optional
from dataclasses import dataclass
from datetime import datetime
import logging
from pathlib import Path


@dataclass
class BackupEntry:
    """Represents a single file backup."""
    original_path: str
    backup_path: str
    timestamp: str
    batch_id: Optional[int] = None
    description: Optional[str] = None


class BatchUndoManager:
    def __init__(self, workspace_root: str):
        self.workspace_root = workspace_root
        self.backup_dir = os.path.join(workspace_root, ".backups")
        self.metadata_file = os.path.join(self.backup_dir, "backup_metadata.json")
        self.backups: Dict[str, List[BackupEntry]] = {}
        
        # Setup logging
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler(os.path.join(workspace_root, "logs", "undo.log")),
                logging.StreamHandler()
            ]
        )
        self.logger = logging.getLogger(__name__)
        
        # Ensure backup directory exists
        os.makedirs(self.backup_dir, exist_ok=True)
        os.makedirs(os.path.join(workspace_root, "logs"), exist_ok=True)
        
        # Load existing metadata
        self._load_metadata()

    def _load_metadata(self) -> None:
        """Load backup metadata from JSON file."""
        try:
            if os.path.exists(self.metadata_file):
                with open(self.metadata_file, 'r') as f:
                    data = json.load(f)
                    # Convert dictionary to BackupEntry objects
                    self.backups = {
                        batch_id: [BackupEntry(**entry) for entry in entries]
                        for batch_id, entries in data.items()
                    }
        except Exception as e:
            self.logger.error(f"Error loading backup metadata: {e}")
            self.backups = {}

    def _save_metadata(self) -> None:
        """Save backup metadata to JSON file."""
        try:
            # Convert BackupEntry objects to dictionaries
            data = {
                batch_id: [vars(entry) for entry in entries]
                for batch_id, entries in self.backups.items()
            }
            with open(self.metadata_file, 'w') as f:
                json.dump(data, f, indent=2)
        except Exception as e:
            self.logger.error(f"Error saving backup metadata: {e}")

    def create_backup(self, file_path: str, batch_id: Optional[int] = None, description: Optional[str] = None) -> Optional[str]:
        """Create a backup of a file before modification."""
        try:
            if not os.path.exists(file_path):
                self.logger.warning(f"File not found: {file_path}")
                return None
            
            # Create timestamped backup path
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            rel_path = os.path.relpath(file_path, self.workspace_root)
            backup_path = os.path.join(
                self.backup_dir,
                f"{timestamp}_{rel_path.replace('/', '_')}"
            )
            
            # Create backup
            os.makedirs(os.path.dirname(backup_path), exist_ok=True)
            shutil.copy2(file_path, backup_path)
            
            # Create backup entry
            entry = BackupEntry(
                original_path=file_path,
                backup_path=backup_path,
                timestamp=timestamp,
                batch_id=batch_id,
                description=description
            )
            
            # Store in backups dictionary
            batch_key = str(batch_id) if batch_id else "unbatched"
            if batch_key not in self.backups:
                self.backups[batch_key] = []
            self.backups[batch_key].append(entry)
            
            # Save metadata
            self._save_metadata()
            
            self.logger.info(f"Created backup of {rel_path} for batch {batch_key}")
            return backup_path
            
        except Exception as e:
            self.logger.error(f"Error creating backup: {e}")
            return None

    def undo_last_batch(self) -> bool:
        """Undo all changes from the last batch by restoring backups."""
        try:
            # Find the most recent batch
            batch_keys = [k for k in self.backups.keys() if k != "unbatched"]
            if not batch_keys:
                self.logger.warning("No batched backups found to undo")
                return False
            
            last_batch = max(batch_keys)
            entries = self.backups[last_batch]
            
            # Restore each file in the batch
            success = True
            for entry in entries:
                if not self._restore_file(entry):
                    success = False
            
            if success:
                # Remove the batch from backups
                del self.backups[last_batch]
                self._save_metadata()
                self.logger.info(f"Successfully undid batch {last_batch}")
            
            return success
            
        except Exception as e:
            self.logger.error(f"Error undoing last batch: {e}")
            return False

    def _restore_file(self, entry: BackupEntry) -> bool:
        """Restore a single file from its backup."""
        try:
            if not os.path.exists(entry.backup_path):
                self.logger.error(f"Backup file not found: {entry.backup_path}")
                return False
            
            # Restore from backup
            os.makedirs(os.path.dirname(entry.original_path), exist_ok=True)
            shutil.copy2(entry.backup_path, entry.original_path)
            
            self.logger.info(f"Restored {entry.original_path} from backup")
            return True
            
        except Exception as e:
            self.logger.error(f"Error restoring file: {e}")
            return False

    def list_backups(self, batch_id: Optional[int] = None) -> List[BackupEntry]:
        """List all backups, optionally filtered by batch_id."""
        if batch_id is not None:
            return self.backups.get(str(batch_id), [])
        
        all_backups = []
        for entries in self.backups.values():
            all_backups.extend(entries)
        return sorted(all_backups, key=lambda x: x.timestamp, reverse=True)

    def cleanup_old_backups(self, days: int = 7) -> int:
        """Remove backups older than specified days."""
        try:
            cutoff = datetime.now().timestamp() - (days * 24 * 60 * 60)
            files_removed = 0
            
            for batch_id in list(self.backups.keys()):
                entries = self.backups[batch_id]
                current_entries = []
                
                for entry in entries:
                    try:
                        backup_time = datetime.strptime(
                            entry.timestamp,
                            "%Y%m%d_%H%M%S"
                        ).timestamp()
                        
                        if backup_time < cutoff:
                            if os.path.exists(entry.backup_path):
                                os.remove(entry.backup_path)
                                files_removed += 1
                        else:
                            current_entries.append(entry)
                            
                    except Exception as e:
                        self.logger.error(f"Error processing backup entry: {e}")
                
                if current_entries:
                    self.backups[batch_id] = current_entries
                else:
                    del self.backups[batch_id]
            
            self._save_metadata()
            self.logger.info(f"Removed {files_removed} old backup files")
            return files_removed
            
        except Exception as e:
            self.logger.error(f"Error cleaning up old backups: {e}")
            return 0


def main():
    workspace_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    undo_manager = BatchUndoManager(workspace_root)
    
    # Parse command line arguments
    import argparse
    parser = argparse.ArgumentParser(description="Manage and undo batched changes")
    parser.add_argument("--undo", action="store_true", help="Undo the last batch")
    parser.add_argument("--list", action="store_true", help="List all backups")
    parser.add_argument("--cleanup", type=int, metavar="DAYS",
                       help="Remove backups older than DAYS days")
    
    args = parser.parse_args()
    
    if args.undo:
        if undo_manager.undo_last_batch():
            print("Successfully undid last batch")
            return 0
        print("Failed to undo last batch")
        return 1
        
    elif args.list:
        backups = undo_manager.list_backups()
        if not backups:
            print("No backups found")
            return 0
        
        print("\nExisting backups:")
        for entry in backups:
            print(f"\n{entry.timestamp} - Batch {entry.batch_id or 'unbatched'}")
            print(f"  File: {os.path.relpath(entry.original_path, workspace_root)}")
            if entry.description:
                print(f"  Description: {entry.description}")
        return 0
        
    elif args.cleanup is not None:
        removed = undo_manager.cleanup_old_backups(args.cleanup)
        print(f"Removed {removed} old backup files")
        return 0
        
    else:
        parser.print_help()
        return 0


if __name__ == "__main__":
    exit(main())