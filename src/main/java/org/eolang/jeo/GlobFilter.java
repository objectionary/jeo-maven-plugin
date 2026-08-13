/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;

/**
 * Filter that matches paths against a set of glob patterns.
 * <p>
 *     Excludes patterns take precedence over includes patterns.
 * </p>
 * Returns true if the path matches any of the include patterns and does not match any of
 * the exclude patterns.
 * @since 0.13.0
 */
public final class GlobFilter implements Predicate<Path> {

    /**
     * Includes glob patterns.
     */
    private final Set<String> includes;

    /**
     * Excludes glob patterns.
     */
    private final Set<String> excludes;

    /**
     * Compiled include matchers, computed lazily and cached.
     */
    private final Scalar<Set<PathMatcher>> whitelist;

    /**
     * Compiled exclude matchers, computed lazily and cached.
     */
    private final Scalar<Set<PathMatcher>> blacklist;

    /**
     * Ctor.
     *
     * @param includes Glob patterns to include
     * @param excludes Glob patterns to exclude
     */
    GlobFilter(final Set<String> includes, final Set<String> excludes) {
        this(includes, excludes, GlobFilter::matcher);
    }

    /**
     * Ctor.
     *
     * @param includes Glob patterns to include
     * @param excludes Glob patterns to exclude
     * @param compiler Factory that compiles a glob pattern into a matcher
     */
    GlobFilter(
        final Set<String> includes,
        final Set<String> excludes,
        final Function<String, PathMatcher> compiler
    ) {
        this.includes = includes;
        this.excludes = excludes;
        this.whitelist = new Synced<>(
            new Sticky<>(
                () -> includes.stream()
                    .map(compiler)
                    .collect(Collectors.toSet())
            )
        );
        this.blacklist = new Synced<>(
            new Sticky<>(
                () -> excludes.stream()
                    .map(compiler)
                    .collect(Collectors.toSet())
            )
        );
    }

    @Override
    public String toString() {
        final String inclusions;
        if (this.includes.isEmpty()) {
            inclusions = "no inclusions";
        } else {
            inclusions = this.includes.stream().collect(
                Collectors.joining(
                    ", ",
                    String.format("%d inclusions (", this.includes.size()),
                    ")"
                )
            );
        }
        final String exclusions;
        if (this.excludes.isEmpty()) {
            exclusions = "no exclusions";
        } else {
            exclusions = this.excludes.stream().collect(
                Collectors.joining(
                    ", ",
                    String.format("%d exclusions (", this.excludes.size()),
                    ")"
                )
            );
        }
        return String.format("%s and %s", inclusions, exclusions);
    }

    @Override
    public boolean test(final Path path) {
        final Set<PathMatcher> white = new Unchecked<>(this.whitelist).value();
        final Set<PathMatcher> black = new Unchecked<>(this.blacklist).value();
        final boolean included;
        if (black.stream().anyMatch(matcher -> matcher.matches(path))) {
            included = false;
        } else {
            included = white.isEmpty() || white.stream()
                .anyMatch(matcher -> matcher.matches(path));
        }
        return included;
    }

    /**
     * Create a PathMatcher for the given glob pattern.
     * @param pattern Glob pattern to match
     * @return PathMatcher for the glob pattern
     */
    private static PathMatcher matcher(final String pattern) {
        return FileSystems.getDefault().getPathMatcher(
            String.format("glob:%s", pattern)
        );
    }
}
