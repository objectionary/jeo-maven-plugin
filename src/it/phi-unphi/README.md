# "Phi" and "Unphi" Integration Test

This test resembles
the [hone-maven-plugin](https://github.com/objectionary/hone-maven-plugin/tree/master)
integration test that can be
found [here](https://github.com/objectionary/hone-maven-plugin/tree/master/src/it/simple).
When it is needed, I will update this test according
to the changes made in the hone-maven-plugin integration test.

To run this test exclusively, execute the command below:

```shell
mvn clean integration-test -Dinvoker.test=phi-unphi -DskipTests
```
