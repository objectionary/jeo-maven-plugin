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
 * Max stack and locals.
 * @since 0.3
 */
public final class DirectivesMaxs implements Iterable<Directive> {

    /**
     * Max stack size.
     */
    private final int stack;

    /**
     * Max locals size.
     */
    private final int locals;

    /**
     * Constructor.
     */
    public DirectivesMaxs() {
        this(0, 0);
    }

    /**
     * Constructor.
     *
     * @param stack Max stack size.
     * @param locals Max locals size.
     */
    public DirectivesMaxs(final int stack, final int locals) {
        this.stack = stack;
        this.locals = locals;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives().add("o")
            .attr("name", "maxs")
            .attr("abstract", "")
            .attr("line", new Random().nextInt(Integer.MAX_VALUE))
            .append(new DirectivesData("stack", this.stack))
            .append(new DirectivesData("locals", this.locals))
            .up()
            .iterator();
    }
}
