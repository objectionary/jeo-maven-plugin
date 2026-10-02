/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD FAILURE"): assertionMessage("BUILD FAILURE not found")
assert log.contains("The number of threads must be 0 or positive, but got: -1"): assertionMessage("The descriptive error was not reported")

private String assertionMessage(String message) {
    return String.format(
      "'%s', you can find the entire log in the 'file://%s' file",
      message,
      new File(basedir, 'build.log').absolutePath)
}

true