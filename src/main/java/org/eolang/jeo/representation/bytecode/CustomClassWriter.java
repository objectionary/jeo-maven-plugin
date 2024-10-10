/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.bytecode;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.PluginStartup;
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.CheckClassAdapter;

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
     * @param verify Verify bytecode.
     */
    CustomClassWriter(final boolean verify) {
        this(CustomClassWriter.prestructor(verify));
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
     * Which class writer to use.
     * @param verify Verify bytecode.
     * @return A verified class writer if verify is true, otherwise custom class writer.
     */
    private static ClassesAwareWriter prestructor(final boolean verify) {
        final ClassesAwareWriter result;
        if (verify) {
            result = new VerifiedClassWriter();
        } else {
            result = new ClassesAwareWriter();
        }
        return result;
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
        private ClassesAwareWriter() {
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

    /**
     * Class writer that verifies the bytecode.
     * @since 0.2
     */
    @ToString
    @EqualsAndHashCode(callSuper = false)
    private static final class VerifiedClassWriter extends ClassesAwareWriter {

        @Override
        public byte[] toByteArray() {
            final byte[] bytes = super.toByteArray();
            VerifiedClassWriter.verify(bytes);
            return bytes;
        }

        /**
         * Verify the bytecode.
         * @param bytes The bytecode to verify.
         */
        private static void verify(final byte[] bytes) {
            final ClassNode clazz = new ClassNode();
            new ClassReader(bytes)
                .accept(new CheckClassAdapter(clazz, false), ClassReader.SKIP_DEBUG);
            final Optional<Type> syper = Optional.ofNullable(clazz.superName)
                .map(Type::getObjectType);
            final List<Type> interfaces = clazz.interfaces.stream().map(Type::getObjectType)
                .collect(Collectors.toList());
            for (final MethodNode method : clazz.methods) {
                try {
                    final SimpleVerifier verifier =
                        new SimpleVerifier(
                            Type.getObjectType(clazz.name),
                            syper.orElse(null),
                            interfaces,
                            (clazz.access & Opcodes.ACC_INTERFACE) != 0
                        );
                    verifier.setClassLoader(Thread.currentThread().getContextClassLoader());
                    new Analyzer<>(verifier).analyze(clazz.name, method);
                } catch (final ClassFormatError | AnalyzerException exception) {
                    throw new IllegalStateException(
                        String.format(
                            "Bytecode verification failed for the class '%s' and method '%s'",
                            clazz.name,
                            method.name
                        ),
                        exception
                    );
                }
            }
        }
    }
}
