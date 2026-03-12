#!/bin/bash

set -e -o pipefail

THIS_SCRIPT=$(realpath "$0")
THIS_SCRIPT_DIR=$(dirname "$THIS_SCRIPT")

cd "$THIS_SCRIPT_DIR"

for d in */; do
  pushd "$d"
  ./quickbuild.sh
  popd
done
