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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
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
    private final BytecodeLabel start;

    /**
     * End label.
     */
    private final BytecodeLabel end;

    /**
     * Handler label.
     */
    private final BytecodeLabel handler;

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
        final BytecodeLabel start,
        final BytecodeLabel end,
        final BytecodeLabel handler,
        final String type
    ) {
        final BytecodeLabel empty = new BytecodeLabel((String) null);
        this.start = Optional.ofNullable(start).orElse(empty);
        this.end = Optional.ofNullable(end).orElse(empty);
        this.handler = Optional.ofNullable(handler).orElse(empty);
        this.type = type;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "trycatch",
            Stream.of(
                this.start.directives(false),
                this.end.directives(false),
                this.handler.directives(false),
                DirectivesTryCatch.nullable(this.type)
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }

    /**
     * Wpraps a nullable string into a directive.
     * @param value The value that may be null.
     * @return The directives.
     */
    private static Iterable<Directive> nullable(final String value) {
        final Iterable<Directive> result;
        if (Objects.nonNull(value)) {
            result = new DirectivesValue(value);
        } else {
            result = new DirectivesEoObject("nop");
        }
        return result;
    }
}
