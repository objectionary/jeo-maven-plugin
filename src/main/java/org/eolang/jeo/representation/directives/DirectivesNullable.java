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
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.eolang.jeo.representation.HexData;
import org.xembly.Directive;

/**
 * Directives for data that might be nullable.
 * @since 0.4
 * @todo #589:30min Add Unit Tests for DirectivesNullable class.
 *  DirectivesNullable class is not covered by unit tests.
 *  Add unit tests for DirectivesNullable class in order to increase the code coverage
 *  and improve the quality of the code.
 */
public final class DirectivesNullable implements Iterable<Directive> {

    /**
     * Data to store.
     */
    private final HexData data;

    /**
     * The name of data to store.
     */
    private final String name;

    /**
     * Constructor.
     * @param name The name of data to store.
     * @param data Data to store.
     * @param <T> Type of data.
     */
    public <T> DirectivesNullable(final String name, final T data) {
        this(name, new HexData(data));
    }

    /**
     * Constructor.
     * @param name The name of data to store.
     * @param data Data to store.
     */
    public DirectivesNullable(final String name, final HexData data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.data.isNull()) {
            result = new DirectivesValue(this.name, "").iterator();
        } else {
            result = new DirectivesValue(this.data, this.name).iterator();
        }
        return result;
    }
}
