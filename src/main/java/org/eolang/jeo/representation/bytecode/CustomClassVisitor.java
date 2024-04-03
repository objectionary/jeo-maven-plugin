package org.eolang.jeo.representation.bytecode;

import java.lang.reflect.Field;
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

public final class CustomClassVisitor extends ClassVisitor {

    private final CustomClassWriter writer;


    public CustomClassVisitor(final int api) {
        this(api, new CustomClassWriter());
    }

    public CustomClassVisitor(final CustomClassWriter writer) {
        this(new DefaultVersion().api(), writer);
    }

    public CustomClassVisitor(final int api, final CustomClassWriter writer) {
        super(api, writer);
        this.writer = writer;
    }

    public MethodVisitor visitCustomMethodWithComputation(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions
    ) {

        try {
            final Field field = ClassWriter.class.getDeclaredField("compute");
            field.setAccessible(true);
            field.setInt(this.writer, 4);
            final MethodVisitor original = this.visitMethod(
                access, name, descriptor, signature, exceptions
            );
            field.setInt(this.writer, 0);
            return original;
        } catch (final NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Bytecode bytecode() {
        return new Bytecode(this.writer.toByteArray());
    }
}
