# Scala 2 Integration Test

Integration test that attempts to transform a simple Scala 2 program.
The integration test verifies that the program can run successfully, and the
jeo-maven-plugin doesn't break anything.
If you only need to run this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=scala2 -DskipTests
```
