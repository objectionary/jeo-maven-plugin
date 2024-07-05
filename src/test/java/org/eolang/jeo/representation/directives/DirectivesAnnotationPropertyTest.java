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
import java.util.List;
import org.eolang.jeo.representation.DataType;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.xmir.XmlAnnotationProperty;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAnnotationProperty}.
 *
 * @since 0.3
 */
final class DirectivesAnnotationPropertyTest {

    @Test
    void createsPlainProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create a plain property with a name and a value",
            new Xembler(
                DirectivesAnnotationProperty.plain("name", "particular name")
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='annotation-property' and count(o) = 3]",
                "./o[@base='annotation-property']/o[1][text()='50 4C 41 49 4E']"
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
                "./o[@base='annotation-property']/o[1][text()='45 4E 55 4D']"
            )
        );
    }

    @Test
    void createsArrayProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an array property with a name",
            new Xembler(
                DirectivesAnnotationProperty.array(
                    "name",
                    new DirectivesAnnotationProperty(DirectivesAnnotationProperty.Type.PLAIN)
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='annotation-property']",
                "./o[@base='annotation-property']/o[1][text()='41 52 52 41 59']"
            )
        );
    }

    @Test
    void createsAnnotationProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an annotation property with a name and a descriptor",
            new Xembler(
                DirectivesAnnotationProperty.annotation(
                    "name",
                    Type.getDescriptor(DataType.class),
                    new DirectivesAnnotationProperty(DirectivesAnnotationProperty.Type.PLAIN)
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@base='annotation-property']",
                "./o[@base='annotation-property']/o[1][text()='41 4E 4E 4F 54 41 54 49 4F 4E']"
            )
        );
    }

    @Test
    void createsAnnotationPlainProperty() throws ImpossibleModificationException {
        final String name = "hello";
        final int value = 1;
        final XmlAnnotationProperty property = new XmlAnnotationProperty(
            new XmlNode(
                new Xembler(DirectivesAnnotationProperty.plain(name, value)).xml()
            )
        );
        final List<Object> params = property.params();
        MatcherAssert.assertThat(
            "Incorrect annotation property type",
            property.type(),
            Matchers.equalTo("PLAIN")
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property name",
            params.get(0),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property value",
            params.get(1),
            Matchers.equalTo(value)
        );
    }

    @Test
    void createsAnnotationEnumProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "Lorg/eolang/jeo/representation/DataType";
        final String value = "BOOL";
        final XmlAnnotationProperty property = new XmlAnnotationProperty(
            new XmlNode(
                new Xembler(DirectivesAnnotationProperty.enump(name, descriptor, value)).xml()
            )
        );
        final List<Object> params = property.params();
        MatcherAssert.assertThat(
            "Incorrect annotation property type",
            property.type(),
            Matchers.equalTo("ENUM")
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property name",
            params.get(0),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property descriptor",
            params.get(1),
            Matchers.equalTo(descriptor)
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property value",
            params.get(2),
            Matchers.equalTo(value)
        );
    }

    @Test
    void createsAnnotationArrayProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "java/lang/Override";
        final boolean visible = true;
        final XmlAnnotationProperty property = new XmlAnnotationProperty(
            new XmlNode(
                new Xembler(
                    DirectivesAnnotationProperty.array(
                        name,
                        new DirectivesAnnotation(descriptor, visible)
                    )
                ).xml()
            )
        );
        final List<Object> params = property.params();
        MatcherAssert.assertThat(
            "Incorrect annotation property type",
            property.type(),
            Matchers.equalTo("ARRAY")
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property name",
            params.get(0),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property child",
            (BytecodeAnnotation) params.get(1),
            Matchers.equalTo(new BytecodeAnnotation(descriptor, visible))
        );
    }

    @Test
    void createsAnnotationAnnotationProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "java/lang/Override";
        final boolean visible = true;
        final XmlAnnotationProperty property = new XmlAnnotationProperty(
            new XmlNode(
                new Xembler(
                    DirectivesAnnotationProperty.annotation(
                        name,
                        descriptor,
                        new DirectivesAnnotation(descriptor, visible)
                    )
                ).xml()
            )
        );
        final List<Object> params = property.params();
        MatcherAssert.assertThat(
            "Incorrect annotation property type",
            property.type(),
            Matchers.equalTo("ANNOTATION")
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property name",
            params.get(0),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property descriptor",
            params.get(1),
            Matchers.equalTo(descriptor)
        );
        MatcherAssert.assertThat(
            "Incorrect annotation property child",
            (BytecodeAnnotation) params.get(2),
            Matchers.equalTo(new BytecodeAnnotation(descriptor, visible))
        );
    }
}
