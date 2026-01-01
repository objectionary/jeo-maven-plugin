/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link FilteredClasses}.
 * @since 0.14.0
 */
final class FilteredClassesTest {

    @Test
    void returnsFilteredPathsWhenFilterMatchesSomeWithDefaultLogger() {
        final Path first = Paths.get("A.class");
        final Path second = Paths.get("B.class");
        final List<Path> result = new FilteredClasses(
            new Project(Stream.of(first, second, Paths.get("C.txt"))),
            new GlobFilter(Collections.singleton("*.class"), Collections.emptySet())
        ).all().collect(java.util.stream.Collectors.toList());
        MatcherAssert.assertThat(
            "Should return only paths that match the filter",
            result,
            Matchers.allOf(
                Matchers.iterableWithSize(2),
                Matchers.hasItem(first),
                Matchers.hasItem(second)
            )
        );
    }

    @Test
    void returnsEmptyWhenNoPathsMatchFilter() {
        MatcherAssert.assertThat(
            "No paths should match the filter",
            new FilteredClasses(
                new Project(Stream.of(Paths.get("A.txt"), Paths.get("B.txt"))),
                new GlobFilter(Collections.singleton("*.class"), Collections.emptySet())
            ).all().collect(java.util.stream.Collectors.toList()),
            Matchers.empty()
        );
    }

    @Test
    void logsMessageWithCorrectCount() {
        final List<String> logs = new ArrayList<>(1);
        final Path root = Paths.get("/dev/null");
        new FilteredClasses(
            new Project(
                root,
                Stream.of(
                    Paths.get("FirstAdded.class"),
                    Paths.get("SecondAdded.class"),
                    Paths.get("Skipped.txt")
                )
            ),
            new GlobFilter(Collections.emptySet(), Collections.singleton("*.txt")), logs::add
        ).all();
        MatcherAssert.assertThat(
            "Should log the correct number of files found",
            logs,
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItem(
                    Matchers.containsString(
                        String.format(
                            "Found 2 files in %s using no inclusions and 1 exclusions (*.txt)",
                            root
                        )
                    )
                )
            )
        );
    }

    /**
     * Test project implementation of {@link Classes}.
     * <p>
     *     This class is used to simulate a project structure with
     *     a root path and a stream of paths.
     * </p>
     * @since 0.14.0
     */
    private static final class Project implements Classes {
        /**
         * Project root path.
         */
        private final Path dir;

        /**
         * Stream of paths representing project files.
         */
        private final Stream<Path> paths;

        /**
         * Constructor.
         * @param paths Stream of paths representing project files
         */
        private Project(final Stream<Path> paths) {
            this(Paths.get("/dev/null"), paths);
        }

        /**
         * Constructor.
         * @param root Project root path
         * @param paths Stream of paths representing project files
         */
        private Project(final Path root, final Stream<Path> paths) {
            this.dir = root;
            this.paths = paths;
        }

        @Override
        public long total() {
            return this.all().count();
        }

        @Override
        public Path root() {
            return this.dir;
        }

        @Override
        public Stream<Path> all() {
            return this.paths;
        }

        @Override
        public String toString() {
            return this.dir.toString();
        }
    }
}
