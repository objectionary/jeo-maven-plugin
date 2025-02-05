/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */ // in seconds

def integrationTestCommand = "mvn invoker:run -Dinvoker.test=phi-unphi -DskipTests"
profilerCommandBase = "/Users/lombrozo/Workspace/Tools/async-profiler-2.9-macos/profiler.sh"
profilingDuration = 1_000_000
//profilingDuration = 180

def executeCommand(String command, boolean wait = true) {
    def process = command.execute()
    if (wait) {
        process.consumeProcessOutput(System.out, System.err)
        process.waitFor()
    }
    return process
}

def waitForLogMessage(def process, String message) {
    def reader = new BufferedReader(new InputStreamReader(process.inputStream))
    String line
    while ((line = reader.readLine()) != null) {
        println line // Print process output for debugging
        if (line.contains(message)) {
            return
        }
    }
    throw new RuntimeException("Log message '${message}' not found in process output.")
}

def getIntegrationTestPid() {
    def processListCommand = "jps -lv"
    def processOutput = processListCommand.execute().text
    def matchingLine = processOutput.split("\n").find { it.contains("phi-unphi") }
    if (matchingLine) {
        return matchingLine.split(" ")[0].trim()
    } else {
        throw new RuntimeException("Failed to find PID for the integration test process.")
    }
}

def profileIntegrationTest(String pid) {
    def outputFilename = "flamegraph-phi-unphi-${pid}.html"
    def profilerCommand = "${profilerCommandBase} -d ${profilingDuration} -f ${outputFilename} ${pid}"
//    def profilerCommand = "${profilerCommandBase} -f ${outputFilename} ${pid}"
    executeCommand(profilerCommand)
    println "Profiling completed. Output saved to ${outputFilename}"
}

println "Starting integration test in the background..."
def integrationTestProcess = executeCommand(integrationTestCommand, false)

println "Waiting for specific log message to start profiling..."
waitForLogMessage(integrationTestProcess, "Building: phi-unphi/pom.xml")

println "Fetching PID of the integration test process..."
def pid = getIntegrationTestPid()
println "Found PID: ${pid}"

println "Starting profiler..."
profileIntegrationTest(pid)

println "Waiting for the integration test to complete..."
integrationTestProcess.waitFor()
println "Integration test completed."
