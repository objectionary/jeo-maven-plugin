/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import org.cactoos.Scalar;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;

/**
 * Representation of project license.
 *
 * @since 0.6.27
 */
public final class License implements Scalar<String> {

    /**
     * The name of file with license.
     */
    private final String name;

    /**
     * Ctor with default value.
     */
    public License() {
        this("LICENSE.txt");
    }

    /**
     * Primary ctor.
     *
     * @param name The name of file with license.
     */
    public License(final String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return new TextOf(new ResourceOf(this.name)).toString();
    }

    @Override
    public String toString() {
        return this.value();
    }
}
