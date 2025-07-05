/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Random name.
 * @since 0.11.1
 */
public final class RandName {

    /**
     * Global name counter.
     */
    private static final Supplier<Long> GLOBAL = new AtomicLong(0)::incrementAndGet;

    /**
     * Suffix for the name.
     */
    private final String suffix;

    /**
     * Number generator for the name.
     */
    private final Supplier<Long> number;

    /**
     * Constructor.
     * @param suffix Suffix for the name.
     */
    public RandName(final String suffix) {
        this(suffix, RandName.GLOBAL);
    }

    /**
     * Constructor.
     * @param suffix Suffix for the name.
     * @param number Number generator for the name.
     */
    private RandName(final String suffix, final Supplier<Long> number) {
        this.suffix = suffix;
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("%s%d", this.suffix, this.number.get());
    }
}
