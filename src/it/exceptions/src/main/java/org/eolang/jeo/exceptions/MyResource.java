/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.exceptions;

/**
 * Resource that used in {@link Application}.
 * We need it to test try-with-resources statement.
 */
public class MyResource implements AutoCloseable {

    /**
     * Resource name.
     */
    private final String name;

    /**
     * Constructor.
     * @param name Resource name.
     */
    public MyResource(final String name) {
        this.name = name;
    }

    @Override
    public void close() {
        System.out.println(String.format("Closing '%s'", this.name));
    }
}
