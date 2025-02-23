/*
 * MIT License
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS"): "BUILD FAILED"
assert log.contains("Tests run:"): "Tests are not running"
// Check verification mode.
assert log.contains("Xmir verification after disassembling is disabled. Skipping.")
assert log.contains("Xmir verification before assembling is disabled. Skipping.")
//Check that we have generated EO object files.
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/Application.xmir').exists()
//assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/Receptionist.xmir').exists()
true
