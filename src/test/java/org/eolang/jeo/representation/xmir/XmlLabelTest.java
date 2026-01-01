/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlLabel}.
 * @since 0.3
 */
final class XmlLabelTest {

    @Test
    void retrievesLabelIdentifier() throws ImpossibleModificationException {
        final BytecodeLabel expected = new BytecodeLabel("some");
        MatcherAssert.assertThat(
            "Can't retrieve correct label identifier",
            new XmlLabel(
                new NativeXmlNode(
                    new Xembler(expected.directives(0, new Format())).xml()
                )
            ).bytecode(),
            Matchers.equalTo(expected)
        );
    }
}
