package org.eolang.jeo.representation.bytecode;

import com.jcabi.log.Logger;
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
        Logger.info(
            this,
            String.format(
                "Writing try-catch entry into the method with the following values: start=%s, end=%s, handler=%s, type=%s",
                this.start,
                this.end,
                this.handler,
                this.type
            )
        );
        visitor.visitTryCatchBlock(this.start, this.end, this.handler, this.type);
    }
}
