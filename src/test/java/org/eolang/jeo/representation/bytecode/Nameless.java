/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * This class represents an XMIR object without names in its inner objects.
 * @since 0.13.0
 * @todo #1150:60min This class was added to hide the problem with random name generation
 *  It's better to fix the problem with random name generation in directives and remove this class.
 *  Find all the usages of {@link org.eolang.jeo.representation.directives.RandName} in directives
 *  and make them deterministic. Especially, in tests.
 */
@EqualsAndHashCode
final class Nameless {

    /**
     * Directives to use for XMIR generation.
     */
    @EqualsAndHashCode.Exclude
    private final Iterable<Directive> directives;

    /**
     * Constructor.
     * @param directives Directives to use.
     */
    Nameless(final Iterable<Directive> directives) {
        this.directives = directives;
    }

    @EqualsAndHashCode.Include
    @Override
    public String toString() {
        return new Xembler(
            new Directives(this.directives)
                .xpath("//o[not(starts-with(@name,'j')) and not(starts-with(@name,'@'))]")
                .attr("name", ""),
            new Transformers.Node()
        ).xmlQuietly();
    }
}
