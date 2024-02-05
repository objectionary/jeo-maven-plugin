package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public final class BytecodeField {
    private final String name;
    private final String descriptor;
    private final String signature;
    private final Object value;
    private final int access;

    private final Collection<BytecodeAnnotation> annotations;

    public BytecodeField(
        final String name,
        final String descriptor,
        final String signature,
        final Object value,
        final int access
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.value = value;
        this.access = access;
        this.annotations = new ArrayList<>(0);
    }

    public void write(final ClassVisitor visitor) {
        final FieldVisitor fvisitor = visitor.visitField(
            this.access,
            this.name,
            this.descriptor,
            this.signature,
            this.value
        );
        this.annotations.forEach(annotation -> annotation.write(fvisitor));
    }

    public void withAnnotation(final String descriptor, final boolean visible) {
        this.annotations.add(new BytecodeAnnotation(descriptor, visible));
    }
}
