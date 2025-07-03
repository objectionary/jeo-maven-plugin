/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Annotations test passed successfully!")
// Check verification mode.
//todo: enable // assert log.contains("Verifying all the XMIR files after disassembling")
//todo: enable // assert log.contains("Verifying all the XMIR files before assembling")
//Check that we have generated XMIR object file.
assert new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/annotations/JeoAnnotation.xmir').exists()
File app = new File(basedir, 'target/generated-sources/jeo-xmir/org/eolang/jeo/annotations/AnnotationsApplication.xmir')
assert app.exists()
// Check that XMIR contains expected elements.
assert !app.text.contains("<listing>") : "Listings are disabled by default (see the omitListings option), but it is still present"
// Check formatting of the XMIR file.
app.eachLine { line ->
  if(line.contains("meta")) {
    def spaces = line.takeWhile { it == ' ' }.size()
    assert spaces % 2 == 0 : "Line has incorrect indentation (${spaces} spaces): $line"
  }
}
// Check that XMIR does not contain comments.
assert !app.text.contains("<!--") : "comments in generated XMIR are disabled by default (see the 'omitComments' option), but they are present"
true
