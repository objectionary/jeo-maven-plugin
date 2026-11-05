/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

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
            "Elapsed time should replace the default value of the 'ms' attribute",
            new MeasuredEo(
                new Directives().add("object").attr("ms", "-1").up()
            ).asXml().xpath("/object/@ms").get(0),
            Matchers.not("-1")
        );
    }
}
