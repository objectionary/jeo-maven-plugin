# Spring Fat Jar Integration Test

Integration test that checks the correct transformation of an application
written with using of the Springs Framework. This integration test starts the
application with several beans and prints results to the console.
This is a similar integration test with the "spring" test in
the [spring](../spring) test.

The only difference is that in this test we download all the dependencies,
unpack them, transform using jeo-maven-plugin and then pack them back. In other
words, we test the fat jar transformation.

If you need to run only this test, use the following command:
```shell
mvn clean integration-test invoker:run -Dinvoker.test=spring-fat -DskipTests
```
