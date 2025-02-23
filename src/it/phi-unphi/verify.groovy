import java.nio.file.Files

/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS"): assertionMessage("BUILD FAILED")
assert log.contains("sin(42.000000) = -0.916522"): assertionMessage("sin(42.000000) = -0.916522 not found")
assert log.contains("We have the field with the unicode character 'Φ'"): assertionMessage("We can't find the field with the unicode character 'Φ'")

private String assertionMessage(String message) {
    generateGitHubIssue()
    return String.format(
      "'%s', you can find the entire log in the 'file://%s' file",
      message,
      new File(basedir, 'build.log').absolutePath)
}

private void generateGitHubIssue() {
    new File(basedir, 'build.log').text
    copyAll('org/eolang/hone/App')
    copyAll('org/eolang/hone/param/Parameter')
    def issue = new File(basedir, "issue.md")
    issue.text = '''I run the following [integration test](https://github.com/objectionary/jeo-maven-plugin/tree/master/src/it/phi-unphi):

 bytecode -> (disassemble) `xmir`  -> `phi` -> `unphi` -> `xmir` (assemble) -> bytecode.

 And this test fails because `phi/unphi` alter the original `xmir` file.

 **Steps to reproduce:**

 1) I generate `App.xmir` from `App.class` file (`jeo:disassemble`):
 [App.xmir.disassemble.txt](todo)
 2) Then I use `eo:0.39.0:xmir-to-phi` to generate `App.phi`:
 [App.phi.txt](todo)
 3) Then I run `eo:0.39.0:phi-to-xmir` to generate `App.xmir` (and it is generated):
 [App.xmir.unphi.txt](todo)

 **Expected behaviour:**

 `App.xmir.disassemble.txt` and `App.xmir.unphi.txt` files should be the same. In other words, `phi/unphi` does not
 have to change the original `xmir` file.

 **Actual behaviour:**

 `App.xmir.disassemble.txt` and `App.xmir.unphi.txt` files are different. `phi/unphi` significantly changes the original `xmir`.

 **Details:**
    '''

    // Generate the Bash script
    def bashScript = """#!/bin/bash

# Attachments
file1="App.xmir.disassemble.txt"
file2="App.phi.txt"
file3="App.xmir.unphi.txt"

# Create the issue using the 'gh' CLI
gh issue create \\
    --title "Integration Test Failure: `phi/unphi` alters xmir file" \\
    --body "\$(cat ${basedir}/issue.md)" \\
    --label "bug" \\
    --repo objectionary/eo
    """

    def file = new File(basedir, "create_issue.sh")
    file.text = bashScript
    println "Bash script generated: file://${basedir}/create_issue.sh"
}

private void copyAll(final String subpath) {
    def name = subpath.tokenize('/').last()
    copy('target/generated-sources/jeo-disassemble/' + subpath + ".xmir", name + '.xmir.disassemble.txt')
    copy('target/generated-sources/eo-phi/' + subpath + ".phi", name + '.phi.txt')
    copy('target/generated-sources/eo-unphi/' + subpath + '.xmir', name + '.xmir.unphi.txt')
}

private void copy(final String source, final String destination) {
    def sourceFile = new File(basedir, source).toPath()
    def destinationFile = new File(basedir, destination).toPath()
    Files.copy(sourceFile, destinationFile);
    if (Files.exists(destinationFile)) {
        println "'${source}' moved and renamed to '${destination}'!"
    } else {
        println "Failed to move the '${source}' file to '${destination}'."
    }
}

true
