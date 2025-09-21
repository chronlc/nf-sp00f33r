# PROMPTS & TEMPLATES - MAG-SP00F PROJECT (REMEMBER MCP OPTIMIZED)

---

## 🧠 REMEMBER MCP INTEGRATION

### Memory Configuration
```
Project Context: mag-sp00f Android magstripe emulation app
Package: com.mag-sp00f.app
Target: Android 14+, security research, NFC/HCE
Memory Scope: workspace
Language Specific: Java/Kotlin for Android, Python for scripts
```

### Persistent Rules Storage
- Anti-corruption patterns and detection rules
- Naming convention preferences and violations
- Code quality standards and metrics
- APDU validation patterns and responses
- Performance optimization guidelines

---

## 🚨 CRITICAL ANTI-CORRUPTION PROMPTS

### FILE CORRUPTION DETECTION PROMPT
```
"Before any file edit, scan for corruption indicators:
- Multiple import blocks throughout file
- Duplicate function/class definitions
- Code appended after main logic
- Mixed naming conventions
- Functions outside proper scope
- Repeated code patterns

If ANY corruption detected: DELETE entire file → Regenerate from scratch with ALL requirements → Verify syntax → Ensure production-grade quality"
```

### EFFICIENCY-FIRST PROMPT
```
"Optimize for user time and efficiency:
- Complete tasks in single operations when possible
- Use multi_replace_string_in_file for multiple edits
- Read large file sections instead of piecemeal access
- Batch related operations together
- Minimize redundant file operations
- Track and report efficiency metrics"
```

### ZERO-TOLERANCE QUALITY PROMPT
```
"Enforce zero-tolerance quality standards:
- No placeholders, stubs, TODOs, or demo code
- All code must be production-ready
- Comprehensive error handling required
- Clear docstrings and comments for humans
- Consistent naming conventions enforced
- Full syntax validation before file writes"
```

---

## 🔄 BATCH PROCESSING PROMPTS

### BATCH EXECUTION PROMPT
```
"Process exactly ONE batch of max 4-5 atomic tasks:
1. Load current batch from batches.yaml
2. Check each file for corruption before editing
3. Execute tasks with quality monitoring
4. Run complete audit after batch completion
5. Mark batch COMPLETE with summary
6. Update Remember MCP with progress and learnings
7. NEVER revisit or repeat completed batches"
```

### QUALITY GATE PROMPT
```
"After each batch, mandatory quality validation:
- Run audit_codebase.py for corruption detection
- Run naming_auditor.py for convention compliance
- Run lint_codebase.py for code quality
- Verify all files pass syntax validation
- Check performance metrics and improvement
- Update Remember MCP with quality scores
- DO NOT proceed until all issues resolved"
```

### ERROR RECOVERY PROMPT
```
"When file corruption or quality issues detected:
1. STOP current operations immediately
2. Backup current state if salvageable
3. DELETE corrupted file completely
4. Regenerate entire file from scratch
5. Implement ALL requirements in clean code
6. Validate syntax and quality thoroughly
7. Document incident and prevention measures
8. Update Remember MCP with recovery patterns"
```

---

## 🎯 PROJECT-SPECIFIC PROMPTS

### MAGSTRIPE EMULATION PROMPT
```
"For mag-sp00f NFC/HCE development:
- Focus on Track2 to MSD conversion accuracy
- Implement proper APDU command handling
- Ensure HCE service reliability
- Validate against VISA TEST MSD workflows
- Support both contactless and downgrade modes
- Include comprehensive logging and analysis
- Maintain security research focus"
```

### PN532 TESTING PROMPT
```
"For PN532 terminal automation:
- Create minimal, non-looping validation scripts
- Support /dev/ttyUSB0 and /dev/rfcomm0 configurations
- Implement proper serial communication handling
- Validate APDU responses against known-good patterns
- Provide clear success/failure reporting
- Include performance and reliability metrics
- Integrate with Remember MCP for test result storage"
```

### ANDROID DEVELOPMENT PROMPT
```
"For Android application development:
- Use Material Design 3 components
- Implement proper HCE service architecture
- Follow Android 14+ best practices
- Ensure NFC permissions and capabilities
- Create intuitive UI for security researchers
- Include real-time APDU logging display
- Maintain com.mag-sp00f.app package structure"
```

---

## 📝 DOCUMENTATION PROMPTS

### COMPREHENSIVE DOCUMENTATION PROMPT
```
"Create detailed, user-focused documentation:
- Include clear setup and installation instructions
- Provide comprehensive feature explanations
- Document all testing and validation procedures
- Include security and privacy considerations
- Explain technical architecture and design decisions
- Provide troubleshooting and FAQ sections
- Update Remember MCP with documentation patterns"
```

### CODE DOCUMENTATION PROMPT
```
"Write code with human maintainers in mind:
- Top-level file docstrings explaining purpose
- Function/method docstrings with parameters and returns
- Inline comments explaining complex logic
- Clear variable and function naming
- Section headers for logical code blocks
- Error handling explanations
- Performance consideration notes"
```

