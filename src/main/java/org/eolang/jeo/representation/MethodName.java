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

/**
 * Method name.
 * Represents java method name.
 * The original purpose of this class is to handle constructor names.
 * They are represented as '<'init'>' in bytecode which isn't allowed in EO.
 * So, we need to convert it to a valid name and back.
 * @since 0.6
 */
public final class MethodName {

    /**
     * Constructor name in EO.
     */
    private static final String EO_CONSTRUCTOR = "@init@";

    /**
     * Constructor name in bytecode.
     */
    private static final String CONSTRUCTOR = "<init>";

    /**
     * Original name.
     */
    private final String original;

    /**
     * Constructor.
     * @param name Method name.
     */
    public MethodName(final String name) {
        this.original = name;
    }

    /**
     * Converts method name to bytecode.
     * @return Bytecode name.
     */
    public String bytecode() {
        final String result;
        if (MethodName.EO_CONSTRUCTOR.equals(this.original)) {
            result = MethodName.CONSTRUCTOR;
        } else {
            result = this.original;
        }
        return result;
    }

    /**
     * Converts method name to EO.
     * @return EO name.
     */
    public String xmir() {
        final String result;
        if (MethodName.CONSTRUCTOR.equals(this.original)) {
            result = MethodName.EO_CONSTRUCTOR;
        } else {
            result = this.original;
        }
        return result;
    }
}
