/*
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
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.eolang.jeo.representation.directives.Format;
import org.eolang.jeo.representation.xmir.XmlObject;
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
        final Bytecode same = new BytecodeObject()
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
                .instructions()
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

    @Test
    void parsesNestMembersBytecodeAttributes() throws Exception {
        final byte[] original = new BytesOf(new ResourceOf("Check.class")).asBytes();
        final Bytecode actual = new AsmProgram(original).bytecode(0).bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode with the 'NestMembers' attribute",
            actual.toString(),
            Matchers.containsString("NESTMEMBER")
        );
    }

    /**
     * Checks that the AsmProgram correctly parses the 'NestHost' attribute from the bytecode.
     * Comes from the issue:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1274">#1274</a>
     * @throws Exception In case of error
     * @todo #1274:30min Compare the bytecode before and after the conversion.
     *  Instead of just checking for the presence of the 'NestHost' attribute, we should
     *  compare the entire bytecode before and after the conversion to ensure that no other
     *  attributes or instructions are lost or altered during the process.
     *  We can't do it right now, because we have some strange 'arg0' attribute after
     *  the conversion, which is not present in the original bytecode.
     */
    @Test
    void persesNestHostBytecodeAttribute() throws Exception {
        final byte[] original = new BytesOf(new ResourceOf("Check$1Local.class")).asBytes();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode with the 'NestHost' attribute",
            new AsmProgram(original).bytecode(0).bytecode().toString(),
            Matchers.containsString("NESTHOST")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"FixedWidth.class", "DeprecatedMethod.class", "ParamAnnotation.class"})
    void convertsToBytecodeThenToXmirAndThenBackToBytecode(final String resource) throws Exception {
        final BytecodeObject bytecode = new AsmProgram(
            new BytesOf(new ResourceOf(resource)).asBytes()
        ).bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode",
            new XmlObject(
                new Xembler(bytecode.directives(new Format())).xml()
            ).bytecode().bytecode(),
            Matchers.equalTo(bytecode.bytecode())
        );
    }
}
