/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link BytecodeRepresentation}.
 * This class verifies the parsing and conversion of Java bytecode
 * to EO representation, including name extraction and XML generation.
 *
 * @since 0.1.0
 */
final class BytecodeRepresentationTest {

    /**
     * The name of the resource with the simplest class.
     */
    private static final String METHOD_BYTE = "MethodByte.class";

    @Test
    void parsesBytecode() {
        MatcherAssert.assertThat(
            "The simplest class should contain the object with MethodByte name",
            new BytecodeRepresentation(new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE))
                .toXmir().xpath("/object/o/@name").get(0),
            Matchers.equalTo(new PrefixedName("MethodByte").encode())
        );
    }

    @Test
    void generatesXmlWithCorrectObjectAttributes() {
        MatcherAssert.assertThat(
            "XMIR objects should have either 'name' or 'base' attribute, or both",
            new BytecodeRepresentation(
                new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE)
            ).toXmir().toString(),
            Matchers.not(
                XhtmlMatchers.hasXPath(
                    "//o[not(@base) and not(@name) and not(parent::*[@base='Q.org.eolang.bytes'])]"
                )
            )
        );
    }

    /**
     * This test was added to mitigate the issue:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1160">Issue #1160</a>.
     */
    @Test
    void generatesXmlWithFormationsThatCIncludeObjectsWithNames() {
        MatcherAssert.assertThat(
            "A formation must only include objects with names.",
            new BytecodeRepresentation(
                new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE)
            ).toXmir().toString(),
            Matchers.not(XhtmlMatchers.hasXPath("//o[not(@base)]/o[not(@name)]"))
        );
    }

    @Test
    void retrievesName() {
        final ResourceOf input = new ResourceOf(BytecodeRepresentationTest.METHOD_BYTE);
        final String actual = new BytecodeRepresentation(input).name();
        final String expected = "org/eolang/jeo/MethodByte";
        MatcherAssert.assertThat(
            String.format(
                "The name should be retrieved without exceptions and equal to the expected '%s'",
                expected
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }
}
