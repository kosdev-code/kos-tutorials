#!/bin/bash
set -e -o pipefail -u

pushd thermostat/ui
./quickbuild.sh
popd

mvn install --no-snapshot-updates -DskipTests -T4