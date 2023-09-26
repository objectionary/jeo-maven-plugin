package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collection;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

@SuppressWarnings("JTCOP")
public class BytecodeClass {

    private final String name;
    private final ClassWriter writer;

    private final Collection<BytecodeMethod> methods;

    BytecodeClass() {
        this("Simple");
    }

    BytecodeClass(final String name) {
        this(name, new ClassWriter(0));
    }

    private BytecodeClass(final String name, final ClassWriter writer) {
        this.name = name;
        this.writer = writer;
        this.methods = new ArrayList<>(0);
    }

    BytecodeClass withMethod(final String name) {
        this.methods.add(new BytecodeMethod(name, this.writer));
        return this;
    }

    byte[] bytes() {
        this.writer.visit(
            Opcodes.ASM9,
            Opcodes.ACC_PUBLIC,
            this.name,
            null,
            "java/lang/Object",
            null
        );
        this.methods.forEach(BytecodeMethod::generate);
        return this.writer.toByteArray();
    }


    private static class BytecodeMethod {

        private final String name;
        private final ClassWriter writer;

        private BytecodeMethod(final String name, final ClassWriter writer) {
            this.name = name;
            this.writer = writer;
        }

        void generate() {
            this.writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                this.name,
                "()V",
                null,
                null
            );
        }

    }

}
