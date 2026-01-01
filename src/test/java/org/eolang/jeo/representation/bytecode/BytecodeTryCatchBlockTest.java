/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.UUID;
import org.eolang.jeo.representation.directives.DirectivesTryCatch;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Xembler;

/**
 * Test cases for {@link BytecodeTryCatchBlock}.
 * @since 0.13.0
 */
final class BytecodeTryCatchBlockTest {

    @Test
    void convertsToDirectives() {
        final String start = UUID.randomUUID().toString();
        final String end = UUID.randomUUID().toString();
        final String handler = UUID.randomUUID().toString();
        final Format format = new Format();
        MatcherAssert.assertThat(
            "We expected to convert try-catch block to correct directives.",
            new Xembler(
                new BytecodeTryCatchBlock(
                    start, end, handler, "java/lang/Exception"
                ).directives(0, format)
            ).xmlQuietly(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesTryCatch(
                        0,
                        format,
                        new BytecodeLabel(start),
                        new BytecodeLabel(end),
                        new BytecodeLabel(handler),
                        "java/lang/Exception"
                    )
                ).xmlQuietly()
            )
        );
    }

    @Test
    void comparesEquality() {
        final String start = UUID.randomUUID().toString();
        final String end = UUID.randomUUID().toString();
        final String handler = UUID.randomUUID().toString();
        MatcherAssert.assertThat(
            "We expected two try-catch blocks with the same parameters to be equal.",
            new BytecodeTryCatchBlock(
                start, end, handler, "java/lang/Exception"
            ),
            Matchers.equalTo(
                new BytecodeTryCatchBlock(
                    start, end, handler, "java/lang/Exception"
                )
            )
        );
    }
}
