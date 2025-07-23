/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.UUID;
import org.eolang.jeo.representation.directives.DirectivesTryCatch;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

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
        MatcherAssert.assertThat(
            "We expected to convert try-catch block to correct directives.",
            new Nameless(
                new BytecodeTryCatchBlock(
                    start, end, handler, "java/lang/Exception"
                ).directives()
            ),
            Matchers.equalTo(
                new Nameless(
                    new DirectivesTryCatch(
                        new BytecodeLabel(start),
                        new BytecodeLabel(end),
                        new BytecodeLabel(handler),
                        "java/lang/Exception"
                    )
                )
            )
        );
    }
}
