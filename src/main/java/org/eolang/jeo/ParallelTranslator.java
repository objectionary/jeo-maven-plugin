/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Translator that applies a translation to a batch of representations in parallel.
 *
 * <p>This class implements parallel processing of transformations to improve performance.
 * It ensures that each parallel thread has the correct class loader context to avoid
 * class loading issues during concurrent execution.</p>
 * @since 0.2.0
 */
public final class ParallelTranslator implements Translator {

    /**
     * Original translation.
     */
    private final Function<? super Path, ? extends Path> translation;

    /**
     * Class loader.
     */
    private final ClassLoader loader;

    /**
     * Constructor.
     * @param translation Function to apply to each path representation
     */
    ParallelTranslator(final Function<? super Path, ? extends Path> translation) {
        this.translation = translation;
        this.loader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public Stream<Path> apply(final Stream<Path> representations) {
        return representations.parallel().map(this::translate);
    }

    /**
     * Translate a representation.
     * <p>This method is run in parallel. Pay attention to the class loader;
     * it's set for each sub-thread to avoid class loading issues.</p>
     * @param rep Path representation to translate
     * @return Translated path representation
     */
    private Path translate(final Path rep) {
        Thread.currentThread().setContextClassLoader(this.loader);
        return this.translation.apply(rep);
    }

}
