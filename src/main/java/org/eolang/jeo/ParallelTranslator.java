/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;
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
     * Number of threads for parallel processing.
     * <p>When 0, the number of available processors is used automatically.</p>
     */
    private final int threads;

    /**
     * Constructor.
     * @param translation Function to apply to each path representation
     */
    ParallelTranslator(final Function<? super Path, ? extends Path> translation) {
        this(translation, 0);
    }

    /**
     * Constructor.
     * @param translation Function to apply to each path representation
     * @param threads Number of threads (0 = use available processors automatically)
     */
    ParallelTranslator(
        final Function<? super Path, ? extends Path> translation,
        final int threads
    ) {
        this.translation = translation;
        this.loader = Thread.currentThread().getContextClassLoader();
        this.threads = threads;
    }

    @Override
    public Stream<Path> apply(final Stream<Path> representations) {
        final int parallelism;
        if (this.threads == 0) {
            parallelism = Runtime.getRuntime().availableProcessors();
        } else {
            parallelism = this.threads;
        }
        Logger.info(this, "Using %d thread(s) for parallel processing", parallelism);
        @SuppressWarnings("PMD.CloseResource")
        final ForkJoinPool pool = new ForkJoinPool(parallelism);
        try {
            return pool.submit(
                () -> representations.parallel().map(this::translate).collect(Collectors.toList())
            ).get().stream();
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Parallel translation was interrupted", exception);
        } catch (final ExecutionException exception) {
            throw new IllegalStateException("Parallel translation failed", exception);
        } finally {
            pool.shutdown();
        }
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
