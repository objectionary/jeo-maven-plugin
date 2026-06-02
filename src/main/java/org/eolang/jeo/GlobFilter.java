/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * Includes glob patterns (compiled to PathMatchers).
     */
    private final Set<PathMatcher> includes;

    /**
     * Excludes glob patterns (compiled to PathMatchers).
     */
    private final Set<PathMatcher> excludes;

    /**
     * Ctor.
     *
     * @param patterns Glob patterns to include
     * @param exclusions Glob patterns to exclude
     */
    GlobFilter(final Set<String> patterns, final Set<String> exclusions) {
        this.includes = GlobFilter.compile(patterns);
        this.excludes = GlobFilter.compile(exclusions);
    }

    @Override
    public String toString() {
        final String inclusions;
        if (this.includes.isEmpty()) {
            inclusions = "no inclusions";
        } else {
            inclusions = String.format(
                "%d patterns (compiled)",
                this.includes.size()
            );
        }
        final String exclusions;
        if (this.excludes.isEmpty()) {
            exclusions = "no exclusions";
        } else {
            exclusions = String.format(
                "%d exclusions (compiled)",
                this.excludes.size()
            );
        }
        return String.format("%s and %s", inclusions, exclusions);
    }

    @Override
    public boolean test(final Path path) {
        if (this.excludes.stream().anyMatch(m -> m.matches(path))) {
            return false;
        }
        return this.includes.isEmpty() || this.includes.stream()
            .anyMatch(matcher -> matcher.matches(path));
    }

    /**
     * Compile glob patterns to PathMatchers.
     * @param patterns Glob patterns to compile
     * @return Compiled PathMatchers
     */
    private static Set<PathMatcher> compile(final Set<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return Collections.emptySet();
        }
        return patterns.stream()
            .map(GlobFilter::matcher)
            .collect(Collectors.toSet());
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
