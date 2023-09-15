# jeo

[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/jeo-maven-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/jeo-maven-plugin)
[![Javadoc](http://www.javadoc.io/badge/org.eolang/jeo-maven-plugin.svg)](http://www.javadoc.io/doc/org.eolang/jeo-maven-plugin)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE.txt)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/jeo-maven-plugin?branch=master&label=Hits-of-Code)](https://hitsofcode.com/github/volodya-lombrozo/jtcop/view?branch=main&label=Hits-of-Code)
![Lines of code](https://sloc.xyz/github/objectionary/jeo-maven-plugin)

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

* `bytecode-to-eo` create EOlang files in the `target/jeo` directory.
* Provide your optimizations are applied to the EOlang files
  in the `target/jeo` directory.
* `eo-to-bytecode` scans the `target/jeo` directory for EOlang files and
  converts them back to Java bytecode.

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
      <version>1.0.0-SNAPSHOT</version>
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

