/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesMethod}.
 * @since 0.5
 */
final class DirectivesMethodTest {

    @Test
    void transformsToXmir() throws ImpossibleModificationException {
        final String name = "Hello";
        final int access = 100;
        final String descriptor = "()I";
        final String signature = "";
        final String xml = new Xembler(
            new DirectivesMethod(
                name,
                new DirectivesMethodProperties(access, descriptor, signature)
            )
        ).xml();
        System.out.println(xml);
        MatcherAssert.assertThat(
            "We expect that directives will generate correct method",
            new XmlMethod(
                new NativeXmlNode(
                    xml
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

    @Test
    void addsPrefixToTheMethodName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that 'j$' prefix will be added to the method name",
            new Xembler(new BytecodeMethod("φTerm").directives()).xml(),
            XhtmlMatchers.hasXPaths("./o[contains(@as, 'j$φTerm')]")
        );
    }
}
