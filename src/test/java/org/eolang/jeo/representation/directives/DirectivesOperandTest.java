/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesOperand}.
 *
 * @since 0.15.0
 */
final class DirectivesOperandTest {

    @Test
    void convertsValueOperandToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect a plain value operand to be represented as a number",
            new Xembler(new DirectivesOperand(0, new Format(), 42)).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'number') and @name='v0']"
            )
        );
    }

    @Test
    void convertsTypeOperandToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect a type operand to be represented as a jeo type",
            new Xembler(
                new DirectivesOperand(0, new Format(), Type.getType("I"))
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "type").toXpath(),
                "/o[@name='t0']",
                "/o/o[@name='v0']"
            )
        );
    }

    @Test
    void convertsHandleOperandToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect a handle operand to be represented as a jeo handle",
            new Xembler(
                new DirectivesOperand(
                    0,
                    new Format(),
                    new Handle(1, "owner", "name", "()V", false)
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "handle").toXpath(),
                "/o[@name='h0']"
            )
        );
    }

    @Test
    void convertsLabelOperandToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect a label operand to be represented as a jeo label",
            new Xembler(
                new DirectivesOperand(0, new Format(), new BytecodeLabel("target"))
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "label").toXpath()
            )
        );
    }
}
