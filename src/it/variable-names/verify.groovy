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
assert log.contains("First param: firstParam")
assert log.contains("Second param: secondParam")
// Verify that the generated class names contain attributes
// Run `javap` on the compiled class file to inspect LocalVariableTable
String clazz = new File(basedir, "target/classes/org/eolang/jeo/variables/Application").toString()
println "Running javap on ${clazz}..."
def javap = "javap -v ${clazz}".execute()
javap.waitFor()
if (javap.exitValue() != 0) {
    println "javap failed:"
    println javap.err.text
    false
}
// Step 3: Capture and process the output of javap
def output = javap.in.text
println "javap output:\n$output"
// Optional: Check for the presence of LocalVariableTable and parameter names
assert output.contains('LocalVariableTable'): "LocalVariableTable is missing in ${clazz}"

true