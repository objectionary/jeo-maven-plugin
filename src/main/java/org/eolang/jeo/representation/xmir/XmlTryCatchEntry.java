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
                this.element("start").map(labels::label).orElse(null),
                this.element("end").map(labels::label).orElse(null),
                this.element("handler").map(labels::label).orElse(null),
                this.element("type").orElse(null)
            )
        );
    }

    private Optional<String> element(final String name) {
        return this.node.optchild(name)
            .map(node -> node.child("base", "string").text());
    }

    @Override
    public Node node() {
        return this.node.node();
    }
}
