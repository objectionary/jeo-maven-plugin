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

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.DataType;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAnnotationProperty}.
 *
 * @since 0.3
 */
class DirectivesAnnotationPropertyTest {

    @Test
    void createsPlainProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create a plain property with a name and a value",
            new Xembler(
                DirectivesAnnotationProperty.plain("name", "particular name")
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='annotation-property' and count(o) = 3]",
                "./o[@base='annotation-property']/o[@name='type' and text()='50 4C 41 49 4E']"
            )
        );
    }

    @Test
    void createsEnumProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an enum property with a name and a value",
            new Xembler(
                DirectivesAnnotationProperty.enump(
                    "name", Type.getDescriptor(DataType.class), "BOOL"
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='annotation-property' and count(o) = 4]",
                "./o[@base='annotation-property']/o[@name='type' and text()='45 4E 55 4D']"
            )
        );
    }

    @Test
    void createsArrayProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an array property with a name",
            new Xembler(
                DirectivesAnnotationProperty.array("name")
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='annotation-property' and count(o) = 2]",
                "./o[@base='annotation-property']/o[@name='type' and text()='41 52 52 41 59']"
            )
        );
    }

    @Test
    void createsAnnotationProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an annotation property with a name and a descriptor",
            new Xembler(
                DirectivesAnnotationProperty.annotation("name", Type.getDescriptor(DataType.class))
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='annotation-property' and count(o) = 3]",
                "./o[@base='annotation-property']/o[@name='type' and text()='41 4E 4E 4F 54 41 54 49 4F 4E']"
            )
        );
    }
}
