/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * A simple counter class that wraps an AtomicInteger.
 * This class is thread-safe.
 *
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
     * Guards the order of the reports made by all users of the counter.
     */
    private final ReentrantLock lock;

    /**
     * Constructor.
     *
     * @param all Total number of items
     */
    public Counter(final long all) {
        this(Counter.safe(all), new AtomicLong(0), new ReentrantLock());
    }

    /**
     * Constructor.
     *
     * @param all Total number of items
     * @param current Current item number
     * @param lock Lock that orders the reports
     */
    private Counter(final long all, final AtomicLong current, final ReentrantLock lock) {
        this.all = all;
        this.current = current;
        this.lock = lock;
    }

    /**
     * Get the current count and increment it.
     *
     * @return The current count in the format "current/total"
     */
    public String next() {
        return String.format("%d/%d", this.current.incrementAndGet(), this.all);
    }

    /**
     * Get the next count and report it, while no other user of the counter does the same.
     *
     * @param report What to do with the count, in the format "current/total"
     */
    public void next(final Consumer<String> report) {
        this.lock.lock();
        try {
            report.accept(this.next());
        } finally {
            this.lock.unlock();
        }
    }

    private static long safe(final long all) {
        if (all < 0) {
            throw new IllegalArgumentException("Total number of items cannot be negative");
        }
        return all;
    }
}
