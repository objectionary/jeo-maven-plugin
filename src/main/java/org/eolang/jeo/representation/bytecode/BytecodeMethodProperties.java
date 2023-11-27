package org.eolang.jeo.representation.bytecode;

import java.util.stream.IntStream;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class BytecodeMethodProperties {

    private final int access;

    private final String name;
    private final String descriptor;
    private final String signature;

    private final String[] exceptions;

    public BytecodeMethodProperties(final String name, final int... modifiers) {
        this(name, "()V", modifiers);
    }

    public BytecodeMethodProperties(
        String name,
        String descriptor,
        int... modifiers
    ) {
        this(name, descriptor, null, modifiers);
    }

    public BytecodeMethodProperties(
        String name,
        String descriptor,
        String signature,
        int... modifiers
    ) {
        this(IntStream.of(modifiers).sum(), name, descriptor, signature, null);
    }

    public BytecodeMethodProperties(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions
    ) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    MethodVisitor addMethod(ClassWriter writer) {
        return writer.visitMethod(
            this.access,
            this.name,
            this.descriptor,
            this.signature,
            this.exceptions
        );
    }
}
