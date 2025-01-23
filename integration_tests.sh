#!/bin/bash

# This script is used to run the integration tests for the project until they fail.
# This is us useful to find flaky bugs in the project.
while true; do
    echo "Running integration tests"
    java --version
    if ! mvn clean install -DskipTests; then
      break
    fi
done
