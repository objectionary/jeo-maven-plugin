/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
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
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
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
            new RandName("t").toString(),
            Stream.of(
                this.start.directives(),
                this.end.directives(),
                this.handler.directives(),
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
            result = new DirectivesEoObject("nop", new RandName("n").toString());
        }
        return result;
    }
}
