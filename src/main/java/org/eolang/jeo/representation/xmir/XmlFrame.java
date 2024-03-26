package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.objectweb.asm.MethodVisitor;

public final class XmlFrame implements XmlBytecodeEntry {

    private final XmlNode node;

    public XmlFrame(final String... lines) {
        this(String.join("\n", lines));
    }

    public XmlFrame(final String xml) {
        this(new XmlNode(xml));
    }

    public XmlFrame(final XmlNode node) {
        this.node = node;
    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        method.entry(new FrameEntry());
    }

    int type() {
        return (int) new XmlOperand(this.node.child("name", "type")).asObject();
    }

    int nlocal() {
        return (int) new XmlOperand(this.node.child("name", "nlocal")).asObject();
    }

    int nstack() {
        return (int) new XmlOperand(this.node.child("name", "nstack")).asObject();
    }

    Object[] locals() {
        return this.node.child("name", "local")
            .children()
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .toArray(Object[]::new);
    }

    Object[] stack() {
        return this.node.child("name", "stack")
            .children()
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .toArray(Object[]::new);
    }

    private class FrameEntry implements BytecodeEntry {
        @Override
        public void writeTo(final MethodVisitor visitor) {
            visitor.visitFrame(
                XmlFrame.this.type(),
                XmlFrame.this.nlocal(),
                XmlFrame.this.locals(),
                XmlFrame.this.nstack(),
                XmlFrame.this.stack()
            );
        }

        @Override
        public String testCode() {
            return String.format(
                ".visitFrame(%d, %d, new Object[]{ %s }, %d, new Object[]{ %s })",
                XmlFrame.this.type(),
                XmlFrame.this.nlocal(),
                Arrays.toString(XmlFrame.this.locals()),
                XmlFrame.this.nstack(),
                Arrays.toString(XmlFrame.this.stack())
            );
        }
    }
}
