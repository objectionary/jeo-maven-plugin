/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.InnerClass;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * Xml representation of a single bytecode attribute.
 * @since 0.4
 * @todo #589:30min Add Unit Tests for XmlAttribute class.
 *  XmlAttribute class is not covered by unit tests.
 *  Add unit tests for XmlAttribute class in order to increase the code coverage
 *  and improve the quality of the code.
 */
public final class XmlAttribute {

    /**
     * XML node of an attribute.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node XML node as string.
     */
    XmlAttribute(final String node) {
        this(new NativeXmlNode(node));
    }

    /**
     * Constructor.
     * @param node XML node.
     */
    XmlAttribute(final XmlNode node) {
        this.node = new XmlJeoObject(node);
    }

    /**
     * Get attribute.
     * @return Attribute.
     */
    public BytecodeAttribute attribute() {
        final String base = this.node.base().orElseThrow(
            () -> new IllegalArgumentException(
                String.format("Attribute base is not defined in '%s'", this.node)
            )
        );
        final BytecodeAttribute result;
        if (new JeoFqn("inner-class").fqn().equals(base)) {
            result = new InnerClass(
                this.node.child(0)
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(String.class::cast)
                    .filter(s -> !s.isEmpty())
                    .orElse(null),
                this.node.child(1)
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(String.class::cast)
                    .filter(s -> !s.isEmpty())
                    .orElse(null),
                this.node.child(2)
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(String.class::cast)
                    .filter(s -> !s.isEmpty())
                    .orElse(null),
                this.node.child(3)
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(Integer.class::cast)
                    .orElse(0)
            );
        } else if (new JeoFqn("local-variable").fqn().equals(base)) {
            result = new XmlLocalVariable(this.node).attribute();
        } else if (new JeoFqn("source-file").fqn().equals(base)) {
            result = new XmlSourceFile(this.node).attribute();
        } else if (new JeoFqn("enclosing-method").fqn().equals(base)) {
            result = new XmlEnclosingMethod(this.node).attribute();
        } else if (new JeoFqn("nest-host").fqn().equals(base)) {
            result = new XmlNestHost(this.node).attribute();
        } else if (new JeoFqn("nest-members").fqn().equals(base)) {
            result = new XmlNestMembers(this.node).attribute();
        } else if (new JeoFqn("permitted-subclasses").fqn().equals(base)) {
            result = new XmlPermittedSubclasses(this.node).attribute();
        } else {
            throw new IllegalArgumentException(
                String.format("Unknown attribute base '%s'", base)
            );
        }
        return result;
    }
}
