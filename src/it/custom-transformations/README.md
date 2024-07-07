# Custom Transformations Integration Test

Integration test that tries to transform many different java programs into XMIR
and back. The integration test also checks that program can run successfully and
the plugin didn't break anything. If you need to run only this test, use the
following command:

```shell
mvn clean integration-test -Dinvoker.test=custom-transformations -DskipTests
```