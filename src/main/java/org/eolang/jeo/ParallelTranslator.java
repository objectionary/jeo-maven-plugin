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
 * <p>This class implements parallel processing of transformations to improve performance. It
 * ensures that each parallel thread has the correct class loader context to avoid class loading
 * issues during concurrent execution.</p>
 *
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
     *
     * <p>When 0, the number of available processors is used automatically.</p>
     */
    private final int threads;

    /**
     * Constructor.
     *
     * @param translation Function to apply to each path representation
     */
    ParallelTranslator(final Function<? super Path, ? extends Path> translation) {
        this(translation, 0);
    }

    /**
     * Constructor.
     *
     * @param translation Function to apply to each path representation
     * @param threads Number of threads (0 = use available processors automatically)
     */
    ParallelTranslator(
        final Function<? super Path, ? extends Path> translation,
        final int threads
    ) {
        this(translation, threads, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Constructor.
     *
     * @param translation Function to apply to each path representation
     * @param threads Number of threads (0 = use available processors automatically)
     * @param loader Class loader
     */
    private ParallelTranslator(
        final Function<? super Path, ? extends Path> translation,
        final int threads,
        final ClassLoader loader
    ) {
        this.translation = translation;
        this.threads = threads;
        this.loader = loader;
    }

    @Override
    public Stream<Path> apply(final Stream<Path> representations) {
        if (this.threads < 0) {
            throw new IllegalArgumentException(
                String.format(
                    "The number of threads must be 0 or positive, but got: %d",
                    this.threads
                )
            );
        }
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

    private Path translate(final Path rep) {
        Thread.currentThread().setContextClassLoader(this.loader);
        return this.translation.apply(rep);
    }
}
