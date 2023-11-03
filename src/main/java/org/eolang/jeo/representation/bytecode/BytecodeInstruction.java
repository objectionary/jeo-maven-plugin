package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.MethodVisitor;

public interface BytecodeInstruction {
    void generate(final MethodVisitor visitor);
}
