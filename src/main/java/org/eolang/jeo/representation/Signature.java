/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

/**
 * Method name.
 * Represents java method name.
 * Since methods in java are allowed to be overloaded, we should handle this ambiguity.
 * This class is used to represent method name and its descriptor.
 * @since 0.5
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
     * @param encoded Method name and descriptor encoded.
     */
    public Signature(final String encoded) {
        this(Signature.prefix(encoded), Signature.suffix(encoded));
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param descriptor Method descriptor.
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
     * Decode method name.
     * @param encoded Encoded method name and descriptor.
     * @return Method name.
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
     * Decode method descriptor.
     * @param encoded Encoded method name and descriptor.
     * @return Method descriptor.
     */
    private static String suffix(final String encoded) {
        return new EncodedString(encoded.substring(encoded.indexOf('-') + 1)).decode();
    }
}
