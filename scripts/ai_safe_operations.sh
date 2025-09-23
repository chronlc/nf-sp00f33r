#!/bin/bash
# AI-SAFE File Operations - Zero Corruption Guarantee
# Use ONLY these functions for file creation/modification

source "$(dirname "$0")/safe_file_writer.sh"

# Function: Create Kotlin class file (corruption-proof)
create_kotlin_class() {
    local class_name="$1" 
    local package_name="$2"
    local filepath="$3"
    local class_content="$4"
    
    echo "🏗️  Creating Kotlin class: $class_name"
    
    local full_content="package $package_name

$class_content"
    
    safe_write_kotlin "$filepath" "$full_content"
}

# Function: Create any code file with validation
create_code_file() {
    local filepath="$1"
    local content="$2" 
    local max_lines="${3:-500}"
    
    echo "📝 Creating code file: $(basename "$filepath")"
    
    safe_write_file "$filepath" "$content" "$max_lines"
}

# Function: NEVER USE - Blocked dangerous operations
blocked_create_file() {
    echo "🚫 BLOCKED: create_file tool is BANNED due to corruption risk"
    echo "🚫 Use create_code_file with atomic write instead"
    return 1
}

blocked_append_operation() {
    echo "🚫 BLOCKED: Append operations are BANNED"
    echo "🚫 Always regenerate complete files"
    return 1
}

# Function: Validate existing file is corruption-free
validate_file() {
    local filepath="$1"
    
    if check_for_corruption "$filepath"; then
        echo "✅ File validation passed: $filepath"
        return 0
    else
        echo "❌ File validation FAILED: $filepath"
        echo "🔥 REQUIRED ACTION: Delete and regenerate this file"
        return 1
    fi
}

# Function: Emergency clean regeneration
emergency_regenerate() {
    local corrupted_file="$1"
    
    echo "🚨 EMERGENCY: Regenerating corrupted file: $corrupted_file"
    
    # Move corrupted file to quarantine
    local quarantine_name="$(basename "$corrupted_file").corrupted.$(date +%s)"
    mv "$corrupted_file" "$BACKUP_DIR/$quarantine_name"
    
    echo "🔒 Corrupted file quarantined: $BACKUP_DIR/$quarantine_name"
    echo "📝 Ready for clean regeneration of: $corrupted_file"
}

# Export all safe functions
export -f create_kotlin_class
export -f create_code_file  
export -f validate_file
export -f emergency_regenerate
export -f blocked_create_file
export -f blocked_append_operation

echo "🛡️  AI-Safe Operations loaded"
echo "🚫 Dangerous file operations are BLOCKED"
echo "✅ Only corruption-proof operations available"
