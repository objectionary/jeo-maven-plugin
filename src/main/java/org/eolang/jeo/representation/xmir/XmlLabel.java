package org.eolang.jeo.representation.xmir;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.objectweb.asm.Label;

public class XmlLabel implements XmlCommand {

    private final static Map<String, Label> LABELS = new ConcurrentHashMap<>();
    private final XmlNode node;

    XmlLabel(final XmlNode node) {
        this.node = node;
    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        final String id = this.node.child("base", "string").text();
        XmlLabel.LABELS.putIfAbsent(id, new Label());
        method.markLabel(XmlLabel.LABELS.get(id));
    }
}
