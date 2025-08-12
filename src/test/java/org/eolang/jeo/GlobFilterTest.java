/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for {@link GlobFilter}.
 * @since 0.13.0
 */
final class GlobFilterTest {

    /**
     * Applies the glob filter to a path and checks if it matches the expected result.
     * @param description Description of the test case
     * @param includes Set of glob patterns to include
     * @param excludes Set of glob patterns to exclude
     * @param path Path to test against the glob patterns
     * @param expected Expected result of the filter test
     * @checkstyle ParameterNumberCheck (20 lines)
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    @DisplayName("Test GlobFilter behavior")
    void appliesGlobFilter(
        final String description,
        final Set<String> includes,
        final Set<String> excludes,
        final Path path,
        final boolean expected
    ) {
        MatcherAssert.assertThat(
            "We expect the filter to match the path correctly",
            new GlobFilter(includes, excludes).test(path),
            Matchers.is(expected)
        );
    }

    /**
     * Tests the string representation of the GlobFilter.
     * @param description Description of the test case
     * @param filter The GlobFilter to test
     * @param expected Expected string representation of the filter
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("stringCases")
    @DisplayName("Test GlobFilter string representation")
    void convertsGlobFilterToString(
        final String description,
        final GlobFilter filter,
        final String expected
    ) {
        MatcherAssert.assertThat(
            "We expect the filter's string representation to match",
            filter.toString(),
            Matchers.is(expected)
        );
    }

    /**
     * Test cases for GlobFilter.
     * @return Stream of test cases
     */
    static Stream<Arguments> cases() {
        return Stream.of(
            Arguments.of(
                "Included by pattern",
                GlobFilterTest.setOf("**/*.java"),
                GlobFilterTest.setOf(),
                Paths.get("src/Included.java"),
                true
            ),
            Arguments.of(
                "Excluded takes precedence",
                GlobFilterTest.setOf("**/*.java"),
                GlobFilterTest.setOf("src/**"),
                Paths.get("src/Excluded.java"),
                false
            ),
            Arguments.of(
                "Included, not excluded",
                GlobFilterTest.setOf("src/**/*.java"),
                GlobFilterTest.setOf("**/test/**"),
                Paths.get("src/main/Main.java"),
                true
            ),
            Arguments.of(
                "Only excluded",
                GlobFilterTest.setOf(),
                GlobFilterTest.setOf("**/*.class"),
                Paths.get("target/classes/Main.class"),
                false
            ),
            Arguments.of(
                "Not matched by includes",
                GlobFilterTest.setOf("**/*.java"),
                GlobFilterTest.setOf(),
                Paths.get("README.md"),
                false
            ),
            Arguments.of(
                "No includes: allow all except excluded",
                GlobFilterTest.setOf(),
                GlobFilterTest.setOf("**/*.tmp"),
                Paths.get("build/output.log"),
                true
            ),
            Arguments.of(
                "No includes and excluded match",
                GlobFilterTest.setOf(),
                GlobFilterTest.setOf("**/*.tmp"),
                Paths.get("build/output.tmp"),
                false
            ),
            Arguments.of(
                "Empty includes and excludes",
                GlobFilterTest.setOf(),
                GlobFilterTest.setOf(),
                Paths.get("any/path/file.txt"),
                true
            )
        );
    }

    /**
     * Test cases for {@link GlobFilter#toString()}.
     * @return Stream of test cases
     */
    static Stream<Arguments> stringCases() {
        return Stream.of(
            Arguments.of(
                "No inclusions and no exclusions",
                new GlobFilter(GlobFilterTest.setOf(), GlobFilterTest.setOf()),
                "no inclusions and no exclusions"
            ),
            Arguments.of(
                "With inclusions only",
                new GlobFilter(GlobFilterTest.setOf("**/*.java"), GlobFilterTest.setOf()),
                "1 inclusions (**/*.java) and no exclusions"
            ),
            Arguments.of(
                "With exclusions only",
                new GlobFilter(GlobFilterTest.setOf(), GlobFilterTest.setOf("**/*.tmp")),
                "no inclusions and 1 exclusions (**/*.tmp)"
            ),
            Arguments.of(
                "With both inclusions and exclusions",
                new GlobFilter(
                    GlobFilterTest.setOf("src/**/*.java"),
                    GlobFilterTest.setOf("**/test/**")
                ),
                "1 inclusions (src/**/*.java) and 1 exclusions (**/test/**)"
            )
        );
    }

    /**
     * Set of strings for test cases.
     * @param values Values to include in the set
     * @return Set of strings
     */
    private static Set<String> setOf(final String... values) {
        return Stream.of(values).collect(java.util.stream.Collectors.toSet());
    }
}
