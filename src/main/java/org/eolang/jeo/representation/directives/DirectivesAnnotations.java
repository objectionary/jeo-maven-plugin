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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Annotation.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class DirectivesAnnotations implements Iterable<Directive> {

    /**
     * All the annotations.
     */
    private final List<Iterable<Directive>> annotations;

    /**
     * Annotations name.
     */
    private final String name;

    /**
     * Constructor.
     */
    public DirectivesAnnotations() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param name Name.
     */
    public DirectivesAnnotations(final String name) {
        this(new ArrayList<>(0), name);
    }

    /**
     * Constructor.
     * @param annotations Annotations.
     */
    public DirectivesAnnotations(final DirectivesAnnotation... annotations) {
        this("annotations", annotations);
    }

    /**
     * Constructor.
     * @param annotations Annotations.
     */
    public DirectivesAnnotations(final String name, final DirectivesAnnotation... annotations) {
        this(Arrays.asList(annotations), name);
    }


    /**
     * Constructor.
     * @param annotations Annotations.
     */
    public DirectivesAnnotations(final List<Iterable<Directive>> annotations) {
        this(annotations, "annotations");
    }

    /**
     * Constructor.
     * @param annotations Annotations.
     * @param name Name.
     */
    public DirectivesAnnotations(final List<Iterable<Directive>> annotations, final String name) {
        this.annotations = annotations;
        this.name = name;
    }

    /**
     * Add annotation.
     * @param annotation Annotation.
     * @return This object.
     */
    public DirectivesAnnotations add(final DirectivesAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives();
        if (!this.annotations.isEmpty()) {
            directives.add("o")
                .attr("base", "tuple")
                .attr("star", "")
                .attr("name", this.name)
                .attr("line", new Random().nextInt(Integer.MAX_VALUE));
            this.annotations.forEach(directives::append);
            directives.up();
        }
        return directives.iterator();
    }
}
