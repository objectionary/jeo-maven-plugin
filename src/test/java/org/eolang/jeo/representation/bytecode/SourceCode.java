/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import org.cactoos.Input;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Java source code.
 *
 * @since 0.1.0
 */
final class SourceCode extends SimpleJavaFileObject {

    /**
     * Source code.
     */
    private final Input src;

    /**
     * Construct a SimpleJavaFileObject of the given kind and with the
     * given URI.
     *
     * @param name Name of Java class
     * @param input Source of Java class
     */
    SourceCode(final String name, final Input input) {
        this(URI.create(name), input);
    }

    /**
     * Construct a SimpleJavaFileObject of the given kind and with the
     * given URI.
     *
     * @param uri URI of Java class
     * @param input Source of Java class
     */
    private SourceCode(final URI uri, final Input input) {
        super(uri, Kind.SOURCE);
        this.src = input;
    }

    @Override
    public CharSequence getCharContent(final boolean ignore) {
        return new UncheckedText(new TextOf(this.src)).asString();
    }
}
