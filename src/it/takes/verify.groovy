/*
 * MIT License
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("It is the Takes Framework")
assert log.contains("Request #1")
assert log.contains("Request time: ")
//Check that we have generated EO object files.
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/takes/Application.xmir').exists()
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/takes/Application$TimeLog.xmir').exists()
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/takes/Application$RequestCounter.xmir').exists()
/**
 * @todo #189:30min Enable Integration Test for Takes Framework.
 *  First of all, we have to implement all the features required for correct
 *  transformation. Then, we need to add JEO plugin into pom.xml of that test,
 *  the plugin setup you can find below. After that we need to add some
 *  assertions to this file. For example, we need to check that we have
 *  generated EO object files (see commented checks above).
 *
 * <p>
 * {@code
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
