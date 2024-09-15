/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import java.util.Iterator;
import org.eolang.jeo.representation.DefaultVersion;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Class properties as Xembly directives.
 *
 * @since 0.1.0
 */
public final class DirectivesClassProperties implements Iterable<Directive> {

    /**
     * Class bytecode version.
     */
    private final int version;

    /**
     * Access modifiers.
     */
    private final int access;

    /**
     * Class Signature.
     */
    private final String signature;

    /**
     * Class supername.
     */
    private final String supername;

    /**
     * Class interfaces.
     */
    private final String[] interfaces;

    /**
     * Constructor.
     */
    public DirectivesClassProperties() {
        this(0);
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     */
    public DirectivesClassProperties(final int access) {
        this(access, "");
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param signature Class Signature.
     */
    public DirectivesClassProperties(final int access, final String signature) {
        this(access, signature, "", new String[0]);
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param signature Class Signature.
     * @param supername Class supername.
     * @param interfaces Class interfaces.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesClassProperties(
        final int access,
        final String signature,
        final String supername,
        final String... interfaces
    ) {
        this(new DefaultVersion().bytecode(), access, signature, supername, interfaces.clone());
    }

    /**
     * Constructor.
     * @param version Bytecode version.
     * @param access Access modifiers.
     * @param signature Class Signature.
     * @param supername Class supername.
     * @param interfaces Class interfaces.
     * @checkstyle ParameterNumberCheck (6 lines)
     */
    public DirectivesClassProperties(
        final int version,
        final int access,
        final String signature,
        final String supername,
        final String... interfaces
    ) {
        this.version = version;
        this.access = access;
        this.signature = signature;
        this.supername = supername;
        this.interfaces = interfaces.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives()
            .append(new DirectivesData("version", this.version))
            .append(new DirectivesData("access", this.access));
        if (this.signature != null) {
            directives.append(new DirectivesData("signature", this.signature));
        }
        if (this.supername != null) {
            directives.append(new DirectivesData("supername", this.supername));
        }
        if (this.interfaces != null) {
            directives.append(new DirectivesTuple("interfaces", this.interfaces));
        }
        return directives.iterator();
    }
}
