/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.directives.DirectivesMaxs;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlMaxs}.
 *
 * @since 0.18.0
 */
final class XmlMaxsTest {

    @Test
    void convertsToBytecodeMaxs() throws ImpossibleModificationException {
        final BytecodeMaxs maxs = new BytecodeMaxs(1, 2);
        MatcherAssert.assertThat(
            "Can't convert XML maxs to bytecode object",
            new XmlMaxs(
                new NativeXmlNode(
                    new Xembler(new DirectivesMaxs(new Format(), 1, 2)).xml()
                )
            ).bytecode(),
            Matchers.equalTo(maxs)
        );
    }

    @Test
    void refusesMaxsWithZeroOperands() {
        MatcherAssert.assertThat(
            "The message must report that maxs has 0 operands and 2 expected",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlMaxs(
                    new NativeXmlNode(
                        "<o base='Q.jeo.maxs'><o name='φ' base='Q.jeo.maxs'/></o>"
                    )
                ).bytecode(),
                "A maxs with zero operands must be refused"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("has 0 operands"),
                Matchers.containsString("2 expected")
            )
        );
    }

    @Test
    void refusesMaxsWithOneOperand() {
        MatcherAssert.assertThat(
            "The message must report 1 operand present and 2 expected",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlMaxs(
                    new NativeXmlNode(
                        String.join(
                            "",
                            "<o base='Q.jeo.maxs' name='m0'>",
                            "<o base='Q.org.eolang.number' name='stack'>",
                            "00-00-00-00-00-00-00-01</o>",
                            "</o>"
                        )
                    )
                ).bytecode(),
                "A maxs with one operand must be refused"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("has 1 operands"),
                Matchers.containsString("2 expected")
            )
        );
    }

    @Test
    void comparesEqualMaxsObjects() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesMaxs(new Format(), 4, 5)
        ).xml();
        MatcherAssert.assertThat(
            "Maxs objects created from identical XML must be equal",
            new XmlMaxs(new NativeXmlNode(xml)),
            Matchers.equalTo(new XmlMaxs(new NativeXmlNode(xml)))
        );
    }

    @Test
    void producesConsistentHashCode()
        throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesMaxs(new Format(), 2, 3)
        ).xml();
        MatcherAssert.assertThat(
            "Hash code must be consistent with equals",
            new XmlMaxs(new NativeXmlNode(xml)).hashCode(),
            Matchers.equalTo(new XmlMaxs(new NativeXmlNode(xml)).hashCode())
        );
    }
}
