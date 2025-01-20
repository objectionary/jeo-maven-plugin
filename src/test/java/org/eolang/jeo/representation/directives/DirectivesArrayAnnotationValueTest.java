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
import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlAnnotationValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesArrayAnnotationValue}.
 * @since 0.6
 */
final class DirectivesArrayAnnotationValueTest {

    @Test
    void createsArrayProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an array property with a name",
            new Xembler(
                new DirectivesArrayAnnotationValue(
                    "name",
                    Collections.singletonList(new DirectivesPlainAnnotationValue())
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base,'annotation-property')]",
                "./o[contains(@base,'annotation-property')]/o[1]/o[text()='41-52-52-41-59']"
            )
        );
    }

    @Test
    void createsAnnotationArrayProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "java/lang/Override";
        final boolean visible = true;
        MatcherAssert.assertThat(
            "Incorrect array annotation property",
            new XmlAnnotationValue(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesArrayAnnotationValue(
                            name,
                            Collections.singletonList(new DirectivesAnnotation(descriptor, visible))
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeArrayAnnotationValue(
                    name,
                    Collections.singletonList(new BytecodeAnnotation(descriptor, visible))
                )
            )
        );
    }
}
