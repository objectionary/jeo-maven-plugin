/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Objects;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * XML operand.
 *
 * @since 0.3
 */
final class XmlOperand {

    /**
     * Raw XML node which represents an instruction operand.
     */
    private final XmlNode raw;

    /**
     * Constructor.
     *
     * @param node Raw XML operand node
     */
    XmlOperand(final XmlNode node) {
        this.raw = node;
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof XmlOperand) {
            result = Objects.equals(this.asObject(), ((XmlOperand) other).asObject());
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.asObject());
    }

    @Override
    public String toString() {
        return String.format("XmlOperand(raw=%s)", this.raw);
    }

    /**
     * Convert XML operand to an object.
     *
     * @return Object
     */
    Object asObject() {
        final String base = this.base();
        final Object result;
        if (new JeoFqn("handle").fqn().equals(base)) {
            result = new XmlHandle(this.raw).bytecode().asHandle();
        } else if (new JeoFqn("type").fqn().equals(base)) {
            result = new XmlType(this.raw).type();
        } else if (new JeoFqn("annotation").fqn().equals(base)) {
            result = new XmlAnnotation(this.raw).bytecode();
        } else if (new JeoFqn("annotation-property").fqn().equals(base)) {
            result = new XmlAnnotationValue(this.raw).bytecode();
        } else if (new JeoFqn("label").fqn().equals(base)) {
            result = new XmlLabel(this.raw).bytecode();
        } else if (new JeoFqn("constant-dynamic").fqn().equals(base)) {
            result = new XmlConstantDynamic(this.raw).constant();
        } else {
            result = new XmlValue(this.raw).object();
        }
        return result;
    }

    private String base() {
        return new XmlJeoObject(this.raw)
            .base()
            .orElseGet(() -> new XmlClosedObject(this.raw).base());
    }
}
