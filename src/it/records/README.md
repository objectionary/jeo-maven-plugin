# Records Integration Test

Integration test that attempts to transform a simple Java program with
records usage.
The integration test verifies that the program can run successfully, and the
jeo-maven-plugin doesn't break anything.
If you only need to run this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=records -DskipTests
```
