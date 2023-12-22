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
      <version>0.2.8</version>
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

## Transformation method

The plugin can transform Java bytecode into EO and back. Usually, the plugin
transforms each bytecode class file into a separate EO file, maintaining a
one-to-one relationship. If the Java class has name `Foo.class`, the EO file
will have `Foo.eo` (and Foo.xmir for the XMIR representation of the EO file).

### Classes

The first high-level transformation is the conversion of the bytecode class
into `<program>` and `<objects><o name='Classname'/></objects>` XMIR elements.
For example, consider the following Java class:

```java
public class Foo {
}
```

It will be transformed into the following EO:

```eo
[] > j$Foo
  33 > access
  "java/lang/Object" > supername
  * > interfaces
```

`access`(class access modifiers like `public`, `static`, `final` and others),
`supername` (parent class), and `interfaces` (tuple of implemented interfaces)
are attributes of the class element that retain the information necessary for
the reverse transformation.

The `j$*` prefix is employed to prevent name conflicts with EO keywords.
This same prefix is utilized for all EO elements generated from Java bytecode.

By the way, the `XMIR` representation of that EO file will be:

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

The second high-level transformation involves converting the bytecode method
into EO. For example, consider the following Java method:

```java
public class Bar {
    public void foo() {
        return;
    }
}
```

It will be transformed into the following EO:

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

Each method is a child of the [class](#classes) element and contains bytecode
attributes such as `access` (access
modifiers), `descriptor` ([method descriptor](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html)),
and `exceptions` (a tuple of declared exceptions). Additionally, it includes the
`seq` element containing the sequence of bytecode instructions.

It's worth mentioning that Java constructors are also treated as methods with
the name `new`. For instance, consider the following Java constructor:

```java
public class Bar {
    public Bar() {
    }
}
```

It will be transformed into the following EO:

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

Each method and constructor contains a sequence of instructions, with each
instruction represented by either a `opcode` or a `label`. For example, consider
the following Java method:

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

It will have the following set of instructions after compilation (as shown
by `javap -v Bar` output):

```
0: iload_1
1: ifle          6
4: iconst_1
5: ireturn
6: iconst_2
7: ireturn
```

After the transformation provided by `jeo`, the content of the `foo` method in
EO will look like:

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

From the example above (refer to the [Methods](#methods) section), you can
observe that each opcode is represented by the `opcode` object. Each opcode
object includes a name, a numerical argument, and optional operand arguments.
For instance, the `iload` opcode has the following EO representation

```eo
opcode > ILOAD-E
  21
  1
```

Here, `ILOAD-E` is the opcode's name, `21` is its number according to the [Java
specification](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html),
and `1` represents the opcode argument, indicating the 'local
variable with index 1' in this context.

`ILOAD-E` is simply a name assigned to an opcode object. Since it serves a
descriptive purpose and isn't relied upon during transformations, you have
flexibility to modify these names as needed when making changes to the original
`jeo` output (as `jeo` doesn't utilize them during parsing.)

Also, it's worth mentioning that an `opcode` might not have operand arguments,
as is the case with the `IRETURN` opcode:

```eo
opcode > IRETURN-11
  172
```

Alternatively, the opcode argument might be a `label` object, as seen in the
`IFLE` instruction:

```eo
opcode > IFLE-F
  158
  label
    "c361c429-6c81-4b11-9b97-0cbb6e96a2f9"
```

In this instance, the `IFLE` opcode has precisely one operand, which is
a `label`.

#### Labels

Labels serve as markers or references indicating specific points in the code:

1. They might mark the entry- and exit-points of a method for debugging
   purposes.
2. They provide jump points in the code, such as for `if` and `for` statements.
   The example of using labels is in conjunction with the `goto` instruction:

```
opcode > GOTO-1
  167
  label "dbe5a680-4814-4b19-a8e6-15c3c2db3a83"
opcode > ALOAD-2 // skiped by goto
  25             // skiped by goto
  1              // skiped by goto
label "dbe5a680-4814-4b19-a8e6-15c3c2db3a83"
opcode > RETURN-3
  177
```

3. Labels can also be used for exception handling.

Of course, this isn't an exhaustive list of `label` usages.

What is more important, many labels are as crucial as opcodes themselves, and if
subsequent transformations lose these labels, the logic of the program might be
compromised. Therefore, it is extremely important to preserve most of the
labels. However, it's worth noting that you can omit certain labels used solely
for debugging purposes when generating your own classes. For instance, you can
omit labels at the start and end of a method.

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

