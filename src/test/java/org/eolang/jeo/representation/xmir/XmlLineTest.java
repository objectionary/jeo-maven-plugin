/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeLine;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlLine}.
 * @since 0.14.0
 */
final class XmlLineTest {

    @Test
    void parsesXmlLine() throws ImpossibleModificationException {
        final BytecodeLine expected = new BytecodeLine(10, new BytecodeLabel("test"));
        MatcherAssert.assertThat(
            "We expect to parse the XML line correctly",
            new XmlLine(
                new XmlJeoObject(
                    new JcabiXmlNode(
                        new Xembler(expected.directives(0, new Format())).xml()
                    )
                )
            ).bytecode(),
            Matchers.equalTo(expected)
        );
    }
}
