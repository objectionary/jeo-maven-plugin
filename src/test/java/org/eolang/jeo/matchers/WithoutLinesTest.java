/*
 * The MIT License (MIT)
 *
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
 * Test case for {@link WithoutLines}.
 *
 * @since 0.6
 */
final class WithoutLinesTest {

    @Test
    void transformsToXmlWithoutLines() {
        final XML lineless = new WithoutLines(
            new XMLDocument(
                String.join(
                    "",
                    "<root>",
                    "<o line=\"val\">data</o>",
                    "<anotherElement>",
                    "<o line=\"\">data</o>",
                    "</anotherElement>",
                    "</root>"
                )
            )
        ).value();
        final XML expected = new XMLDocument(
            String.join(
                "",
                "<root>",
                "<o>data</o>",
                "<anotherElement>",
                "<o>data</o>",
                "</anotherElement>",
                "</root>"
            )
        );
        MatcherAssert.assertThat(
            String.format(
                "XML transformed: '%s', but does not match with expected XML'%s'",
                lineless,
                expected
            ),
            lineless,
            new IsEqual<>(expected)
        );
    }
}
