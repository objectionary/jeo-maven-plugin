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
final class BytecodeInstructionEntry implements BytecodeEntry {

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
    BytecodeInstructionEntry(
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
    BytecodeInstructionEntry(
        final int opcode,
        final List<Object> args
    ) {
        this.opcode = opcode;
        this.args = args;
    }

    @Override
    public void writeTo(final MethodVisitor visitor) {
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
         * Load an int from an array.
         */
        IALOAD(Opcodes.IALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IALOAD)
        ),

        /**
         * Load a long from an array.
         */
        LALOAD(Opcodes.LALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LALOAD)
        ),

        /**
         * Load a float from an array.
         */
        FALOAD(Opcodes.FALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FALOAD)
        ),

        /**
         * Load a double from an array.
         */
        DALOAD(Opcodes.DALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DALOAD)
        ),

        /**
         * Load an object reference from an array.
         */
        AALOAD(Opcodes.AALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.AALOAD)
        ),

        /**
         * Load a byte or Boolean value from an array.
         */
        BALOAD(Opcodes.BALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.BALOAD)
        ),

        /**
         * Load a char from an array.
         */
        CALOAD(Opcodes.CALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.CALOAD)
        ),

        /**
         * Load a short from an array.
         */
        SALOAD(Opcodes.SALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.SALOAD)
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
         * Store int into array.
         */
        IASTORE(Opcodes.IASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IASTORE)
        ),

        /**
         * Store long into array.
         */
        LASTORE(Opcodes.LASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LASTORE)
        ),

        /**
         * Store float into array.
         */
        FASTORE(Opcodes.FASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FASTORE)
        ),

        /**
         * Store double into array.
         */
        DASTORE(Opcodes.DASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DASTORE)
        ),

        /**
         * Store reference into array.
         */
        AASTORE(Opcodes.AASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.AASTORE)
        ),

        /**
         * Store byte or boolean into array.
         */
        BASTORE(Opcodes.BASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.BASTORE)
        ),

        /**
         * Store char into array.
         */
        CASTORE(Opcodes.CASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.CASTORE)
        ),

        /**
         * Store short into array.
         */
        SASTORE(Opcodes.SASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.SASTORE)
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
         * If value1 is greater than or equal to value2, branch to instruction at branchoffset.
         */
        IF_ICMPGE(Opcodes.IF_ICMPGE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPGE,
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
         * Goes to another instruction at branchoffset.
         */
        GOTO(Opcodes.GOTO, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.GOTO,
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
         * Return a reference from a method.
         */
        ARETURN(Opcodes.ARETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ARETURN)
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
         * Invoke interface method on object objectref and puts the result on the stack.
         */
        INVOKEINTERFACE(Opcodes.INVOKEINTERFACE, (visitor, arguments) ->
            visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                String.valueOf(arguments.get(2)),
                true
            )
        ),

        /**
         * Invokes a dynamic method and puts the result on the stack.
         */
        INVOKEDYNAMIC(Opcodes.INVOKEDYNAMIC, (visitor, arguments) ->
            visitor.visitInvokeDynamicInsn(
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                (org.objectweb.asm.Handle) arguments.get(2),
                arguments.subList(3, arguments.size()).toArray()
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
        ),

        /**
         * Create new array with count elements of primitive type identified by atype.
         */
        NEWARRAY(Opcodes.NEWARRAY, (visitor, arguments) ->
            visitor.visitIntInsn(
                Opcodes.NEWARRAY,
                (int) arguments.get(0)
            )
        ),

        /**
         * Create new array of reference type identified by class reference in constant pool index.
         */
        ANEWARRAY(Opcodes.ANEWARRAY, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.ANEWARRAY,
                String.valueOf(arguments.get(0))
            )
        ),

        /**
         * Get the length of an array.
         */
        ARRAYLENGTH(Opcodes.ARRAYLENGTH, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ARRAYLENGTH)
        ),

        /**
         * Throws an error or exception.
         * Notice that the rest of the stack is cleared, leaving only a reference to the Throwable.
         */
        ATHROW(Opcodes.ATHROW, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ATHROW)
        ),

        /**
         * Checks whether an objectref is of a certain type.
         * The class reference of which is in the constant pool at index.
         */
        CHECKCAST(Opcodes.CHECKCAST, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.CHECKCAST,
                String.valueOf(arguments.get(0))
            )
        ),

        /**
         * Determines if an object objectref is of a given type.
         * Identified by class reference index in constant pool .
         */
        INSTANCEOF(Opcodes.INSTANCEOF, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.INSTANCEOF,
                String.valueOf(arguments.get(0))
            )
        ),

        /**
         * Enter monitor for object ("grab the lock" – start of synchronized() section).
         */
        MONITORENTER(Opcodes.MONITORENTER, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.MONITORENTER)
        ),

        /**
         * Exit monitor for object ("release the lock" – end of synchronized() section).
         */
        MONITOREXIT(Opcodes.MONITOREXIT, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.MONITOREXIT)
        ),

        /**
         * Create new multidimensional array.
         */
        MULTIANEWARRAY(Opcodes.MULTIANEWARRAY, (visitor, arguments) ->
            visitor.visitMultiANewArrayInsn(
                String.valueOf(arguments.get(0)),
                (int) arguments.get(1)
            )
        ),

        /**
         * If value is null, branch to instruction at a label.
         */
        IFNULL(Opcodes.IFNULL, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFNULL,
                (org.objectweb.asm.Label) arguments.get(0)
            )
        ),

        /**
         * If value is not null, branch to instruction at a label.
         */
        IFNONNULL(Opcodes.IFNONNULL, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFNONNULL,
                (org.objectweb.asm.Label) arguments.get(0)
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
            throw new UnrecognizedOpcode(opcode);
        }
    }
}
