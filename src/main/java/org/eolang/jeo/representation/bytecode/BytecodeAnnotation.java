package org.eolang.jeo.representation.bytecode;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public final class BytecodeAnnotation {

    private final String descriptor;
    private final boolean visible;

    public BytecodeAnnotation(final String descriptor, final boolean visible) {
        this.descriptor = descriptor;
        this.visible = visible;
    }

    public BytecodeAnnotation write(final ClassVisitor visitor) {
        visitor.visitAnnotation(this.descriptor, this.visible);
        return this;
    }

    public BytecodeAnnotation write(final FieldVisitor visitor) {
        visitor.visitAnnotation(this.descriptor, this.visible);
        return this;
    }
}
