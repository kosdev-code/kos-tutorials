#!/bin/bash

echo "Building all projects"
printf "\n\n\n\nBuilding adapter tutorial\n\n\n\n"
pushd ./tutorial-adapter/
mvn install -T8

popd

printf "\n\n\n\nBuilding arduino tutorial\n\n\n\n"
pushd ./arduino/
mvn install -T8

popd

printf "\n\n\n\nBuilding rack tutorial\n\n\n\n\n"
pushd ./rack/
mvn install -T8
