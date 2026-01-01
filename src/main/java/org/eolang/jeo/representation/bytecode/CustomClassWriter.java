/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.lang.reflect.Field;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.PluginStartup;
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Custom class writer.
 *
 * @since 0.3
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public final class CustomClassWriter extends ClassVisitor {

    /**
     * Bytecode writer.
     */
    private final ClassesAwareWriter writer;

    /**
     * Constructor.
     */
    CustomClassWriter() {
        this(new ClassesAwareWriter());
    }

    /**
     * Constructor.
     * @param writer Writer.
     */
    private CustomClassWriter(final ClassesAwareWriter writer) {
        this(new DefaultVersion().api(), writer);
    }

    /**
     * Constructor.
     * @param api Java ASM API version.
     * @param writer Writer.
     */
    private CustomClassWriter(final int api, final ClassesAwareWriter writer) {
        super(api, writer);
        this.writer = writer;
    }

    /**
     * Generate class bytecode.
     * @return Bytecode.
     */
    public Bytecode bytecode() {
        return new Bytecode(this.writer.toByteArray());
    }

    /**
     * Visits a method of the class.
     * @param access Access flags.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @param compute If frames should be computed.
     * @return Method visitor.
     * @todo #540:90min Compute StackMap frames for Java methods.
     *  Currently we compute only max locals and max stack values for the methods.
     *  See {@link BytecodeMethod#computeMaxs()} method.
     *  However it's not enough to compute only max locals and max stack values.
     *  We also need to compute StackMap frames for the methods.
     *  When we compute frames we can remove this class entirely.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    MethodVisitor visitMethod(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions,
        final boolean compute
    ) {
        final MethodVisitor result;
        if (compute) {
            result = this.visitMethodWithoutFrames(access, name, descriptor, signature, exceptions);
        } else {
            result = this.visitMethod(access, name, descriptor, signature, exceptions);
        }
        return result;
    }

    /**
     * Visits a method of the class and compute all required stack values, locals, and frames.
     * @param access Access flags.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @return Method visitor.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    @SuppressWarnings({"PMD.AvoidAccessibilityAlteration", "PMD.UseObjectForClearerAPI"})
    private MethodVisitor visitMethodWithoutFrames(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String... exceptions
    ) {
        final ClassVisitor delegate = this.getDelegate();
        try {
            final Field field = ClassWriter.class.getDeclaredField("compute");
            field.setAccessible(true);
            final int previous = field.getInt(delegate);
            field.setInt(delegate, 4);
            final MethodVisitor original = this.visitMethod(
                access, name, descriptor, signature, exceptions
            );
            field.setInt(delegate, previous);
            return original;
        } catch (final NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't set compute field for ASM ClassWriter '%s' and change the computation mode to COMPUTE_ALL_FRAMES",
                    delegate
                ),
                exception
            );
        }
    }

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
     * @checkstyle FinalClassCheck (5 lines)
     */
    @ToString
    @EqualsAndHashCode(callSuper = false)
    private static class ClassesAwareWriter extends ClassWriter {

        /**
         * Constructor.
         * Do not compute frames automatically.
         */
        ClassesAwareWriter() {
            this(0);
        }

        /**
         * Constructor.
         * @param flags Flags. See {@link ClassWriter#COMPUTE_FRAMES} for more information.
         */
        private ClassesAwareWriter(final int flags) {
            super(flags);
        }

        @Override
        public final ClassLoader getClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}
