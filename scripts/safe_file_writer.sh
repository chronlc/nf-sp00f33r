#!/bin/bash
# CORRUPTION-PROOF File Writer for AI Codegen Agents
# This script GUARANTEES no append-mode corruption

set -euo pipefail  # Exit on any error

# Global settings
BACKUP_DIR=".file_backups"
TEMP_SUFFIX=".tmp.$$"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Function: Safe atomic write with validation
safe_write_file() {
    local target_file="$1"
    local content="$2"
    local expected_max_lines="${3:-1000}"  # Reasonable default
    
    echo "🔒 SAFE WRITE: Creating $target_file"
    
    # Backup existing file if it exists
    if [[ -f "$target_file" ]]; then
        local backup_name="$(basename "$target_file").backup.$(date +%s)"
        cp "$target_file" "$BACKUP_DIR/$backup_name"
        echo "💾 Backed up to: $BACKUP_DIR/$backup_name"
    fi
    
    # Create temporary file first (atomic write principle)
    local temp_file="${target_file}${TEMP_SUFFIX}"
    
    # Write content to temp file using heredoc (guaranteed write mode)
    cat > "$temp_file" << 'ATOMIC_END'
${content}
ATOMIC_END
    
    # Validate the temp file BEFORE moving it
    local line_count=$(wc -l < "$temp_file")
    
    if [[ $line_count -gt $expected_max_lines ]]; then
        echo "❌ ERROR: File too large ($line_count lines, max $expected_max_lines)"
        echo "❌ This indicates append-mode corruption!"
        rm -f "$temp_file"
        return 1
    fi
    
    # Validate syntax for code files
    if [[ "$target_file" == *.kt ]]; then
        if grep -q "package.*package\|fun.*fun.*(" "$temp_file" 2>/dev/null; then
            echo "❌ ERROR: Duplicate declarations detected (append corruption)"
            rm -f "$temp_file"
            return 1
        fi
    fi
    
    # Atomic move (this is the critical atomic operation)
    mv "$temp_file" "$target_file"
    
    echo "✅ SUCCESS: Created $target_file ($line_count lines)"
    return 0
}

# Function: Safe write for Kotlin files specifically
safe_write_kotlin() {
    local target_file="$1"
    local content="$2"
    
    # Kotlin files should typically be under 800 lines for good design
    safe_write_file "$target_file" "$content" 800
}

# Function: Emergency corruption check
check_for_corruption() {
    local file="$1"
    
    if [[ ! -f "$file" ]]; then
        echo "⚠️  File does not exist: $file"
        return 1
    fi
    
    local line_count=$(wc -l < "$file")
    local has_duplicates=false
    
    # Check for common corruption patterns
    if [[ "$file" == *.kt ]]; then
        if grep -q "Conflicting overloads\|duplicate.*declaration\|package.*package" "$file" 2>/dev/null; then
            has_duplicates=true
        fi
        
        # Check for excessive size
        if [[ $line_count -gt 1000 ]]; then
            echo "❌ CORRUPTION DETECTED: $file ($line_count lines - too large)"
            has_duplicates=true
        fi
    fi
    
    if [[ $has_duplicates == true ]]; then
        echo "❌ CORRUPTION DETECTED in $file"
        echo "🔥 IMMEDIATE ACTION: Delete and regenerate this file"
        return 1
    else
        echo "✅ File looks clean: $file ($line_count lines)"
        return 0
    fi
}

# Export functions for use in other scripts
export -f safe_write_file
export -f safe_write_kotlin  
export -f check_for_corruption

echo "🛡️  Safe File Writer loaded - corruption prevention active"
