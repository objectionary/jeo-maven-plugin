/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link XmlMethod}.
 *
 * @since 0.1
 */
final class XmlMethodTest {

    @Test
    void createsMethodByValues() {
        final String name = "name";
        final int access = 0;
        final String descriptor = "()V";
        MatcherAssert.assertThat(
            "We expect that method will be correctly parsed",
            new XmlMethod(name, access, descriptor).bytecode(),
            Matchers.equalTo(
                new BytecodeMethod(
                    new BytecodeMethodProperties(name, descriptor, "", access),
                    new BytecodeMaxs()
                )
            )
        );
    }

    @Test
    void createsConstructor() {
        MatcherAssert.assertThat(
            "Method name is not equal to expected, it should be <init>",
            new XmlMethod("object@init@", Opcodes.ACC_PUBLIC, "()V").bytecode().name(),
            Matchers.equalTo("<init>")
        );
    }

    @Test
    void retrievesMethodProperties() {
        final String name = "name";
        final int access = 0;
        final String descriptor = "()V";
        final String[] exceptions = {"java/lang/Exception", "java/lang/Throwable"};
        final XmlMethod method = new XmlMethod(name, access, descriptor, exceptions);
        MatcherAssert.assertThat(
            "Method properties are not equal to expected",
            method.bytecode(),
            Matchers.equalTo(
                new BytecodeMethod(
                    new BytecodeMethodProperties(access, name, descriptor, "", exceptions),
                    new BytecodeMaxs()
                )
            )
        );
    }

    @Test
    void createsMethodWithMaxStackAndMaxLocals() {
        MatcherAssert.assertThat(
            "We expect that max stack and max locals will be correctly parsed",
            new XmlMethod(1, 2).bytecode(),
            Matchers.equalTo(new BytecodeMethod("foo", new BytecodeMaxs(1, 2)))
        );
    }

}
