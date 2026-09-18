/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.directives.DirectivesHandle;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Handle;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlHandle}.
 *
 * @since 0.3
 */
final class XmlHandleTest {

    @Test
    void convertsToHandleObject() throws ImpossibleModificationException {
        final Handle handle = new Handle(
            1, "owner", "name", "desc", false
        );
        MatcherAssert.assertThat(
            "Can't convert XML handler to the correct handle object",
            new XmlHandle(
                new NativeXmlNode(new Xembler(new DirectivesHandle(0, new Format(), handle)).xml())
            ).bytecode().asHandle(),
            Matchers.equalTo(handle)
        );
    }

    @Test
    void refusesAHandleWithTooFewOperands() {
        MatcherAssert.assertThat(
            "the message must name the node and both counts",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlHandle(
                    new NativeXmlNode(
                        String.join(
                            "",
                            "<o base='Q.jeo.handle' name='h'>",
                            "<o base='Q.org.eolang.number' name='f'>",
                            "00-00-00-00-00-00-F0-3F</o>",
                            "<o base='Q.org.eolang.number' name='s'>",
                            "00-00-00-00-00-00-F0-3F</o>",
                            "</o>"
                        )
                    )
                ).bytecode(),
                "a handle with one operand must be refused"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("has 1 operands"),
                Matchers.containsString("5 are expected")
            )
        );
    }
}
