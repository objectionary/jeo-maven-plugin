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

import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesBytes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;
import org.xembly.Xembler;

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

    @Test
    void catchesMethodParsingException() {
        MatcherAssert.assertThat(
            "Exception message doesn't contain the expected text related to the method parsing",
            Assertions.assertThrows(
                ParsingException.class,
                () -> new XmlMethod(
                    new XmlNode(
                        new Xembler(
                            new Directives(
                                new BytecodeMethod(
                                    "someMethodName"
                                ).directives()
                            ).xpath(".//o[contains(@name, 'body')]")
                                .append(new DirectivesBytes("???"))
                        ).xmlQuietly()
                    )
                ).bytecode()
            ).getMessage(),
            Matchers.containsString(
                "Unexpected exception during parsing the method 'someMethodName'"
            )
        );
    }
}
