package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class MarkLabelInstruction implements BytecodeInstruction {

    private final Label label;

    public MarkLabelInstruction(final Label label) {
        this.label = label;
    }

    @Override
    public void generate(final MethodVisitor visitor) {
        visitor.visitLabel(this.label);
    }
}
