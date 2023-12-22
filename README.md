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
    [] > seq
      *
        opcode > RETURN-1
          177
```

Pay attention, that the method is a child of the class element and it contains
bytecode attributes license `access` (access
modifiers), `descriptor` ([method descriptor](https://stackoverflow.com/questions/7526483/what-is-the-difference-between-descriptor-and-signature)),
and `exceptions` (list of declared exceptions) along with the `seq` element that
contains the sequence of the bytecode instructions.

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

