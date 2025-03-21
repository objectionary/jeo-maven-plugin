/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
