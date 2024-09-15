/*
 * MIT License
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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