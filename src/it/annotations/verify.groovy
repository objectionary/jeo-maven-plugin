/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Annotations test passed successfully!")
// Check verification mode.
assert log.contains("Verifying all the XMIR files after disassembling")
assert log.contains("Verifying all the XMIR files before assembling")
//Check that we have generated XMIR object file.
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/annotations/AnnotationsApplication.xmir').exists()
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/annotations/JeoAnnotation.xmir').exists()
true
