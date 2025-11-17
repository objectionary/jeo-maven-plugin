/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Directives for enclosing method attribute.
 * <p>All the 'enclosing-method' attribute directives are sorted according to the JVM specification:
 * {@code
 * EnclosingMethod_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 class_index;
 *     u2 method_index;
 * }}
 * </p>
 * @since 0.14.0
 */
public final class DirectivesEnclosingMethod implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Enclosing class internal name.
     */
    private final String owner;

    /**
     * Enclosing method name.
     */
    private final String method;

    /**
     * Enclosing method descriptor.
     */
    private final String descriptor;

    /**
     * Constructor.
     * @param format Directives format
     * @param owner Enclosing class internal name
     * @param method Enclosing method name
     * @param descriptor Enclosing method descriptor
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesEnclosingMethod(
        final Format format, final String owner, final String method, final String descriptor
    ) {
        this.format = format;
        this.owner = owner;
        this.method = method;
        this.descriptor = descriptor;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String base = "enclosing-method";
        return new DirectivesJeoObject(
            base,
            base,
            new DirectivesValue(this.format, "owner", this.owner),
            new DirectivesValue(this.format, "name", this.method),
            new DirectivesValue(this.format, "descriptor", this.descriptor)
        ).iterator();
    }
}
