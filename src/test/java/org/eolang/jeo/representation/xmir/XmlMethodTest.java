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
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link XmlMethod}.
 * @since 0.1
 */
final class XmlMethodTest {

    @Test
    void createsMethodByValues() {
        final String name = "name";
        final int access = 0;
        final String descriptor = "()V";
        final XmlMethod method = new XmlMethod(name, access, descriptor);
        MatcherAssert.assertThat(
            "Method name is not equal to expected",
            method.name(),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            "Method access is not equal to expected",
            method.access(),
            Matchers.equalTo(access)
        );
        MatcherAssert.assertThat(
            "Method descriptor is not equal to expected",
            method.descriptor(),
            Matchers.equalTo(descriptor)
        );
    }

    @Test
    void createsConstructor() {
        MatcherAssert.assertThat(
            "Method name is not equal to expected, it should be <init>",
            new XmlMethod("new", Opcodes.ACC_PUBLIC, "()V").name(),
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
                new BytecodeMethodProperties(access, name, descriptor, null, exceptions)
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
        final XmlMethod method = new XmlMethod();
        final XmlInstruction instruction = new XmlInstruction(Opcodes.LDC, "Bye world!");
        method.replaceInstructions(instruction.toNode());
        final List<XmlBytecodeEntry> after = method.instructions();
        MatcherAssert.assertThat(
            "Exactly one instruction should be added",
            after,
            Matchers.contains(instruction)
        );
    }

    @Test
    void replacesInstructions() {
        final XmlMethod method = new XmlMethod();
        method.replaceInstructions(new XmlInstruction(Opcodes.LDC, "Bye world!").toNode());
        final XmlInstruction first = new XmlInstruction(Opcodes.INVOKESPECIAL, 1);
        final XmlInstruction second = new XmlInstruction(Opcodes.INVOKEVIRTUAL, 2);
        method.replaceInstructions(
            first.toNode(),
            second.toNode()
        );
        final List<XmlBytecodeEntry> after = method.instructions();
        MatcherAssert.assertThat(
            "Exactly two instruction should be added",
            after,
            Matchers.containsInAnyOrder(first, second)
        );
    }

    @Test
    void retrievesMethodNodes() {
        final XmlMethod method = new XmlMethod();
        method.replaceInstructions(
            new XmlInstruction(Opcodes.LDC, "first").toNode(),
            new XmlInstruction(Opcodes.LDC, "second").toNode(),
            new XmlNode("<o/>")
        );
        final List<XmlNode> nodes = method.nodes();
        MatcherAssert.assertThat(
            "Exactly three node should be added. Pay attention, that the last node is custom xml node",
            nodes,
            Matchers.hasSize(3)
        );
    }
}
