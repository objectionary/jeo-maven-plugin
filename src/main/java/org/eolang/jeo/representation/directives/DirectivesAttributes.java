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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for attributes.
 * @since 0.4
 * @todo #589:30min Add Unit Tests for DirectivesAttributes class.
 *  DirectivesAttributes class is not covered by unit tests.
 *  Add unit tests for DirectivesAttributes class in order to increase the code coverage
 *  and improve the quality of the code.
 */
public final class DirectivesAttributes implements Iterable<Directive> {

    /**
     * Attributes.
     */
    private final List<DirectivesAttribute> attributes;

    /**
     * Constructor.
     */
    public DirectivesAttributes() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param attributes Separate attributes.
     */
    public DirectivesAttributes(final List<DirectivesAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Add attribute.
     * @param attribute One more attribute.
     * @return The same directives.
     */
    public DirectivesAttributes add(final DirectivesAttribute attribute) {
        this.attributes.add(attribute);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.attributes.isEmpty()) {
            result = Collections.emptyIterator();
        } else {
            final Directives directives = new Directives().add("o")
                .attr("base", "tuple")
                .attr("star", "")
//                .attr("name", "attributes")
                ;
            this.attributes.forEach(directives::append);
            result = directives.up().iterator();
        }
        return result;
    }
}
