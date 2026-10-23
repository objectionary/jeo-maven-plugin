/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Locale;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeParamAnnotations}.
 *
 * @since 0.15.0
 */
final class BytecodeParamAnnotationsTest {

    @Test
    void formatsNameWithAsciiDigitsUnderArabicLocale() throws ImpossibleModificationException {
        final Locale before = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("ar-EG"));
        try {
            MatcherAssert.assertThat(
                "Directives must use ASCII digits in any default locale",
                new Xembler(
                    new BytecodeParamAnnotations(
                        0,
                        new BytecodeAnnotations()
                    ).directives(new Format())
                ).xml(),
                Matchers.containsString("name=\"param-annotations-0\"")
            );
        } finally {
            Locale.setDefault(before);
        }
    }

    @Test
    void formatsNameWithAsciiDigitsForHigherIndexUnderArabicLocale()
        throws ImpossibleModificationException {
        final Locale before = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("ar-EG"));
        try {
            MatcherAssert.assertThat(
                "Directives must format multi-digit index with ASCII digits",
                new Xembler(
                    new BytecodeParamAnnotations(
                        12,
                        new BytecodeAnnotations()
                    ).directives(new Format())
                ).xml(),
                Matchers.containsString("name=\"param-annotations-12\"")
            );
        } finally {
            Locale.setDefault(before);
        }
    }
}
