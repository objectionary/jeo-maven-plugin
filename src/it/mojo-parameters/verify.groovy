/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS"): assertionMessage("BUILD SUCCESS not found")
assert log.contains("Disassemble mojo is disabled, skipping"): assertionMessage("The disabled parameter was not respected")
assert log.contains("Bytecode verification is disabled, skipping"): assertionMessage("The skipVerification parameter was not respected")
File debug = new File(basedir, 'target/jeo-xmir-debug/org/eolang/jeo/params/Application.xmir')
assert debug.exists(): assertionMessage("The debug XMIR was not created")
assert debug.text.contains("<listing>"): assertionMessage("Listings are missing despite omitListings=false")
assert debug.text.contains("<!--"): assertionMessage("Comments are missing despite omitComments=false")
File modifiers = new File(basedir, 'target/jeo-xmir-modifiers/org/eolang/jeo/params/Application.xmir')
assert modifiers.exists(): assertionMessage("The modifiers XMIR was not created")
assert modifiers.text.contains("modifiers"): assertionMessage("Method modifiers are missing despite modifiers=true")
assert !new File(basedir, 'target/jeo-xmir-disabled').exists(): assertionMessage("The disabled execution must not produce any XMIR")
assert new File(basedir, 'target/classes-skip-verification/org/eolang/jeo/params/Application.class').exists(): assertionMessage("The assembled class was not created")

private String assertionMessage(String message) {
    return String.format(
      "'%s', you can find the entire log in the 'file://%s' file",
      message,
      new File(basedir, 'build.log').absolutePath)
}

true