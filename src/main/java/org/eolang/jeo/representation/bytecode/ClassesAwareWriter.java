/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.PluginStartup;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Class writer that knows about additional classes loaded.
 * This class works in couple with {@link PluginStartup#init()} ()} method that sets
 * the maven classloader as the current thread classloader.
 * Originally we faced with the problem that {@link ClassWriter} uses classes from ClassLoader
 * to perform {@link MethodVisitor#visitMaxs(int, int)} method and if it can't
 * find the class it throws {@link ClassNotFoundException}. To prevent this we override
 * {@link ClassWriter#getClassLoader()} method and return the current thread classloader that
 * knows about all classes that were compiled on the previous maven phases.
 * You can read more about this problem here:
 * - https://gitlab.ow2.org/asm/asm/-/issues/317918
 * - https://stackoverflow.com/questions/11292701/error-while-instrumenting-class-files-asm-classwriter-getcommonsuperclass
 *
 * @since 0.1
 */
class ClassesAwareWriter extends ClassWriter {

    /**
     * Constructor.
     * Do not compute frames automatically.
     */
    ClassesAwareWriter() {
        this(0);
    }

    /**
     * Constructor.
     *
     * @param flags Flags. See {@link ClassWriter#COMPUTE_FRAMES} for more information
     */
    private ClassesAwareWriter(final int flags) {
        super(flags);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else {
            result = other instanceof ClassesAwareWriter;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return ClassesAwareWriter.class.hashCode();
    }

    @Override
    public String toString() {
        return "ClassesAwareWriter()";
    }

    @Override
    protected final ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
