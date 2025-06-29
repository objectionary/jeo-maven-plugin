/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Xembler;

/**
 * Test cases for {@link BytecodeInstruction}.
 * @since 0.11.0
 */
final class BytecodeInstructionTest {

    @Test
    void covertsInstructionWithTypeToDirectives() {
        MatcherAssert.assertThat(
            "We expect that the bytecode instruction agryment with type 'Type' will be wrapped in sting, see https://github.com/objectionary/jeo-maven-plugin/issues/1125",
            new Xembler(
                new BytecodeInstruction(
                    Opcodes.LDC,
                    Type.getType(Integer.class)
                ).directives()
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath(
                new StringBuilder(0)
                    .append("/o[@base='Q.jeo.opcode.ldc']")
                    .append("/o[@base='Q.jeo.type']")
                    .append("/o[@base='Q.org.eolang.string']")
                    .append("/o[@base='Q.org.eolang.bytes']")
                    .append("/o[text()]")
                    .toString()
            )
        );
    }
}
