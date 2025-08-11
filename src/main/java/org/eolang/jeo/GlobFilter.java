/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
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
     * Includes glob patterns.
     */
    private final Set<String> includes;

    /**
     * Excludes glob patterns.
     */
    private final Set<String> excludes;

    /**
     * Ctor.
     *
     * @param includes Glob patterns to include
     * @param excludes Glob patterns to exclude
     */
    GlobFilter(final Set<String> includes, final Set<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    @Override
    public String toString() {
        return String.format(
            "%d inclusions (%s) and %d exclusions (%s)",
            this.includes.size(),
            String.join(", ", this.includes),
            this.excludes.size(),
            String.join(", ", this.excludes)
        );
    }

    @Override
    public boolean test(final Path path) {
        final Set<PathMatcher> whitelist = this.includes.stream()
            .map(GlobFilter::matcher)
            .collect(Collectors.toSet());
        final Set<PathMatcher> blacklist = this.excludes.stream()
            .map(GlobFilter::matcher)
            .collect(Collectors.toSet());
        final boolean included;
        if (blacklist.stream().anyMatch(m -> m.matches(path))) {
            included = false;
        } else {
            included = whitelist.isEmpty() || whitelist.stream()
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
