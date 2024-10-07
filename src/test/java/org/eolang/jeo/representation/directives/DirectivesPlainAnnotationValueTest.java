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
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.xmir.XmlAnnotationValue;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesPlainAnnotationValue}.
 * @since 0.6
 */
final class DirectivesPlainAnnotationValueTest {

    @Test
    void createsPlainProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create a plain property with a name and a value",
            new Xembler(
                new DirectivesPlainAnnotationValue("name", "particular name")
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base,'annotation-property') and count(o) = 3]",
                "./o[contains(@base,'annotation-property')]/o[1]/o[text()='50 4C 41 49 4E']"
            )
        );
    }

    @Test
    void createsAnnotationPlainProperty() throws ImpossibleModificationException {
        final String name = "hello";
        final int value = 1;
        MatcherAssert.assertThat(
            "Incorrect annotation property for plain property",
            new XmlAnnotationValue(
                new XmlNode(
                    new Xembler(new DirectivesPlainAnnotationValue(name, value)).xml()
                )
            ).bytecode(),
            Matchers.equalTo(new BytecodePlainAnnotationValue(name, value))
        );
    }
}
