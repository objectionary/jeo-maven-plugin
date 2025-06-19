/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.matchers;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link SameXml}.
 * This class verifies the XML comparison functionality,
 * ensuring that XML documents can be properly matched regardless of formatting differences.
 *
 * @since 0.6.0
 */
final class SameXmlTest {

    @Test
    void matchesXmls() {
        final XML input = new XMLDocument(
            String.join(
                "",
                "<root>",
                "<o line=\"val\">data</o>",
                "<anotherElement>",
                "<o line=\"\">data</o>",
                "</anotherElement>",
                "</root>"
            )
        );
        final String lineless = new XMLDocument(
            String.join(
                "",
                "<root>",
                "<o>data</o>",
                "<anotherElement>",
                "<o>data</o>",
                "</anotherElement>",
                "</root>"
            )
        ).toString();
        final boolean matches = new SameXml(input).matchesSafely(lineless);
        MatcherAssert.assertThat(
            String.format(
                "Input XML ('%s') should match with same lineless XML ('%s'), but was not",
                input,
                lineless
            ),
            matches,
            new IsEqual<>(true)
        );
    }
}
