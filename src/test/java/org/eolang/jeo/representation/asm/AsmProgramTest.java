/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeLine;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xembly.Xembler;

/**
 * Test case for {@link AsmProgram}.
 * @since 0.6
 */
final class AsmProgramTest {

    @Test
    void convertsToBytecode() {
        final Bytecode same = new BytecodeProgram()
            .replaceTopClass(new BytecodeClass().helloWorldMethod())
            .bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode",
            new AsmProgram(same.bytes()).bytecode().bytecode(),
            Matchers.equalTo(same)
        );
    }

    @Test
    void skipsDebugLabels() throws Exception {
        MatcherAssert.assertThat(
            "We expect to skip debug labels and lines",
            new AsmProgram(new BytesOf(new ResourceOf("MethodByte.class")).asBytes())
                .bytecode()
                .top()
                .methods()
                .get(1)
                .intructions()
                .stream()
                .anyMatch(entry -> entry instanceof BytecodeLabel || entry instanceof BytecodeLine),
            Matchers.is(false)
        );
    }

    @Test
    void parsesAnnotations() throws Exception {
        final byte[] original = new BytesOf(new ResourceOf("FixedWidth.class")).asBytes();
        final Bytecode expected = new Bytecode(original);
        final Bytecode actual = new AsmProgram(original).bytecode().bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode",
            actual,
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"FixedWidth.class", "DeprecatedMethod.class", "ParamAnnotation.class"})
    void convertsToBytecodeThenToXmirAndThenBackToBytecode(final String resource) throws Exception {
        final BytecodeProgram bytecode = new AsmProgram(
            new BytesOf(new ResourceOf(resource)).asBytes()
        ).bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode",
            new XmlProgram(
                new Xembler(bytecode.directives("")).xml()
            ).bytecode().bytecode(),
            Matchers.equalTo(bytecode.bytecode())
        );
    }
}
