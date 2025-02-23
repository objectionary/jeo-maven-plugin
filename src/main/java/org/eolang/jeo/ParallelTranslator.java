/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Translator that applies a translation to a batch of representations in parallel.
 * @since 0.2
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
     * @param translation Original translation.
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
     * This method is run in parallel.
     * Pay attention to the class loader;
     * It's set for each sub-thread to avoid class loading issues.
     * @param rep Representation to translate.
     * @return Translated representation.
     */
    private Path translate(final Path rep) {
        Thread.currentThread().setContextClassLoader(this.loader);
        return this.translation.apply(rep);
    }

}
