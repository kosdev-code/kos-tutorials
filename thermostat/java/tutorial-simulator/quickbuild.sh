#!/bin/bash
set -e -o pipefail -u

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

mvn -f "$SCRIPT_DIR/pom.xml" install --no-snapshot-updates -T8