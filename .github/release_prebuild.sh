#!/bin/bash
set -e -o pipefail

# Check to make sure the KOSBUILD_VERSION environment variable has been set
if [ -z "${KOSBUILD_VERSION}" ]; then
  echo "ERROR: KOSBUILD_VERSION must be defined for release builds."
  exit 1
fi

# Update the versions across all the projects
cd "${TOP_DIR}"
echo "Updating Java project versions to: ${KOSBUILD_VERSION}"
mvn versions:set -DnewVersion="${KOSBUILD_VERSION}" -DgenerateBackupPoms=false

# Update any UI/Node packages in any of the tutorials if they exist
echo "Updating UI project versions to: ${KOSBUILD_VERSION}"
while IFS= read -r pkg; do
    ui_dir=$(dirname "$pkg")
    echo "Updating UI version in ${ui_dir}..."
    cd "${TOP_DIR}/${ui_dir}" && npm version "${KOSBUILD_VERSION}" --no-git-tag-version && cd "${TOP_DIR}"
done < <(find . -path "*/ui/package.json" -not -path "*/node_modules/*")

exit 0