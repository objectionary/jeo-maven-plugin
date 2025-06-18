/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

/**
 * <p>Method signature representation combining name and descriptor.</p>
 * <p>Represents Java method name and descriptor as a unified signature.
 * Since methods in Java are allowed to be overloaded, we need to handle this
 * ambiguity by combining the method name with its descriptor to create a
 * unique identifier.</p>
 * @since 0.5.0
 */
public final class Signature {

    /**
     * Human-readable method name in source code.
     */
    private final String original;

    /**
     * Method descriptor.
     */
    private final String descr;

    /**
     * Constructor.
     * @param encoded The encoded method name and descriptor
     */
    public Signature(final String encoded) {
        this(Signature.prefix(encoded), Signature.suffix(encoded));
    }

    /**
     * Constructor.
     * @param name The method name
     * @param descriptor The method descriptor
     */
    public Signature(final String name, final String descriptor) {
        this.original = name;
        this.descr = descriptor;
    }

    /**
     * Encoded method name with descriptor.
     * @return Encoded method name with descriptor.
     */
    public String encoded() {
        return String.format(
            "%s-%s",
            this.original,
            new DecodedString(this.descr).encode()
        );
    }

    /**
     * Just a name without suffix.
     * @return Name without suffix.
     */
    public String name() {
        return this.original;
    }

    /**
     * Just a descriptor.
     * @return Descriptor without name.
     */
    public String descriptor() {
        return this.descr;
    }

    /**
     * Decode method name from encoded signature.
     * @param encoded The encoded method name and descriptor
     * @return The decoded method name
     */
    private static String prefix(final String encoded) {
        try {
            return encoded.substring(0, encoded.indexOf('-'));
        } catch (final StringIndexOutOfBoundsException exception) {
            throw new IllegalArgumentException(
                String.format("Invalid encoded method name: %s", encoded),
                exception
            );
        }
    }

    /**
     * Decode method descriptor from encoded signature.
     * @param encoded The encoded method name and descriptor
     * @return The decoded method descriptor
     */
    private static String suffix(final String encoded) {
        return new EncodedString(encoded.substring(encoded.indexOf('-') + 1)).decode();
    }
}
