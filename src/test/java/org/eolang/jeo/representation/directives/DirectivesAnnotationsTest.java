/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.Codec;
import org.eolang.jeo.representation.bytecode.JavaCodec;
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
            new DirectivesAnnotations(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void returnsSingleAnnotation() throws ImpossibleModificationException {
        final String annotation = "Ljava/lang/Override;";
        final Codec codec = new JavaCodec();
        MatcherAssert.assertThat(
            "Must return single annotation with correct descriptor and visibility",
            new Xembler(
                new DirectivesAnnotations().add(new DirectivesAnnotation(annotation, true))
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'seq.of1') and contains(@as,'annotations')]/o",
                String.format(
                    "/o[contains(@base,'seq.of1') and contains(@as,'annotations')]/o/o[1][contains(@base,'string')]/o[1]/o[text()='%s']",
                    new DirectivesValue(annotation).hex(codec)
                ),
                "/o[contains(@base,'seq.of1') and contains(@as,'annotations')]/o/o[2][contains(@base,'true')]"
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
                "/o[contains(@base,'annotation')]/o[1]/o[1]/o[text()='4C-6A-61-76-61-2F-6C-61-6E-67-2F-4F-76-65-72-72-69-64-65-3B']",
                "/o[contains(@base,'annotation')]/o[2][contains(@base,'true')]",
                "/o[contains(@base,'annotation')]/o[contains(@base,'annotation-property')]"
            )
        );
    }
}
