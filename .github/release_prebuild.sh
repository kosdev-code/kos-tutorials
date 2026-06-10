#!/bin/bash
# .github/prebuild.sh

set -e -o pipefail
KOSBUILD_VERSION="1.9.0"

if [ -z "${KOSBUILD_VERSION}" ]; then
  echo "ERROR: KOSBUILD_VERSION must be defined for release builds."
  exit 1
fi

echo "Updating project versions to: ${KOSBUILD_VERSION}"

# 1. Update Maven versions if you have a root pom.xml
if [ -f "pom.xml" ]; then
    mvn versions:set -DnewVersion="${KOSBUILD_VERSION}" -DgenerateBackupPoms=false
else
    # 2. Otherwise, update sub-modules manually if they are independent
    TARGET_DIRS=("rack/app" "thermostat/java" "arduino/java" "app-class/system-app")
    for DIR in "${TARGET_DIRS[@]}"; do
        if [ -d "$DIR" ] && [ -f "$DIR/pom.xml" ]; then
            echo "Updating Maven version in $DIR"
            (cd "$DIR" && mvn versions:set -DnewVersion="${KOSBUILD_VERSION}" -DgenerateBackupPoms=false)
        fi
    done
fi

# 3. Update any UI/Node packages if they exist
if [ -d "ui" ] && [ -f "ui/package.json" ]; then
    echo "Updating UI version..."
    cd ui && npm version "${KOSBUILD_VERSION}" --no-git-tag-version && cd ..
fi

exit 0