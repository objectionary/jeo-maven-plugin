/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlFrame;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesFrame}.
 *
 * @since 0.3
 */
final class DirectivesFrameTest {

    @Test
    void createsCorrectDirectivesForFrame() throws ImpossibleModificationException {
        final int type = Opcodes.F_NEW;
        final int nlocal = 2;
        final Object[] locals = {"java/lang/Object", Opcodes.LONG};
        final int nstack = 2;
        final Object[] stack = {"java/lang/String", Opcodes.DOUBLE};
        final String xml = new Xembler(
            new DirectivesFrame(0, new Format(), type, locals, stack)
        ).xml();
        MatcherAssert.assertThat(
            "We failed to create correct directives for bytecode frame.",
            new XmlFrame(new NativeXmlNode(xml)).bytecode(),
            Matchers.equalTo(new BytecodeFrame(type, nlocal, locals, nstack, stack))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {Opcodes.F_APPEND, Opcodes.F_CHOP, Opcodes.F_SAME, Opcodes.F_SAME1})
    void convertsDifferentFrameTypes(final int type) throws ImpossibleModificationException {
        final Object[] locals = {Opcodes.INTEGER};
        final Object[] stack = {Opcodes.LONG};
        final BytecodeFrame expected = new BytecodeFrame(
            type,
            locals.length,
            locals,
            stack.length,
            stack
        );
        final String xml = new Xembler(
            new DirectivesFrame(0, new Format(), type, locals, stack)
        ).xml();
        MatcherAssert.assertThat(
            String.format("We failed to round-trip the frame of type %d", type),
            new XmlFrame(new NativeXmlNode(xml)).bytecode(),
            Matchers.equalTo(expected)
        );
    }
}

