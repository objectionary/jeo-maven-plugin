/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Counter}.
 * @since 0.15.0
 */
final class CounterTest {

    @Test
    void incrementsCurrentCountCorrectly() {
        final Counter counter = new Counter(3);
        MatcherAssert.assertThat(
            "First call should return 1/3",
            counter.next(),
            Matchers.is("1/3")
        );
        MatcherAssert.assertThat(
            "Second call should return 2/3",
            counter.next(),
            Matchers.is("2/3")
        );
        MatcherAssert.assertThat(
            "Third call should return 3/3",
            counter.next(),
            Matchers.is("3/3")
        );
    }

    @Test
    void throwsExceptionForNegativeTotal() {
        MatcherAssert.assertThat(
            "We should get the correct exception message",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Counter(-1)
            ).getMessage(),
            Matchers.is("Total number of items cannot be negative")
        );
    }

    @Test
    void handlesZeroTotalWithoutIncrementing() {
        final Counter counter = new Counter(0);
        MatcherAssert.assertThat(
            "First call should return 1/0",
            counter.next(),
            Matchers.is("1/0")
        );
        MatcherAssert.assertThat(
            "Second call should return 2/0",
            counter.next(),
            Matchers.is("2/0")
        );
    }

    @Test
    void handlesLargeCountsWithoutOverflow() {
        final Counter counter = new Counter(Integer.MAX_VALUE);
        MatcherAssert.assertThat(
            "First call should return 1/MAX_VALUE",
            counter.next(),
            Matchers.is(String.format("1/%d", Integer.MAX_VALUE))
        );
        MatcherAssert.assertThat(
            "Second call should return 2/MAX_VALUE",
            counter.next(),
            Matchers.is(String.format("2/%d", Integer.MAX_VALUE))
        );
    }
}
