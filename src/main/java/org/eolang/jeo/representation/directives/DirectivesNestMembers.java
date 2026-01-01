/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;

/**
 * Directives for NestMembers attribute.
 * @since 0.14.0
 */
public final class DirectivesNestMembers implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Members class internal names.
     */
    private final List<String> members;

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param members Members class internal names.
     */
    public DirectivesNestMembers(final Format format, final List<String> members) {
        this.format = format;
        this.members = members;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "nest-members",
            "nest-members",
            new DirectivesValues(this.format, "members", this.members.toArray())
        ).iterator();
    }
}
