# ULTIMATE AGENT LOADER: MAG-SP00F PROJECT (EFFICIENCY-OPTIMIZED)

---

## PROJECT IDENTITY
- **Name:** mag-sp00f
- **Package:** com.mag-sp00f.app
- **Purpose:** Advanced magstripe emulation & security analysis for Android 14+
- **Memory System:** Remember MCP for persistent context and rules

---

## CRITICAL ANTI-PATTERN RULES (AGENT VIOLATION PREVENTION)

### 🚨 FILE CORRUPTION PREVENTION
- **NEVER append or patch files** - Always delete and regenerate entire file from scratch
- **NO duplicate imports throughout file** - Single import block at top only
- **NO repeated code blocks** - One function/class per file, no duplicates
- **NO case-sensitive naming errors** - Enforce snake_case (Python), PascalCase (Java/Kotlin)
- **NO functions outside scope** - All functions properly contained within classes/modules
- **NO leftover legacy code** - Clean slate rewrites when corruption detected

### 🔧 FILE REGENERATION PROTOCOL
When files show ANY of these symptoms:
- Multiple import blocks
- Duplicate functions/classes
- Code appended after main logic
- Mixed naming conventions
- Syntax errors from patching

**IMMEDIATE ACTION:** Delete entire file → Regenerate from scratch with ALL requirements → Verify syntax → Write clean production code, use terminal text editor approach

### 📋 LARGE FILE HANDLING
For files exceeding agent view window:
1. Split into logical sections for review
2. Read each section completely before editing
3. Regenerate entire file maintaining all sections
4. Verify complete file integrity post-generation

---

## PHASE 0: ENVIRONMENT & STARTUP OPTIMIZATION

### Memory System Initialization
- Load Remember MCP for persistent project context
- Retrieve stored rules, preferences, and project state
- Auto-detect VSCode extensions and workspace configuration
- Initialize efficiency tracking metrics

### Project Structure Validation
```
/mag-sp00f/
├── android-app/           # Main Android application
├── scripts/              # Agent automation tools (NEVER in shipped app)
├── docs/                 # Technical documentation
├── .new/                 # Optimized files and documentation
├── LOADER.md             # This file - agent operational guide
├── README.md             # Project overview and setup
├── instructions.md       # Detailed operational rules
├── prompts.md           # Agent prompt templates
└── project_manifest.yaml # Project structure and batching
```

### Startup Self-Check Report
```
✓ Remember MCP connectivity
✓ Project structure integrity
✓ Required extensions active
✓ Build tools accessible
✓ PN532 terminal script functional
✓ Naming convention compliance
```

---

## PHASE 1: MANIFEST & BATCH GENERATION (EFFICIENCY-FIRST)

### Auto-Generate Core Files
- `/project_manifest.yaml` - Feature extraction from README.md
- `/batches.yaml` - Atomic task groupings (max 4-5 tasks per batch)
- `/CHANGELOG.md` - All agent actions logged with timestamps
- `/project_info.md` - Current project state and metrics

### VSCode Automation Setup
- `/.vscode/tasks.json` - Build, run, debug, agent script execution
- `/.vscode/launch.json` - Debug configurations for Android and scripts
- Validate automation files after every major update
- Include automation status in all environment checks

---

## PHASE 2: AGENT SCRIPT ECOSYSTEM (EXPANDABLE & EFFICIENT)

### Core Automation Scripts
```python
scripts/
├── audit_codebase.py      # Detect patching, duplicates, placeholders
├── naming_auditor.py      # Enforce naming conventions
├── lint_codebase.py       # Code quality and style validation
├── manifest_generator.py  # Auto-generate project manifests
├── task_tracker.py        # Batch progress and completion tracking
├── backup_manager.py      # Auto-backup before destructive operations
├── export_for_release.py  # Clean agent scripts from shipped code
└── pn532_terminal.py      # PN532 testing and validation
```

### Script Quality Standards
- **Top-level docstring:** Clear purpose and usage
- **Function docstrings:** Parameters, returns, exceptions
- **Section comments:** Explain logic flow and decisions
- **Error handling:** Graceful failures with user-friendly messages
- **Efficiency focus:** Minimize redundant operations
- **Remember MCP integration:** Store script status and results

---

## PHASE 3: BATCHED EXECUTION WITH EFFICIENCY METRICS

### Batch Processing Protocol
1. **Load batch from batches.yaml** (max 4-5 atomic tasks)
2. **Pre-batch validation:** Check for file corruption indicators
3. **Execute tasks sequentially** with corruption monitoring
4. **Post-batch audit:** Run all validation scripts
5. **Efficiency tracking:** Log time, operations, success rate
6. **Memory sync:** Update Remember MCP with progress and learnings

