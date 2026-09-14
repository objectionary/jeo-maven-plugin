/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

/**
 * Entry in the worklist.
 *
 * @param <T> Type of the element
 * @since 0.6
 */
class Entry<T> {

    /**
     * Bytecode instruction index.
     */
    private final int indx;

    /**
     * Value.
     */
    private final T evalue;

    /**
     * Constructor.
     *
     * @param index Index
     * @param value Value
     */
    Entry(final int index, final T value) {
        this.indx = index;
        this.evalue = value;
    }

    /**
     * Index.
     *
     * @return Index
     */
    int index() {
        return this.indx;
    }

    /**
     * Value.
     *
     * @return Value
     */
    T value() {
        return this.evalue;
    }
}
