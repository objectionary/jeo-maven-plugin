/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.xembly.Directive;

/**
 * Bytecode frame values XMIR representation.
 *
 * @since 0.14.0
 */
public final class DirectivesFrameValues implements Iterable<Directive> {

    /**
     * Frame value aliases indexed by their ASM opcode.
     */
    private static final List<String> ALIASES = Collections.unmodifiableList(
        Arrays.asList(
            "top", "integer", "float", "double", "long", "null", "uninit_this", "object", "uninit"
        )
    );

    /**
     * Class names that look like an alias and must be written as a descriptor.
     */
    private static final Pattern ALIAS_LIKE = Pattern.compile("[a-z_]+");

    /**
     * The format of the directives.
     */
    private final Format format;

    /**
     * Name of the values array.
     */
    private final String name;

    /**
     * Frame values.
     */
    private final Object[] values;

    /**
     * Constructor.
     *
     * @param format The format of the directives
     * @param name Name of the values array
     * @param values Frame values
     */
    public DirectivesFrameValues(final Format format, final String name, final Object... values) {
        this.format = format;
        this.name = name;
        this.values = values.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesValues(
            this.format,
            this.name,
            Arrays.stream(this.values).map(DirectivesFrameValues::alias).toArray(Object[]::new)
        ).iterator();
    }

    private static Object alias(final Object value) {
        final Object res;
        if (value instanceof Integer
            && (Integer) value >= 0
            && (Integer) value < DirectivesFrameValues.ALIASES.size()) {
            res = DirectivesFrameValues.ALIASES.get((Integer) value);
        } else if (value instanceof String
            && DirectivesFrameValues.ALIAS_LIKE.matcher((String) value).matches()) {
            res = String.format("L%s;", value);
        } else {
            res = value;
        }
        return res;
    }
}
