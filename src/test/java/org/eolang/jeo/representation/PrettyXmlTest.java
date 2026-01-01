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
                ))
        );
    }
}
