/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Directives for source file and debug info.
 * @since 0.14.0
 */
public final class DirectivesSourceFile implements Iterable<Directive> {
    /**
     * Directives format.
     */
    private final Format format;

    /**
     * The name of the source file from which this class was compiled.
     */
    private final String source;

    /**
     * The correspondence between source and compiled elements of this class.
     */
    private final String debug;

    /**
     * Constructor.
     * @param format Directives format.
     * @param source The name of the source file from which this class was compiled.
     * @param debug The correspondence between source and compiled elements of this class.
     */
    public DirectivesSourceFile(final Format format, final String source, final String debug) {
        this.format = format;
        this.source = source;
        this.debug = debug;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String attribute = "source-file";
        return new DirectivesJeoObject(
            attribute,
            attribute,
            new DirectivesValue(this.format, "source", this.source),
            new DirectivesValue(this.format, "debug", this.debug)
        ).iterator();
    }
}
