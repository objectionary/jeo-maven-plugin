# Without POM Integration Test

Integration test that attempts to transform a simple project without a POM file.
Here, we disassemble a simple Java class file, and reassemble it back to a class
file.

The purpose of this test is to verify that the jeo-maven-plugin can be used in a
project without a POM file.

To run only this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=without-pom -DskipTests
```
