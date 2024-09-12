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
import org.eolang.jeo.representation.Signature;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Annotation.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class DirectivesAnnotation implements Iterable<Directive> {

    /**
     * Annotation descriptor.
     */
    private final String descriptor;

    /**
     * Annotation visible.
     */
    private final boolean visible;

    /**
     * Annotation properties.
     */
    private final List<Iterable<Directive>> properties;

    /**
     * Constructor.
     * @param descriptor Descriptor.
     * @param visible Visible.
     */
    public DirectivesAnnotation(
        final String descriptor,
        final boolean visible
    ) {
        this(descriptor, visible, new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param descriptor Descriptor.
     * @param visible Visible.
     * @param props Properties.
     */
    @SafeVarargs
    public DirectivesAnnotation(
        final String descriptor,
        final boolean visible,
        final Iterable<Directive>... props
    ) {
        this(descriptor, visible, Arrays.asList(props));
    }

    /**
     * Constructor.
     * @param descriptor Descriptor.
     * @param visible Visible.
     * @param properties Properties.
     */
    public DirectivesAnnotation(
        final String descriptor,
        final boolean visible,
        final List<Iterable<Directive>> properties
    ) {
        this.descriptor = descriptor;
        this.visible = visible;
        this.properties = properties;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives().add("o")
            .attr("base", "annotation")
            .attr(
                "name",
                new Signature(
                    String.format("annotation-%d", new Random().nextInt(Integer.MAX_VALUE)),
                    this.descriptor
                ).encoded()
            )
            .attr("line", new Random().nextInt(Integer.MAX_VALUE))
            .append(new DirectivesData(this.descriptor))
            .append(new DirectivesData(this.visible));
        this.properties.forEach(directives::append);
        return directives.up().iterator();
    }
}
