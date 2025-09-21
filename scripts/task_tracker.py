#!/usr/bin/env python3
"""
task_tracker.py
Tracks progress of batches and tasks, updates status in batches.yaml and CHANGELOG.md.
To be used by the agent for progress monitoring only. Never included in shipped app.
"""

import os
import yaml
from typing import Dict, List, Optional
from dataclasses import dataclass
from datetime import datetime


@dataclass
class Task:
    description: str
    status: str
    notes: Optional[str] = None


@dataclass
class Batch:
    id: int
    name: str
    tasks: List[Task]
    status: str


class TaskTracker:
    def __init__(self, workspace_root: str):
        self.workspace_root = workspace_root
        self.batches_path = os.path.join(workspace_root, "batches.yaml")
        self.changelog_path = os.path.join(workspace_root, "CHANGELOG.md")

    def load_batches(self) -> Dict:
        """Load current batch status from batches.yaml."""
        with open(self.batches_path, "r") as f:
            return yaml.safe_load(f)

    def save_batches(self, data: Dict) -> None:
        """Save updated batch status to batches.yaml."""
        with open(self.batches_path, "w") as f:
            yaml.safe_dump(data, f, sort_keys=False, allow_unicode=True)

    def update_batch_status(self, batch_id: int, status: str) -> None:
        """Update the status of a specific batch."""
        data = self.load_batches()
        
        for batch in data["batches"]:
            if batch["id"] == batch_id:
                batch["status"] = status
                self.save_batches(data)
                
                # Log to changelog
                self.log_to_changelog(f"- Batch {batch_id} ({batch['name']}) marked as {status}")
                break

    def update_task_status(self, batch_id: int, task_index: int, status: str, notes: Optional[str] = None) -> None:
        """Update the status of a specific task within a batch."""
        data = self.load_batches()
        
        for batch in data["batches"]:
            if batch["id"] == batch_id:
                if 0 <= task_index < len(batch["tasks"]):
                    task = batch["tasks"][task_index]
                    
                    # Convert string task to dict if needed
                    if isinstance(task, str):
                        task = {"description": task, "status": "not_started"}
                        batch["tasks"][task_index] = task
                    
                    task["status"] = status
                    if notes:
                        task["notes"] = notes
                    
                    self.save_batches(data)
                    
                    # Log to changelog
                    self.log_to_changelog(
                        f"- Task '{task['description']}' in Batch {batch_id} marked as {status}"
                        + (f"\n  Notes: {notes}" if notes else "")
                    )
                    break

    def get_batch_progress(self, batch_id: int) -> Dict:
        """Get progress statistics for a specific batch."""
        data = self.load_batches()
        
        for batch in data["batches"]:
            if batch["id"] == batch_id:
                total_tasks = len(batch["tasks"])
                completed_tasks = sum(
                    1 for task in batch["tasks"]
                    if isinstance(task, dict) and task.get("status") == "completed"
                )
                
                return {
                    "batch_name": batch["name"],
                    "total_tasks": total_tasks,
                    "completed_tasks": completed_tasks,
                    "completion_percentage": (completed_tasks / total_tasks) * 100 if total_tasks > 0 else 0
                }
        
        return {}

    def get_overall_progress(self) -> Dict:
        """Get overall project progress statistics."""
        data = self.load_batches()
        
        total_tasks = 0
        completed_tasks = 0
        batch_status = {}
        
        for batch in data["batches"]:
            batch_tasks = len(batch["tasks"])
            batch_completed = sum(
                1 for task in batch["tasks"]
                if isinstance(task, dict) and task.get("status") == "completed"
            )
            
            total_tasks += batch_tasks
            completed_tasks += batch_completed
            batch_status[batch["id"]] = {
                "name": batch["name"],
                "completion": (batch_completed / batch_tasks * 100) if batch_tasks > 0 else 0
            }
        
        return {
            "total_tasks": total_tasks,
            "completed_tasks": completed_tasks,
            "overall_completion": (completed_tasks / total_tasks * 100) if total_tasks > 0 else 0,
            "batch_status": batch_status
        }

    def log_to_changelog(self, message: str) -> None:
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

    def generate_progress_report(self) -> str:
        """Generate a markdown progress report."""
        progress = self.get_overall_progress()
        
        report = [
            "# Project Progress Report",
            f"\nGenerated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
            f"\n## Overall Progress: {progress['overall_completion']:.1f}%",
            f"- Total Tasks: {progress['total_tasks']}",
            f"- Completed Tasks: {progress['completed_tasks']}",
            "\n## Batch Status\n"
        ]
        
        for batch_id, batch in progress["batch_status"].items():
            report.append(
                f"### Batch {batch_id}: {batch['name']}\n"
                f"- Completion: {batch['completion']:.1f}%"
            )
        
        return "\n".join(report)


def main():
    workspace_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    
    tracker = TaskTracker(workspace_root)
    
    # Generate and save progress report
    report = tracker.generate_progress_report()
    report_path = os.path.join(workspace_root, "docs", "progress_report.md")
    
    os.makedirs(os.path.dirname(report_path), exist_ok=True)
    with open(report_path, "w") as f:
        f.write(report)
    
    print(f"Progress report generated at {report_path}")
    return 0


if __name__ == "__main__":
    exit(main())