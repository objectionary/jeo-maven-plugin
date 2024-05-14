package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeClass;

public final class XmlAttributes {

    private final XmlNode node;

    public XmlAttributes(final XmlNode node) {
        this.node = node;
    }

    public void writeTo(final BytecodeClass bytecode) {
        this.node.children().map(XmlAttribute::new).forEach(attr -> attr.writeTo(bytecode));
    }
}
