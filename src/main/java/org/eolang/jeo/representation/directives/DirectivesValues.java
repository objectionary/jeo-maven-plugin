/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
     * @param vaues Values themselves.
     * @param <T> Values type.
     */
    @SafeVarargs
    <T> DirectivesValues(final T... vaues) {
        this("", vaues);
    }

    /**
     * Constructor.
     * @param name Group of values name.
     * @param vals Values themselves.
     * @param <T> Values type.
     */
    @SafeVarargs
    <T> DirectivesValues(final String name, final T... vals) {
        this.name = name;
        this.values = vals.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesSeq(
            this.nonEmptyName(),
            Arrays.stream(this.values)
                .filter(Objects::nonNull)
                .map(DirectivesValue::new)
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
