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
import org.eolang.jeo.representation.DecodedString;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Directives for a method parameter.
 * @since 0.6
 */
public final class DirectivesMethodParam implements Iterable<Directive> {

    /**
     * Index of the parameter.
     */
    private final int index;

    /**
     * Name of the parameter.
     */
    private final String name;

    /**
     * Method parameter access modifier.
     */
    private final int access;

    /**
     * Type of the parameter.
     */
    private final Type type;

    /**
     * Annotations of the parameter.
     */
    private final DirectivesAnnotations annotations;

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param name Name of the parameter.
     * @param access Access modifier of the parameter.
     * @param type Type of the parameter.
     * @param annotations Annotations of the parameter.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodParam(
        final int index,
        final String name,
        final int access,
        final Type type,
        final DirectivesAnnotations annotations
    ) {
        this.index = index;
        this.name = name;
        this.access = access;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "param",
            String.format(
                "param-%s-%s-%d-%d",
                new DecodedString(this.type.toString()).encode(),
                this.name,
                this.access,
                this.index
            ),
            this.annotations
        ).iterator();
    }
}
