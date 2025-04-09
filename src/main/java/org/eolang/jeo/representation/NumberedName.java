/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

/**
 * Name with the number at the end.
 * For example, `foo`, `foo-2`, `foo-3`.
 * @since 0.9
 */
public final class NumberedName {

    /**
     * Number of the name.
     */
    private final int number;

    /**
     * Original name.
     */
    private final String name;

    public NumberedName(final String encoded) {
        this(NumberedName.suffix(encoded), NumberedName.prefix(encoded));
    }

    /**
     * Constructor.
     * @param number Number of the name starting from 1.
     * @param name Name of an object.
     */
    public NumberedName(final int number, final String name) {
        this.number = number;
        this.name = name;
    }

    /**
     * Name without the number.
     * @return Name without the number.
     */
    public String plain() {
        return this.name;
    }

    @Override
    public String toString() {
        if (this.number < 1) {
            throw new IllegalArgumentException(
                String.format("Number must be greater than 0, but was: %d", this.number)
            );
        }
        final StringBuilder result = new StringBuilder(this.name);
        if (this.number > 1) {
            result.append('-').append(this.number);
        }
        return result.toString();
    }

    /**
     * Returns the number of the name.
     * @param encoded Encoded name, like `foo-2`.
     * @return Number of the name, like `2`.
     */
    private static int suffix(final String encoded) {
        final int result;
        final int index = encoded.lastIndexOf('-');
        if (index == -1) {
            result = 1;
        } else {
            final String number = encoded.substring(index + 1);
            try {
                result = Integer.parseInt(number);
            } catch (final NumberFormatException ex) {
                throw new IllegalArgumentException(
                    String.format("Invalid number in name: %s", number),
                    ex
                );
            }
        }
        return result;
    }

    /**
     * Gets the name without the number.
     * @param encoded Encoded name, like `foo-2`.
     * @return Name without the number, like `foo`.
     */
    private static String prefix(final String encoded) {
        final String result;
        final int index = encoded.lastIndexOf('-');
        if (index == -1) {
            result = encoded;
        } else {
            result = encoded.substring(0, index);
        }
        return result;
    }
}
