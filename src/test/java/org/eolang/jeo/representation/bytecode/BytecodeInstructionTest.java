/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link BytecodeInstruction}.
 * @since 0.11.0
 */
final class BytecodeInstructionTest {

    @Test
    void covertsInstructionWithTypeToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the bytecode instruction argument with type 'Type' will be wrapped in sting, see https://github.com/objectionary/jeo-maven-plugin/issues/1125",
            new Xembler(
                new BytecodeInstruction(
                    Opcodes.LDC,
                    Type.getType(Integer.class)
                ).directives(0, new Format())
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesInstruction(
                        0,
                        new Format(),
                        Opcodes.LDC,
                        Type.getType(Integer.class)
                    )
                ).xml()
            )
        );
    }
}
