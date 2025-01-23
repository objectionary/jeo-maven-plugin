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
import org.eolang.jeo.representation.bytecode.Codec;
import org.eolang.jeo.representation.bytecode.PlainCodec;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAnnotation}.
 * @since 0.1
 */
final class DirectivesAnnotationsTest {

    @Test
    void returnsEmptyDirectviesIfNoAnnotations() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Must return empty directives if no annotations",
            new Xembler(new DirectivesAnnotations()).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'jeo.seq.of0') and contains(@as,'annotations')]"
            )
        );
    }

    @Test
    void returnsSingleAnnotation() throws ImpossibleModificationException {
        final String annotation = "Ljava/lang/Override;";
        Codec codec = new PlainCodec();
        MatcherAssert.assertThat(
            "Must return single annotation with correct descriptor and visibility",
            new Xembler(
                new DirectivesAnnotations().add(new DirectivesAnnotation(annotation, true))
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'seq.of1') and contains(@as,'annotations')]/o",
                String.format(
                    "/o[contains(@base,'seq.of1') and contains(@as,'annotations')]/o/o[1][contains(@base,'string')]/o[text()='%s']",
                    new DirectivesValue(annotation).hex(codec)
                ),
                String.format(
                    "/o[contains(@base,'seq.of1') and contains(@as,'annotations')]/o/o[2][contains(@base,'bool')]/o[text()='%s']",
                    new DirectivesValue(true).hex(codec)
                )
            )
        );
    }

    @Test
    void createsAnnotationWithProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an annotation with a property",
            new Xembler(
                new DirectivesAnnotation(
                    "Ljava/lang/Override;",
                    true,
                    new DirectivesPlainAnnotationValue("name", "something")
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'annotation') and count(o) = 3]",
                "/o[contains(@base,'annotation')]/o[1]/o[text()='4C-6A-61-76-61-2F-6C-61-6E-67-2F-4F-76-65-72-72-69-64-65-3B']",
                "/o[contains(@base,'annotation')]/o[2]/o[text()='01-']",
                "/o[contains(@base,'annotation')]/o[contains(@base,'annotation-property')]"
            )
        );
    }
}
