#!/bin/bash
EXIT_CODE=0

# Helper function to safely build a module
build_module() {
    local DIR=$1
    printf "\n\n--- Checking Directory: $DIR ---\n"
    
    if [ -d "$DIR" ]; then
        pushd "$DIR" > /dev/null
        echo "Starting build in $DIR..."
        if bash ./quickbuild.sh; then
            echo "Successfully built $DIR"
        else
            echo "ERROR: Build failed in $DIR"
            EXIT_CODE=1
        fi
        popd > /dev/null
    else
        echo "SKIP/ERROR: Directory $DIR does not exist. Current path: $(pwd)"
        EXIT_CODE=1
    fi
}

# Run the builds
build_module "./rack"
build_module "./thermostat/java/tutorial-thermostat"
build_module "./thermostat/java/tutorial-simulator"
build_module "./thermostat/arduino/java"

# Exit with the cumulative exit code
exit $EXIT_CODE
