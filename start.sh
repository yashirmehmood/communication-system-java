#!/bin/bash
# ==============================================
# Player Communication System Launcher
# ==============================================
# This script launches the Java Player Communication System
# using Maven. All user input is handled by Main.java.
# ----------------------------------------------

# Optional: fail immediately if any command fails
set -e

# Print a header
echo "=============================================="
echo "       Player Communication System"
echo "=============================================="
echo

# Launch the application via Maven
mvn exec:java -Dexec.mainClass="com.example.playercomm.Main"

echo
echo "Program finished."