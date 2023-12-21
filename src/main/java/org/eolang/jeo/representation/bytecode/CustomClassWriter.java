package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.JeoMojo;
import org.objectweb.asm.ClassWriter;

/**
 * Custom class writer.
 * This class works in couple with {@link JeoMojo#initClassloader()} method that sets
 * the maven classloader as the current thread classloader.
 * Originally we faced with the problem that {@link ClassWriter} uses classes from ClassLoader
 * to perform {@link org.objectweb.asm.MethodVisitor#visitMaxs(int, int)} method and if it can't
 * find the class it throws {@link ClassNotFoundException}. To prevent this we override
 * {@link ClassWriter#getClassLoader()} method and return the current thread classloader that
 * knows about all classes that were compiled on the previous maven phases.
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
