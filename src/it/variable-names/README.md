# Variable Names

In this integration test, we verify that all the transformations preserve
variable names. To run this test, execute the command below:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=variable-names -DskipTests
```