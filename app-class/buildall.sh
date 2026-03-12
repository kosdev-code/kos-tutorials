#!/bin/bash

set -e -o pipefail

for d in */; do
  pushd "$d"
  ./quickbuild.sh
  popd
done
