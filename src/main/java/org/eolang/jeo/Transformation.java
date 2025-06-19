/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;

/**
 * Transformation interface.
 *
 * <p>This interface defines the contract for transformations that convert
 * files from one format to another. Implementations handle specific transformation
 * types like assembling XMIR to bytecode or disassembling bytecode to XMIR.</p>
 * @since 0.6.0
 */
public interface Transformation {

    /**
     * The path to the file to be transformed.
     * @return Path to the source file
     */
    Path source();

    /**
     * The path to the transformed file.
     * @return Path to the target file after transformation
     */
    Path target();

    /**
     * Transform the file.
     * @return Transformed file content as byte array
     */
    byte[] transform();
}
