/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;

/**
 * Transformation that does nothing, for tests.
 *
 * @since 0.18.0
 */
final class FakeTransformation implements Transformation {

    /**
     * Source and target of the transformation.
     */
    private final Path path;

    /**
     * Constructor.
     *
     * @param path Source and target of the transformation
     */
    FakeTransformation(final Path path) {
        this.path = path;
    }

    @Override
    public Path source() {
        return this.path;
    }

    @Override
    public Path target() {
        return this.path;
    }

    @Override
    public byte[] transform() {
        return new byte[0];
    }
}
