/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.directives.DirectivesUnknownAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeObject}.
 * @since 0.15.0
 */
final class BytecodeUnknownAttributeTest {

    @Test
    void convertsToDirectives() throws ImpossibleModificationException {
        final String type = "Some-Type";
        final int index = 0;
        final byte[] data = {0, 1, 2, 3, 4, 5};
        final Format fmt = new Format(Format.COMMENTS, false);
        MatcherAssert.assertThat(
            "We expect to receive the same XML representation",
            new Xembler(
                new BytecodeUnknownAttribute(
                    type,
                    data
                ).directives(index, fmt)
            ).xml(),
            Matchers.equalTo(
                new Xembler(new DirectivesUnknownAttribute(fmt, index, type, data)).xml()
            )
        );
    }
}
