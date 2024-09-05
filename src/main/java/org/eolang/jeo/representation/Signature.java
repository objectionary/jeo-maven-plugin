/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Method name.
 * Represents java method name.
 * Since methods in java are allowed to be overloaded, we should handle this ambiguity.
 * This class is used to represent method name and its descriptor.
 * @since 0.5
 */
public final class Signature {

    /**
     * Base64 decoder.
     */
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * Base64 encoder.
     */
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * Human-readable method name in source code.
     */
    private final String name;

    /**
     * Method descriptor.
     */
    private final String descriptor;

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
        this.name = name;
        this.descriptor = descriptor;
    }

    /**
     * Encoded method name with descriptor.
     * @return Encoded method name with descriptor.
     */
    public String encoded() {
        return String.format(
            "%s-%s",
            this.name,
            Signature.ENCODER.encodeToString(this.descriptor.getBytes(StandardCharsets.UTF_8))
        );
    }

    /**
     * Just a name without suffix.
     * @return Name without suffix.
     */
    public String name() {
        return this.name;
    }

    /**
     * Just a descriptor.
     * @return Descriptor without name.
     */
    public String descriptor() {
        return this.descriptor;
    }

    /**
     * Decode method name.
     * @param encoded Encoded method name and descriptor.
     * @return Method name.
     */
    private static String prefix(final String encoded) {
        return encoded.substring(0, encoded.indexOf('-'));
    }

    /**
     * Decode method descriptor.
     * @param encoded Encoded method name and descriptor.
     * @return Method descriptor.
     */
    private static String suffix(final String encoded) {
        return new String(
            Signature.DECODER.decode(encoded.substring(encoded.indexOf('-') + 1)),
            StandardCharsets.UTF_8
        );
    }
}
