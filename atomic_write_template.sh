#!/bin/bash
# Atomic File Write Template
# Use this pattern to prevent append-mode corruption

# Example usage:
create_kotlin_file() {
    local filepath="$1"
    local content="$2"
    
    cat > "$filepath" << 'EOF'
${content}
EOF
    
    echo "✅ Created clean file: $filepath ($(wc -l < "$filepath") lines)"
}

# Verify file was created correctly
verify_file() {
    local filepath="$1"
    local expected_lines="$2"
    
    local actual_lines=$(wc -l < "$filepath")
    if [ "$actual_lines" -gt $((expected_lines * 3)) ]; then
        echo "❌ File corruption detected: $actual_lines lines (expected ~$expected_lines)"
        return 1
    else
        echo "✅ File looks clean: $actual_lines lines"
        return 0
    fi
}
