# This script is used to run the integration tests for the project until they fail.
# This is us useful to find flaky bugs in the project.
while [ true  ]; do
    echo "Running integration tests"
    java --version
#    mvn clean install -DskipTests
    mvn clean integration-test -Dinvoker.test=generics -DskipTests
    if [[ "$?" -ne 0 ]]; then
      break
    fi
done