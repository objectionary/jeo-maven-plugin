/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeUnknownAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlUnknownAttribute}.
 * @since 0.15.0
 */
final class XmlUnknownAttributeTest {

    @Test
    void parsesTypeAndData() throws ImpossibleModificationException {
        final BytecodeUnknownAttribute origin = new BytecodeUnknownAttribute(
            "Some-Type",
            new byte[]{0, 1, 2, 3, 4, 5}
        );
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode attribute",
            new XmlUnknownAttribute(
                new JcabiXmlNode(new Xembler(origin.directives(0, new Format())).xml())
            ).bytecode(),
            Matchers.equalTo(origin)
        );
    }
}
