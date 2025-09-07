/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.directives.DirectivesFrameValues;
import org.eolang.jeo.representation.directives.DirectivesValues;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlFrameValues}.
 * @since 0.14.0
 */
final class XmlFrameValuesTest {

    @Test
    void parsesFrameValuesFromAliasedValues() throws ImpossibleModificationException {
        final Object[] expected = {
            "java/lang/Object",
            Opcodes.TOP,
            Opcodes.INTEGER,
            Opcodes.FLOAT,
            Opcodes.DOUBLE,
            Opcodes.LONG,
            Opcodes.NULL,
            Opcodes.UNINITIALIZED_THIS,
            9,
        };
        MatcherAssert.assertThat(
            "Here we check that our parser understands 'top', 'double', 'flat' and other aliases.",
            new XmlFrameValues(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesFrameValues(
                            new Format(),
                            "values",
                            expected
                        )
                    ).xml()
                )
            ).values(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void parsesFrameValuesFromBareValues() throws ImpossibleModificationException {
        final Object[] expected = {
            "java/lang/Object",
            Opcodes.TOP,
            Opcodes.INTEGER,
            Opcodes.FLOAT,
            Opcodes.DOUBLE,
            Opcodes.LONG,
            Opcodes.NULL,
            Opcodes.UNINITIALIZED_THIS,
            9,
        };
        MatcherAssert.assertThat(
            "Here we check that our parser understands '0', '1', '3' and other bare values.",
            new XmlFrameValues(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesValues(
                            new Format(),
                            "values",
                            expected
                        )
                    ).xml()
                )
            ).values(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void understandsIntAliases() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Here we check that our parser understands 'top', 'integer', 'float' and other aliases.",
            new XmlFrameValues(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesValues(
                            new Format(),
                            "name",
                            "byte",
                            "short",
                            "char",
                            "boolean"
                        )
                    ).xml()
                )
            ).values(),
            Matchers.equalTo(
                new Object[]{Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER}
            )
        );
    }
}
