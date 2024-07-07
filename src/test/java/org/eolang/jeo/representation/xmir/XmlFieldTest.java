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
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesField;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlField}.
 *
 * @since 0.3
 */
final class XmlFieldTest {

    @Test
    void parsesXmirFieldSuccessfully() throws ImpossibleModificationException {
        final int access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        final String name = "serialVersionUID";
        final String descriptor = "J";
        final long value = 7_099_057_708_183_571_937L;
        final XmlField field = new XmlField(
            new XmlNode(
                new Xembler(
                    new DirectivesField(
                        access,
                        name,
                        descriptor,
                        "",
                        value
                    )
                ).xml()
            )
        );
        MatcherAssert.assertThat(
            String.format(
                "Failed to parse XMIR field. Expected name '%s', actual '%s'. Expected descriptor '%s', actual '%s'. Expected value '%s', actual '%s'",
                name,
                field.name(),
                descriptor,
                field.descriptor(),
                value,
                field.value()
            ),
            name.equals(field.name())
                && descriptor.equals(field.descriptor())
                && field.value().equals(value),
            Matchers.is(true)
        );
    }

    @Test
    void parsesFieldWithStringValue() throws ImpossibleModificationException {
        final String expected = "7099057708183571937";
        MatcherAssert.assertThat(
            "Failed to parse XMIR field",
            new XmlField(
                new XmlNode(
                    new Xembler(
                        new DirectivesField(
                            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                            "serialVersionUID",
                            "Ljava/lang/String;",
                            "",
                            expected
                        )
                    ).xml()
                )
            ).value(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void parsesFieldWithEmptyStringValue() throws ImpossibleModificationException {
        final String expected = "";
        MatcherAssert.assertThat(
            "Failed to parse XMIR field with empty initial value",
            new XmlField(
                new XmlNode(
                    new Xembler(
                        new DirectivesField(
                            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                            "serialVersionUID",
                            "Ljava/lang/String;",
                            "",
                            expected
                        )
                    ).xml()
                )
            ).value(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void parsesFieldWithNullableStringValue() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Failed to parse XMIR field with null initial value",
            new XmlField(
                new XmlNode(
                    new Xembler(
                        new DirectivesField(
                            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                            "serialVersionUID",
                            "Ljava/lang/String;",
                            "",
                            null
                        )
                    ).xml()
                )
            ).value(),
            Matchers.nullValue()
        );
    }

    @Test
    void retrievesFieldAnnotations() throws ImpossibleModificationException {
        final String override = "java/lang/Override";
        final String safe = "java/lang/SafeVarargs";
        final Optional<XmlAnnotations> opt = new XmlField(
            new XmlNode(
                new Xembler(
                    new DirectivesField()
                        .annotation(new DirectivesAnnotation(override, true))
                        .annotation(new DirectivesAnnotation(safe, true))
                ).xml()
            )
        ).annotations();
        MatcherAssert.assertThat(
            "Annotations are not found",
            opt.isPresent(),
            Matchers.is(true)
        );
        final List<XmlAnnotation> all = opt.get().all();
        final XmlAnnotation first = all.get(0);
        MatcherAssert.assertThat(
            "First annotation descriptor is not correct",
            first.descriptor(),
            Matchers.equalTo(override)
        );
        MatcherAssert.assertThat(
            "First annotation visibility is not correct",
            first.visible(),
            Matchers.is(true)
        );
        final XmlAnnotation second = all.get(1);
        MatcherAssert.assertThat(
            "Second annotation descriptor is not correct",
            second.descriptor(),
            Matchers.equalTo(safe)
        );
        MatcherAssert.assertThat(
            "Second annotation visibility is not correct",
            second.visible(),
            Matchers.is(true)
        );
    }
}
