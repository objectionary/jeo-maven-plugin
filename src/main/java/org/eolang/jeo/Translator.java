/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Translator that applies a translation to a batch of representations.
 * <p>This interface defines the contract for batch processing of file transformations.
 * Implementations can process files sequentially or in parallel to improve performance.</p>
 * @since 0.1.0
 */
public interface Translator {

    /**
     * Apply the translation for all representations.
     * @param representations Stream of intermediate representations to translate
     * @return Stream of translated intermediate representations
     */
    Stream<Path> apply(Stream<Path> representations);
}
