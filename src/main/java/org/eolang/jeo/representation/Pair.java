/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.Map;

/**
 * Pair of opcode and its name.
 *
 * @since 0.12.0
 */
class Pair implements Map.Entry<Integer, String> {

    /**
     * Opcode key number.
     */
    private final int key;

    /**
     * Opcode name.
     */
    private final String name;

    /**
     * Constructor for creating a pair of opcode and its name.
     *
     * @param key Opcode key number
     * @param value Opcode name
     */
    Pair(final int key, final String value) {
        this.key = key;
        this.name = value;
    }

    @Override
    public Integer getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.name;
    }

    @Override
    public String setValue(final String value) {
        throw new UnsupportedOperationException("setValue is not supported for Pair entry");
    }
}
