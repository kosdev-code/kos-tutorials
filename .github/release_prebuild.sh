#! /bin/bash
THIS_SCRIPT=$(realpath "$0")
THIS_SCRIPT_DIR=$(dirname "$THIS_SCRIPT")
TOP_DIR="${THIS_SCRIPT_DIR}/.."
set -e -o pipefail

if [ -z "${KOSBUILD_VERSION}" ]; then
  echo "check release version is for release builds, KOSBUILD_VERSION must be defined"
  exit 1
fi

cd "${TOP_DIR}"
mvn versions:set -DnewVersion="${KOSBUILD_VERSION}" -DgenerateBackupPoms=false

exit 0





#!/bin/bash
# .github/prebuild.sh

set -e -o pipefail

# Check to make sure the KOSBUILD_VERSION environment variable has been set
if [ -z "${KOSBUILD_VERSION}" ]; then
  echo "ERROR: KOSBUILD_VERSION must be defined for release builds."
  exit 1
fi

# Update the versions across all the projects
echo "Updating project versions to: ${KOSBUILD_VERSION}"
cd "${TOP_DIR}"
mvn versions:set -DnewVersion="${KOSBUILD_VERSION}" -DgenerateBackupPoms=false

# 3. Update any UI/Node packages in any of the tutorials if they exist
while IFS= read -r pkg; do
    ui_dir=$(dirname "$pkg")
    echo "Updating UI version in ${ui_dir}..."
    cd "${TOP_DIR}/${ui_dir}" && npm version "${KOSBUILD_VERSION}" --no-git-tag-version && cd "${TOP_DIR}"
done < <(find . -path "*/ui/package.json" -not -path "*/node_modules/*")

exit 0