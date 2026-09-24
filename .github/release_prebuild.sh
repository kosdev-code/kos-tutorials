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

# Update the .kos.json version in any UI projects (the UI kab version is read from .kos.json)
echo "Updating UI .kos.json versions to: ${KOSBUILD_VERSION}"
while IFS= read -r kos; do
    echo "Updating .kos.json version in ${kos}..."
    node -e "
        const fs = require('fs');
        const file = '${kos}';
        const json = JSON.parse(fs.readFileSync(file, 'utf8'));
        if (!json.version) process.exit(0);
        json.version = '${KOSBUILD_VERSION}';
        fs.writeFileSync(file, JSON.stringify(json, null, 2) + '\n');
    "
done < <(find . -path "*/ui/*/.kos.json" -not -path "*/node_modules/*" -not -path "*/dist/*")

exit 0