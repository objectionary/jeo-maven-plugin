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
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * An annotation value that is itself an annotation.
 * @since 0.6
 */
public final class DirectivesAnnotationAnnotationValue implements Iterable<Directive> {

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The descriptor of the annotation.
     */
    private final String descriptor;

    /**
     * Inner values.
     */
    private final List<Iterable<Directive>> values;

    /**
     * Constructor.
     * @param name The name of the annotation property.
     * @param descriptor The descriptor of the annotation.
     * @param values The inner annotation values.
     */
    public DirectivesAnnotationAnnotationValue(
        final String name, final String descriptor, final List<Iterable<Directive>> values
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.values = values;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .add("o").attr("base", new JeoFqn("annotation-property").fqn())
            .append(new DirectivesValue("ANNOTATION"))
            .append(new DirectivesValue(this.name))
            .append(new DirectivesValue(this.descriptor))
            .append(
                this.values.stream()
                    .map(Directives::new)
                    .reduce(new Directives(), Directives::append)
            )
            .up()
            .iterator();
    }
}
