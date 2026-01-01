/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
import java.nio.file.Paths
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("First param: firstParam")
assert log.contains("Second param: secondParam")
// Run `javap` on the compiled class file to inspect LocalVariableTable
String dir = new File(basedir, "target/classes").getAbsolutePath()
String name = "org.eolang.jeo.variables.Application"
println "Running javap on ${name}..."
command = "javap -v -p -cp ${dir} ${name}"
println "Executing command: ${command}"
def javap = command.execute()
def outBuf = new StringBuffer()
def errBuf = new StringBuffer()
javap.consumeProcessOutput(outBuf, errBuf)
// Close child's stdin so it can't block waiting for input
javap.outputStream.close()
if (!javap.waitFor(30, java.util.concurrent.TimeUnit.SECONDS)) {
    javap.destroy()
    println "javap timed out"
    println "javap err:\n${javap.err.text}"
    println "javap output:\n${javap.in.text}"
    return false
}
if (javap.exitValue() != 0) {
    println "javap failed"
    println "javap err:\n${javap.err.text}"
    false
}
// Capture and process the output of javap
def output = outBuf.toString()
println "javap output:\n$output"
assert output.contains('LocalVariableTable'): "LocalVariableTable is missing in ${name}"

true
