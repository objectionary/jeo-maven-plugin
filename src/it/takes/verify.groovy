/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("It is the Takes Framework")
assert log.contains("Request #1")
assert log.contains("Request time: ")
//Check that we have generated EO object files.
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/takes/Application.xmir').exists()
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/takes/Application$TimeLog.xmir').exists()
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/takes/Application$RequestCounter.xmir').exists()

true
