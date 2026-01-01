/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.DefaultVersion;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.eolang.jeo.representation.directives.Format;

/**
 * Class properties.
 *
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.DataClass"})
public final class BytecodeClassProperties {

    /**
     * Bytecode version.
     */
    private final int version;

    /**
     * Access modifiers.
     */
    private final int access;

    /**
     * Supername.
     */
    private final String supername;

    /**
     * Interfaces.
     */
    private final String[] interfaces;

    /**
     * Signature.
     */
    private final String signature;

    /**
     * Constructor.
     * @param access Access modifiers.
     */
    public BytecodeClassProperties(final int access) {
        this(new DefaultVersion().bytecode(), access, null, "java/lang/Object", new String[0]);
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param signature Signature.
     * @param supername Supername.
     * @param interfaces Interfaces.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeClassProperties(
        final int access,
        final String signature,
        final String supername,
        final String... interfaces
    ) {
        this(new DefaultVersion().bytecode(), access, signature, supername, interfaces);
    }

    /**
     * Constructor.
     * @param version Bytecode version.
     * @param access Access modifiers.
     * @param signature Signature.
     * @param supername Supername.
     * @param interfaces Interfaces.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeClassProperties(
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

    /**
     * API version.
     * @return Api version.
     */
    public int version() {
        return this.version;
    }

    /**
     * Access modifiers.
     * @return Access modifiers.
     */
    public int access() {
        return this.access;
    }

    /**
     * Class signature.
     * @return Class signature.
     */
    public String signature() {
        return this.signature;
    }

    /**
     * Superclass name.
     * @return Superclass name.
     */
    public String supername() {
        return this.supername;
    }

    /**
     * All class interfaces.
     * @return All class interfaces.
     */
    public String[] interfaces() {
        return this.interfaces.clone();
    }

    public DirectivesClassProperties directives(final Format format, final ClassName name) {
        return new DirectivesClassProperties(
            format,
            this.version,
            this.access,
            name,
            this.supername,
            this.interfaces
        );
    }
}
