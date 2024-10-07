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
import java.time.DayOfWeek;
import org.eolang.jeo.representation.bytecode.BytecodeEnumAnnotationValue;
import org.eolang.jeo.representation.xmir.XmlAnnotationValue;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test for {@link DirectivesEnumAnnotationValue}.
 * @since 0.6
 */
final class DirectivesEnumAnnotationValueTest {

    @Test
    void createsEnumProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an enum property with a name and a value",
            new Xembler(
                new DirectivesEnumAnnotationValue(
                    "name", Type.getDescriptor(DayOfWeek.class), "MONDAY"
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base,'annotation-property') and count(o) = 4]",
                "./o[contains(@base,'annotation-property')]/o[1]/o[text()='45 4E 55 4D']"
            )
        );
    }

    @Test
    void createsAnnotationEnumProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "Lorg/eolang/jeo/representation/DataType";
        final String value = "BOOL";
        MatcherAssert.assertThat(
            "Incorrect annotation property for enum property",
            new XmlAnnotationValue(
                new XmlNode(
                    new Xembler(new DirectivesEnumAnnotationValue(name, descriptor, value)).xml()
                )
            ).bytecode(),
            Matchers.equalTo(new BytecodeEnumAnnotationValue(name, descriptor, value))
        );
    }
}
