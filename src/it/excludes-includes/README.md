# Excludes and Includes Integration Test

This integration test verifies the functionality of the `excludes` and
`includes`options in the `jeo-maven-plugin`. It ensures that the plugin
correctly chooses Java class files (`.class`) based on the specified patterns.

If you need to run only this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=excludes-includes -DskipTests
```
