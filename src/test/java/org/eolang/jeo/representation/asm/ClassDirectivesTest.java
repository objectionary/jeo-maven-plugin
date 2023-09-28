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
package org.eolang.jeo.representation.asm;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link ClassDirectives}.
 * @since 0.1.0
 */
class ClassDirectivesTest {

    @Test
    void parsesSimpleClassWithoutConstructor() throws ImpossibleModificationException {
        final ClassDirectives directives = new ClassDirectives();
        new ClassReader(new BytecodeClass().bytecode().asBytes()).accept(directives, 0);
        MatcherAssert.assertThat(
            "Can't parse simple class without constructor",
            new XMLDocument(new Xembler(directives).xml()),
            XhtmlMatchers.hasXPath("/program/objects/o[@name='class__Simple']")
        );
    }

    @Test
    void parsesSimpleClassWithMethod() throws ImpossibleModificationException {
        final ClassDirectives directives = new ClassDirectives();
        new ClassReader(
            new BytecodeClass("WithMethod")
                .withMethod("main")
                .up()
                .bytecode()
                .asBytes()
        ).accept(directives, 0);
        MatcherAssert.assertThat(
            "Can't parse simple class with method",
            new XMLDocument(new Xembler(directives).xml()),
            Matchers.allOf(
                XhtmlMatchers.hasXPath(
                    "/program/objects/o[@name='class__WithMethod']/o[@name='main']"
                ),
                XhtmlMatchers.hasXPath(
                    "/program/objects/o[@name='class__WithMethod']/o[@name='main']/o[@base='seq']"
                )
            )
        );
    }
}
