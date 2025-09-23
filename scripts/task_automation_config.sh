#!/bin/bash
# Task-specific automation config for safe file operations
# This ensures safe scripts are ALWAYS used for specific tasks

# Task 1: Fix File Corruption - always use safe_file_writer.sh
alias fix_corruption="./safe_file_writer.sh"
alias regenerate_file="./ai_safe_operations.sh emergency_regenerate"

# Task 2: Fix Data Models - always use ai_safe_operations.sh  
alias fix_data_model="./ai_safe_operations.sh create_code_file"
alias create_kotlin_class="./ai_safe_operations.sh create_kotlin_class"

# Task 3: Fix UI Components - always use safe operations
alias fix_ui_component="./ai_safe_operations.sh create_code_file"
alias fix_fragment="./ai_safe_operations.sh create_code_file"

# Task 4: Fix XML Resources - always use safe operations
alias fix_xml_resource="./safe_file_writer.sh"
alias create_layout="./safe_file_writer.sh"

# Task 5: Test and Build - always use validation
alias test_build="./ai_safe_operations.sh validate_file"
alias pre_build_check="./corruption_detector.py"

# Export functions for global use
export -f fix_corruption fix_data_model fix_ui_component fix_xml_resource test_build

echo "🛡️ Task automation configured - safe operations enforced!"
