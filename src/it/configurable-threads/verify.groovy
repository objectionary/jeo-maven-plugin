/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text
assert log.contains('BUILD SUCCESS')
assert log.contains('Using 1 thread(s) for parallel processing') : 'Disassemble should use exactly 1 thread'
assert log.contains('Using 4 thread(s) for parallel processing') : 'Assemble should use exactly 4 threads'
File xmir = new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/ct/Application.xmir')
assert xmir.exists() : "Disassembled XMIR file was not created: ${xmir}"
File clazz = new File(basedir, 'target/classes/org/eolang/jeo/ct/Application.class')
assert clazz.exists() : "Assembled class file was not created: ${clazz}"
true
