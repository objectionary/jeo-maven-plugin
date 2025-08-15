/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Optional;
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
            .append(new DirectivesValue("version", this.version))
            .append(new DirectivesValue("access", this.access))
            .append(
                new DirectivesValue("signature", Optional.ofNullable(this.signature).orElse(""))
            );
        if (this.supername != null) {
            directives.append(new DirectivesValue("supername", this.supername));
        }
        if (this.interfaces != null) {
            directives.append(new DirectivesValues("interfaces", this.interfaces));
        }
        return directives.iterator();
    }
}
