package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.w3c.dom.Node;

public class XmlTryCatchEntry implements XmlBytecodeEntry {

    private final XmlNode node;

    public XmlTryCatchEntry(final XmlNode node) {
        this.node = node;
    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        final AllLabels labels = new AllLabels();
        final String start = this.node.child("start").child("base", "string").text();
        final String end = this.node.child("end").child("base", "string").text();
        final String handler = this.node.child("handler").child("base", "string").text();
        final String type = this.node.child("type").child("base", "string").text();
        method.entry(
            new BytecodeTryCatchBlock(
                labels.label(start),
                labels.label(end),
                labels.label(handler),
                type
            )
        );
    }

    @Override
    public Node node() {
        return this.node.node();
    }
}
