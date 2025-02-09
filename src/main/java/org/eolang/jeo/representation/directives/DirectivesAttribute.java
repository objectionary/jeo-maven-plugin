/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for an attribute.
 * @since 0.4
 */
public final class DirectivesAttribute implements Iterable<Directive> {

    /**
     * The base of the attribute.
     */
    private final String base;

    /**
     * Data to store.
     */
    private final List<Iterable<Directive>> data;

    /**
     * Constructor.
     * @param base The name of the attribute.
     * @param data Properties of an attribute.
     */
    @SafeVarargs
    public DirectivesAttribute(final String base, final Iterable<Directive>... data) {
        this(base, Arrays.asList(data));
    }

    /**
     * Constructor.
     * @param base The base of the attribute.
     * @param data Properties of an attribute.
     */
    private DirectivesAttribute(final String base, final List<Iterable<Directive>> data) {
        this.base = base;
        this.data = data;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            this.base,
            this.data.stream().map(Directives::new).toArray(Directives[]::new)
        ).iterator();
    }
}
