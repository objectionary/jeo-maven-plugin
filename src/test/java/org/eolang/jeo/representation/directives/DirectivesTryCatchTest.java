/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlTryCatchEntry;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesTryCatch}.
 * This class verifies the generation of try-catch block directives,
 * ensuring proper handling of exception handling constructs in bytecode.
 *
 * @since 0.5.0
 */
final class DirectivesTryCatchTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final BytecodeLabel start = new BytecodeLabel();
        final BytecodeLabel end = new BytecodeLabel();
        final BytecodeLabel handler = new BytecodeLabel();
        final String type = "java/lang/Exception";
        MatcherAssert.assertThat(
            "We expected to convert try-catch directives to correct bytecode.",
            new XmlTryCatchEntry(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesTryCatch(start, end, handler, type)
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(new BytecodeTryCatchBlock(start, end, handler, type))
        );
    }

    /**
     * Checks several different cases of converting try-catch statements with different structures.
     * @param start Where the try-catch block starts.
     * @param end Where the try-catch block ends.
     * @param handler Code to handle try-catch block.
     * @param type The type of error that might occur.
     * @throws ImpossibleModificationException in case of incorrect XML.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    @ParameterizedTest
    @MethodSource("cases")
    void convertsToXmirIfSomeLabelsAreAbsent(
        final BytecodeLabel start,
        final BytecodeLabel end,
        final BytecodeLabel handler,
        final String type
    ) throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We failed to convert try-catch directives to correct XML.",
            new XmlTryCatchEntry(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesTryCatch(start, end, handler, type)
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(new BytecodeTryCatchBlock(start, end, handler, type))
        );
    }

    /**
     * Test cases.
     * @return Test cases.
     */
    static Stream<Arguments> cases() {
        return Stream.of(
            Arguments.of(
                new BytecodeLabel(),
                new BytecodeLabel(),
                new BytecodeLabel(),
                "java/lang/Exception"
            ),
            Arguments.of(new BytecodeLabel(), new BytecodeLabel(), new BytecodeLabel(), null),
            Arguments.of(new BytecodeLabel(), new BytecodeLabel(), null, null),
            Arguments.of(new BytecodeLabel(), null, null, null),
            Arguments.of(null, null, null, null)
        );
    }

}
