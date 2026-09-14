/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Fingerprint}.
 * @since 0.15.0
 */
final class FingerprintTest {

    @Test
    void differsWhenTheModeDiffers() {
        MatcherAssert.assertThat(
            "two modes must not share one fingerprint",
            new Fingerprint(new Format(Format.MODE, "debug")).toString(),
            Matchers.not(
                Matchers.equalTo(new Fingerprint(new Format(Format.MODE, "short")).toString())
            )
        );
    }

    @Test
    void repeatsItselfForTheSameOptions() {
        MatcherAssert.assertThat(
            "the same options must give the same fingerprint",
            new Fingerprint(new Format(Format.MODE, "debug")).toString(),
            Matchers.equalTo(new Fingerprint(new Format(Format.MODE, "debug")).toString())
        );
    }
}
