/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlAttributes}.
 * @since 0.6
 */
final class XmlAttributesTest {

    @Test
    void convertsToBytecode() throws ImpossibleModificationException {
        final BytecodeAttributes expected = new BytecodeAttributes(
            new InnerClass("name", "outer", "inner", 0)
        );
        MatcherAssert.assertThat(
            "We expect the attributes to be converted to a correct bytecode domain class",
            new XmlAttributes(
                new NativeXmlNode(
                    new Xembler(expected.directives(new Format(), "attributes")).xml()
                )
            ).attributes(),
            Matchers.equalTo(expected)
        );
    }
}
