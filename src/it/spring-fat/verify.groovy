/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS"): "BUILD FAILED"
assert log.contains("Tests run:"): "Tests are not running"
// Check verification mode.
assert log.contains("XMIR verification after disassembling is disabled")
assert log.contains("XMIR verification before assembling is disabled")
//Check that we have generated EO object files.
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/Application.xmir').exists()
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/Receptionist.xmir').exists()
true
