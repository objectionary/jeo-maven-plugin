/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.regex.Pattern;

/**
 * Name representation with optional numeric suffix.
 *
 * <p>This class handles names that may have numeric suffixes for disambiguation.
 * For example: `foo`, `foo-2`, `foo-3`. Names without suffixes are treated
 * as having number 1.</p>
 * @since 0.9.0
 */
public final class NumberedName {

    /**
     * A trailing group that is a number of a name.
     *
     * <p>A dash is legal in a JVM method name and Kotlin emits "box-impl" for
     * a value class, so a trailing group that is not a number belongs to the
     * name. The digits are bounded to keep the number inside an int.</p>
     */
    private static final Pattern NUMBER = Pattern.compile("[1-9][0-9]{0,8}");

    /**
     * Number of the name.
     */
    private final int number;

    /**
     * Original name.
     */
    private final String name;

    /**
     * Constructor from encoded name.
     * @param encoded The encoded name (e.g., "foo-2")
     */
    public NumberedName(final String encoded) {
        this(NumberedName.suffix(encoded), NumberedName.prefix(encoded));
    }

    /**
     * Constructor.
     * @param number The number of the name starting from 1
     * @param name The base name of the object
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
     * Extract the number suffix from an encoded name.
     * @param encoded The encoded name (e.g., "foo-2")
     * @return The numeric suffix (e.g., 2)
     */
    private static int suffix(final String encoded) {
        final int result;
        final int index = encoded.lastIndexOf('-');
        if (index == -1 || !NumberedName.NUMBER.matcher(encoded.substring(index + 1)).matches()) {
            result = 1;
        } else {
            result = Integer.parseInt(encoded.substring(index + 1));
        }
        return result;
    }

    /**
     * Extract the base name without the number suffix.
     * @param encoded The encoded name (e.g., "foo-2")
     * @return The base name without number (e.g., "foo")
     */
    private static String prefix(final String encoded) {
        final String result;
        final int index = encoded.lastIndexOf('-');
        if (index == -1 || !NumberedName.NUMBER.matcher(encoded.substring(index + 1)).matches()) {
            result = encoded;
        } else {
            result = encoded.substring(0, index);
        }
        return result;
    }
}
