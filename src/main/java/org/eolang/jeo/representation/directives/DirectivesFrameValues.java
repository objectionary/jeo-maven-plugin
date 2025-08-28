/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import org.xembly.Directive;

/**
 * Bytecode frame values XMIR representation.
 * @since 0.14.0
 */
public final class DirectivesFrameValues implements Iterable<Directive> {

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
     * @param format The format of the directives.
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

    /**
     * Map value to its alias or keep it as is.
     * This method was added to simplify the XMIR representation of bytecode frames.
     * <p>
     *     You can read more about the original intention right here:
     *     <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1211">Issue</a>.
     * </p>
     * @param value Value to map
     * @return Alias of the value or the value itself
     * @checkstyle CyclomaticComplexityCheck (50 lines)
     */
    private static Object alias(final Object value) {
        final Object res;
        if (value instanceof Integer) {
            switch ((Integer) value) {
                case 0:
                    res = "top";
                    break;
                case 1:
                    res = "integer";
                    break;
                case 2:
                    res = "float";
                    break;
                case 3:
                    res = "double";
                    break;
                case 4:
                    res = "long";
                    break;
                case 5:
                    res = "null";
                    break;
                case 6:
                    res = "uninit_this";
                    break;
                case 7:
                    res = "object";
                    break;
                case 8:
                    res = "uninit";
                    break;
                default:
                    res = value;
                    break;
            }
        } else {
            res = value;
        }
        return res;
    }
}
