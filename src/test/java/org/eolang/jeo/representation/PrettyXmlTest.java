/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

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
}
