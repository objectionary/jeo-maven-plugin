/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple counter class that wraps an AtomicInteger.
 * This class is thread-safe.
 * @since 0.15
 */
public final class Counter {

    /**
     * Total.
     */
    private final long all;

    /**
     * Current.
     */
    private final AtomicLong current;

    /**
     * Constructor.
     * @param all Total number of items.
     */
    public Counter(final long all) {
        this(Counter.safe(all), new AtomicLong(0));
    }

    /**
     * Constructor.
     * @param all Total number of items.
     * @param current Current item number.
     */
    private Counter(final long all, final AtomicLong current) {
        this.all = all;
        this.current = current;
    }

    /**
     * Get the current count and increment it.
     * @return The current count in the format "current/total".
     */
    public String next() {
        return String.format("%d/%d", this.current.incrementAndGet(), this.all);
    }

    /**
     * Ensure the total number of items is non-negative.
     * @param all Total number of items.
     * @return The total number of items if valid.
     * @throws IllegalArgumentException if the total number of items is negative.
     */
    private static long safe(final long all) {
        if (all < 0) {
            throw new IllegalArgumentException("Total number of items cannot be negative");
        }
        return all;
    }
}
