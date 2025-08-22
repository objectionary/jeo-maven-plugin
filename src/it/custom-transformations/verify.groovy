/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Hello, World!")
assert log.contains("Runnable test passed successfully!")
assert log.contains("Predicate test passed successfully true!")
assert log.contains("Consumer test passed successfully with Consumer!")
assert log.contains("Function test passed successfully with 8!")
assert log.contains("streams test passed successfully!")
//Check that we have generated XMIR object file.
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/Application.xmir').exists()
//assert new File(basedir, 'target/generated-sources/jeo-eo/org/eolang/jeo/Application.eo').exists()
//Check that class file was changed
assert log.contains("Application.class disassembled to")
assert log.contains("Application.xmir assembled to")
//Check that the output contains method modifiers: https://github.com/objectionary/jeo-maven-plugin/issues/1263
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/Application.xmir').text.contains("modifiers")
true
