/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for bytes.
 * @since 0.6
 */
public final class DirectivesBytes implements Iterable<Directive> {

    /**
     * Hex representation of bytes.
     * For example,
     * "00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F"
     */
    private final String hex;

    /**
     * Constructor.
     * @param hex Hex representation of bytes.
     */
    public DirectivesBytes(final String hex) {
        this.hex = hex;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesClosedObject(
            "Q.org.eolang.bytes",
            new Directives().add("o").set(this.hex).up()
        ).iterator();
    }
}
