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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Class useful for generating bytecode for testing purposes.
 * @since 0.1.0
 */
@SuppressWarnings({"JTCOP.RuleAllTestsHaveProductionClass", "JTCOP.RuleCorrectTestName"})
final class BytecodeClass {

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
    BytecodeClass() {
        this("Simple");
    }

    /**
     * Constructor.
     * @param name Class name.
     */
    BytecodeClass(final String name) {
        this(name, new ClassWriter(0));
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

    /**
     * Add method.
     * @param mname Method name.
     * @return This object.
     */
    BytecodeMethod withMethod(final String mname) {
        final BytecodeMethod method = new BytecodeMethod(mname, this.writer, this);
        this.methods.add(method);
        return method;
    }

    /**
     * Generate bytecode.
     * @return Bytecode.
     */
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

    /**
     * Bytecode method.
     * @since 0.1.0
     */
    static final class BytecodeMethod {

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
         * Constructor.
         * @param name Method name.
         * @param writer ASM class writer.
         * @param clazz Original class.
         */
        private BytecodeMethod(
            final String name,
            final ClassWriter writer,
            final BytecodeClass clazz
        ) {
            this.name = name;
            this.writer = writer;
            this.clazz = clazz;
            this.instructions = new ArrayList<>(0);
        }

        /**
         * Add instruction.
         * @param opcode Opcode.
         * @param args Arguments.
         * @return This object.
         */
        BytecodeMethod instruction(final int opcode, final Object... args) {
            this.instructions.add(new BytecodeInstruction(opcode, args));
            return this;
        }

        /**
         * Return to the original class.
         * @return Original class.
         * @checkstyle MethodNameCheck (3 lines)
         */
        @SuppressWarnings("PMD.ShortMethodName")
        BytecodeClass up() {
            return this.clazz;
        }

        /**
         * Generate bytecode.
         */
        private void generate() {
            final MethodVisitor visitor = this.writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                this.name,
                "()V",
                null,
                null
            );
            this.instructions.forEach(instruction -> instruction.generate(visitor));
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
                    default:
                        throw new IllegalStateException(
                            String.format("Unexpected value: %d", this.opcode)
                        );
                }
            }
        }
    }
}
