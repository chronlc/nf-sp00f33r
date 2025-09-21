#!/usr/bin/env python3
"""
integration_test.py
Validates the interaction and workflow between different scripts and tools.
Tests end-to-end functionality from development through release.
For agent use during development only. Not shipped with app.

Features:
- Tests script interactions and dependencies
- Validates complete workflow sequences
- Checks manifest and batch processing
- Verifies backup/restore reliability
- Tests release preparation chain
"""

import os
import sys
import shutil
import unittest
import tempfile
import logging
from typing import List, Dict, Optional
from pathlib import Path
from datetime import datetime

# Import project scripts
from audit_codebase import CodebaseAuditor
from manifest_generator import ManifestGenerator
from naming_auditor import NamingAuditor
from task_tracker import TaskTracker
from undo_last_batch import BatchUndoManager
from export_for_release import ReleasePreparation


class IntegrationTestBase(unittest.TestCase):
    """Base class for integration testing with workspace setup."""
    
    @classmethod
    def setUpClass(cls):
        """Create a complete test workspace."""
        cls.temp_dir = tempfile.mkdtemp()
        cls.workspace_root = os.path.join(cls.temp_dir, "workspace")
        
        # Create workspace structure
        dirs = [
            "scripts",
            "android-app/src/main/java/com/nf_sp00f/app",
            "android-app/src/test",
            "docs",
            ".backups"
        ]
        
        for dir_path in dirs:
            os.makedirs(os.path.join(cls.workspace_root, dir_path), exist_ok=True)
        
        # Setup logging
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s'
        )
        cls.logger = logging.getLogger(cls.__name__)
    
    @classmethod
    def tearDownClass(cls):
        """Clean up test workspace."""
        shutil.rmtree(cls.temp_dir)
    
    def setUp(self):
        """Reset state before each test."""
        self.logger.info(f"\nRunning integration test: {self._testMethodName}")
    
    def create_test_file(self, rel_path: str, content: str) -> str:
        """Create a test file in the workspace."""
        full_path = os.path.join(self.workspace_root, rel_path)
        os.makedirs(os.path.dirname(full_path), exist_ok=True)
        with open(full_path, "w") as f:
            f.write(content)
        return full_path


class TestWorkflowSequence(IntegrationTestBase):
    """Test complete workflow sequences."""
    
    def setUp(self):
        super().setUp()
        self.auditor = CodebaseAuditor(self.workspace_root)
        self.manifest_gen = ManifestGenerator(self.workspace_root)
        self.naming_auditor = NamingAuditor(self.workspace_root)
        self.task_tracker = TaskTracker(self.workspace_root)
        self.undo_manager = BatchUndoManager(self.workspace_root)
        self.release_prep = ReleasePreparation(self.workspace_root)
    
    def test_development_workflow(self):
        """Test the development workflow from start to release."""
        # 1. Create test files
        kotlin_file = self.create_test_file(
            "android-app/src/main/java/com/nf_sp00f/app/Test.kt",
            """
            class TestClass {
                fun testMethod() {
                    // TODO: Implement this
                }
            }
            """
        )
        
        # 2. Run code audit
        issues = self.auditor.scan_file(kotlin_file)
        self.assertTrue(any("TODO" in issue.description for issue in issues))
        
        # 3. Generate manifest
        manifest = self.manifest_gen.generate_manifest()
        self.assertTrue("TestClass" in str(manifest))
        
        # 4. Check naming conventions
        naming_issues = self.naming_auditor.check_file(kotlin_file)
        self.assertEqual(len(naming_issues), 0)  # Should follow conventions
        
        # 5. Create backup
        backup_path = self.undo_manager.create_backup(kotlin_file, batch_id=1)
        self.assertTrue(os.path.exists(backup_path))
        
        # 6. Modify file
        with open(kotlin_file, "w") as f:
            f.write("""
            class TestClass {
                fun testMethod() {
                    return "Implemented"
                }
            }
            """)
        
        # 7. Validate changes
        issues = self.auditor.scan_file(kotlin_file)
        self.assertFalse(any("TODO" in issue.description for issue in issues))
        
        # 8. Test undo
        self.assertTrue(self.undo_manager.undo_last_batch())
        with open(kotlin_file, "r") as f:
            content = f.read()
            self.assertTrue("TODO" in content)
        
        # 9. Prepare for release
        self.assertTrue(self.release_prep.validate_codebase())
        self.assertEqual(len(self.release_prep.issues), 0)


class TestErrorRecovery(IntegrationTestBase):
    """Test error handling and recovery across scripts."""
    
    def setUp(self):
        super().setUp()
        self.undo_manager = BatchUndoManager(self.workspace_root)
        self.manifest_gen = ManifestGenerator(self.workspace_root)
    
    def test_batch_failure_recovery(self):
        """Test recovery from a failed batch operation."""
        # 1. Create initial files
        files = {
            "Test1.kt": "original content 1",
            "Test2.kt": "original content 2",
        }
        
        file_paths = []
        for name, content in files.items():
            path = self.create_test_file(
                f"android-app/src/main/java/com/nf_sp00f/app/{name}",
                content
            )
            file_paths.append(path)
            self.undo_manager.create_backup(path, batch_id=1)
        
        # 2. Simulate partial failure
        with open(file_paths[0], "w") as f:
            f.write("modified content 1")
        
        # Simulate crash before second file is modified
        
        # 3. Recover using undo
        self.assertTrue(self.undo_manager.undo_last_batch())
        
        # 4. Verify recovery
        for path, (_, content) in zip(file_paths, files.items()):
            with open(path, "r") as f:
                self.assertEqual(f.read(), content)
    
    def test_manifest_recovery(self):
        """Test recovery from manifest corruption."""
        # 1. Create valid manifest
        manifest = self.manifest_gen.generate_manifest()
        manifest_path = os.path.join(self.workspace_root, "project_manifest.yaml")
        
        # 2. Backup manifest
        self.undo_manager.create_backup(manifest_path, batch_id=1)
        
        # 3. Corrupt manifest
        with open(manifest_path, "w") as f:
            f.write("invalid: yaml: content")
        
        # 4. Recover manifest
        self.assertTrue(self.undo_manager.undo_last_batch())
        
        # 5. Verify recovery
        with open(manifest_path, "r") as f:
            recovered_manifest = f.read()
            self.assertTrue("invalid: yaml: content" not in recovered_manifest)


def run_integration_tests():
    """Run all integration tests."""
    suite = unittest.TestSuite()
    
    # Add test classes
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestWorkflowSequence))
    suite.addTests(unittest.TestLoader().loadTestsFromTestCase(TestErrorRecovery))
    
    # Run tests
    runner = unittest.TextTestRunner(verbosity=2)
    return runner.run(suite)


if __name__ == "__main__":
    result = run_integration_tests()
    exit(not result.wasSuccessful())