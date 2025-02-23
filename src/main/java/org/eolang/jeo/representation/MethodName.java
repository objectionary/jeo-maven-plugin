/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

/**
 * Method name.
 * Represents java method name.
 * The original purpose of this class is to handle constructor names.
 * They are represented as '<'init'>' in bytecode which isn't allowed in EO.
 * So, we need to convert it to a valid name and back.
 * @since 0.6
 */
public final class MethodName {

    /**
     * Constructor name in EO.
     */
    private static final String EO_CONSTRUCTOR = "object@init@";

    /**
     * Constructor name in bytecode.
     */
    private static final String CONSTRUCTOR = "<init>";

    /**
     * Static initializer name.
     */
    private static final String STATIC = "<clinit>";

    /**
     * Static initializer name in EO.
     */
    private static final String EO_STATIC = "class@clinit@";

    /**
     * Original name.
     */
    private final String original;

    /**
     * Constructor.
     * @param name Method name.
     */
    public MethodName(final String name) {
        this.original = name;
    }

    /**
     * Converts method name to bytecode.
     * @return Bytecode name.
     */
    public String bytecode() {
        final String result;
        switch (this.original) {
            case MethodName.EO_CONSTRUCTOR:
                result = MethodName.CONSTRUCTOR;
                break;
            case MethodName.EO_STATIC:
                result = MethodName.STATIC;
                break;
            default:
                result = this.original;
                break;
        }
        return result;
    }

    /**
     * Converts method name to EO.
     * @return EO name.
     */
    public String xmir() {
        final String result;
        switch (this.original) {
            case MethodName.CONSTRUCTOR:
                result = MethodName.EO_CONSTRUCTOR;
                break;
            case MethodName.STATIC:
                result = MethodName.EO_STATIC;
                break;
            default:
                result = this.original;
                break;
        }
        return result;
    }
}
