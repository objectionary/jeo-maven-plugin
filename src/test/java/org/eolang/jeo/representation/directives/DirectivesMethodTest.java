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

import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlNode;
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
        final XmlMethod method = new XmlMethod(
            new XmlNode(
                new Xembler(
                    new DirectivesMethod(
                        name,
                        new DirectivesMethodProperties(access, descriptor, signature)
                    )
                ).xml()
            )
        );
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
        MatcherAssert.assertThat(
            "Method signature is not equal to expected",
            method.signature(),
            Matchers.equalTo(signature)
        );
    }

}