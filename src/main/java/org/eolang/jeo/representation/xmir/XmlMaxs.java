package org.eolang.jeo.representation.xmir;

public final class XmlMaxs {

    private final XmlNode node;

    public XmlMaxs(final XmlNode node) {
        this.node = node;
    }

    public int stack() {
        return (int) new XmlOperand(this.node.child("name", "stack")).asObject();
    }

    public int locals() {

        return (int) new XmlOperand(this.node.child("name", "locals")).asObject();
    }
}
