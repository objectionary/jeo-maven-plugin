package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.ClassWriter;

public class CustomClassWriter extends ClassWriter {
    public CustomClassWriter() {
        super(ClassWriter.COMPUTE_FRAMES);
    }

    @Override
    protected ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
