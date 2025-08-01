/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")

assert log.contains("Total 1 files were disassembled")
assert log.contains("Included.class disassembled")

assert log.contains("Total 1 files were assembled")
assert log.contains("Included.xmir assembled")

assert log.contains("included class successfully invoked!")
assert log.contains("not included class successfully invoked!")
assert log.contains("ignored class successfully invoked!")
assert log.contains("deeply ignored class successfully invoked!")
true
