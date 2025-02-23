/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;

/**
 * Transformation.
 * @since 0.6
 */
public interface Transformation {

    /**
     * The path to the file to be transformed.
     * @return Path to the file.
     */
    Path source();

    /**
     * The path to the transformed file.
     * @return Path to the transformed file.
     */
    Path target();

    /**
     * Transform the file.
     * @return Transformed file.
     */
    byte[] transform();
}
