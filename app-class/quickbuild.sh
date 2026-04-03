#!/bin/bash
set -e -o pipefail -u
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Building thermostat..."
"$SCRIPT_DIR/system-app/quickbuild.sh"

echo "Building thermostat simulator..."
"$SCRIPT_DIR/device-management/quickbuild.sh"
