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
     * Name of the object.
     */
    private final String name;

    /**
     * The 'as' attribute of the object.
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

    /**
     * Constructor.
     * @param hex Hex representation of bytes.
     */
    public DirectivesBytes(final String hex) {
        this(hex, "");
    }

    /**
     * Constructor.
     * @param hex Hex representation of bytes.
     * @param name Name of the object.
     */
    public DirectivesBytes(final String hex, final String name) {
        this(hex, name, "");
    }

    /**
     * Constructor.
     * @param hex Hex representation of bytes.
     * @param name Name of the object.
     * @param as The 'as' attribute of the object.
     * @checkstyle ParameterNameCheck (5 lines)
     */
    public DirectivesBytes(final String hex, final String name, final String as) {
        this.hex = hex;
        this.name = name;
        this.as = as;
    }

    @Override
    public Iterator<Directive> iterator() {
        final DirectivesClosedObject directives;
        if (this.name.isEmpty()) {
            directives = new DirectivesClosedObject(
                "Φ.org.eolang.bytes",
                this.as,
                new Directives().add("o").attr("as", "v1").set(this.hex).up()
            );
        } else {
            directives = new DirectivesClosedObject(
                "Φ.org.eolang.bytes",
                this.as,
                this.name,
                new Directives().add("o").attr("as", "v1").set(this.hex).up()
            );
        }
        return directives.iterator();
    }
}