### CHANGELOG PROMPT
```
"Maintain comprehensive change tracking:
- Log all file creations, modifications, deletions
- Record batch completion with timestamps
- Document quality improvements and metrics
- Note efficiency optimizations and their impact
- Track Remember MCP integration milestones
- Include performance benchmarks and trends
- Summarize user-facing improvements"
```

---

## 🔍 AUDIT & VALIDATION PROMPTS

### SYNTAX VALIDATION PROMPT
```
"Before writing any file:
1. Validate all syntax in memory
2. Check import statements for duplicates
3. Verify function/class scope containment
4. Confirm naming convention compliance
5. Ensure no placeholder or stub code
6. Validate production-grade quality
7. Store validation patterns in Remember MCP"
```

### PERFORMANCE MONITORING PROMPT
```
"Track and optimize performance metrics:
- Batch completion time and efficiency
- File regeneration frequency (minimize)
- Quality audit success rate (maximize)
- User satisfaction with output quality
- Remember MCP sync performance
- Resource utilization optimization
- Continuous improvement identification"
```

### SECURITY VALIDATION PROMPT
```
"Enforce security and privacy standards:
- No user data exposure in logs or scripts
- No arbitrary shell command execution
- Agent scripts never in shipped application
- All sensitive operations logged and auditable
- Secure memory handling for magstripe data
- Proper Android permission management
- Privacy-first design principles"
```

---

## 🚀 WORKFLOW OPTIMIZATION PROMPTS

### EFFICIENCY OPTIMIZATION PROMPT
```
"Maximize operational efficiency:
- Batch related file operations together
- Use parallel processing when safe
- Minimize redundant validations
- Cache validation results where appropriate
- Optimize Remember MCP sync frequency
- Prefer bulk operations over individual actions
- Track and report efficiency improvements"
```

### HANDOFF PREPARATION PROMPT
```
"Prepare comprehensive handoff documentation:
- Current batch status and completion percentage
- Outstanding NEEDS INPUT items with context
- Quality metrics and recent improvements
- Active files and their current state
- Remember MCP sync status and timestamp
- Next recommended actions with priorities
- Performance trends and optimization opportunities"
```

### RELEASE PREPARATION PROMPT
```
"Prepare for release with comprehensive validation:
- Run export_for_release.py to clean agent scripts
- Validate no development artifacts in shipped code
- Verify naming convention compliance project-wide
- Confirm no file corruption anywhere in codebase
- Generate final quality and security checklist
- Update Remember MCP with release metadata
- Document final project state and achievements"
```

---

## 🎛️ REMEMBER MCP SPECIFIC PROMPTS

### MEMORY STORAGE PROMPT
```
"Store critical information in Remember MCP:
- User preferences for coding standards and style
- Common error patterns and their solutions
- Successful code patterns and architectures
- Quality metrics and improvement trends
- Project-specific rules and requirements
- Efficiency optimization discoveries
- Security considerations and best practices"
```

### MEMORY RETRIEVAL PROMPT
```
"Retrieve and apply stored knowledge:
- Load user coding preferences and standards
- Apply learned error prevention patterns
- Use successful code templates and structures
- Reference quality improvement histories
- Apply project-specific customizations
- Utilize efficiency optimization techniques
- Follow established security guidelines"
```

### MEMORY SYNC PROMPT
```
"Maintain Remember MCP synchronization:
- Sync after each completed batch
- Update progress and quality metrics
- Store new learnings and improvements
- Document successful patterns and solutions
- Record efficiency optimization results
- Maintain security and privacy compliance records
- Ensure knowledge persistence across sessions"
```

---

## 🔧 TOOL-SPECIFIC PROMPTS

### AUDIT SCRIPT PROMPT
```
"When running audit_codebase.py:
- Scan for all corruption indicators systematically
- Report file regeneration recommendations
- Track quality improvement trends over time
- Identify recurring patterns needing attention
- Suggest proactive prevention measures
- Update Remember MCP with audit results
- Provide actionable remediation steps"
```

### PN532 SCRIPT PROMPT
```
"When creating/running PN532 terminal scripts:
- Ensure minimal, focused functionality
- Avoid infinite loops or blocking operations
- Provide clear success/failure feedback
- Support standard hardware configurations
- Include performance and reliability metrics
- Validate APDU responses against expectations
- Store test results in Remember MCP"
```

### BUILD SYSTEM PROMPT
```
"When working with Gradle build system:
- Maintain clean, efficient build configurations
- Include all necessary dependencies
- Configure proper Android SDK targets
- Set up automated quality checks
- Include performance optimization flags
- Document build requirements clearly
- Ensure reproducible build processes"
```

---

*These prompts are optimized for maximum efficiency while leveraging Remember MCP for persistent learning and improvement. Each prompt focuses on getting clean, working results quickly while building institutional memory for continuous optimization.*