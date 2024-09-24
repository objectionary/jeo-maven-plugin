/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link XmlInstruction}.
 * @since 0.1
 * @todo #715:60min Simplify Xml Transformation Tests
 *  Currently we compare XMLs with their String representations.
 *  It leads to significant efforts to update this XML when the structure changes.
 *  We should use domain classes from {@link org.eolang.jeo.representation.bytecode} package.
 *  We should refactor all "Xml..." tests to use these classes.
 */
final class XmlInstructionTest {

    /**
     * Default instruction which we use for testing.
     * This XML is compared with all other XMLs.
     */
    private static final XmlInstruction INSTRUCTION =
        new XmlInstruction(
            new StringBuilder()
                .append("<o base='org.eolang.jeo.opcode' line='999' name='INVOKESPECIAL'>")
                .append(
                    "<o base='org.eolang.jeo.int' data='bytes'><o base='bytes' data='bytes'>00 00 00 00 00 00 00 B7</o></o>"
                )
                .append(
                    "<o base='org.eolang.jeo.int' data='bytes'><o base='bytes' data='bytes'>00 00 00 00 00 00 00 01</o></o>"
                )
                .append(
                    "<o base='org.eolang.jeo.int' data='bytes'><o base='bytes' data='bytes'>00 00 00 00 00 00 00 02</o></o>"
                )
                .append(
                    "<o base='org.eolang.jeo.int' data='bytes'><o base='bytes' data='bytes'>00 00 00 00 00 00 00 03</o></o>"
                )
                .append("</o>")
                .toString()
        );

    @Test
    void comparesSuccessfullyWithSpaces() {
        MatcherAssert.assertThat(
            "Xml Instruction nodes with different empty spaces, but with the same content should be the same, but it wasn't",
            new XmlInstruction(false, Opcodes.INVOKESPECIAL, 1, 2, 3),
            Matchers.equalTo(XmlInstructionTest.INSTRUCTION)
        );
    }

    @Test
    void comparesSuccessfullyWithDifferentTextNodes() {
        MatcherAssert.assertThat(
            "Xml Instruction with different arguments should not be equal, but it was",
            new XmlInstruction(Opcodes.INVOKESPECIAL, 32, 23, 14),
            Matchers.not(Matchers.equalTo(XmlInstructionTest.INSTRUCTION))
        );
    }

    @Test
    void comparesDeeply() {
        MatcherAssert.assertThat(
            "Xml Instruction with different child content should not be equal, but it was",
            new XmlInstruction(Opcodes.INVOKESPECIAL),
            Matchers.not(Matchers.equalTo(XmlInstructionTest.INSTRUCTION))
        );
    }

    @Test
    void comparesDifferentInstructions() {
        MatcherAssert.assertThat(
            "Xml Instruction with different content should not be equal, but it was",
            new XmlInstruction(Opcodes.DUP),
            Matchers.not(Matchers.equalTo(XmlInstructionTest.INSTRUCTION))
        );
    }
}
