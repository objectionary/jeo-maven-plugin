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
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode instruction.
 * @since 0.1.0
 */
final class CommandInstruction implements BytecodeInstruction {

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
    CommandInstruction(
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
    CommandInstruction(
        final int opcode,
        final List<Object> args
    ) {
        this.opcode = opcode;
        this.args = args;
    }

    @Override
    public void generate(final MethodVisitor visitor) {
        Instruction.find(this.opcode).generate(visitor, this.args);
    }

    /**
     * Bytecode Instruction.
     * @since 0.1.0
     */
    private enum Instruction {

        /**
         * Load the int value 0 onto the stack.
         */
        ICONST_0(Opcodes.ICONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_0)
        ),

        /**
         * Load the int value 1 onto the stack.
         */
        ICONST_1(Opcodes.ICONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_1)
        ),

        /**
         * Load the int value 2 onto the stack.
         */
        ICONST_2(Opcodes.ICONST_2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_2)
        ),

        /**
         * Load the int value 3 onto the stack.
         */
        ICONST_3(Opcodes.ICONST_3, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_3)
        ),

        /**
         * Load the int value 4 onto the stack.
         */
        ICONST_4(Opcodes.ICONST_4, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_4)
        ),

        /**
         * Load the int value 5 onto the stack.
         */
        ICONST_5(Opcodes.ICONST_5, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_5)
        ),

        /**
         * Load the double value 0 onto the stack.
         */
        DCONST_0(Opcodes.DCONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCONST_0)
        ),

        /**
         * Load the double value 1 onto the stack.
         */
        DCONST_1(Opcodes.DCONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCONST_1)
        ),

        /**
         * Push a byte onto the stack as an integer value.
         */
        BIPUSH(Opcodes.BIPUSH, (visitor, arguments) ->
            visitor.visitIntInsn(Opcodes.BIPUSH, (int) arguments.get(0))
        ),

        /**
         * Push a constant #index from a constant pool onto the stack.
         */
        LDC(Opcodes.LDC, (visitor, arguments) ->
            visitor.visitLdcInsn(arguments.get(0))
        ),

        /**
         * Load an int value from a local variable #index.
         */
        ILOAD(Opcodes.ILOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.ILOAD, (int) arguments.get(0))
        ),

        /**
         * Load a double value from a local variable #index.
         */
        DLOAD(Opcodes.DLOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.DLOAD, (int) arguments.get(0))
        ),

        /**
         * Load a reference onto the stack from a local variable #index.
         */
        ALOAD(Opcodes.ALOAD, (visitor, arguments) -> {
            final Object argument = arguments.get(0);
            if (!(argument instanceof Integer)) {
                throw new IllegalStateException(
                    String.format(
                        "Unexpected argument type for ALOAD instruction: %s, value: %s",
                        argument.getClass().getName(),
                        argument
                    )
                );
            }
            visitor.visitVarInsn(Opcodes.ALOAD, (int) argument);
        }
        ),

        /**
         * Add two integers.
         */
        IADD(Opcodes.IADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IADD)
        ),

        /**
         * Store int value into variable #index.
         */
        ISTORE(Opcodes.ISTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.ISTORE, (int) arguments.get(0))
        ),

        /**
         * Store a reference into a local variable #index.
         */
        ASTORE(Opcodes.ASTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.ASTORE, (int) arguments.get(0))
        ),

        /**
         * Discard the top value on the stack.
         */
        POP(Opcodes.POP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.POP)
        ),

        /**
         * Duplicate the value on top of the stack.
         */
        DUP(Opcodes.DUP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP)
        ),

        /**
         * Compare two doubles.
         */
        DCMPL(Opcodes.DCMPL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCMPL)
        ),

        /**
         * If value is less than or equal to 0, branch to instruction at branchoffset.
         */
        IFLE(Opcodes.IFLE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFLE,
                (org.objectweb.asm.Label) arguments.get(0)
            )
        ),

        /**
         * If value1 is less than or equal to value2, branch to instruction at branchoffset.
         */
        IF_ICMPLE(Opcodes.IF_ICMPLE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPLE,
                (org.objectweb.asm.Label) arguments.get(0)
            )
        ),

        /**
         * Return an integer from a method.
         */
        IRETURN(Opcodes.IRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IRETURN)
        ),

        /**
         * Return void from a method.
         */
        RETURN(Opcodes.RETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.RETURN)
        ),

        /**
         * Get a static field value of a class, where the field is
         * identified by field reference in the constant pool index.
         */
        GETSTATIC(Opcodes.GETSTATIC, (visitor, arguments) ->
            visitor.visitFieldInsn(
                Opcodes.GETSTATIC,
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                String.valueOf(arguments.get(2))
            )
        ),

        /**
         * Get a field.
         * Get a field value of an object objectref, where the field is
         * identified by field reference in the constant pool index.
         */
        GETFIELD(Opcodes.GETFIELD, (visitor, arguments) ->
            visitor.visitFieldInsn(
                Opcodes.GETFIELD,
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                String.valueOf(arguments.get(2))
            )
        ),

        /**
         * Set field to value.
         * Set field to value in an object objectref, where the field
         * is identified by a field reference index in constant pool.
         */
        PUTFIELD(Opcodes.PUTFIELD, (visitor, arguments) ->
            visitor.visitFieldInsn(
                Opcodes.PUTFIELD,
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                String.valueOf(arguments.get(2))
            )
        ),

        /**
         * Invoke virtual method.
         * Invoke virtual method on object objectref and puts the result on the
         * stack (might be void); the method is identified by method reference
         * index in constant pool
         */
        INVOKEVIRTUAL(Opcodes.INVOKEVIRTUAL, (visitor, arguments) ->
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                String.valueOf(arguments.get(2)),
                false
            )
        ),

        /**
         * Invoke instance method on object objectref and puts the result on the stack.
         * Might be void. The method is identified by method reference index in constant pool.
         */
        INVOKESPECIAL(Opcodes.INVOKESPECIAL, (visitor, arguments) ->
            visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                String.valueOf(arguments.get(2)),
                false
            )
        ),

        /**
         * Invoke a class (static) method.
         */
        INVOKESTATIC(Opcodes.INVOKESTATIC, (visitor, arguments) ->
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                String.valueOf(arguments.get(2)),
                false
            )
        ),

        /**
         * Create new object of type identified by class reference in constant pool index.
         */
        NEW(Opcodes.NEW, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.NEW,
                String.valueOf(arguments.get(0))
            )
        );

        /**
         * Opcode.
         */
        private final int opcode;

        /**
         * Bytecode generation function.
         */
        private BiConsumer<MethodVisitor, List<Object>> generator;

        /**
         * Constructor.
         * @param opcode Opcode.
         * @param visit Bytecode generation function.
         */
        Instruction(
            final int opcode,
            final BiConsumer<MethodVisitor, List<Object>> visit
        ) {
            this.opcode = opcode;
            this.generator = visit;
        }

        /**
         * Generate bytecode.
         * @param visitor Method visitor.
         * @param arguments Arguments.
         */
        void generate(final MethodVisitor visitor, final List<Object> arguments) {
            this.generator.accept(visitor, arguments);
        }

        /**
         * Get instruction by opcode.
         * @param opcode Opcode.
         * @return Instruction.
         */
        static Instruction find(final int opcode) {
            for (final Instruction instruction : Instruction.values()) {
                if (instruction.opcode == opcode) {
                    return instruction;
                }
            }
            throw new IllegalStateException(
                String.format("Unexpected instruction with opcode: %d", opcode)
            );
        }
    }
}
