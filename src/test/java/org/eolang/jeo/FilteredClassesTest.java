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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link FilteredClasses}.
 *
 * @since 0.14.0
 */
final class FilteredClassesTest {

    @Test
    void returnsFilteredPathsWhenFilterMatchesSomeWithDefaultLogger() {
        final Path root = Paths.get("/dev/null");
        final Path first = root.resolve("A.class");
        final Path second = root.resolve("B.class");
        MatcherAssert.assertThat(
            "Should return only paths that match the filter",
            new FilteredClasses(
                new FilteredClassesTest.Project(
                    root, Stream.of(first, second, root.resolve("C.txt"))
                ),
                new GlobFilter(Collections.singleton("*.class"), Collections.emptySet())
            ).all().collect(Collectors.toList()),
            Matchers.allOf(
                Matchers.iterableWithSize(2),
                Matchers.hasItem(first),
                Matchers.hasItem(second)
            )
        );
    }

    @Test
    void returnsEmptyWhenNoPathsMatchFilter() {
        final Path root = Paths.get("/dev/null");
        MatcherAssert.assertThat(
            "No paths should match the filter",
            new FilteredClasses(
                new FilteredClassesTest.Project(
                    root, Stream.of(root.resolve("A.txt"), root.resolve("B.txt"))
                ),
                new GlobFilter(Collections.singleton("*.class"), Collections.emptySet())
            ).all().collect(Collectors.toList()),
            Matchers.empty()
        );
    }

    @Test
    void matchesARelativeIncludeAgainstAnAbsoluteRoot() {
        final Path root = Paths.get("/tmp/generated/app/target/classes");
        final Path api = root.resolve("com/acme/api/Api.class");
        MatcherAssert.assertThat(
            "a relative include must match a class under an absolute root (see #1765)",
            new FilteredClasses(
                new FilteredClassesTest.Project(
                    root, Stream.of(api, root.resolve("com/acme/internal/Impl.class"))
                ),
                new GlobFilter(
                    Collections.singleton("com/acme/api/*.class"), Collections.emptySet()
                )
            ).all().collect(Collectors.toList()),
            Matchers.contains(api)
        );
    }

    @Test
    void doesNotExcludeEveryClassBecauseAnAncestorDirectoryIsNamedGenerated() {
        final Path root = Paths.get("/tmp/generated/app/target/classes");
        final Path api = root.resolve("com/acme/api/Api.class");
        MatcherAssert.assertThat(
            "'**/generated/**' must not match every class under a 'generated' ancestor dir",
            new FilteredClasses(
                new FilteredClassesTest.Project(root, Stream.of(api)),
                new GlobFilter(Collections.emptySet(), Collections.singleton("**/generated/**"))
            ).all().collect(Collectors.toList()),
            Matchers.contains(api)
        );
    }

    @Test
    void logsMessageWithCorrectCount() {
        final List<String> logs = new ArrayList<>(1);
        final Path root = Paths.get("/dev/null");
        new FilteredClasses(
            new FilteredClassesTest.Project(
                root,
                Stream.of(
                    root.resolve("FirstAdded.class"),
                    root.resolve("SecondAdded.class"),
                    root.resolve("Skipped.txt")
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
     *
     * <p>This class is used to simulate a project structure with a root path and a stream of
     * paths.</p>
     *
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
         *
         * @param root Project root path
         * @param paths Stream of paths representing project files
         */
        private Project(final Path root, final Stream<Path> paths) {
            this.dir = root;
            this.paths = paths;
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
