package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.ClassVisitor;

public final class BytecodeField {
    private final String name;
    private final String descriptor;
    private final String signature;
    private final Object value;
    private final int access;

    private final ClassVisitor visitor;

    public BytecodeField(
        final String name,
        final String descriptor,
        final String signature,
        final Object value,
        final int access,
        final ClassVisitor visitor
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.value = value;
        this.access = access;
        this.visitor = visitor;
    }

    public void write() {
        this.visitor.visitField(
            this.access,
            this.name,
            this.descriptor,
            this.signature,
            this.value
        );
    }
}
