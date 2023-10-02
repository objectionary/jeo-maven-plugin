/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package org.eolang.jeo.representation.asm;

import com.jcabi.xml.XML;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Class useful for generating bytecode for testing purposes.
 * @since 0.1.0
 * @todo #108:90min Refactor BytecodeClass.
 *  The class is too big and has too many responsibilities.
 *  It should be refactored to be more readable and maintainable.
 *  Maybe it makes sence to split it into several classes and put all
 *  of them into the separate package.
 */
@SuppressWarnings({"JTCOP.RuleAllTestsHaveProductionClass", "JTCOP.RuleCorrectTestName"})
public final class BytecodeClass {

    /**
     * Class name.
     */
    private final String name;

    /**
     * ASM class writer.
     */
    private final ClassWriter writer;

    /**
     * Methods.
     */
    private final Collection<BytecodeMethod> methods;

    /**
     * Constructor.
     */
    public BytecodeClass() {
        this("Simple");
    }

    /**
     * Constructor.
     * @param name Class name.
     */
    public BytecodeClass(final String name) {
        this(name.replace(".", "/"), new ClassWriter(ClassWriter.COMPUTE_MAXS));
    }

    /**
     * Constructor.
     * @param name Class name.
     * @param writer ASM class writer.
     */
    private BytecodeClass(final String name, final ClassWriter writer) {
        this.name = name;
        this.writer = writer;
        this.methods = new ArrayList<>(0);
    }

    public BytecodeClass helloWorld() {
        return this.withMethod("main", Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC)
            .descriptor("([Ljava/lang/String;)V")
            .instruction(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .instruction(Opcodes.LDC, "Hello, world!")
            .instruction(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V"
            )
            .instruction(Opcodes.RETURN)
            .up();
    }

    /**
     * Add method.
     * @param mname Method name.
     * @param modifiers Access modifiers.
     * @return This object.
     */
    public BytecodeMethod withMethod(final String mname, final int... modifiers) {
        final BytecodeMethod method = new BytecodeMethod(mname, this.writer, this, modifiers);
        this.methods.add(method);
        return method;
    }

    public XML xml() {
        return new BytecodeRepresentation(this.bytecode().asBytes()).toEO();
    }

    /**
     * Generate bytecode.
     * @return Bytecode.
     */
    public Bytecode bytecode() {
        this.writer.visit(
            new DefaultVersion().java(),
            Opcodes.ACC_PUBLIC,
            this.name,
            null,
            "java/lang/Object",
            null
        );
        this.methods.forEach(BytecodeMethod::generate);
        this.writer.visitEnd();
        final byte[] bytes = this.writer.toByteArray();
        CheckClassAdapter.verify(new ClassReader(bytes), true, new PrintWriter(System.out));
        return new Bytecode(bytes);
    }

    /**
     * Bytecode method.
     * @since 0.1.0
     */
    public static final class BytecodeMethod {

        /**
         * Method name.
         */
        private final String name;

        /**
         * ASM class writer.
         */
        private final ClassWriter writer;

        /**
         * Original class.
         */
        private final BytecodeClass clazz;

        /**
         * Method Instructions.
         */
        private final List<BytecodeInstruction> instructions;

        /**
         * Access.
         */
        private final int[] modifiers;

        /**
         * Method Descriptor.
         */
        private final AtomicReference<String> descr;

        /**
         * Constructor.
         * @param name Method name.
         * @param writer ASM class writer.
         * @param clazz Original class.
         * @param modifiers Access modifiers.
         * @checkstyle ParameterNumberCheck (5 lines)
         */
        private BytecodeMethod(
            final String name,
            final ClassWriter writer,
            final BytecodeClass clazz,
            final int... modifiers
        ) {
            this.name = name;
            this.writer = writer;
            this.clazz = clazz;
            this.instructions = new ArrayList<>(0);
            this.modifiers = Arrays.copyOf(modifiers, modifiers.length);
            this.descr = new AtomicReference<>("()V");
        }

        /**
         * Return to the original class.
         * @return Original class.
         * @checkstyle MethodNameCheck (3 lines)
         */
        @SuppressWarnings("PMD.ShortMethodName")
        public BytecodeClass up() {
            return this.clazz;
        }

        /**
         * Add instruction.
         * @param opcode Opcode.
         * @param args Arguments.
         * @return This object.
         */
        public BytecodeMethod instruction(final int opcode, final Object... args) {
            this.instructions.add(new BytecodeInstruction(opcode, args));
            return this;
        }

        /**
         * Set method descriptor.
         * @param descriptor Descriptor.
         * @return This object.
         */
        public BytecodeMethod descriptor(final String descriptor) {
            this.descr.set(descriptor);
            return this;
        }

        /**
         * Generate bytecode.
         */
        private void generate() {
            int access = 0;
            for (final int modifier : this.modifiers) {
                access |= modifier;
            }
            final MethodVisitor visitor = this.writer.visitMethod(
                access,
                this.name,
                this.descr.get(),
                null,
                null
            );
            this.instructions.forEach(instruction -> instruction.generate(visitor));
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }

        /**
         * Bytecode instruction.
         * @since 0.1.0
         */
        static final class BytecodeInstruction {

            /**
             * Opcode.
             */
            private final int opcode;

            /**
             * Arguments.
             */
            private final List<Object> args;

            /**
             * Constructor.
             * @param opcode Opcode.
             * @param args Arguments.
             */
            BytecodeInstruction(
                final int opcode,
                final Object... args
            ) {
                this(opcode, Arrays.asList(args));
            }

            /**
             * Constructor.
             * @param opcode Opcode.
             * @param args Arguments.
             */
            BytecodeInstruction(
                final int opcode,
                final List<Object> args
            ) {
                this.opcode = opcode;
                this.args = args;
            }

            /**
             * Generate bytecode.
             * @param visitor Method visitor.
             */
            void generate(final MethodVisitor visitor) {
                switch (this.opcode) {
                    case Opcodes.LDC:
                        visitor.visitLdcInsn(this.args.get(0));
                        break;
                    case Opcodes.INVOKEVIRTUAL:
                        visitor.visitMethodInsn(
                            this.opcode,
                            String.valueOf(this.args.get(0)),
                            String.valueOf(this.args.get(1)),
                            String.valueOf(this.args.get(2)),
                            false
                        );
                        break;
                    case Opcodes.IRETURN:
                        visitor.visitInsn(Opcodes.IRETURN);
                        break;
                    case Opcodes.BIPUSH:
                        visitor.visitIntInsn(Opcodes.BIPUSH, (Integer) this.args.get(0));
                        break;
                    case Opcodes.GETSTATIC:
                        visitor.visitFieldInsn(
                            Opcodes.GETSTATIC,
                            this.args.get(0).toString(),
                            this.args.get(1).toString(),
                            this.args.get(2).toString()
                        );
                        break;
                    case Opcodes.RETURN:
                        visitor.visitInsn(Opcodes.RETURN);
                        break;
                    default:
                        throw new IllegalStateException(
                            String.format("Unexpected value: %d", this.opcode)
                        );
                }
            }
        }
    }
}
