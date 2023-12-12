package org.eolang.jeo.representation.xmir;

import java.util.Optional;
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
        method.entry(
            new BytecodeTryCatchBlock(
                this.label("start").map(labels::label).orElse(null),
                this.label("end").map(labels::label).orElse(null),
                this.label("handler").map(labels::label).orElse(null),
                this.type().map(HexString::new).map(HexString::decode).orElse(null)
            )
        );
    }

    Optional<String> label(final String name) {
        return this.node.optchild("name", name)
            .map(node -> node.child("base", "string").text());
    }

    Optional<String> type(){
        return this.node.optchild("name", "type").map(XmlNode::text);
    }

    @Override
    public Node node() {
        return this.node.node();
    }
}
