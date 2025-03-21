/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Glad to see you, Spring...")
//Check that we have generated EO object files.
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/Application.xmir').exists()
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/Receptionist.xmir').exists()
def app = new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/App.xmir')
assert app.exists()
assert app.text.contains("metas")
assert app.text.contains("<head>package</head>")
//Check that if class doesn't have instructions, it doesn't have "opcode" imports.
def empty = new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/spring/WithoutInstructions.xmir')
assert empty.exists()
true
