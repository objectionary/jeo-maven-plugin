/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Test cases for {@link PrettyXml}.
 *
 * @since 0.11.0
 */
final class PrettyXmlTest {

    @Test
    void formatsXml() {
        MatcherAssert.assertThat(
            "We can't format XML output with correct indentation (two spaces)",
            new PrettyXml(
                String.join(
                    "\n",
                    "<o>",
                    "<o>",
                    "<o>3.14</o>",
                    "</o>",
                    "</o>",
                    ""
                )
            ).toString(),
            Matchers.equalTo(
                String.join(
                    "\n",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                    "",
                    "<o>",
                    "  <o>",
                    "    <o>3.14</o>",
                    "  </o>",
                    "</o>",
                    ""
                )
            )
        );
    }

    @Test
    void preservesListingLineBreaksAndIndentation() {
        MatcherAssert.assertThat(
            "PrettyXml must keep line breaks and indentation inside <listing>",
            new PrettyXml(
                String.join(
                    "\n",
                    "<object>",
                    "<listing>// class version 65.0 (65)",
                    "public class App {",
                    "  public &lt;init&gt;()V",
                    "    RETURN",
                    "}",
                    "</listing>",
                    "<o>  foo  bar  </o>",
                    "</object>",
                    ""
                )
            ).toString(),
            Matchers.equalTo(
                String.join(
                    "\n",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                    "",
                    "<object>",
                    "  <listing>// class version 65.0 (65)",
                    "public class App {",
                    "  public &lt;init&gt;()V",
                    "    RETURN",
                    "}",
                    "</listing>",
                    "  <o>foo bar</o>",
                    "</object>",
                    ""
                )
            )
        );
    }

    @Test
    void preservesBlankLinesInsideListing() {
        MatcherAssert.assertThat(
            "PrettyXml must keep blank lines and trailing spaces inside <listing>",
            new PrettyXml(
                String.join(
                    "\n",
                    "<object>",
                    "  <listing>first",
                    "",
                    "  second</listing>",
                    "</object>",
                    ""
                )
            ).toString(),
            Matchers.equalTo(
                String.join(
                    "\n",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                    "",
                    "<object>",
                    "  <listing>first",
                    "",
                    "  second</listing>",
                    "</object>",
                    ""
                )
            )
        );
    }

    @Test
    @Timeout(5L)
    void handlesCollidingPlaceholderTokensWithoutHanging() {
        MatcherAssert.assertThat(
            "PrettyXml must advance placeholder counter without hanging on colliding text",
            new PrettyXml(
                String.join(
                    "\n",
                    "<object>",
                    "<listing>jeo-listing-0-13 jeo-listing-0-16 jeo-listing-0-</listing>",
                    "</object>",
                    ""
                )
            ).toString(),
            Matchers.equalTo(
                String.join(
                    "\n",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                    "",
                    "<object>",
                    "  <listing>jeo-listing-0-13 jeo-listing-0-16 jeo-listing-0-</listing>",
                    "</object>",
                    ""
                )
            )
        );
    }

    @Test
    void restoresMoreThanTenDistinctListingsAccurately() {
        final StringBuilder input = new StringBuilder("<object>");
        final StringBuilder expected = new StringBuilder(
            String.join("\n", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "", "<object>", "")
        );
        for (int idx = 0; idx < 12; idx += 1) {
            final String line = String.format("  <listing>item-%d\n    line2</listing>", idx);
            input.append('\n').append(line);
            expected.append(line).append('\n');
        }
        input.append("\n</object>\n");
        expected.append("</object>\n");
        MatcherAssert.assertThat(
            "PrettyXml must restore all 12 listings without prefix collisions",
            new PrettyXml(input.toString()).toString(),
            Matchers.equalTo(expected.toString())
        );
    }
}
