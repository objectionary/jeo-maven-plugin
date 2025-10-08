# Without POM Integration Test

Integration test that attempts to transform a simple project without a POM file.
Here, we disassemble a simple Java class file, and reassemble it back to a class
file.

The purpose of this test is to verify that the jeo-maven-plugin can be used in a
project without a POM file. Moreover, in this particular test we don't specify
`disassemble` `outputDir` parameter, so the plugin should use the default value,
which is the directory where the command is executed.

To run only this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=without-pom-same-dir -DskipTests
```
