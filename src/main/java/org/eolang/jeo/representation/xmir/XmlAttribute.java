package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeClass;

public final class XmlAttribute {

    private final XmlNode node;

    public XmlAttribute(final XmlNode node) {
        this.node = node;
    }

    public void writeTo(final BytecodeClass bytecode) {
        final String name = this.node.attribute("name")
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Attribute name is missing in XML node %s", this.node))
            );
        if ("InnerClass".equals(name)) {
            bytecode.withAttribute(
                new BytecodeAttribute.InnerClass(
                    this.node.optchild("name", "name")
                        .map(XmlOperand::new)
                        .map(XmlOperand::asObject)
                        .map(String.class::cast)
                        .orElse(null),
                    this.node.optchild("name", "outer")
                        .map(XmlOperand::new)
                        .map(XmlOperand::asObject)
                        .map(String.class::cast)
                        .orElse(null),
                    this.node.optchild("name", "inner")
                        .map(XmlOperand::new)
                        .map(XmlOperand::asObject)
                        .map(String.class::cast)
                        .orElse(null),
                    this.node.optchild("name", "access")
                        .map(XmlOperand::new)
                        .map(XmlOperand::asObject)
                        .map(Integer.class::cast)
                        .orElse(0)
                )
            );
        }
    }
}
