/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Yegor likes write code in EO")
assert log.contains("Lev likes write code in Rust")
assert log.contains("Maxim likes write code in PHP")
assert log.contains("Roman likes write code in Kotlin")
//Check that we have generated EO object files.
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/Application.xmir').exists()
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/Developer.xmir').exists()
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/Language.xmir').exists()
true
