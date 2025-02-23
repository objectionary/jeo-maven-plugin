/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */ // in seconds

test = "jna"
def integrationTestCommand = "mvn invoker:run -Dinvoker.test=${test} -DskipTests"
profilerCommandBase = project.properties.getProperty("PROFILER") ?: System.getProperty("profiler.command")
if (!profilerCommandBase) {
    throw new RuntimeException("Error: PROFILER is not set. Ensure it is defined in your .env file or provided as a system property (-Dprofiler.command).")
}
durationProperty = project.properties.getProperty("DURATION")
profilingDuration = durationProperty ? Integer.parseInt(durationProperty) : 1_000_000

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
    def matchingLine = processOutput.split("\n").find { it.contains("target/it/${test}") }
    if (matchingLine) {
        return matchingLine.split(" ")[0].trim()
    } else {
        throw new RuntimeException("Failed to find PID for the integration test process.")
    }
}

def profileIntegrationTest(String pid) {
    def outputFilename = "flamegraph-${test}-${pid}.html"
    def profilerCommand = "${profilerCommandBase} -d ${profilingDuration} -f ${outputFilename} ${pid}"
//    def profilerCommand = "${profilerCommandBase} -f ${outputFilename} ${pid}"
    executeCommand(profilerCommand)
    println "Profiling completed. Output saved to ${outputFilename}"
}

println "Starting integration test in the background..."
def integrationTestProcess = executeCommand(integrationTestCommand, false)

println "Waiting for specific log message to start profiling..."
waitForLogMessage(integrationTestProcess, "Building: ${test}/pom.xml")

println "Fetching PID of the integration test process..."
def pid = getIntegrationTestPid()
println "Found PID: ${pid}"

println "Starting profiler..."
profileIntegrationTest(pid)

println "Waiting for the integration test to complete..."
integrationTestProcess.waitFor()
println "Integration test completed."
