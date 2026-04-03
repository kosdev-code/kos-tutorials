#!/bin/bash
EXIT_CODE=0

# List all your targets here (directories or specific scripts)
TARGETS=(
    "rack"
    "thermostat/java"
    "arduino/java"
    "app-class"
)

# Every target has a quickbuild.sh script that knows how to build itself
for ITEM in "${TARGETS[@]}"; do
    echo "--- Building: $ITEM ---"

    "./$ITEM/quickbuild.sh" || EXIT_CODE=1
done

exit $EXIT_CODE
