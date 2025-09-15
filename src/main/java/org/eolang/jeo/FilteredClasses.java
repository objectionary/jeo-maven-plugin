/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Filtered classes.
 * @since 0.14.0
 */
final class FilteredClasses implements Classes {

    /**
     * Original classes.
     */
    private final Classes original;

    /**
     * Glob filter to apply.
     */
    private final GlobFilter filter;

    /**
     * Logger to use for logging messages.
     */
    private final Consumer<String> logger;

    /**
     * Constructor.
     * @param original Original classes to filter
     * @param filter Glob filter to apply
     */
    FilteredClasses(final Classes original, final GlobFilter filter) {
        this(original, filter, s -> Logger.info(FilteredClasses.class, s));
    }

    /**
     * Constructor with custom logger.
     * @param original Original classes to filter
     * @param filter Glob filter to apply
     * @param logger Logger to use for logging messages
     */
    FilteredClasses(
        final Classes original,
        final GlobFilter filter,
        final Consumer<String> logger
    ) {
        this.original = original;
        this.filter = filter;
        this.logger = logger;
    }

    @Override
    public Path root() {
        return this.original.root();
    }

    @Override
    public Stream<Path> all() {
        final List<Path> res = this.original.all().filter(this.filter).collect(Collectors.toList());
        final int size = res.size();
        this.logger.accept(
            String.format("Found %d files in %s using %s", size, this.original, this.filter)
        );
        return res.stream();
    }

    @Override
    public String toString() {
        return this.original.toString();
    }
}
