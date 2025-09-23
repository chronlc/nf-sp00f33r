#!/bin/bash
# Auto-generated corruption fix script
# Run this to delete corrupted files for clean regeneration

echo '🔧 Fixing file corruption detected by AI codegen agent...'

# Issues in /home/user/DEVCoDE/mag-sp00f_v1/android-app/src/main/java/com/mag_sp00f/app/cardreading/CardReadingCallback.kt:
# Duplicate functions: ['onAttackAnalysisComplete', 'onEmvStepComplete', 'onApduReceived', 'onReadingError', 'onEmvDataReceived', 'onReadingComplete', 'onReadingStarted', 'onPartialDataReceived', 'onCardDetected', 'onReadingStatistics', 'onProgressUpdate']
echo 'Deleting corrupted file: /home/user/DEVCoDE/mag-sp00f_v1/android-app/src/main/java/com/mag_sp00f/app/cardreading/CardReadingCallback.kt'
rm -f '/home/user/DEVCoDE/mag-sp00f_v1/android-app/src/main/java/com/mag_sp00f/app/cardreading/CardReadingCallback.kt'

echo '✅ Corrupted files removed. Use atomic write pattern to regenerate:'
echo 'cat > filename.kt << \'EOF\''
echo '[complete file content]'
echo 'EOF'
