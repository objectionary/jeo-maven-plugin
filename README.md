# jeo-maven-plugin

[![logo](https://www.objectionary.com/cactus-100.svg)](https://www.objectionary.com)

[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/jeo-maven-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/jeo-maven-plugin)
[![Javadoc](https://www.javadoc.io/badge/org.eolang/jeo-maven-plugin.svg)](https://www.javadoc.io/doc/org.eolang/jeo-maven-plugin)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE.txt)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/jeo-maven-plugin?branch=master&label=Hits-of-Code)](https://hitsofcode.com/github/objectionary/jeo-maven-plugin/view?branch=master&label=Hits-of-Code)
![Lines of code](https://sloc.xyz/github/objectionary/jeo-maven-plugin)
[![codecov](https://codecov.io/gh/objectionary/jeo-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/jeo-maven-plugin)

**jeo-maven-plugin** is a Maven plugin dedicated to disassembling Java bytecode.
The process involves translating the Java bytecode into
the [EO](https://github.com/objectionary/eo)
programming language.
The plugin also provides the ability to assemble EO back into Java bytecode.

## How to use

You need at least **Maven 3.1+** and **Java 11+** to run the plugin.
(Actually, the plugin requires **Java 8+**, but since the main
dependency [eo](https://github.com/objectionary/eo) requires **Java 11**,
we are obligated to use it as well.)

The plugin can convert compiled `.class` files into EO by using
the `disassemble` goal.
The `assemble` goal can convert EO back into bytecode.
The default phase for the plugin
is [process-classes](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#default-lifecycle).

To optimize Java bytecode, you need to use both goals in the following order:

* `disassemble` creates EO files in the `target/generated-sources`
  directory.
* Apply your optimizations to the EO files
  in the `target/generated-sources` directory.
* `assemble` scans the `target/generated-sources` directory for EO
  files and converts them back to Java bytecode.

More details about plugin usage can be found in our
[Maven site](https://objectionary.github.io/jeo-maven-plugin).

## Invoke the plugin from the command line

You can run the plugin directly from the command line using the following
commands:

```bash
mvn jeo:disassemble
```

or

```bash
mvn jeo:assemble
```

## Invoke the plugin from the Maven lifecycle

You can run the plugin from the Maven lifecycle by adding the following
configuration to your `pom.xml` file:

```xml

<build>
  <plugins>
    <plugin>
      <groupId>org.eolang</groupId>
      <artifactId>jeo-maven-plugin</artifactId>
      <version>0.14.12</version>
      <executions>
        <execution>
          <id>bytecode-to-eo</id>
          <phase>process-classes</phase>
          <goals>
            <goal>disassemble</goal>
          </goals>
        </execution>
        <execution>
          <id>eo-to-bytecode</id>
          <phase>process-classes</phase>
          <goals>
            <goal>assemble</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

### Exclude debug information

In order to exclude debug information in the generated EO files, you can set
the `short` option.

```xml

<configuration>
  <mode>short</mode>
</configuration>
```

This option will exclude line numbers and local variable names,
together with their corresponding labels. The default mode is `debug`.

### Disable bytecode verification

Each time the plugin converts EO back to bytecode, it verifies it. If the
verification fails, the build also fails. You can disable this verification by
setting the `skipVerification` parameter to `true`:

```xml

<configuration>
  <skipVerification>true</skipVerification>
</configuration>
```

At times, it might be beneficial to generate intentionally flawed bytecode.

### Enable XMIR Verification

After generating XMIR or before the `assemble` goal,
you might need to check its correctness.
We do it by using [objectionary/lints](https://github.com/objectionary/lints)
repository.
By default, the plugin does not run lints.
To enable them, you need to set `xmirVerification` to `true`:

```xml

<configuration>
  <xmirVerification>true</xmirVerification>
</configuration>
```

### Extend Logging Output

You can extend the logging output of the plugin by setting the `debug` parameter
to `true`:

```xml
<configuration>
  <debug>true</debug>
</configuration>
```

This will enable more detailed logging output, which can be useful for debugging
purposes.

Without the `debug` parameter:

```txt
1/3 WithoutPackage.xmir (9Kb) disassembled in 1s
```

With the `debug` parameter:

```txt
1/3 .../WithoutPackage.class disassembled to .../WithoutPackage.xmir (9Kb) in 1s
```

## Run Without a POM File

You can run the plugin in a project that does not contain a `pom.xml` file.  
For example, to disassemble compiled classes, use:

```bash
mvn org.eolang:jeo-maven-plugin:${project.version}:disassemble \
  -Djeo.disassemble.sourcesDir=input \
  -Djeo.disassemble.outputDir=xmir
```

The full list of available parameters is documented
in [DisassembleMojo.java](src/main/java/org/eolang/jeo/DisassembleMojo.java).

Similarly, you can run the `assemble` goal:

```bash
mvn org.eolang:jeo-maven-plugin:${project.version}:assemble \
    -Djeo.assemble.sourcesDir=xmir \
    -Djeo.assemble.outputDir=output
```

(Again, the full list of available parameters is documented
in [AssembleMojo.java](src/main/java/org/eolang/jeo/AssembleMojo.java).)
Note that the parameters for assembling use the `jeo.assemble` prefix,
while the parameters for disassembling use the `jeo.disassemble` prefix.

## Disassembling Example

The plugin can transform Java bytecode into EO and back. Usually, the plugin
transforms each bytecode class file into a separate EO file, maintaining a
one-to-one relationship. If the Java class has the name `Application.class`, the
EO
file will have the name `Application.xmir`.

For example, consider the following Java class:

```java
package org.eolang.jeo;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

with the following bytecode representation:

```bytecode
{
  public org.eolang.jeo.Application();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lorg/eolang/jeo/Application;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #13                 // String Hello, World!
         5: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 5: 0
        line 6: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  args   [Ljava/lang/String;
}
```

After running the `jeo:disassemble` goal, the plugin will generate the
following EO:

```eo
+package j$org.j$eolang.j$jeo

jeo.class > j$Application
  jeo.int > version
    00-00-00-00-00-00-00-34
  jeo.int > access
    00-00-00-00-00-00-00-21
  "java/lang/Object" > supername
  jeo.seq.of0 > interfaces-696199744
  jeo.method > j$object@init@-%28%29V
    jeo.int
      00-00-00-00-00-00-00-01
    "()V"
    ""
    jeo.seq.of0 > aca3ae21-f727-41cc-b8df-791e7031904e-1635871459
    jeo.maxs
      jeo.int
        00-00-00-00-00-00-00-01
      jeo.int
        00-00-00-00-00-00-00-01
    jeo.params
    jeo.seq.of0 > annotations-1586121642
    jeo.seq.of3 > body-1998918745
      jeo.opcode.aload
        jeo.int
          00-00-00-00-00-00-00-19
        jeo.int
          00-00-00-00-00-00-00-00
      jeo.opcode.invokespecial
        jeo.int
          00-00-00-00-00-00-00-B7
        "java/lang/Object"
        "<init>"
        "()V"
        jeo.bool
          00-
      jeo.opcode.return
        jeo.int
          00-00-00-00-00-00-00-B1
    jeo.seq.of0 > trycatchblocks-object@init@-1849817803
    jeo.seq.of0 > local-variable-table-1794934203
  jeo.method > j$main-%28%5BLjava%2Flang%2FString%3B%29V
    jeo.int
      00-00-00-00-00-00-00-09
    "([Ljava/lang/String;)V"
    ""
    jeo.seq.of0 > f6c6c536-0b96-4357-bbba-d2966968b266-658978372
    jeo.maxs
      jeo.int
        00-00-00-00-00-00-00-02
      jeo.int
        00-00-00-00-00-00-00-01
    jeo.params
      jeo.param > param-%5BLjava%2Flang%2FString%3B-arg0-0-0-1064778491
        jeo.seq.of0 > param-annotations-0-666850426
    jeo.seq.of0 > annotations-639467587
    jeo.seq.of4 > body-1023152593
      jeo.opcode.getstatic
        jeo.int
          00-00-00-00-00-00-00-B2
        "java/lang/System"
        "out"
        "Ljava/io/PrintStream;"
      jeo.opcode.ldc
        jeo.int
          00-00-00-00-00-00-00-12
        "Hello, World!"
      jeo.opcode.invokevirtual
        jeo.int
          00-00-00-00-00-00-00-B6
        "java/io/PrintStream"
        "println"
        "(Ljava/lang/String;)V"
        jeo.bool
          00-
      jeo.opcode.return
        jeo.int
          00-00-00-00-00-00-00-B1
    jeo.seq.of0 > trycatchblocks-main-727934521
    jeo.seq.of0 > local-variable-table-2134319645
  jeo.seq.of0 > annotations-785801830
  jeo.seq.of0 > attributes-744976367
```

As you can see, there are many EO objects that represent the Java bytecode
primitives, like `jeo.opcode`, `jeo.int`, `jeo.method`, etc.

## Full List of Jeo Objects

During disassembly, the `jeo-maven-plugin` creates a set of objects
representing bytecode primitives.
These objects provide a way to handle various aspects of Java bytecode.
Below is the full list of these objects, grouped by category:

---

### Bytecode Instructions

* **`jeo.opcode.*`**
  Represents a single bytecode instruction like `aload_0`, `iconst_0`, etc.

### Classes, Methods, and Fields

* **`jeo.class`**
  Represents a Java class.
* **`jeo.method`**
  Represents a Java method.
* **`jeo.field`**
  Represents a Java field.
* **`jeo.params`**
  Represents method parameters.
* **`jeo.param`**
  Represents a single method parameter.
* **`jeo.maxs`**
  Represents the maximum stack and local variable sizes.

### Primitive Values

* **`jeo.int`** - Represents an integer value.
* **`jeo.bool`** - Represents a boolean value.
* **`jeo.string`** - Represents a string value.
* **`jeo.float`** - Represents a float value.
* **`jeo.double`** - Represents a double value.
* **`jeo.long`** - Represents a long value.
* **`jeo.char`** - Represents a char value.
* **`jeo.short`** - Represents a short value.
* **`jeo.byte`** - Represents a byte value.
* **`jeo.bytes`** - Represents a byte array.

### Collections and Complex Types

* **`jeo.nullable`**
  Represents an object that can be `null`.
* **`jeo.array`**
  Represents an array of objects.
* **`jeo.type`**
  Represents a Java type.
* **`jeo.seq.*`**
  Represents a sequence of objects with a specific size,
  like `jeo.seq.of0`, `jeo.seq.of1`, etc.

### Annotations and Metadata

* **`jeo.annotation`**
  Represents a Java annotation.
* **`jeo.annotation-property`**
  Represents a single annotation element.
* **`jeo.annotation-default-value`**
  Represents a default value of a Java interface method or annotation property.
* **`jeo.inner-class`**
  Represents a Java inner class annotation property.

### Local Variables and Control Flow

* **`jeo.local-variable`**
  Represents a local variable entry.
* **`jeo.try-catch`**
  Represents a try-catch block.

### Labels, Handles, Frames

* **`jeo.label`**
  Represents a Java label.
* **`jeo.handle`**
  Represents a Java method handle.
* **`jeo.frame`**
  Represents a stack frame.

## How to Build the Plugin

To build the plugin from the source code, you need to clone the repository and
run the following command:

```bash
mvn clean install -Pqulice,long
```

Pay attention to the `qulice` profile, which activates the static analysis
tools. The `long` profile is optional and runs the full test suite, including
long-running integration tests.

## How to Run Benchmarks

To run the benchmarks, you need to execute the following command:

```bash
mvn clean verify -Pbenchmark
```

Before running the benchmarks, make sure you have the `.env` file in the root
directory of the project. The file should contain the following environment
variables:

```bash
PROFILER=/path/to/async-profiler/profiler.sh
```

## How to Contribute

Fork the repository, make changes, then send us
a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request, please run the full Maven build:

```bash
mvn clean install -Pqulice
```

You will need [Maven 3.3+](https://maven.apache.org) and Java 11+ installed.
