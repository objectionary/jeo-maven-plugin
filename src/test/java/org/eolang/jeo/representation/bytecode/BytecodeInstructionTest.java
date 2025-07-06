/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;
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
                BytecodeInstructionTest.withoutNames(
                    new BytecodeInstruction(
                        Opcodes.LDC,
                        Type.getType(Integer.class)
                    ).directives()
                )
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    BytecodeInstructionTest.withoutNames(
                        new DirectivesInstruction(
                            Opcodes.LDC,
                            Type.getType(Integer.class)
                        )
                    )
                ).xml()
            )
        );
    }

    /**
     * Set the name attribute to empty for all elements in the directives.
     * This was added to compare generated XMIR independently of the names, generated
     * randomly by {@link org.eolang.jeo.representation.directives.RandName}.
     * <p>
     *     <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1156">Issue #1156</a>
     * </p>
     * @param directives Directives to process
     * @return Directives with empty names
     */
    private static Directives withoutNames(final Iterable<Directive> directives) {
        return new Directives(directives).xpath("//o").attr("name", "");
    }
}
