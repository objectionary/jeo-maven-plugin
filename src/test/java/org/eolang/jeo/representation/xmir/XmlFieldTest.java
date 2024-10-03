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

import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesField;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlField}.
 *
 * @since 0.3
 */
final class XmlFieldTest {

    @Test
    void parsesXmirFieldSuccessfully() throws ImpossibleModificationException {
        final int access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        final String name = "serialVersionUID";
        final String descriptor = "J";
        final long value = 7_099_057_708_183_571_937L;
        MatcherAssert.assertThat(
            "Failed to parse XMIR field",
            new XmlField(
                new XmlNode(
                    new Xembler(
                        new DirectivesField(
                            access,
                            name,
                            descriptor,
                            "",
                            value
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeField(
                    name,
                    descriptor,
                    null,
                    value,
                    access,
                    new BytecodeAnnotations()
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("values")
    void parsesFieldWithValues(final Object value) throws ImpossibleModificationException {
        final int access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        final String name = "serialVersionUID";
        final String descriptor = "Ljava/lang/String;";
        final String xml = new Xembler(
            new DirectivesField(access, name, descriptor, null, value)
        ).xml();
        System.out.println(xml);
        MatcherAssert.assertThat(
            "We expect the value to be the same",
            new XmlField(
                new XmlNode(
                    xml
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeField(
                    name, descriptor, null, value, access
                )
            )
        );
    }

    @Test
    void retrievesFieldAnnotations() throws ImpossibleModificationException {
        final String override = "java/lang/Override";
        final String safe = "java/lang/SafeVarargs";
        MatcherAssert.assertThat(
            "Annotations are not found",
            new XmlField(
                new XmlNode(
                    new Xembler(
                        new DirectivesField(
                            new DirectivesAnnotation(override, true),
                            new DirectivesAnnotation(safe, true)
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeField(
                    "unknown",
                    "I",
                    null,
                    0,
                    1,
                    new BytecodeAnnotations(
                        new BytecodeAnnotation(override, true),
                        new BytecodeAnnotation(safe, true)
                    )
                )
            )
        );
    }

    /**
     * Provide values.
     * Test cases for {@link #parsesFieldWithValues(Object)}.
     * @return Values.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Object> values() {
        return Stream.of("7099057708183571937", "", null);
    }
}
