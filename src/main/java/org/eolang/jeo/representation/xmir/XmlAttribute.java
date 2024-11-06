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
        this(new XmlNode(node));
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
