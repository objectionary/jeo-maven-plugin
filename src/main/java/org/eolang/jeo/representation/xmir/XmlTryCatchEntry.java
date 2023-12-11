package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.w3c.dom.Node;

public class XmlTryCatchEntry implements XmlBytecodeEntry {

    private final XmlNode node;

    public XmlTryCatchEntry(final XmlNode node) {
        this.node = node;
    }


    @Override
    public void writeTo(final BytecodeMethod method) {

    }

    @Override
    public Node node() {
        return this.node.node();
    }
}
