/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.HashMap;

/**
 * Map with maximum values.
 *
 * @param <K> Key type
 * @param <V> Value type
 * @since 0.6
 * @checkstyle IllegalTypeCheck (5 lines)
 */
class MaxValueMap<K, V extends InstructionsFlow.Reducible<V>> extends HashMap<K, V> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6517835829882158842L;

    /**
     * Constructor.
     */
    MaxValueMap() {
        super(0);
    }

    /**
     * Is the value greater than the current one?
     *
     * @param key Key
     * @param value Value
     * @return True if it is
     */
    boolean isGreaterThan(final K key, final V value) {
        return this.get(key) != null && this.get(key).compareTo(value) >= 0;
    }

    /**
     * Put the value if it is greater.
     *
     * @param key Key
     * @param value Value
     */
    void putIfGreater(final K key, final V value) {
        this.merge(key, value, MaxValueMap::max);
    }

    private static <T extends InstructionsFlow.Reducible<T>> T max(
        final T first, final T second
    ) {
        final T result;
        if (first.compareTo(second) > 0) {
            result = first;
        } else {
            result = second;
        }
        return result;
    }
}
