/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD FAILURE"): assertionMessage("BUILD FAILURE not found")
assert log.contains("Verifying all the XMIR files before assembling"): assertionMessage("XMIR verification was not started")
assert log.contains("Cannot find the declaration of element 'program'"): assertionMessage("The invalid XMIR was not reported")

private String assertionMessage(String message) {
    return String.format(
      "'%s', you can find the entire log in the 'file://%s' file",
      message,
      new File(basedir, 'build.log').absolutePath)
}

true