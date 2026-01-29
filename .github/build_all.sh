#!/bin/bash
# Initialize a flag
EXIT_CODE=0

printf "\n\n\n\nBuilding rack tutorial\n\n\n\n\n"
pushd ./rack/java
bash ./quickbuild.sh || EXIT_CODE=1
popd

printf "\n\n\n\nBuilding Thermostat tutorial\n\n\n\n\n"
pushd ./thermostat/java/tutorial-simulator
bash ./quickbuild.sh || EXIT_CODE=1
popd
pushd ./thermostat/java/tutorial-thermostat
bash ./quickbuild.sh || EXIT_CODE=1
popd

printf "\n\n\n\nBuilding arduino tutorial\n\n\n\n"
pushd ./arduino/java
bash ./quickbuild.sh || EXIT_CODE=1
popd

echo "Building all projects"
printf "\n\n\n\nBuilding adapter tutorial\n\n\n\n"
pushd ./tutorial-adapter/java 
bash ./quickbuild.sh || EXIT_CODE=1
popd

printf "\n\n\n\nBuilding adapter-documentation\n\n\n\n\n"
pushd ./adapter-documentation/java
bash ./quickbuild.sh || EXIT_CODE=1
popd

exit $EXIT_CODE
