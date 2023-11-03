package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.objectweb.asm.Label;

public class XmlLabel implements XmlCommand {
    private final XmlNode node;

    XmlLabel(final XmlNode node) {
        this.node = node;
    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        method.markLabel(new Label());
    }
}
