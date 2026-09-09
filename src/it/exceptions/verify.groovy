/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Exception in try-catch statement")
assert log.contains("Exception in try-catch-finally statement")
assert log.contains("Finally block in try-catch-finally statement")
assert log.contains("Closing 'Resource Without Exception'")
assert log.contains("Exception in try-catch-with-resources statement")
assert log.contains("Closing 'Resource With Exception'")
assert log.contains("Exception in try-catch-with-resources statement with suppressed exception")
assert log.contains("Exception during closing resource")

//Check that we have generated EO object files.
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/exceptions/Application.xmir').exists()

true
