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
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.DefaultVersion;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;

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
     * Signature.
     */
    private final String signature;

    /**
     * Supername.
     */
    private final String supername;

    /**
     * Interfaces.
     */
    private final String[] interfaces;

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

    public DirectivesClassProperties directives() {
        return new DirectivesClassProperties(
            this.version,
            this.access,
            this.signature,
            this.supername,
            this.interfaces
        );
    }
}
