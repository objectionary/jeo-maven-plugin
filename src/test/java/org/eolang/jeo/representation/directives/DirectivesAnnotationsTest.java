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
import org.eolang.jeo.representation.HexData;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAnnotation}.
 * @since 0.1
 */
final class DirectivesAnnotationsTest {

    @Test
    void returnsEmptyDirectviesIfNoAnnotations() {
        MatcherAssert.assertThat(
            "Must return empty directives if no annotations",
            new DirectivesAnnotations().iterator().hasNext(),
            Matchers.is(false)
        );
    }

    @Test
    void returnsSingleAnnotation() throws ImpossibleModificationException {
        final String annotation = "Ljava/lang/Override;";
        final String xml = new Xembler(
            new DirectivesAnnotations()
                .add(new DirectivesAnnotation(annotation, true))
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Must return single annotation with correct descriptor and visibility, but was: %n%s",
                xml
            ),
            xml,
            Matchers.allOf(
                XhtmlMatchers.hasXPath(
                    "/o[@base='tuple' and @name='annotations']/o"
                ),
                XhtmlMatchers.hasXPath(
                    String.format(
                        "/o[@base='tuple' and @name='annotations']/o/o[@base='string' and @name='descriptor' and text()='%s']",
                        new HexData(annotation).value()
                    )
                ),
                XhtmlMatchers.hasXPath(
                    String.format(
                        "/o[@base='tuple' and @name='annotations']/o/o[@base='bool' and @name='visible' and text()='%s']",
                        new HexData(true).value()
                    )
                )
            )
        );
    }

    @Test
    void createsAnnotationWithProperty() throws ImpossibleModificationException {
        final DirectivesAnnotation annotation = new DirectivesAnnotation(
            "Ljava/lang/Override;", true
        );
        annotation.append(DirectivesAnnotationProperty.plain("name", "something"));
        MatcherAssert.assertThat(
            "Can't create an annotation with a property",
            new Xembler(annotation).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@base='annotation' and count(o) = 3]",
                "/o[@base='annotation']/o[@name='descriptor' and text()='4C 6A 61 76 61 2F 6C 61 6E 67 2F 4F 76 65 72 72 69 64 65 3B']",
                "/o[@base='annotation']/o[@name='visible' and text()='01']",
                "/o[@base='annotation']/o[@base='annotation-property']"
            )
        );
    }
}
