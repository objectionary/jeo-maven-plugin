# Integration test for the eo-to-bytecode goal

This integration test checks the correct transformation of already
generated XMIR files to bytecode. In other words, it checks the
`assemble` goal of the `jeo-maven-plugin` plugin.

If you need to run only this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=eo-to-bytecode -DskipTests
```