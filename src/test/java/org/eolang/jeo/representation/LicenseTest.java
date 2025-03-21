/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test suite for {@link License}.
 *
 * @since 0.6.27
 */
final class LicenseTest {

    @Test
    void readsLicenseFileCorrectly() throws Exception {
        final String name = "LICENSE.txt";
        MatcherAssert.assertThat(
            new FormattedText(
                "Unexpected file:'%s' content",
                name
            ).asString(),
            new License(name).value(),
            Matchers.containsString("MIT")
        );
    }
}
