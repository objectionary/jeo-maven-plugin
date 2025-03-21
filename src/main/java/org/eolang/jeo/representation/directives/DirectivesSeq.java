/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.scalar.LengthOf;
import org.cactoos.scalar.Unchecked;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives sequence.
 * @since 0.6
 */
public final class DirectivesSeq implements Iterable<Directive> {
    /**
     * Random number generator.
     */
    private static final Random RANDOM = new SecureRandom();

    /**
     * Name of the sequence.
     */
    private final String name;

    /**
     * Directives.
     */
    private final List<? extends Iterable<Directive>> directives;

    /**
     * Constructor.
     * @param name Name of the sequence.
     * @param elements Elements to wrap.
     */
    public DirectivesSeq(final String name, final List<? extends Iterable<Directive>> elements) {
        this.name = name;
        this.directives = elements;
    }

    /**
     * Constructor.
     * @param elements Elements to wrap.
     */
    @SafeVarargs
    DirectivesSeq(final Iterable<Directive>... elements) {
        this("seq", elements);
    }

    /**
     * Constructor.
     *
     * @param name Name of the sequence.
     * @param elements Elements to wrap.
     */
    @SafeVarargs
    private DirectivesSeq(final String name, final Iterable<Directive>... elements) {
        this(name, Arrays.asList(elements));
    }

    @Override
    public Iterator<Directive> iterator() {
        final int number = Math.abs(DirectivesSeq.RANDOM.nextInt());
        final List<Directives> all = this.stream()
            .map(Directives::new)
            .collect(Collectors.toList());
        return new DirectivesJeoObject(
            String.format("seq.of%d", all.size()),
            String.format("%s-%d", this.name, number),
            all
        ).iterator();
    }

    /**
     * Stream of directives.
     * @return Stream of directives.
     */
    private Stream<Directives> stream() {
        return this.directives.stream()
            .filter(Objects::nonNull)
            .filter(dirs -> new Unchecked<>(new LengthOf(dirs)).value() > 0)
            .map(Directives::new);
    }
}
