#!/bin/bash
# Intelligent Task Dispatcher - Routes tasks to appropriate safe operations
# This prevents corruption by enforcing proper protocols

source ./task_automation_config.sh
source ./ai_safe_operations.sh

# Task dispatcher function
dispatch_task() {
    local task_type="$1"
    local target_file="$2" 
    local content="$3"
    
    case "$task_type" in
        "fix_emv_parser")
            echo "🔥 Dispatching: EMV Parser Fix (RFIDIOt+Proxmark3)"
            create_code_file "$target_file" "$content" 300
            ;;
        "fix_data_model")
            echo "💾 Dispatching: Data Model Fix (Mutable)"
            create_code_file "$target_file" "$content" 100
            ;;
        "fix_ui_fragment")
            echo "🎨 Dispatching: UI Fragment Fix"
            create_code_file "$target_file" "$content" 400
            ;;
        "fix_xml_layout")
            echo "📐 Dispatching: XML Layout Fix"
            safe_write_file "$target_file" "$content" 200
            ;;
        "fix_adapter")
            echo "🔗 Dispatching: Adapter Fix"
            create_code_file "$target_file" "$content" 150
            ;;
        "validate_build")
            echo "✅ Dispatching: Build Validation"
            validate_file "$target_file"
            ;;
        *)
            echo "❌ Unknown task type: $task_type"
            echo "Available: fix_emv_parser, fix_data_model, fix_ui_fragment, fix_xml_layout, fix_adapter, validate_build"
            return 1
            ;;
    esac
}

# Export dispatcher function
export -f dispatch_task

echo "🎯 Task Dispatcher ready - routing to safe operations!"