### Quality Gates
- Zero tolerance for file appending/patching
- Mandatory syntax validation before file writes
- Naming convention enforcement
- Production-grade code standards
- Performance optimization checks

### Error Recovery
- **File corruption detected:** Immediate regeneration protocol
- **Build failures:** Auto-backup restoration available
- **Naming violations:** Automatic correction with user notification
- **Syntax errors:** Full file regeneration with enhanced validation

---

## PHASE 4: SPECIALIZED TOOLING

### PN532 Terminal Integration
- Automated testing of mag-sp00f NFC emulation
- APDU command validation against expected responses
- Integration with Android HCE testing workflows
- Validation of Track2 to MSD conversion accuracy

### VISA TEST MSD Validation
Expected APDU workflows stored for validation:
```
AID #1 - A0000000031010 (Visa MSD)
AID #2 - A0000000980840 (US Common Debit)

Step 1: SELECT PPSE
Step 2: SELECT AID  
Step 3: GPO (Get Processing Options)
Step 4: READ RECORD
```

### Remember MCP Integration
- Store project preferences and coding standards
- Maintain agent performance metrics
- Preserve successful code patterns
- Track common error patterns for prevention

---

## PHASE 5: QUALITY ENFORCEMENT (ZERO-TOLERANCE)

### Naming Convention Enforcement
- **Python files:** snake_case
- **Java/Kotlin files:** PascalCase for classes, camelCase for methods
- **Package names:** com.mag-sp00f.app
- **Resource files:** lowercase with underscores

### Code Quality Standards
- No placeholders, stubs, or TODO comments
- No demo/sample data in production code
- All functions properly scoped and contained
- Single responsibility principle enforced
- Error handling comprehensive and user-friendly

### Security & Privacy
- No user data exposure in logs or agent scripts
- No arbitrary shell command execution without review
- Agent scripts never included in shipped application
- All sensitive operations logged and auditable

---

## PHASE 6: EFFICIENCY OPTIMIZATION & REPORTING

### Performance Metrics
- Batch completion time tracking
- File regeneration frequency (target: minimize)
- Audit success rate (target: 100%)
- Code quality score progression
- User satisfaction with output quality

### Handoff Reporting
```
Current Status:
├── Active Batch: [ID and description]
├── Completion Rate: [percentage]
├── Outstanding Issues: [NEEDS INPUT items]
├── Quality Score: [current metrics]
├── Next Recommended Actions: [prioritized list]
└── Remember MCP Sync Status: [timestamp]
```

### Release Preparation
- Execute export_for_release.py
- Validate all agent scripts removed from build
- Generate final quality checklist
- Verify naming convention compliance
- Confirm no file corruption exists

---

## CONTINUOUS IMPROVEMENT PROTOCOL

### Agent Learning Integration
- Log successful patterns to Remember MCP
- Document recurring issues and solutions
- Update rules based on user feedback
- Optimize batch sizing for maximum efficiency
- Refine corruption detection algorithms

### Documentation Maintenance
- Auto-update instructions.md with new rules
- Enhance prompts.md with learned patterns
- Maintain CHANGELOG.md with detailed action log
- Sync all documentation with Remember MCP

---

## EMERGENCY PROCEDURES

### File Corruption Recovery
1. **Detect:** Multiple imports, duplicate functions, syntax errors
2. **Backup:** Auto-save current state if salvageable
3. **Regenerate:** Delete corrupted file completely
4. **Rebuild:** Create from scratch with all requirements
5. **Validate:** Comprehensive syntax and quality check
6. **Document:** Log incident and prevention measures

### Performance Degradation Response
1. **Identify:** Batch completion time increasing
2. **Analyze:** Review recent changes and patterns
3. **Optimize:** Adjust batch sizing and validation frequency
4. **Monitor:** Track improvement metrics
5. **Learn:** Update Remember MCP with optimization insights

---

## SUCCESS METRICS

- **Zero file corruption incidents per project**
- **100% naming convention compliance**
- **Sub-2-minute average batch completion time**
- **95%+ first-pass audit success rate**
- **Comprehensive Remember MCP integration**
- **Production-ready code quality throughout**

---

*This LOADER.md is optimized for maximum efficiency while maintaining zero tolerance for the common agent failures that waste user time. All operations focus on getting clean, working results quickly while building institutional memory for continuous improvement.*
