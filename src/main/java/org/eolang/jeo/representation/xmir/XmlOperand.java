/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * XML operand.
 * @since 0.3
 */
@ToString
@EqualsAndHashCode
final class XmlOperand {

    /**
     * Raw XML node which represents an instruction operand.
     */
    @EqualsAndHashCode.Exclude
    private final XmlNode raw;

    /**
     * Constructor.
     * @param node Raw XML operand node.
     */
    XmlOperand(final XmlNode node) {
        this.raw = node;
    }

    /**
     * Convert XML operand to an object.
     * @return Object.
     */
    @EqualsAndHashCode.Include
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
