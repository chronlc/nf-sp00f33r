#!/bin/bash

# 💀 PN532 RECONNECT SCecho
echo "🔍 Checking for existing rfcomm connections..."
RFCOMM_STATUS=$(sudo rfcomm show ${RFCOMM_PORT} 2>/dev/null | grep -v "Usage" | grep -v "Commands" | head -5)
if [ ! -z "$RFCOMM_STATUS" ] && [ "$RFCOMM_STATUS" != "Can't get device info: No such device" ]; then
    echo "⚡ Found existing connection on rfcomm${RFCOMM_PORT}, releasing..."
    echo "$RFCOMM_STATUS"
    sudo rfcomm release ${RFCOMM_PORT} 2>/dev/null || true
    sleep 1
    echo "✅ Released existing connection"
else
    echo "✅ No existing connections found"
fi

echo
echo "📞 Starting rfcomm connect (this may take a few seconds)..."
echo "� Output will be logged to ${LOG_FILE}"

# Clear the log file first
> ${LOG_FILE}

# Start rfcomm connect and log output
sudo rfcomm connect ${RFCOMM_PORT} ${PN532_MAC} >> ${LOG_FILE} 2>&1 &‍☠️ ELITE BLUETOOTH RFCOMM CONNECTION 🏴‍☠️

echo "💀 PN532 RECONNECT SEQUENCE 💀"
echo "🏴‍☠️ Elite h4x0r PN532 connection script 🏴‍☠️"
echo

# HC-06 PN532 Module Configuration
PN532_MAC="00:14:03:05:5C:CB"
RFCOMM_PORT="1"
DEVICE_PATH="/dev/rfcomm${RFCOMM_PORT}"
LOG_FILE="/tmp/pn532_connect.log"

echo "🔐 Getting sudo access..."
sudo ls -al > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Sudo access granted"
else
    echo "❌ Sudo access failed"
    exit 1
fi

echo
echo "🔍 Checking for existing rfcomm processes..."
EXISTING_PROCS=$(ps aux | grep "rfcomm connect" | grep -v grep | wc -l)
if [ $EXISTING_PROCS -gt 0 ]; then
    echo "⚡ Found ${EXISTING_PROCS} existing rfcomm processes, killing them..."
    sudo pkill -f "rfcomm connect"
    sleep 2
    echo "✅ Killed existing processes"
else
    echo "✅ No existing rfcomm processes found"
fi

echo
echo "� Checking for existing rfcomm connections..."
RFCOMM_STATUS=$(sudo rfcomm show 2>/dev/null)
if [ ! -z "$RFCOMM_STATUS" ]; then
    echo "⚡ Found existing connections, releasing them..."
    echo "$RFCOMM_STATUS"
    sudo rfcomm release ${RFCOMM_PORT} 2>/dev/null || true
    sleep 1
    echo "✅ Released existing connections"
else
    echo "✅ No existing connections found"
fi

echo
echo "📞 Starting rfcomm connect (this may take a few seconds)..."
echo "🔍 Output will be logged to ${LOG_FILE}"

# Start rfcomm connect and log output
sudo rfcomm connect ${RFCOMM_PORT} ${PN532_MAC} > ${LOG_FILE} 2>&1 &
CONNECT_PID=$!
echo "✅ Connection started (PID: ${CONNECT_PID})"

echo
echo "⏱️  Waiting 5 seconds for connection to establish..."
sleep 5

echo
echo "🔍 Checking connection status..."

# Check if process is still running
if ps -p ${CONNECT_PID} > /dev/null 2>&1; then
    echo "✅ Connection process still running"
    
    # Check if device exists
    if [ -e "${DEVICE_PATH}" ]; then
        echo "✅ Device ${DEVICE_PATH} exists"
        echo "🏴‍☠️ PN532 CONNECTED ON ${DEVICE_PATH}! 🏴‍☠️"
        echo
        echo "💀 Connection details:"
        echo "   MAC: ${PN532_MAC}"
        echo "   Port: ${DEVICE_PATH}"
        echo "   PID: ${CONNECT_PID}"
        echo
        echo "📞 Ready for EMV flow testing! 📞"
    else
        echo "⚠️ Process running but device not found yet, checking log..."
        if [ -f "${LOG_FILE}" ]; then
            echo "📋 Connection log:"
            cat ${LOG_FILE}
        fi
    fi
else
    echo "❌ Connection process terminated"
    if [ -f "${LOG_FILE}" ]; then
        echo "📋 Error log:"
        cat ${LOG_FILE}
        echo
        echo "🚨 Connection failed! Check the log above for details."
    fi
    exit 1
fi