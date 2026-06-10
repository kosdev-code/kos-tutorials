#!/bin/bash
set -e -o pipefail -u
mvn install --no-snapshot-updates -T4
