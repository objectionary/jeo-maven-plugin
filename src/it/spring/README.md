# Spring Fat Jar Integration Test

Integration test that checks the correct transformation of an application
written with using of the Springs Framework. This integration test starts the
application with several beans and prints results to the console.

If you need to run only this test, use the following command:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=spring -DskipTests
```
