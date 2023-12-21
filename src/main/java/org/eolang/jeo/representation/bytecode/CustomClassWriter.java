package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.ClassWriter;

/**
 * Custom class writer.
 *
 * @since 0.1
 */
public class CustomClassWriter extends ClassWriter {
    CustomClassWriter() {
        super(ClassWriter.COMPUTE_FRAMES);
    }

    @Override
    protected ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
