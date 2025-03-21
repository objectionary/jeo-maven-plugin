/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import java.util.stream.Collectors;
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
    private final XmlNode node;

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
        this.node = node;
    }

    /**
     * Get attribute.
     * @return Attribute.
     */
    public BytecodeAttribute attribute() {
        final String base = this.node.attribute("base")
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Attribute base is missing in XML node %s", this.node)
                )
            );
        final BytecodeAttribute result;
        if (new JeoFqn("inner-class").fqn().equals(base)) {
            result = new InnerClass(
                Optional.ofNullable(this.node.children().collect(Collectors.toList()).get(0))
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(String.class::cast)
                    .filter(s -> !s.isEmpty())
                    .orElse(null),
                Optional.ofNullable(this.node.children().collect(Collectors.toList()).get(1))
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(String.class::cast)
                    .filter(s -> !s.isEmpty())
                    .orElse(null),
                Optional.ofNullable(this.node.children().collect(Collectors.toList()).get(2))
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(String.class::cast)
                    .filter(s -> !s.isEmpty())
                    .orElse(null),
                Optional.ofNullable(this.node.children().collect(Collectors.toList()).get(3))
                    .map(XmlOperand::new)
                    .map(XmlOperand::asObject)
                    .map(Integer.class::cast)
                    .orElse(0)
            );
        } else if (new JeoFqn("local-variable").fqn().equals(base)) {
            result = new XmlLocalVariable(this.node).attribute();
        } else {
            throw new IllegalArgumentException(
                String.format("Unknown attribute base '%s'", base)
            );
        }
        return result;
    }
}
