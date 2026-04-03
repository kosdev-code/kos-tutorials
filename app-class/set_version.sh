#!/bin/bash

set -e -o pipefail

THIS_SCRIPT=$(realpath "$0")
THIS_SCRIPT_DIR=$(dirname "$THIS_SCRIPT")

for d in "$THIS_SCRIPT_DIR"/*/; do
  echo "Updating Maven version in $d"
  pushd "$d"

  mvn versions:set -DnewVersion="${KOSBUILD_VERSION}" -DgenerateBackupPoms=false

  popd
done
