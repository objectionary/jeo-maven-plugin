/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert basedir.toPath().resolve('xmir').resolve('MethodByte.xmir').toFile().exists()
assert basedir.toPath().resolve('output').resolve('MethodByte.class').toFile().exists()
true
