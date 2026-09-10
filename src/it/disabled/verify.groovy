/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text
assert log.contains('BUILD SUCCESS')
assert log.contains('Disassemble mojo is disabled, skipping')
assert log.contains('Assemble mojo is disabled, skipping')
assert !log.contains('The number of threads must be 0 or positive')
true
