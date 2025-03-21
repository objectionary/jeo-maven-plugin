# JNA Usage Test

This integration test was added in order to mitigate the problem
with JNA usage: https://github.com/objectionary/hone-maven-plugin/issues/58

To run this test exclusively, execute the command below:

```shell
mvn clean integration-test -Dinvoker.test=jna -DskipTests
```
