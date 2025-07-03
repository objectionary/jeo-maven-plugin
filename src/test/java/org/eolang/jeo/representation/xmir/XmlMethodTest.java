/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlMethod}.
 * This class verifies the functionality of XML method representation,
 * ensuring proper creation and handling of method objects from XML format.
 *
 * @since 0.1.0
 */
final class XmlMethodTest {

    @Test
    void transformsToBytecode() throws ImpossibleModificationException {
        final String name = "Hello";
        final int access = 100;
        final String descriptor = "()I";
        final String signature = "";
        MatcherAssert.assertThat(
            "We expect that directives will generate correct method",
            new XmlMethod(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesMethod(
                            name,
                            new DirectivesMethodProperties(access, descriptor, signature)
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeMethod(
                    new BytecodeMethodProperties(name, descriptor, signature, access),
                    new BytecodeMaxs()
                )
            )
        );
    }

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

    @Test
    void transformsAnnotationsToXmir() throws ImpossibleModificationException {
        final String descriptor = "Consumer";
        final boolean visible = true;
        final String name = "foo";
        MatcherAssert.assertThat(
            "We expect that annotation dirictes will be added to the method",
            new XmlMethod(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesMethod(name)
                            .withAnnotation(new DirectivesAnnotation(descriptor, visible))
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeMethod(
                    name,
                    new BytecodeAnnotations(
                        new BytecodeAnnotation(descriptor, visible)
                    )
                )
            )
        );
    }

}
