import java.nio.file.Files

/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS"): assertionMessage("BUILD FAILED")
assert log.contains("sin(42.000000) = -0.916522"): assertionMessage("sin(42.000000) = -0.916522 not found")

private String assertionMessage(String message) {
    return String.format(
      "'%s', you can find the entire log in the 'file://%s' file",
      message,
      new File(basedir, 'build.log').absolutePath)
}

true
