package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class BytecodeTryCatchBlock implements BytecodeEntry {

    private final Label start;
    private final Label end;
    private final Label handler;
    private final String type;

    public BytecodeTryCatchBlock(
        final Label start,
        final Label end,
        final Label handler,
        final String type
    ) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

    @Override
    public void writeTo(final MethodVisitor visitor) {
        visitor.visitTryCatchBlock(this.start, this.end, this.handler, this.type);
    }
}
