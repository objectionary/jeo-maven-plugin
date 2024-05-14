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
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for an attribute.
 * @since 0.4
 * @todo #589:30min Add Unit Tests for DirectivesAttribute class.
 *  DirectivesAttribute class is not covered by unit tests.
 *  Add unit tests for DirectivesAttribute class in order to increase the code coverage
 *  and improve the quality of the code.
 */
public final class DirectivesAttribute implements Iterable<Directive> {

    /**
     * The name of the attribute.
     */
    private final String name;

    /**
     * Data to store.
     */
    private final List<Iterable<Directive>> data;

    /**
     * Constructor.
     * @param name The name of the attribute.
     * @param data Properties of an attribute.
     */
    @SafeVarargs
    public DirectivesAttribute(final String name, final Iterable<Directive>... data) {
        this(name, Arrays.asList(data));
    }

    /**
     * Constructor.
     * @param name The name of the attribute.
     * @param data Properties of an attribute.
     */
    public DirectivesAttribute(final String name, final List<Iterable<Directive>> data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives().add("o").attr("name", this.name);
        this.data.forEach(directives::append);
        return directives.up().iterator();
    }
}
