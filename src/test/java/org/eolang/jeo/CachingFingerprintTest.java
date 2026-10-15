/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.EnumSet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Fingerprint invalidation when replacing cached transformation output.
 *
 * @since 0.0.0
 */
final class CachingFingerprintTest {

    @Test
    void replacesAReadOnlyOldFingerprint(@TempDir final Path temp) throws IOException {
        Assumptions.assumeTrue(Files.getFileStore(temp).supportsFileAttributeView("posix"));
        Files.write(temp.resolve("source.class"), new byte[]{1});
        new Caching(CachingFingerprintTest.transformation(temp, "mode-A"), "A").transform();
        final Path stamp = temp.resolve("target.xmir.jeo-cache");
        Files.setPosixFilePermissions(stamp, Collections.singleton(PosixFilePermission.OWNER_READ));
        try {
            Assumptions.assumeFalse(
                Files.isWritable(stamp), "A privileged user bypasses permissions"
            );
            new Caching(CachingFingerprintTest.transformation(temp, "mode-B"), "B").transform();
        } finally {
            Files.setPosixFilePermissions(
                stamp, EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE)
            );
        }
        MatcherAssert.assertThat(
            "The old fingerprint accepted output produced with different options",
            new String(
                new Caching(CachingFingerprintTest.transformation(temp, "mode-A"), "A").transform(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo("mode-A")
        );
    }

    @Test
    void dropsOldOptionsWhenWritingWithoutAFingerprint(
        @TempDir final Path temp
    ) throws IOException {
        Files.write(temp.resolve("source.class"), new byte[]{1});
        new Caching(CachingFingerprintTest.transformation(temp, "mode-A"), "A").transform();
        Files.setLastModifiedTime(temp.resolve("target.xmir"), FileTime.fromMillis(0));
        new Caching(CachingFingerprintTest.transformation(temp, "mode-B")).transform();
        MatcherAssert.assertThat(
            "Unfingerprinted output inherited the preceding options fingerprint",
            new String(
                new Caching(CachingFingerprintTest.transformation(temp, "mode-A"), "A").transform(),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo("mode-A")
        );
    }

    private static Transformation transformation(final Path directory, final String marker) {
        return new Transformation() {
            @Override
            public Path source() {
                return directory.resolve("source.class");
            }

            @Override
            public Path target() {
                return directory.resolve("target.xmir");
            }

            @Override
            public byte[] transform() {
                return marker.getBytes(StandardCharsets.UTF_8);
            }
        };
    }
}
