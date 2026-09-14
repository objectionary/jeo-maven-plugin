/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.lang.reflect.Field;
import java.util.Objects;
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Custom class writer.
 *
 * @since 0.3
 */
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
     *
     * @param writer Writer
     */
    private CustomClassWriter(final ClassesAwareWriter writer) {
        this(new DefaultVersion().api(), writer);
    }

    /**
     * Constructor.
     *
     * @param api Java ASM API version
     * @param writer Writer
     */
    private CustomClassWriter(final int api, final ClassesAwareWriter writer) {
        super(api, writer);
        this.writer = writer;
    }

    /**
     * Generate class bytecode.
     *
     * @return Bytecode
     */
    public Bytecode bytecode() {
        return new Bytecode(this.writer.toByteArray());
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof CustomClassWriter) {
            result = Objects.equals(this.writer, ((CustomClassWriter) other).writer);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.writer);
    }

    @Override
    public String toString() {
        return String.format("CustomClassWriter(writer=%s)", this.writer);
    }

    /**
     * Visits a method of the class.
     *
     * @param access Access flags
     * @param name Method name
     * @param descriptor Method descriptor
     * @param signature Method signature
     * @param exceptions Method exceptions
     * @param compute If frames should be computed
     * @return Method visitor
     * @todo #540:90min Compute StackMap frames for Java methods.
     *  Currently we compute only max locals and max stack values for the methods.
     *  See {@link BytecodeMethod#computeMaxs()} method.
     *  However it's not enough to compute only max locals and max stack values.
     *  We also need to compute StackMap frames for the methods.
     *  When we compute frames we can remove this class entirely.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
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

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
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
}
