/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.manifests.Manifests;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Fingerprint}.
 *
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

    @Test
    void includesCurrentJeoVersion() {
        final String version;
        if (Manifests.exists("JEO-Version")) {
            version = Manifests.read("JEO-Version");
        } else {
            version = "unknown";
        }
        MatcherAssert.assertThat(
            "the fingerprint must contain the actual JEO version",
            new Fingerprint(new Format()).toString(),
            Matchers.containsString(String.format("jeo-version=%s", version))
        );
    }

    @Test
    void usesFallbackWhenJeoVersionIsMissing() {
        MatcherAssert.assertThat(
            "the fingerprint must use a fallback when the manifest attribute is missing",
            new Fingerprint(new Format(), "unknown").toString(),
            Matchers.containsString("jeo-version=unknown")
        );
    }

    @Test
    void differsWhenJeoVersionDiffers() {
        MatcherAssert.assertThat(
            "different JEO versions must produce different fingerprints",
            new Fingerprint(new Format(), "1.0.0").toString(),
            Matchers.not(
                Matchers.equalTo(new Fingerprint(new Format(), "2.0.0").toString())
            )
        );
    }
}
