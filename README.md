<img alt="logo" src="https://www.objectionary.com/cactus.svg" height="100px" />

[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/jeo-maven-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/jeo-maven-plugin)
[![Javadoc](http://www.javadoc.io/badge/org.eolang/jeo-maven-plugin.svg)](http://www.javadoc.io/doc/org.eolang/jeo-maven-plugin)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE.txt)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/jeo-maven-plugin?branch=master&label=Hits-of-Code)](https://hitsofcode.com/github/objectionary/jeo-maven-plugin/view?branch=master&label=Hits-of-Code)
![Lines of code](https://sloc.xyz/github/objectionary/jeo-maven-plugin)
[![codecov](https://codecov.io/gh/objectionary/jeo-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/jeo-maven-plugin)

**jeo** stands for "Java EOlang Optimizations". **jeo-maven-plugin** is a Maven
plugin dedicated to optimizing Java bytecode. The process involves translating
the Java bytecode into the [EOlang](https://github.com/objectionary/eo)
programming language. Utilizing the optimization steps provided by EOlang, the
original code undergoes an enhancement process. Upon completion, the optimized
EOlang program is translated back to Java bytecode, achieving efficient and
optimized performance.

# How to use

The plugin can be run using several approaches but for all of them you need
at least Maven 3.1.+ and Java 11+.
The plugin can convert compiled classes into EOlang by using
the `bytecode-to-eo` goal. The `eo-to-bytecode` goal can convert EOlang back
into bytecode. The default phase for the plugin
is [process-classes](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#default-lifecycle).
If you are a developer of optimizations in EOlang you probably need to
use the both goals in the following order:

* `bytecode-to-eo` create EOlang files in the `target/generated-sources`
  directory.
* Provide your optimizations are applied to the EOlang files
  in the `target/generated-sources` directory.
* `eo-to-bytecode` scans the `target/generated-sources` directory for EOlang
  files and converts them back to Java bytecode.

More details about plugin usage you can find in our
[Maven site](https://objectionary.github.io/jeo-maven-plugin).

## Invoke the plugin from the command line

You can run the plugin directly from the command line using the following
commands:

```bash
mvn jeo:bytecode-to-eo
```

or

```bash
mvn jeo:eo-to-bytecode
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
      <version>0.2.6</version>
      <executions>
        <execution>
          <id>bytecode-to-eo</id>
          <phase>process-classes</phase>
          <goals>
            <goal>bytecode-to-eo</goal>
          </goals>
        </execution>
        <execution>
          <id>eo-to-bytecode</id>
          <phase>process-classes</phase>
          <goals>
            <goal>eo-to-bytecode</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

## Transformation

The plugin can transform Java bytecode into EO and back. Usually the plugin
transforms each bytecode class file into a separate EO file. The relationship is
one-to-one. If the Java class is named`Foo.class`, the EO file will be
named `Foo.eo` (and `Foo.xmir` for XMIR representation of the EO file.)

### Classes

The fist high-level transformation is the transformation of the bytecode class
into `<program>` and  `<objects><o name = 'Foo'/></objects>` XMIR elements.
For example, the following Java class:

```java
public class Foo {
}
```

will be transformed into the following EO:

```eo
[] > j$Foo
  33 > access
  "java/lang/Object" > supername
  * > interfaces
```

`access`, `supername`, and `interfaces` are the attributes of the
class element that keep the information required for back transformation.
The `j$*` prefix is used to avoid name conflicts with EO keywords. The same
prefix is used for all EO elements generated from Java bytecode.

The XMIR representation of the EO file will be:

```xml

<program>
  <objects>
    <o name="j$Foo">
      <attribute name="access" value="33"/>
      <attribute name="supername" value="java/lang/Object"/>
      <attribute name="interfaces" value="*"/>
    </o>
  </objects>
</program>
```

### Methods

The second high-level transformation is the transformation of the bytecode
method into EO. For example, the following Java method:

```java
public class Bar {
    public void foo() {
        return;
    }
}
```

will be transformed into the following EO:

```eo
[] > j$Bar
  33 > access
  "java/lang/Object" > supername
  * > interfaces
  [] > j$foo
    1 > access
    "()V" > descriptor
    * > exceptions
    seq > @
      tuple
        opcode > RETURN-1
          177
```

Pay attention, that the method is a child of the class element and it contains
bytecode attributes license `access` (access
modifiers), `descriptor` ([method descriptor](https://stackoverflow.com/questions/7526483/what-is-the-difference-between-descriptor-and-signature)),
and `exceptions` (list of declared exceptions) along with the `seq` element that
contains the sequence of the bytecode instructions.
It worth to mention that Java constructors are treated as methods with the
name `new`. So the following Java constructor:

```java
public class Bar {
    public Bar() {
    }
}
```

will be transformed into the following EO:

```eo
[] > j$Bar
  33 > access
  "java/lang/Object" > supername
  * > interfaces
  [] > new
    1 > access
    "()V" > descriptor
    * > exceptions
    seq > @
      tuple
        // list of instructions
```

### Instructions

Each method and constructor contains a sequence of instructions. Each
instruction might be presented by several different objects: `opcode`
or `label`. For example the following Java method:

```java
public class Bar {
    public int foo(int x) {
        if (x < 0) {
            return 1;
        }
        return 2;
    }
}
```

will have the following set of instructions after compilation (`javap -v Bar`
output):

```
0: iload_1
1: ifle          6
4: iconst_1
5: ireturn
6: iconst_2
7: ireturn
```

after the transformation the content of the `bar` method in EO will look like:

```eo
seq > @
  tuple
    label
      "67b715c8-7d74-413a-9bba-f6920c8ba68b"
    opcode > ILOAD-E
       21
       1
    opcode > IFLE-F
      158
      label
        "c361c429-6c81-4b11-9b97-0cbb6e96a2f9"
    opcode > ICONST_1-10
      4
    opcode > IRETURN-11
      172
    label
      "c361c429-6c81-4b11-9b97-0cbb6e96a2f9"
    opcode > ICONST_2-12
      5
    opcode > IRETURN-13
      172
    label
      "8f341f7f-e357-4a78-b604-bcaae28e3c1f"
```

#### Opcode

From the examle above ([Methods section](#methods)) you can see that each
opcode is presented by the `opcode` object. Each `opcode` object has name,
number argument, and optional operands-arguments. For example, the `ildoad`
opcode has the following EO representation:

```eo
opcode > ILOAD-E
  21
  1
```

where `ILOAD-E` is the name of the opcode, `21` is the number of opcode from the
[java specification](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html),
and `1` is the operand-argument that in this context means "local variable with
index 1".

`ILOAD-E` is just a name of an `opcode` object. Since it is a name - we don't
rely on this during transformations, it's just for readability. So if you
perform some changes on the original `jeo` output, you can change this names as
you want (`jeo` doesn't use them during parsing.)

Also it worth mentioning that `opcode` might not have operand-arguments,
as in case with `IRETURN` opcode:

```eo
opcode > IRETURN-11
  172
```
Or opcode-argument might be a `label` object, as in case of `IFLE` instruction:
```eo
opcode > IFLE-F
  158
  label
    "c361c429-6c81-4b11-9b97-0cbb6e96a2f9"
```
In this case `IFLE` opcode has exactly one operand which is `label`.

#### Labels



## How to Contribute

Fork repository, make changes, then send us
a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
$ mvn clean install -Pqulice
```

You will need [Maven 3.3+](https://maven.apache.org) and Java 11+ installed.

