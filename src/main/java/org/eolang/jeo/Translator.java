/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Translator that applies a translation to a batch of representations.
 * @since 0.1
 */
public interface Translator {

    /**
     * Apply the translation for all representations.
     * @param representations IRs to translate.
     * @return Translated IRs.
     */
    Stream<Path> apply(Stream<Path> representations);
}
