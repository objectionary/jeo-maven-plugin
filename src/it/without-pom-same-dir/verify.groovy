/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert basedir.toPath()
  .resolve("target")
  .resolve("generated-sources")
  .resolve("jeo-xmir")
  .resolve('MethodByte.xmir')
  .toFile()
  .exists()
assert basedir.toPath()
  .resolve("target")
  .resolve("classes")
  .resolve('MethodByte.class')
  .toFile()
  .exists()
true
