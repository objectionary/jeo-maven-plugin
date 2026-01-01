/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.JavaCodec;
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
                "/o[contains(@name,'annotations')]",
                "/o[count(o)=1]",
                "/o/o[contains(@base,'seq.of0')]"
            )
        );
    }

    @Test
    void returnsSingleAnnotation() throws ImpossibleModificationException {
        final String annotation = "Ljava/lang/Override;";
        MatcherAssert.assertThat(
            "Must return single annotation with correct descriptor and visibility",
            new Xembler(
                new DirectivesAnnotations().add(new DirectivesAnnotation(annotation, true))
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "seq.of1").toXpath(),
                "/o[contains(@name,'annotations')]/o",
                String.format(
                    "/o[contains(@name,'annotations')]/o/o[2][contains(@base,'string')]/o[1]/o[text()='%s']",
                    new DirectivesValue(0, new Format(), annotation).hex(new JavaCodec())
                ),
                "/o[contains(@name,'annotations')]/o/o[3][contains(@base,'true')]"
            )
        );
    }

    @Test
    void createsAnnotationWithProperty() throws ImpossibleModificationException {
        final Format format = new Format();
        MatcherAssert.assertThat(
            "Can't create an annotation with a property",
            new Xembler(
                new DirectivesAnnotation(
                    0,
                    format,
                    "Ljava/lang/Override;",
                    true,
                    new DirectivesPlainAnnotationValue(0, format, "name", "something")
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "annotation").toXpath(),
                "/o[count(o) = 4]",
                "/o/o[2]/o[1]/o[text()='4C-6A-61-76-61-2F-6C-61-6E-67-2F-4F-76-65-72-72-69-64-65-3B']",
                "/o/o[3][contains(@base,'true')]",
                new JeoBaseXpath("/o/o", "annotation-property").toXpath()
            )
        );
    }
}
