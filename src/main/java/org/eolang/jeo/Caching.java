/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cached transformation.
 *
 * <p>This class implements a caching mechanism for transformations. It checks if a transformation
 * has already been performed by comparing file modification times, and skips redundant
 * transformations to improve performance.</p>
 *
 * @since 0.6.0
 */
public final class Caching implements Transformation {

    /**
     * Original transformation.
     */
    private final Transformation origin;

    /**
     * Options the target was produced with, empty when they are unknown.
     */
    private final String print;

    /**
     * Constructor.
     *
     * @param origin Original transformation to cache
     */
    Caching(final Transformation origin) {
        this(origin, "");
    }

    /**
     * Constructor.
     *
     * @param origin Original transformation to cache
     * @param fingerprint Options the target is produced with
     */
    Caching(final Transformation origin, final String fingerprint) {
        this.origin = origin;
        this.print = fingerprint;
    }

    @Override
    public Path source() {
        return this.origin.source();
    }

    @Override
    public Path target() {
        return this.origin.target();
    }

    @Override
    public byte[] transform() {
        try {
            return this.tryTransform();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to transform '%s' to '%s'",
                    this.source(),
                    this.target()
                ),
                exception
            );
        }
    }

    private byte[] tryTransform() throws IOException {
        final byte[] result;
        final Path target = this.target();
        if (this.alreadyTransformed()) {
            Logger.info(
                this,
                "The file '%s' is already transformed to '%s'. Skipping.",
                this.source(),
                target
            );
            result = Files.readAllBytes(target);
        } else {
            final byte[] transform = this.origin.transform();
            Files.createDirectories(target.getParent());
            Files.write(target, transform);
            if (!this.print.isEmpty()) {
                Files.write(
                    this.cache(), this.stamp(transform).getBytes(StandardCharsets.UTF_8)
                );
            }
            result = transform;
        }
        return result;
    }

    private boolean alreadyTransformed() throws IOException {
        return Files.exists(this.target())
            && Files.exists(this.source())
            && this.unchanged();
    }

    private boolean unchanged() throws IOException {
        final boolean same;
        final Path cache = this.cache();
        if (this.print.isEmpty()) {
            same = this.newer();
        } else if (Files.exists(cache)) {
            final String stored = new String(Files.readAllBytes(cache), StandardCharsets.UTF_8);
            final String actual = this.stamp(Files.readAllBytes(this.target()));
            same = stored.contains("\n")
                && stored.substring(0, stored.lastIndexOf('\n'))
                .equals(actual.substring(0, actual.lastIndexOf('\n')))
                && (stored.equals(actual) || this.newer());
        } else {
            same = false;
        }
        return same;
    }

    private boolean newer() throws IOException {
        return Files.getLastModifiedTime(this.target())
            .compareTo(Files.getLastModifiedTime(this.source())) >= 0;
    }

    private String stamp(final byte[] target) throws IOException {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return String.join(
                "\n",
                this.print,
                new BigInteger(1, digest.digest(Files.readAllBytes(this.source()))).toString(16),
                new BigInteger(1, digest.digest(target)).toString(16)
            );
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private Path cache() {
        final Path target = this.target();
        return target.resolveSibling(String.format("%s.jeo-cache", target.getFileName()));
    }
}
