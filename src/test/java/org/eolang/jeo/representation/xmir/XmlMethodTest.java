/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
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
        final BytecodeClass clazz = new BytecodeClass();
        MatcherAssert.assertThat(
            "We expect that method will be correctly parsed",
            new XmlMethod(name, access, descriptor).bytecode(clazz),
            Matchers.equalTo(
                new BytecodeMethod(
                    new BytecodeMethodProperties(name, descriptor, "", access),
                    clazz,
                    new BytecodeMaxs()
                )
            )
        );
    }

    @Test
    void createsConstructor() {
        MatcherAssert.assertThat(
            "Method name is not equal to expected, it should be <init>",
            new XmlMethod("@init@", Opcodes.ACC_PUBLIC, "()V").name(),
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
            method.properties(),
            Matchers.equalTo(
                new BytecodeMethodProperties(access, name, descriptor, "", exceptions)
            )
        );
    }

    @Test
    void createsMethodWithoutInstructions() {
        MatcherAssert.assertThat(
            "Method instructions are not empty",
            new XmlMethod().instructions(),
            Matchers.empty()
        );
    }

    @Test
    void addsInstructions() {
        final XmlInstruction instruction = new XmlInstruction(Opcodes.LDC, "Bye world!");
        MatcherAssert.assertThat(
            "Exactly one instruction should be added",
            new XmlMethod().withInstructions(instruction.toNode()).instructions(),
            Matchers.contains(instruction)
        );
    }

    @Test
    void replacesInstructions() {
        final XmlInstruction first = new XmlInstruction(Opcodes.INVOKESPECIAL, 1);
        final XmlInstruction second = new XmlInstruction(Opcodes.INVOKEVIRTUAL, 2);
        final XmlMethod method = new XmlMethod().withInstructions(
            new XmlInstruction(Opcodes.LDC, "Bye world!").toNode()
        ).withInstructions(
            first.toNode(),
            second.toNode()
        );
        MatcherAssert.assertThat(
            "Exactly two instructions should be added",
            method.instructions(),
            Matchers.containsInAnyOrder(first, second)
        );
    }

    @Test
    void cleansInstructions() {
        MatcherAssert.assertThat(
            "Instructions should be cleaned",
            new XmlMethod()
                .withInstructions(new XmlInstruction(Opcodes.POP).toNode())
                .withoutInstructions()
                .instructions(),
            Matchers.empty()
        );
    }

    @Test
    void retrievesMethodNodes() {
        final List<XmlNode> nodes = new XmlMethod().withInstructions(
            new XmlInstruction(Opcodes.LDC, "first").toNode(),
            new XmlInstruction(Opcodes.LDC, "second").toNode(),
            new XmlNode("<o/>")
        ).nodes();
        MatcherAssert.assertThat(
            "Exactly three node should be added. Pay attention, that the last node is custom xml node",
            nodes,
            Matchers.hasSize(3)
        );
    }

    @Test
    void createsMethodWithMaxStackAndMaxLocals() {
        final Optional<XmlMaxs> max = new XmlMethod(1, 2).maxs();
        MatcherAssert.assertThat(
            "Max stack is not present",
            max.isPresent(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Max stack is not equal to expected",
            max.get(),
            Matchers.equalTo(new XmlMaxs(1, 2))
        );
    }

    @Test
    void cleansMaxValues() {
        MatcherAssert.assertThat(
            "Max values should be cleaned",
            new XmlMethod(2, 1).withoutMaxs().maxs().isPresent(),
            Matchers.is(false)
        );
    }
}
