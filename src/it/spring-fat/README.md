# Spring Fat Jar Integration Test

This integration test verifies the accurate transformation of applications
developed with the Spring Framework. It initiates the application with various
beans and outputs the results to the console, functioning similarly to the
integration test found in the [spring](../spring) directory.

The distinctive aspect of this test is its comprehensive process of handling
dependencies: it downloads all required dependencies, unpacks them, applies
transformations via the `jeo-maven-plugin`, and subsequently repackages them.

The process is as follows:

1. **Download all dependencies**
2. **Unpack them**
3. **Disassemble**: Apply transformation via the `jeo-maven-plugin:dissassemble`
   goal for all the
   unpacked dependencies
4. **Assemble**: Apply back transformation via the `jeo-maven-plugin:assemble`
   goal
5. **Build the application**: Compiles the transformed application into a fat
   jar.

To exclusively run this test, execute the command below:

```shell
mvn clean integration-test -Dinvoker.test=spring-fat -DskipTests
```

## The First Results


Here is the summary of the first results of the `spring-fat` integration test:

- The application starts and runs successfully.
- The average test time is approximately **130 seconds**.
- The total number of classes is **5844**.
- The Disassembly phase takes approximately **1 minute**.
- The Assembly phase takes approximately **21 seconds**.

## Developer Notes

### Excluded from the default build pipeline

This test remains rather long and has not yet been optimized. As a result, it
is excluded from the default build pipeline. To run this test alongside all
others, you need to activate the `long` Maven profile. Use the command below:

```bash
mvn clean install -Plong
```

### Bytecode verification

Some Spring Boot components were compiled with optional dependencies, and these
dependencies are not present in the corresponding POM files. Therefore, we
cannot find these dependencies during the test. However, the current
implementation of bytecode verification requires that all classes are loaded by
the `ClassLoader` before verification, which is not possible in this case. As a
result, we skip bytecode verification for this test.

```xml

<skipVerification>true</skipVerification>
```

You can read more about this problem in [pom.xml](pom.xml) in
the `jeo-maven-plugin` configuration.


