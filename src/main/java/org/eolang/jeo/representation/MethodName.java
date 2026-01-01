/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

/**
 * Method name converter between Java bytecode and EO representations.
 *
 * <p>This class handles the conversion of Java method names, particularly
 * special cases like constructors and static initializers. Constructor names
 * are represented as '&lt;init&gt;' in bytecode which isn't allowed in EO,
 * so they need to be converted to valid EO names and back.</p>
 * @since 0.6.0
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
     * @param name The method name (in either bytecode or EO format)
     */
    public MethodName(final String name) {
        this.original = name;
    }

    /**
     * Convert method name to bytecode format.
     * @return The method name in bytecode format
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
     * Convert method name to XMIR/EO format.
     * @return The method name in XMIR/EO format
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
