/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MeasuredEo}.
 *
 * @since 0.15.0
 */
final class MeasuredEoTest {

    @Test
    void writesElapsedTimeIntoRootElement() throws Exception {
        MatcherAssert.assertThat(
            "Elapsed time should replace default value of 'ms'",
            new MeasuredEo(
                new Directives().add("object").attr("ms", "-1").up()
            ).asXml().xpath("/object/@ms").get(0),
            Matchers.not("-1")
        );
    }

    @Test
    void writesAsciiDigitsUnderArabicLocale() throws Exception {
        final Locale before = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("ar-EG"));
        try {
            MatcherAssert.assertThat(
                "Attribute 'ms' must contain ASCII digits in Arabic locale",
                new MeasuredEo(
                    new Directives().add("object").attr("ms", "-1").up()
                ).asXml().xpath("/object/@ms").get(0),
                Matchers.matchesPattern("^[0-9]+$")
            );
        } finally {
            Locale.setDefault(before);
        }
    }

    @Test
    void parsesElapsedTimeUnderArabicLocale() throws Exception {
        final Locale before = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("ar-EG"));
        try {
            MatcherAssert.assertThat(
                "Attribute 'ms' must be parseable as a non-negative long",
                Long.parseLong(
                    new MeasuredEo(
                        new Directives().add("object").attr("ms", "-1").up()
                    ).asXml().xpath("/object/@ms").get(0)
                ),
                Matchers.greaterThanOrEqualTo(0L)
            );
        } finally {
            Locale.setDefault(before);
        }
    }

    @Test
    void writesAsciiDigitsUnderPersianLocale() throws Exception {
        final Locale before = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("fa-IR"));
        try {
            MatcherAssert.assertThat(
                "Attribute 'ms' must contain ASCII digits in Persian locale",
                new MeasuredEo(
                    new Directives().add("object").attr("ms", "-1").up()
                ).asXml().xpath("/object/@ms").get(0),
                Matchers.matchesPattern("^[0-9]+$")
            );
        } finally {
            Locale.setDefault(before);
        }
    }

    @Test
    void preservesSurroundingAttributes() throws Exception {
        final Locale before = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("ar-EG"));
        try {
            MatcherAssert.assertThat(
                "Surrounding name attribute should be preserved",
                new MeasuredEo(
                    new Directives()
                        .add("object")
                        .attr("name", "test-object")
                        .attr("ms", "-1")
                        .up()
                ).asXml().xpath("/object/@name").get(0),
                Matchers.equalTo("test-object")
            );
        } finally {
            Locale.setDefault(before);
        }
    }
}
