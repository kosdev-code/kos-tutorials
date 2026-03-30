#!/bin/bash
EXIT_CODE=0

# List all your targets here (directories or specific scripts)
TARGETS=(
    "./rack"
    "./thermostat/java/tutorial-thermostat"
    "./thermostat/java/tutorial-simulator"
    "./arduino/java"
    "./app-class/buildall.sh"
)

for ITEM in "${TARGETS[@]}"; do
    echo "--- Building: $ITEM ---"

    # 1. If it's a directory, try to run quickbuild inside it
    if [ -d "$ITEM" ]; then
        (cd "$ITEM" && bash quickbuild.sh) || EXIT_CODE=1

    # 2. If it's a file, just run it
    elif [ -f "$ITEM" ]; then
        bash "$ITEM" || EXIT_CODE=1

    # 3. If neither, it's missing
    else
        echo "ERROR: $ITEM not found"
        EXIT_CODE=1
    fi
done

exit $EXIT_CODE
