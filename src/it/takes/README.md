# Takes Integration Test

Integration test that checks correct transformation simple Java application
written with using Takes Framework. This application starts HTTP server,
sends a single HTTP request and prints response to the console.
If you need to run only this test, use the following command:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=takes -DskipTests
```