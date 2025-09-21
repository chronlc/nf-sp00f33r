#!/usr/bin/env python3
"""
test_scripts.py
Runs automated tests on all development scripts to ensure reliability and correctness.
To be used by the agent for quality assurance only. Never included in shipped app.

Features:
- Tests script functionality
- Validates error handling
- Checks script interactions
- Verifies backup/restore operations
- Tests release preparation
"""

import os
import shutil
import unittest
import tempfile
from typing import Optional, Dict, List
from pathlib import Path
import json
import yaml
import logging
from datetime import datetime


class TestScriptBase(unittest.TestCase):
    """Base class for script testing with common setup."""
    
    @classmethod
    def setUpClass(cls):
        """Create a temporary test workspace."""
        cls.temp_dir = tempfile.mkdtemp()
        cls.workspace_root = os.path.join(cls.temp_dir, "workspace")
        cls.scripts_dir = os.path.join(cls.workspace_root, "scripts")
        cls.android_dir = os.path.join(cls.workspace_root, "android-app")
        cls.logs_dir = os.path.join(cls.workspace_root, "logs")
        cls.logs_dir = os.path.join(cls.workspace_root, "logs")
        cls.android_src_dir = os.path.join(cls.android_dir, "src/main/java")
        
        # Create test workspace structure
        for dir_path in [
            cls.scripts_dir,
            cls.android_dir,
            cls.android_src_dir,
            cls.logs_dir
        ]:
            os.makedirs(dir_path)
        
        # Create logs directory early to ensure it exists
        os.makedirs(cls.logs_dir, exist_ok=True)
        
        # Setup logging
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s'
        )
        cls.logger = logging.getLogger(cls.__name__)
    
    @classmethod
    def tearDownClass(cls):
        """Clean up temporary directory."""
        shutil.rmtree(cls.temp_dir)
    
    def setUp(self):
        """Reset workspace before each test."""
        self.logger.info(f"\nRunning test: {self._testMethodName}")
    
    def create_test_file(self, rel_path: str, content: str) -> str:
        """Create a test file in the workspace."""
        full_path = os.path.join(self.workspace_root, rel_path)
        os.makedirs(os.path.dirname(full_path), exist_ok=True)
        with open(full_path, "w") as f:
            f.write(content)
        return full_path


class TestAuditCodebase(TestScriptBase):
    """Test audit_codebase.py functionality."""
    
    def setUp(self):
        super().setUp()
        from audit_codebase import CodebaseAuditor
        self.auditor = CodebaseAuditor(self.workspace_root)
    
    def test_placeholder_detection(self):
        """Test detection of placeholder code."""
        test_file = self.create_test_file(
            "android-app/src/main/java/Test.kt",
            "class Test {\n    // TODO: Implement this\n}"
        )
        
        issues = self.auditor.scan_file(test_file)
        self.assertTrue(any("TODO" in issue.description for issue in issues))
    
    def test_legacy_code_detection(self):
        """Test detection of commented-out legacy code."""
        test_file = self.create_test_file(
            "android-app/src/main/java/Test.kt",
            """class Test {
                /* Old implementation
                   This is legacy code that was replaced
                   fun oldStuff() {}
                */
                
                // old version of the method
                fun newStuff() {
                    return "new"
                }
            }"""
        )
        
        issues = self.auditor.scan_file(test_file)
        self.assertTrue(any("legacy" in issue.description.lower() for issue in issues))
    
    def test_duplicate_detection(self):
        """Test detection of duplicate functions."""
        test_file = self.create_test_file(
            "android-app/src/main/java/Test.kt",
            "class Test {\n    fun duplicate() {}\n    fun duplicate() {}\n}"
        )
        
        issues = self.auditor.scan_file(test_file)
        self.assertTrue(any("duplicate" in issue.description.lower() for issue in issues))


class TestUndoLastBatch(TestScriptBase):
    """Test undo_last_batch.py functionality."""
    
    def setUp(self):
        super().setUp()
        from undo_last_batch import BatchUndoManager
        self.undo_manager = BatchUndoManager(self.workspace_root)
    
    def test_backup_creation(self):
        """Test creating file backups."""
        test_file = self.create_test_file(
            "android-app/src/main/java/Test.kt",
            "original content"
        )
        
        backup_path = self.undo_manager.create_backup(test_file, batch_id=1)
        self.assertIsNotNone(backup_path)
        self.assertTrue(os.path.exists(backup_path))
    
    def test_batch_restore(self):
        """Test restoring a batch of files."""
        # Create initial files
        files = {
            "Test1.kt": "content1",
            "Test2.kt": "content2"
        }
        
        created_files = {}
        # Create initial state and backup
        for name, content in files.items():
            path = self.create_test_file(f"android-app/src/main/java/{name}", content)
            created_files[name] = path
            
        # Create batch backup
        for name, path in created_files.items():
            self.undo_manager.create_backup(path, batch_id=1)
        
        # Modify the files after backup
        for path in created_files.values():
            with open(path, "w") as f:
                f.write("modified content")
        
        # Restore the batch
        self.assertTrue(self.undo_manager.undo_last_batch())
        
        # Verify content was restored
        for name, content in files.items():
            path = created_files[name]
            with open(path, "r") as f:
                restored_content = f.read()
                self.assertEqual(restored_content, content, 
                    f"File {name} content mismatch. Expected: {content}, Got: {restored_content}")
        
        # Verify restoration
        for name, content in files.items():
            path = os.path.join(self.workspace_root, f"android-app/src/main/java/{name}")
            with open(path, "r") as f:
                self.assertEqual(f.read(), content)


class TestExportForRelease(TestScriptBase):
    """Test export_for_release.py functionality."""
    
    def setUp(self):
        super().setUp()
        from export_for_release import ReleasePreparation
        self.release_prep = ReleasePreparation(self.workspace_root)
    
    def test_development_file_removal(self):
        """Test removal of development files."""
        # Create test development files
        dev_files = [
            "scripts/test.py",
            "android-app/__pycache__/test.pyc",
            ".backups/backup.txt"
        ]
        
        for file_path in dev_files:
            self.create_test_file(file_path, "test content")
        
        self.assertTrue(self.release_prep.remove_development_files())
        
        # Verify files were removed
        for file_path in dev_files:
            full_path = os.path.join(self.workspace_root, file_path)
            self.assertFalse(os.path.exists(full_path))
    
    def test_release_validation(self):
        """Test release validation checks."""
        # Create a Python file where it shouldn't be
        self.create_test_file(
            "android-app/src/main/java/test.py",
            "print('should not be here')"
        )
        
        self.assertFalse(self.release_prep.validate_codebase())
        self.assertTrue(any(
            issue.issue_type == "UNAUTHORIZED_FILE"
            for issue in self.release_prep.issues
        ))


def run_tests():
    """Run all script tests."""
    # Create test suite
    suite = unittest.TestSuite()
    
    # Add test classes
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestAuditCodebase))
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestUndoLastBatch))
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestExportForRelease))
    
    # Run tests
    runner = unittest.TextTestRunner(verbosity=2)
    return runner.run(suite)


if __name__ == "__main__":
    result = run_tests()
    exit(not result.wasSuccessful())