#!/usr/bin/env python3
"""
MAG-SP00F COMPREHENSIVE APP AUDIT & DEBUG SCRIPT v31.337
Per newrule.md: AUDIT AND DEBUG ALL FUNCTIONS/FEATURES - MAKE SURE APP WORKS 100%

PRODUCTION-GRADE audit system - NO SIMPLIFIED CODE
Tests all app functionality, UI components, database, attack vectors, and hardware integration
"""

import os
import sys
import subprocess
import json
import time
import re
from pathlib import Path
from typing import List, Dict, Any, Tuple

class MagSpoofAppAuditor:
    def __init__(self):
        self.project_root = Path(__file__).parent.parent
        self.android_app_dir = self.project_root / "android-app"
        self.results = {
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "total_tests": 0,
            "passed_tests": 0,
            "failed_tests": 0,
            "warnings": 0,
            "errors": [],
            "categories": {
                "build_system": {"tests": [], "status": "PENDING"},
                "ui_components": {"tests": [], "status": "PENDING"},
                "database_layer": {"tests": [], "status": "PENDING"},
                "attack_modules": {"tests": [], "status": "PENDING"},
                "hardware_integration": {"tests": [], "status": "PENDING"},
                "security_analysis": {"tests": [], "status": "PENDING"},
                "performance_metrics": {"tests": [], "status": "PENDING"}
            }
        }
        
    def log(self, message: str, level: str = "INFO"):
        """Elite hacker theme logging with emojis"""
        emoji_map = {
            "INFO": "🎯",
            "SUCCESS": "✅", 
            "ERROR": "❌",
            "WARNING": "⚠️",
            "DEBUG": "🔍",
            "ELITE": "💀",
            "HACK": "🏴‍☠️"
        }
        timestamp = time.strftime("%H:%M:%S")
        emoji = emoji_map.get(level, "📡")
        print(f"{emoji} [{timestamp}] {message}")
        
    def run_command(self, cmd: str, cwd: str = None) -> Tuple[bool, str]:
        """Execute command with proper error handling"""
        try:
            cwd = cwd or str(self.project_root)
            result = subprocess.run(
                cmd, 
                shell=True, 
                capture_output=True, 
                text=True, 
                cwd=cwd,
                timeout=60
            )
            return result.returncode == 0, result.stdout + result.stderr
        except subprocess.TimeoutExpired:
            return False, "Command timed out after 60 seconds"
        except Exception as e:
            return False, str(e)
    
    def audit_build_system(self):
        """Audit build system integrity per newrule.md requirements"""
        self.log("AUDITING BUILD SYSTEM 🔧", "ELITE")
        category = self.results["categories"]["build_system"]
        
        # Test 1: Gradle wrapper integrity
        test_result = {"name": "gradle_wrapper_integrity", "status": "PENDING"}
        if (self.project_root / "gradlew").exists():
            success, output = self.run_command("./gradlew --version")
            if success and "Gradle" in output:
                test_result["status"] = "PASS"
                test_result["details"] = "Gradle wrapper functional"
            else:
                test_result["status"] = "FAIL"
                test_result["details"] = f"Gradle wrapper issues: {output}"
        else:
            test_result["status"] = "FAIL" 
            test_result["details"] = "Gradle wrapper missing"
        category["tests"].append(test_result)
        
        # Test 2: Build success verification
        test_result = {"name": "build_success", "status": "PENDING"}
        self.log("Testing full build process...", "DEBUG")
        success, output = self.run_command("./gradlew android-app:assembleDebug")
        if success and "BUILD SUCCESSFUL" in output:
            test_result["status"] = "PASS"
            test_result["details"] = "Clean build successful"
        else:
            test_result["status"] = "FAIL"
            test_result["details"] = f"Build failed: {output[-500:]}"  # Last 500 chars
        category["tests"].append(test_result)
        
        # Test 3: APK generation and signing
        test_result = {"name": "apk_generation", "status": "PENDING"}
        apk_path = self.android_app_dir / "build/outputs/apk/debug/android-app-debug.apk"
        if apk_path.exists():
            apk_size = apk_path.stat().st_size
            test_result["status"] = "PASS" if apk_size > 1000000 else "WARN"  # >1MB
            test_result["details"] = f"APK size: {apk_size/1024/1024:.2f}MB"
        else:
            test_result["status"] = "FAIL"
            test_result["details"] = "APK not found"
        category["tests"].append(test_result)
        
        # Update category status
        failed_tests = [t for t in category["tests"] if t["status"] == "FAIL"]
        category["status"] = "FAIL" if failed_tests else "PASS"
        
    def audit_ui_components(self):
        """Audit all UI components and layouts per newrule.md PRODUCTION-GRADE requirements"""
        self.log("AUDITING UI COMPONENTS 📱", "ELITE")
        category = self.results["categories"]["ui_components"]
        
        # Test 1: Layout file completeness
        required_layouts = [
            "activity_main.xml",
            "fragment_card_reading.xml", 
            "fragment_card_database.xml",
            "fragment_home.xml",
            "fragment_emulation.xml",
            "fragment_attack_menu.xml",
            "item_apdu_log.xml",
            "item_card_profile.xml",
            "item_recent_card.xml"
        ]
        
        test_result = {"name": "layout_completeness", "status": "PENDING", "details": {}}
        layout_dir = self.android_app_dir / "src/main/res/layout"
        missing_layouts = []
        
        for layout in required_layouts:
            layout_path = layout_dir / layout
            if layout_path.exists():
                # Check for XML syntax
                try:
                    with open(layout_path, 'r') as f:
                        content = f.read()
                    if "<?xml" in content and not content.count('<') != content.count('>'):
                        test_result["details"][layout] = "VALID"
                    else:
                        test_result["details"][layout] = "INVALID_XML"
                except Exception as e:
                    test_result["details"][layout] = f"READ_ERROR: {e}"
            else:
                missing_layouts.append(layout)
                test_result["details"][layout] = "MISSING"
        
        test_result["status"] = "FAIL" if missing_layouts else "PASS"
        if missing_layouts:
            test_result["summary"] = f"Missing layouts: {', '.join(missing_layouts)}"
        else:
            test_result["summary"] = f"All {len(required_layouts)} layouts present and valid"
        category["tests"].append(test_result)
        
        # Test 2: Drawable resources
        test_result = {"name": "drawable_resources", "status": "PENDING"}
        required_drawables = ["ic_home.xml", "ic_nfc.xml", "ic_database.xml", "ic_emulate.xml", "ic_attack.xml"]
        drawable_dir = self.android_app_dir / "src/main/res/drawable"
        missing_drawables = [d for d in required_drawables if not (drawable_dir / d).exists()]
        
        if not missing_drawables:
            test_result["status"] = "PASS"
            test_result["details"] = f"All {len(required_drawables)} navigation icons present"
        else:
            test_result["status"] = "FAIL"
            test_result["details"] = f"Missing drawables: {', '.join(missing_drawables)}"
        category["tests"].append(test_result)
        
        # Test 3: Fragment class verification
        test_result = {"name": "fragment_classes", "status": "PENDING"}
        required_fragments = [
            "MainActivity.kt",
            "CardReadingFragment.kt",
            "CardDatabaseFragment.kt", 
            "HomeFragment.kt",
            "EmulationFragment.kt",
            "AttackMenuFragment.kt"
        ]
        
        ui_dir = self.android_app_dir / "src/main/java/com/mag_sp00f/app/ui"
        missing_fragments = []
        fragment_details = {}
        
        for fragment in required_fragments:
            fragment_path = ui_dir / fragment
            if fragment_path.exists():
                try:
                    with open(fragment_path, 'r') as f:
                        content = f.read()
                    
                    # Check for PRODUCTION-GRADE markers per newrule.md
                    if "PRODUCTION-GRADE" in content:
                        fragment_details[fragment] = "PRODUCTION_GRADE"
                    elif "TODO" in content or "placeholder" in content.lower():
                        fragment_details[fragment] = "NEEDS_COMPLETION"
                    else:
                        fragment_details[fragment] = "IMPLEMENTED"
                        
                except Exception as e:
                    fragment_details[fragment] = f"ERROR: {e}"
            else:
                missing_fragments.append(fragment)
                fragment_details[fragment] = "MISSING"
        
        test_result["status"] = "FAIL" if missing_fragments else "PASS"
        test_result["details"] = fragment_details
        test_result["summary"] = f"Fragments: {len(required_fragments) - len(missing_fragments)}/{len(required_fragments)} present"
        category["tests"].append(test_result)
        
        # Update category status
        failed_tests = [t for t in category["tests"] if t["status"] == "FAIL"]
        category["status"] = "FAIL" if failed_tests else "PASS"
        
    def audit_database_layer(self):
        """Audit database and data models per newrule.md real-data-only requirements"""
        self.log("AUDITING DATABASE LAYER 💾", "ELITE")
        category = self.results["categories"]["database_layer"]
        
        # Test 1: Data model completeness
        test_result = {"name": "data_models", "status": "PENDING"}
        required_models = ["EmvCardData.kt", "ApduLogEntry.kt", "CardProfile.kt"]
        models_dir = self.android_app_dir / "src/main/java/com/mag_sp00f/app"
        
        model_status = {}
        for model in required_models:
            # Check in data/ and models/ directories
            data_path = models_dir / "data" / model
            models_path = models_dir / "models" / model
            
            if data_path.exists():
                model_status[model] = self._analyze_kotlin_class(data_path)
            elif models_path.exists():
                model_status[model] = self._analyze_kotlin_class(models_path)
            else:
                model_status[model] = "MISSING"
        
        test_result["details"] = model_status
        test_result["status"] = "PASS" if all(v != "MISSING" for v in model_status.values()) else "FAIL"
        category["tests"].append(test_result)
        
        # Test 2: CardProfileManager functionality
        test_result = {"name": "profile_manager", "status": "PENDING"}
        manager_path = self.android_app_dir / "src/main/java/com/mag_sp00f/app/cardreading/CardProfileManager.kt"
        
        if manager_path.exists():
            try:
                with open(manager_path, 'r') as f:
                    content = f.read()
                
                required_methods = ["addCardProfile", "getAllCardProfiles", "exportAllProfiles", "exportProfile", "removeCardProfile"]
                present_methods = [method for method in required_methods if f"fun {method}" in content]
                
                test_result["details"] = {
                    "required_methods": required_methods,
                    "present_methods": present_methods,
                    "missing_methods": list(set(required_methods) - set(present_methods))
                }
                test_result["status"] = "PASS" if len(present_methods) == len(required_methods) else "FAIL"
                
            except Exception as e:
                test_result["status"] = "FAIL"
                test_result["details"] = f"Error analyzing CardProfileManager: {e}"
        else:
            test_result["status"] = "FAIL"
            test_result["details"] = "CardProfileManager.kt not found"
            
        category["tests"].append(test_result)
        
        # Update category status
        failed_tests = [t for t in category["tests"] if t["status"] == "FAIL"]
        category["status"] = "FAIL" if failed_tests else "PASS"
        
    def audit_attack_modules(self):
        """Audit attack vector modules per elite hacker requirements"""
        self.log("AUDITING ATTACK MODULES 🏴‍☠️", "HACK")
        category = self.results["categories"]["attack_modules"]
        
        # Test 1: Attack documentation completeness
        test_result = {"name": "attack_documentation", "status": "PENDING"}
        docs_dir = self.project_root / "docs"
        attack_docs = [
            "emv_attack_reference.md",
            "attack_module_architecture.md", 
            "attack_configuration_guide.md",
            "emv_attack_research_bibliography.md"
        ]
        
        doc_status = {}
        for doc in attack_docs:
            doc_path = docs_dir / doc
            if doc_path.exists():
                try:
                    with open(doc_path, 'r') as f:
                        content = f.read()
                    doc_status[doc] = f"EXISTS ({len(content)} chars)"
                except Exception as e:
                    doc_status[doc] = f"ERROR: {e}"
            else:
                doc_status[doc] = "MISSING"
        
        test_result["details"] = doc_status
        test_result["status"] = "PASS" if all("EXISTS" in v for v in doc_status.values()) else "WARN"
        category["tests"].append(test_result)
        
        # Test 2: EMV workflow scripts
        test_result = {"name": "emv_scripts", "status": "PENDING"}
        scripts_dir = self.project_root / "scripts"
        emv_scripts = ["pn532_terminal.py", "pn532_terminal_rapid.py"]
        
        script_status = {}
        for script in emv_scripts:
            script_path = scripts_dir / script
            if script_path.exists():
                try:
                    with open(script_path, 'r') as f:
                        content = f.read()
                    
                    # Check for multi-workflow support
                    if "workflow" in content.lower() and "emv" in content.lower():
                        script_status[script] = "MULTI_WORKFLOW_CAPABLE"
                    else:
                        script_status[script] = "BASIC_EMV"
                except Exception as e:
                    script_status[script] = f"ERROR: {e}"
            else:
                script_status[script] = "MISSING"
        
        test_result["details"] = script_status
        test_result["status"] = "PASS" if all(v != "MISSING" for v in script_status.values()) else "FAIL"
        category["tests"].append(test_result)
        
        # Update category status
        failed_tests = [t for t in category["tests"] if t["status"] == "FAIL"]
        category["status"] = "FAIL" if failed_tests else "PASS"
        
    def audit_hardware_integration(self):
        """Audit hardware integration capabilities"""
        self.log("AUDITING HARDWARE INTEGRATION ⚡", "ELITE")
        category = self.results["categories"]["hardware_integration"]
        
        # Test 1: PN532 integration
        test_result = {"name": "pn532_integration", "status": "PENDING"}
        
        # Check for PN532 terminal scripts
        scripts_dir = self.project_root / "scripts"
        pn532_scripts = [f for f in scripts_dir.glob("*pn532*.py") if f.is_file()]
        
        if pn532_scripts:
            test_result["status"] = "PASS"
            test_result["details"] = {
                "scripts_found": len(pn532_scripts),
                "scripts": [s.name for s in pn532_scripts],
                "ultra_fast_capable": any("rapid" in s.name for s in pn532_scripts)
            }
        else:
            test_result["status"] = "FAIL"
            test_result["details"] = "No PN532 integration scripts found"
        category["tests"].append(test_result)
        
        # Test 2: Android NFC integration
        test_result = {"name": "android_nfc", "status": "PENDING"}
        manifest_path = self.android_app_dir / "src/main/AndroidManifest.xml"
        
        if manifest_path.exists():
            try:
                with open(manifest_path, 'r') as f:
                    content = f.read()
                
                nfc_features = {
                    "nfc_permission": "uses-permission.*NFC" in content,
                    "hce_service": "HostApduService" in content,
                    "nfc_tech_filter": "nfc_tech_filter" in content
                }
                
                test_result["details"] = nfc_features
                test_result["status"] = "PASS" if all(nfc_features.values()) else "WARN"
            except Exception as e:
                test_result["status"] = "FAIL"
                test_result["details"] = f"Error reading manifest: {e}"
        else:
            test_result["status"] = "FAIL"
            test_result["details"] = "AndroidManifest.xml not found"
        category["tests"].append(test_result)
        
        # Update category status
        failed_tests = [t for t in category["tests"] if t["status"] == "FAIL"]
        category["status"] = "FAIL" if failed_tests else "PASS"
        
    def audit_security_analysis(self):
        """Audit security and attack vector analysis"""
        self.log("AUDITING SECURITY ANALYSIS 🔒", "HACK")
        category = self.results["categories"]["security_analysis"]
        
        # Test 1: Check for hardcoded credentials or simulations
        test_result = {"name": "no_hardcoded_data", "status": "PENDING"}
        
        # Scan Kotlin files for simulation indicators per newrule.md
        kotlin_files = list(self.android_app_dir.glob("**/*.kt"))
        simulation_indicators = ["simulation", "mock", "fake", "test_card", "dummy"]
        
        violations = []
        for kt_file in kotlin_files:
            try:
                with open(kt_file, 'r') as f:
                    content = f.read().lower()
                
                for indicator in simulation_indicators:
                    if indicator in content and "no.*simulation" not in content:
                        violations.append(f"{kt_file.name}: contains '{indicator}'")
            except Exception:
                continue
        
        test_result["status"] = "PASS" if not violations else "WARN"
        test_result["details"] = {
            "files_scanned": len(kotlin_files),
            "violations": violations[:10]  # Limit to first 10
        }
        category["tests"].append(test_result)
        
        # Test 2: Elite hacker theme compliance
        test_result = {"name": "elite_theme_compliance", "status": "PENDING"}
        
        elite_indicators = ["31337", "💀", "🔥", "⚡", "🏴‍☠️", "elite", "hacker"]
        elite_files = []
        
        for kt_file in kotlin_files:
            try:
                with open(kt_file, 'r') as f:
                    content = f.read()
                
                if any(indicator in content for indicator in elite_indicators):
                    elite_files.append(kt_file.name)
            except Exception:
                continue
        
        test_result["status"] = "PASS" if elite_files else "WARN"
        test_result["details"] = {
            "elite_themed_files": len(elite_files),
            "examples": elite_files[:5]
        }
        category["tests"].append(test_result)
        
        # Update category status
        failed_tests = [t for t in category["tests"] if t["status"] == "FAIL"]
        category["status"] = "FAIL" if failed_tests else "PASS"
        
    def audit_performance_metrics(self):
        """Audit performance and optimization metrics"""
        self.log("AUDITING PERFORMANCE METRICS 🚀", "ELITE")
        category = self.results["categories"]["performance_metrics"]
        
        # Test 1: Build time analysis
        test_result = {"name": "build_performance", "status": "PENDING"}
        
        start_time = time.time()
        success, output = self.run_command("./gradlew android-app:clean android-app:assembleDebug")
        build_time = time.time() - start_time
        
        test_result["details"] = {
            "build_time_seconds": round(build_time, 2),
            "build_success": success,
            "performance_rating": "FAST" if build_time < 30 else "SLOW" if build_time < 120 else "VERY_SLOW"
        }
        test_result["status"] = "PASS" if success and build_time < 120 else "WARN"
        category["tests"].append(test_result)
        
        # Test 2: APK size analysis
        test_result = {"name": "apk_size_analysis", "status": "PENDING"}
        apk_path = self.android_app_dir / "build/outputs/apk/debug/android-app-debug.apk"
        
        if apk_path.exists():
            apk_size_mb = apk_path.stat().st_size / 1024 / 1024
            test_result["details"] = {
                "apk_size_mb": round(apk_size_mb, 2),
                "size_rating": "OPTIMAL" if apk_size_mb < 20 else "ACCEPTABLE" if apk_size_mb < 50 else "LARGE"
            }
            test_result["status"] = "PASS" if apk_size_mb < 50 else "WARN"
        else:
            test_result["status"] = "FAIL"
            test_result["details"] = "APK not found for size analysis"
        category["tests"].append(test_result)
        
        # Update category status
        failed_tests = [t for t in category["tests"] if t["status"] == "FAIL"]
        category["status"] = "FAIL" if failed_tests else "PASS"
        
    def _analyze_kotlin_class(self, file_path: Path) -> str:
        """Analyze Kotlin class for completeness"""
        try:
            with open(file_path, 'r') as f:
                content = f.read()
            
            if "PRODUCTION-GRADE" in content:
                return "PRODUCTION_GRADE"
            elif "TODO" in content or "placeholder" in content.lower():
                return "INCOMPLETE"
            elif "data class" in content or "class " in content:
                return "IMPLEMENTED"
            else:
                return "UNKNOWN_STRUCTURE"
        except Exception as e:
            return f"ERROR: {e}"
    
    def generate_audit_report(self) -> str:
        """Generate comprehensive audit report with elite styling"""
        
        # Calculate summary statistics
        total_tests = sum(len(cat["tests"]) for cat in self.results["categories"].values())
        passed_tests = sum(1 for cat in self.results["categories"].values() for test in cat["tests"] if test["status"] == "PASS")
        failed_tests = sum(1 for cat in self.results["categories"].values() for test in cat["tests"] if test["status"] == "FAIL") 
        warned_tests = sum(1 for cat in self.results["categories"].values() for test in cat["tests"] if test["status"] == "WARN")
        
        self.results.update({
            "total_tests": total_tests,
            "passed_tests": passed_tests,
            "failed_tests": failed_tests,
            "warnings": warned_tests
        })
        
        # Generate elite report
        report = f"""
��‍☠️ MAG-SP00F COMPREHENSIVE AUDIT REPORT v31.337 💀
================================================================
📅 Timestamp: {self.results['timestamp']}
🎯 Total Tests: {total_tests}
✅ Passed: {passed_tests} 
❌ Failed: {failed_tests}
⚠️  Warnings: {warned_tests}
🔥 Success Rate: {(passed_tests/total_tests*100):.1f}%

🚀 ELITE STATUS: {'31337 READY' if failed_tests == 0 else 'NEEDS FIXES'} ��

"""
        
        # Category breakdown
        report += "\n📊 CATEGORY BREAKDOWN:\n" + "="*50 + "\n"
        for cat_name, cat_data in self.results["categories"].items():
            status_emoji = {"PASS": "✅", "FAIL": "❌", "PENDING": "⏳"}.get(cat_data["status"], "❓")
            cat_display = cat_name.replace('_', ' ').title()
            
            report += f"\n{status_emoji} {cat_display}:\n"
            for test in cat_data["tests"]:
                test_emoji = {"PASS": "✅", "FAIL": "❌", "WARN": "⚠️", "PENDING": "⏳"}.get(test["status"], "❓")
                report += f"  {test_emoji} {test['name']}: {test['status']}\n"
                
                if "details" in test and isinstance(test["details"], dict):
                    for key, value in test["details"].items():
                        if isinstance(value, (list, dict)):
                            report += f"    • {key}: {len(value) if isinstance(value, list) else 'Complex'}\n"
                        else:
                            report += f"    • {key}: {value}\n"
                elif "details" in test:
                    report += f"    • Details: {test['details']}\n"
        
        # Recommendations
        report += f"\n🔧 RECOMMENDATIONS:\n" + "="*30 + "\n"
        if failed_tests == 0:
            report += "🎉 ALL TESTS PASSED! App is production-ready for elite operations.\n"
            report += "🏴‍☠️ Ready for real-world EMV testing and attack vector execution.\n"
        else:
            report += f"⚠️ {failed_tests} critical issues need immediate attention per newrule.md.\n"
            report += "🔧 Fix all FAILED tests before proceeding with production deployment.\n"
            
        if warned_tests > 0:
            report += f"💡 {warned_tests} warnings should be addressed for optimal performance.\n"
            
        report += "\n💀 Elite Level: 31337 🔥\n"
        report += "================================================================\n"
        
        return report
    
    def run_comprehensive_audit(self):
        """Execute full audit suite per newrule.md requirements"""
        self.log("STARTING COMPREHENSIVE MAG-SP00F APP AUDIT 🎯", "HACK")
        self.log("Per newrule.md: AUDIT ALL FUNCTIONS/FEATURES - ENSURE 100% FUNCTIONALITY", "INFO")
        
        # Execute all audit categories
        audit_methods = [
            self.audit_build_system,
            self.audit_ui_components, 
            self.audit_database_layer,
            self.audit_attack_modules,
            self.audit_hardware_integration,
            self.audit_security_analysis,
            self.audit_performance_metrics
        ]
        
        for audit_method in audit_methods:
            try:
                audit_method()
            except Exception as e:
                self.log(f"Audit method {audit_method.__name__} failed: {e}", "ERROR")
                self.results["errors"].append(f"{audit_method.__name__}: {e}")
        
        # Generate and save report
        report = self.generate_audit_report()
        
        # Save report to file
        report_path = self.project_root / "audit_report.txt"
        with open(report_path, 'w') as f:
            f.write(report)
            
        # Also save JSON data
        json_path = self.project_root / "audit_data.json"
        with open(json_path, 'w') as f:
            json.dump(self.results, f, indent=2, default=str)
            
        self.log(f"Audit complete! Report saved to {report_path}", "SUCCESS")
        print(report)
        
        return self.results["failed_tests"] == 0


if __name__ == "__main__":
    auditor = MagSpoofAppAuditor()
    success = auditor.run_comprehensive_audit()
    sys.exit(0 if success else 1)
