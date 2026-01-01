/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;

/**
 * Convenient transformation wrapper that provides informative error messages in case of failure.
 * @since 0.14.0
 * @checkstyle IllegalCatchCheck (100 lines)
 */
final class Informative implements Transformation {

    /**
     * Original transformation to delegate to.
     */
    private final Transformation original;

    /**
     * Constructor.
     * @param original Delegate transformation.
     */
    Informative(final Transformation original) {
        this.original = original;
    }

    @Override
    public Path source() {
        return this.original.source();
    }

    @Override
    public Path target() {
        return this.original.target();
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public byte[] transform() {
        try {
            return this.original.transform();
        } catch (final RuntimeException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to transform %s to %s: %s",
                    this.original.source(),
                    this.original.target(),
                    exception.getMessage()
                ),
                exception
            );
        }
    }
}
