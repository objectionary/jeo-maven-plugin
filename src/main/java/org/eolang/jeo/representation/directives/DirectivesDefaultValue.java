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
import java.util.Random;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * This class represents a default annotation value.
 * <p>
 *     {@code
 *        public @interface NestedAnnotation {
 *          String name() default "nested-default";
 *        }
 *     }
 * </p>
 * For example, in the code above, the default value is "nested-default".
 *
 * @since 0.3
 */
public final class DirectivesDefaultValue implements Iterable<Directive> {

    /**
     * Default value.
     */
    private final Iterable<Directive> value;

    /**
     * Constructor.
     * @param value Default value.
     */
    public DirectivesDefaultValue(final Iterable<Directive> value) {
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives()
            .add("o")
            .attr("base", "annotation-default-value")
            .attr("name", "annotation-defvalue")
            .attr("line", new Random().nextInt(Integer.MAX_VALUE));
        directives.append(this.value);
        return directives.up().iterator();
    }
}
