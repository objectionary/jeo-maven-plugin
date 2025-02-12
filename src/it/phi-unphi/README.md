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

## Short Explanation

This integration test comprises the following steps:

1. Disasseble bytecode of a class file. `jeo:disassemble` goal.
2. Transform the disassembled code to `phi`. `eo:xmir-to-phi` goal.
3. Transform the `phi` code back to the disassembled code. `eo:phi-to-xmir`
   goal.
4. Assemble the disassembled code. `jeo:assemble` goal.

Detailed steps and configurations can be found in the [pom.xml](pom.xml) file.
