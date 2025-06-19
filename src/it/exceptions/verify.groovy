/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Exception in try-catch statement")
assert log.contains("Exception in try-catch-finally statement")
assert log.contains("Finally block in try-catch-finally statement")
assert log.contains("Closing 'Resource Without Exception'")
assert log.contains("Exception in try-catch-with-resources statement")
assert log.contains("Closing 'Resource With Exception'")
assert log.contains("Exception in try-catch-with-resources statement with suppressed exception")
assert log.contains("Exception during closing resource")

//Check that we have generated EO object files.
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/exceptions/Application.xmir').exists()
/**
 * @todo #320:30min Enable 'exceptions' Integration Test.
 *  First of all, we have to implement all the features required for correct
 *  transformation of exceptions. Then, we need to add JEO plugin into pom.xml
 *  of that test. The plugin setup you can find below. After that we need to add
 *  some assertions to this file. For example, we need to check that we have
 *  generated EO object files (see commented checks above).
 *  Right now this test just compiles java as is without any transformations.
 *
 * <p>{@code
 * <plugin>
 *   <groupId>org.eolang</groupId>
 *   <artifactId>jeo-maven-plugin</artifactId>
 *   <version>@project.version@</version>
 *   <executions>
 *     <execution>
 *       <id>bytecode-to-eo</id>
 *       <goals>
 *         <goal>disassemble</goal>
 *       </goals>
 *     </execution>
 *     <execution>
 *       <id>eo-to-bytecode</id>
 *       <goals>
 *         <goal>assemble</goal>
 *       </goals>
 *     </execution>
 *   </executions>
 * </plugin>
 *
 *}
 * </p>
 */

true
