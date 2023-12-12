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

import java.util.Iterator;
import java.util.Objects;
import org.objectweb.asm.Label;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Try catch directives.
 * @since 0.1
 */
public final class DirectivesTryCatch implements Iterable<Directive> {

    /**
     * Start label.
     */
    private final Label start;

    /**
     * End label.
     */
    private final Label end;

    /**
     * Handler label.
     */
    private final Label handler;

    /**
     * Exception type.
     */
    private final String type;

    /**
     * Constructor.
     * @param start Start label
     * @param end End label
     * @param handler Handler label
     * @param type Exception type
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesTryCatch(
        final Label start,
        final Label end,
        final Label handler,
        final String type
    ) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives().add("o")
            .attr("base", "trycatch");
        if (Objects.nonNull(this.start)) {
            directives.append(new DirectivesLabel(this.start, "start"));
        }
        if (Objects.nonNull(this.end)) {
            directives.append(new DirectivesLabel(this.end, "end"));
        }
        if (Objects.nonNull(this.handler)) {
            directives.append(new DirectivesLabel(this.handler, "handler"));
        }
        if (Objects.nonNull(this.type)) {
            directives.append(new DirectivesData("type", this.type));
        }
        return directives.up().iterator();
    }
}
