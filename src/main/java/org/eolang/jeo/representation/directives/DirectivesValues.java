/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.xembly.Directive;

/**
 * Array representation of some values as Xembly directives.
 * We used to use "tuple" for this, but it leads to some confusion.
 * You can read more about this problem
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/707">here</a>
 * @since 0.6
 */
public final class DirectivesValues implements Iterable<Directive> {

    /**
     * Pattern to remove all non-digits from the beginning of a string.
     */
    private static final Pattern DIGITS = Pattern.compile("^[0-9]");

    /**
     * Tuple name.
     */
    private final String name;

    /**
     * Tuple values.
     */
    private final Object[] values;

    /**
     * Constructor.
     * @param name Group of values name.
     * @param vals Values themselves.
     * @param <T> Values type.
     */
    @SafeVarargs
    public <T> DirectivesValues(final String name, final T... vals) {
        this.name = name;
        this.values = vals.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        final AtomicInteger index = new AtomicInteger(0);
        return new DirectivesSeq(
            this.nonEmptyName(),
            Arrays.stream(this.values)
                .map(
                    value -> {
                        final Iterable<Directive> result;
                        if (value instanceof BytecodeLabel) {
                            result = ((BytecodeEntry) value).directives();
                        } else {
                            result = new DirectivesValue(
                                String.format("x%d", index.getAndIncrement()),
                                value
                            );
                        }
                        return result;
                    }
                )
                .collect(Collectors.toList())
        ).iterator();
    }

    /**
     * Name of the group of values.
     * @return Name of the group of values.
     */
    private String nonEmptyName() {
        final String result;
        if (this.name.isEmpty()) {
            result = DirectivesValues.randomName();
        } else {
            result = this.name;
        }
        return result;
    }

    /**
     * Generate random name.
     * @return Random name.
     */
    private static String randomName() {
        return DirectivesValues.DIGITS.matcher(
            UUID.randomUUID().toString().toLowerCase(Locale.getDefault())
        ).replaceAll("a");
    }
}
