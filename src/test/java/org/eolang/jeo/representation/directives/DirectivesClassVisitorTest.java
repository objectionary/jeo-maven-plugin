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
package org.eolang.jeo.representation.directives;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesClassVisitor}.
 * @since 0.1.0
 */
final class DirectivesClassVisitorTest {

    @Test
    void parsesSimpleClassWithoutConstructor() throws ImpossibleModificationException {
        final DirectivesClassVisitor directives = new DirectivesClassVisitor();
        new ClassReader(
            new BytecodeProgram(new BytecodeClass()).bytecode().asBytes()
        ).accept(directives, 0);
        MatcherAssert.assertThat(
            "Can't parse simple class without constructor",
            new Xembler(directives).xml(),
            new HasClass("Simple")
        );
    }

    @Test
    void parsesSimpleClassWithMethod() throws ImpossibleModificationException {
        final DirectivesClassVisitor directives = new DirectivesClassVisitor();
        final String clazz = "WithMethod";
        new ClassReader(
            new BytecodeProgram(
                new BytecodeClass(clazz)
                    .helloWorldMethod()
            ).bytecode().asBytes()
        ).accept(directives, 0);
        final String xml = new Xembler(directives).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't parse simple class with method, result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            new HasMethod("main").inside(clazz)
        );
    }

    @Test
    void parsesClassWithPackage() throws ImpossibleModificationException {
        final DirectivesClassVisitor directives = new DirectivesClassVisitor();
        final String clazz = "some/package/ClassInPackage";
        new ClassReader(
            new BytecodeProgram(new BytecodeClass(clazz)
                .helloWorldMethod()
            ).bytecode()
                .asBytes()
        ).accept(directives, 0);
        final String xml = new Xembler(directives).xml();
        final String name = "ClassInPackage";
        final String pckg = "some.package";
        MatcherAssert.assertThat(
            String.format(
                "Can't parse '%s' class that placed under package '%s', result is: %n%s%n",
                name,
                pckg,
                new XMLDocument(xml)
            ),
            xml,
            new HasClass(name).inside(pckg)
        );
    }
}
