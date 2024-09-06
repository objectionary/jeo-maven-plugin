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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.ToString;
import org.eolang.jeo.representation.directives.OpcodeName;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode instruction.
 *
 * @since 0.1.0
 */
@ToString
@SuppressWarnings("PMD.ExcessiveClassLength")
public final class BytecodeInstructionEntry implements BytecodeEntry {

    /**
     * Opcode.
     */
    private final int opcode;

    /**
     * Arguments.
     */
    private final List<Object> args;

    /**
     * All labels.
     */
    private final AllLabels labels;

    /**
     * Constructor.
     * @param opcode Opcode.
     * @param args Arguments.
     */
    public BytecodeInstructionEntry(final int opcode, final List<Object> args) {
        this(new AllLabels(), opcode, args);
    }

    /**
     * Constructor.
     *
     * @param labels All labels.
     * @param opcode Opcode.
     * @param args Arguments.
     */
    BytecodeInstructionEntry(
        final AllLabels labels,
        final int opcode,
        final Object... args
    ) {
        this(labels, opcode, Arrays.asList(args));
    }

    /**
     * Constructor.
     *
     * @param opcode Opcode.
     * @param args Arguments.
     */
    BytecodeInstructionEntry(
        final AllLabels labels,
        final int opcode,
        final List<Object> args
    ) {
        this.labels = labels;
        this.opcode = opcode;
        this.args = args;
    }

    @Override
    public void writeTo(final MethodVisitor visitor) {
        Instruction.find(this.opcode).generate(visitor, this.args);
    }

    @Override
    public String testCode() {
        final String args = Stream.concat(
            Stream.of(String.format("Opcodes.%s", new OpcodeName(this.opcode).simplified())),
            this.args.stream().map(arg -> {
                if (arg instanceof String) {
                    return String.format("\"%s\"", arg);
                }
                if (arg instanceof Label) {
                    return String.format(
                        "labels.label(\"%s\")",
                        this.labels.uid((Label) arg)
                    );
                }
                return String.valueOf(arg);
            })
        ).collect(Collectors.joining(", "));
        return String.format(".opcode(%s)", args);
    }

    /**
     * Bytecode Instruction.
     *
     * @since 0.1.0
     */
    @SuppressWarnings("PMD.ExcessiveClassLength")
    private enum Instruction {

        /**
         * Do nothing.
         */
        NOP(Opcodes.NOP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.NOP)
        ),

        /**
         * Push null.
         */
        ACONST_NULL(Opcodes.ACONST_NULL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ACONST_NULL)
        ),

        /**
         * Load the int value −1 onto the stack
         */
        ICONST_M1(Opcodes.ICONST_M1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_M1)
        ),

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
         * Load the long value 0 onto the stack.
         */
        LCONST_0(Opcodes.LCONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LCONST_0)
        ),

        /**
         * Load the long value 1 onto the stack.
         */
        LCONST_1(Opcodes.LCONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LCONST_1)
        ),

        /**
         * Load the float value 0 onto the stack.
         */
        FCONST_0(Opcodes.FCONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCONST_0)
        ),

        /**
         * Load the float value 1 onto the stack.
         */
        FCONST_1(Opcodes.FCONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCONST_1)
        ),

        /**
         * Load the float value 2 onto the stack.
         */
        FCONST_2(Opcodes.FCONST_2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCONST_2)
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
         * Push a short onto the stack as an integer value.
         */
        SIPUSH(Opcodes.SIPUSH, (visitor, arguments) ->
            visitor.visitIntInsn(Opcodes.SIPUSH, (int) arguments.get(0))
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
         * Load a long value from a local variable #index.
         */
        LLOAD(Opcodes.LLOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.LLOAD, (int) arguments.get(0))
        ),

        /**
         * Load a float value from a local variable #index.
         */
        FLOAD(Opcodes.FLOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.FLOAD, (int) arguments.get(0))
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
         * Store int value into variable #index.
         */
        ISTORE(Opcodes.ISTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.ISTORE, (int) arguments.get(0))
        ),

        /**
         * Store long value into variable #index.
         */
        LSTORE(Opcodes.LSTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.LSTORE, (int) arguments.get(0))
        ),

        /**
         * Store float value into variable #index.
         */
        FSTORE(Opcodes.FSTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.FSTORE, (int) arguments.get(0))
        ),

        /**
         * Store double value into variable #index.
         */
        DSTORE(Opcodes.DSTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.DSTORE, (int) arguments.get(0))
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
         * Discard the top two values on the stack.
         */
        POP2(Opcodes.POP2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.POP2)
        ),

        /**
         * Duplicate the value on top of the stack.
         */
        DUP(Opcodes.DUP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP)
        ),

        /**
         * Insert a copy of the top value into the stack two values from the top.
         * value1 and value2 must not be of the type double or long.
         */
        DUP_X1(Opcodes.DUP_X1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP_X1)
        ),

        /**
         * Copy the top one or two operand stack values and insert two or three values down.
         * Insert a copy of the top value into the stack two (if value2 is double or long it takes
         * up the entry of value3, too) or three values (if value2 is neither double nor long) from
         * the top.
         */
        DUP_X2(Opcodes.DUP_X2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP_X2)
        ),

        /**
         * Duplicate top operand stack word or value2 word.
         */
        DUP2(Opcodes.DUP2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP2)
        ),

        /**
         * Duplicate two words and insert beneath third word.
         */
        DUP2_X1(Opcodes.DUP2_X1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP2_X1)
        ),

        /**
         * Duplicate two words and insert beneath fourth word.
         */
        DUP2_X2(Opcodes.DUP2_X2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP2_X2)
        ),

        /**
         * Swap the top two values on the stack.
         * Note that value1 and value2 must not be double or long
         */
        SWAP(Opcodes.SWAP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.SWAP)
        ),

        /**
         * Add two integers.
         */
        IADD(Opcodes.IADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IADD)
        ),
        /**
         * Add two longs.
         */
        LADD(Opcodes.LADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LADD)
        ),

        /**
         * Add two floats.
         */
        FADD(Opcodes.FADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FADD)
        ),

        /**
         * Add two doubles.
         */
        DADD(Opcodes.DADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DADD)
        ),

        /**
         * Subtract two integers.
         */
        ISUB(Opcodes.ISUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ISUB)
        ),

        /**
         * Subtract two longs.
         */
        LSUB(Opcodes.LSUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LSUB)
        ),

        /**
         * Subtract two floats.
         */
        FSUB(Opcodes.FSUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FSUB)
        ),

        /**
         * Subtract two doubles.
         */
        DSUB(Opcodes.DSUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DSUB)
        ),

        /**
         * Multiply two integers.
         */
        IMUL(Opcodes.IMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IMUL)
        ),

        /**
         * Multiply two longs.
         */
        LMUL(Opcodes.LMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LMUL)
        ),

        /**
         * Multiply two floats.
         */
        FMUL(Opcodes.FMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FMUL)
        ),

        /**
         * Multiply two doubles.
         */
        DMUL(Opcodes.DMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DMUL)
        ),

        /**
         * Divide two integers.
         */
        IDIV(Opcodes.IDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IDIV)
        ),

        /**
         * Divide two longs.
         */
        LDIV(Opcodes.LDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LDIV)
        ),

        /**
         * Divide two floats.
         */
        FDIV(Opcodes.FDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FDIV)
        ),

        /**
         * Divide two doubles.
         */
        DDIV(Opcodes.DDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DDIV)
        ),

        /**
         * Remainder of division of two integers.
         */
        IREM(Opcodes.IREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IREM)
        ),

        /**
         * Remainder of division of two longs.
         */
        LREM(Opcodes.LREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LREM)
        ),

        /**
         * Remainder of division of two floats.
         */
        FREM(Opcodes.FREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FREM)
        ),

        /**
         * Remainder of division of two doubles.
         */
        DREM(Opcodes.DREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DREM)
        ),

        /**
         * Negate int.
         */
        INEG(Opcodes.INEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.INEG)
        ),

        /**
         * Negate long.
         */
        LNEG(Opcodes.LNEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LNEG)
        ),

        /**
         * Negate float.
         */
        FNEG(Opcodes.FNEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FNEG)
        ),

        /**
         * Negate double.
         */
        DNEG(Opcodes.DNEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DNEG)
        ),

        /**
         * Shift left int.
         */
        ISHL(Opcodes.ISHL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ISHL)
        ),

        /**
         * Shift left long.
         */
        LSHL(Opcodes.LSHL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LSHL)
        ),

        /**
         * Arithmetic shift right int.
         */
        ISHR(Opcodes.ISHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ISHR)
        ),

        /**
         * Arithmetic shift right long.
         */
        LSHR(Opcodes.LSHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LSHR)
        ),

        /**
         * Logical shift right int.
         */
        IUSHR(Opcodes.IUSHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IUSHR)
        ),

        /**
         * Logical shift right long.
         */
        LUSHR(Opcodes.LUSHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LUSHR)
        ),

        /**
         * Boolean AND int.
         */
        IAND(Opcodes.IAND, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IAND)
        ),

        /**
         * Boolean AND long.
         */
        LAND(Opcodes.LAND, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LAND)
        ),

        /**
         * Boolean OR int.
         */
        IOR(Opcodes.IOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IOR)
        ),

        /**
         * Boolean OR long.
         */
        LOR(Opcodes.LOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LOR)
        ),

        /**
         * Boolean XOR int.
         */
        IXOR(Opcodes.IXOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IXOR)
        ),

        /**
         * Boolean XOR long.
         */
        LXOR(Opcodes.LXOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LXOR)
        ),

        /**
         * Increment local variable #index by signed byte const in next byte.
         */
        IINC(Opcodes.IINC, (visitor, arguments) ->
            visitor.visitIincInsn(
                (int) arguments.get(0),
                (int) arguments.get(1)
            )
        ),

        /**
         * Convert int to long.
         */
        I2L(Opcodes.I2L, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2L)
        ),

        /**
         * Convert int to float.
         */
        I2F(Opcodes.I2F, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2F)
        ),

        /**
         * Convert int to double.
         */
        I2D(Opcodes.I2D, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2D)
        ),

        /**
         * Convert long to int.
         */
        L2I(Opcodes.L2I, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.L2I)
        ),

        /**
         * Convert long to float.
         */
        L2F(Opcodes.L2F, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.L2F)
        ),

        /**
         * Convert long to double.
         */
        L2D(Opcodes.L2D, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.L2D)
        ),

        /**
         * Convert float to int.
         */
        F2I(Opcodes.F2I, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.F2I)
        ),

        /**
         * Convert float to long.
         */
        F2L(Opcodes.F2L, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.F2L)
        ),

        /**
         * Convert float to double.
         */
        F2D(Opcodes.F2D, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.F2D)
        ),

        /**
         * Convert double to int.
         */
        D2I(Opcodes.D2I, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.D2I)
        ),

        /**
         * Convert double to long.
         */
        D2L(Opcodes.D2L, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.D2L)
        ),

        /**
         * Convert double to float.
         */
        D2F(Opcodes.D2F, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.D2F)
        ),

        /**
         * Convert int to byte.
         */
        I2B(Opcodes.I2B, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2B)
        ),

        /**
         * Convert int to char.
         */
        I2C(Opcodes.I2C, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2C)
        ),

        /**
         * Convert int to short.
         */
        I2S(Opcodes.I2S, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2S)
        ),

        /**
         * Push 0 if the two longs are the same, 1 if value1 is greater than value2, -1 otherwise.
         */
        LCMP(Opcodes.LCMP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LCMP)
        ),

        /**
         * Push 0 if the two floats are the same, 1 if value1 is greater than value2, -1 otherwise.
         */
        FCMPL(Opcodes.FCMPL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCMPL)
        ),

        /**
         * Compare two floats, 1 on NaN.
         */
        FCMPG(Opcodes.FCMPG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCMPG)
        ),

        /**
         * Push 0 if the two doubles are the same, 1 if value1 is greater than value2, -1 otherwise.
         */
        DCMPL(Opcodes.DCMPL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCMPL)
        ),

        /**
         * Compare two doubles, 1 on NaN.
         */
        DCMPG(Opcodes.DCMPG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCMPG)
        ),

        /**
         * If value is 0, branch to instruction at branchoffset.
         */
        IFEQ(Opcodes.IFEQ, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFEQ,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is not 0, branch to instruction at branchoffset.
         */
        IFNE(Opcodes.IFNE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFNE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is less than 0, branch to instruction at branchoffset.
         */
        IFLT(Opcodes.IFLT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFLT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is greater than or equal to 0, branch to instruction at branchoffset.
         */
        IFGE(Opcodes.IFGE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFGE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is greater than 0, branch to instruction at branchoffset.
         */
        IFGT(Opcodes.IFGT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFGT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is less than or equal to 0, branch to instruction at branchoffset.
         */
        IFLE(Opcodes.IFLE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFLE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If the two integer values are equal, branch to instruction at branchoffset.
         */
        IF_ICMPEQ(Opcodes.IF_ICMPEQ, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPEQ,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If the two integer values are not equal, branch to instruction at branchoffset.
         */
        IF_ICMPNE(Opcodes.IF_ICMPNE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPNE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is less than value2, branch to instruction at branchoffset.
         */
        IF_ICMPLT(Opcodes.IF_ICMPLT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPLT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is greater than or equal to value2, branch to instruction at branchoffset.
         */
        IF_ICMPGE(Opcodes.IF_ICMPGE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPGE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is greater than value2, branch to instruction at branchoffset.
         */
        IF_ICMPGT(Opcodes.IF_ICMPGT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPGT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is less than or equal to value2, branch to instruction at branchoffset.
         */
        IF_ICMPLE(Opcodes.IF_ICMPLE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPLE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If references are equal, branch to instruction at branchoffset.
         */
        IF_ACMPEQ(Opcodes.IF_ACMPEQ, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ACMPEQ,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If references are not equal, branch to instruction at branchoffset.
         */
        IF_ACMPNE(Opcodes.IF_ACMPNE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ACMPNE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * Goes to another instruction at branchoffset.
         */
        GOTO(Opcodes.GOTO, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.GOTO,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * Jump to subroutine at branchoffset
         */
        JSR(Opcodes.JSR, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.JSR,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * Return from subroutine.
         */
        RET(Opcodes.RET, (visitor, arguments) ->
            visitor.visitVarInsn(
                Opcodes.RET,
                (int) arguments.get(0)
            )
        ),

        /**
         * Access jump table by key match and jump.
         * Continue execution from an address in the table at offset index
         */
        TABLESWITCH(Opcodes.TABLESWITCH, (visitor, arguments) -> {
            visitor.visitTableSwitchInsn(
                (int) arguments.get(0),
                (int) arguments.get(1),
                Label.class.cast(arguments.get(2)),
                arguments.subList(3, arguments.size())
                    .stream()
                    .map(Label.class::cast)
                    .toArray(Label[]::new)
            );
        }
        ),

        /**
         * Access jump table by key match and jump.
         * A target address is looked up from a table using a key and execution
         * continues from the instruction at that address
         */
        LOOKUPSWITCH(Opcodes.LOOKUPSWITCH, (visitor, arguments) -> {
            final List<Label> labels = arguments.stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .collect(Collectors.toList());
            visitor.visitLookupSwitchInsn(
                labels.get(0),
                arguments.stream()
                    .filter(Integer.class::isInstance)
                    .mapToInt(Integer.class::cast)
                    .toArray(),
                labels.subList(1, labels.size()).toArray(new Label[0])
            );
        }
        ),

        /**
         * Return an integer from a method.
         */
        IRETURN(Opcodes.IRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IRETURN)
        ),

        /**
         * Return a long from a method.
         */
        LRTURN(Opcodes.LRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LRETURN)
        ),

        /**
         * Return a float from a method.
         */
        FRETURN(Opcodes.FRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FRETURN)
        ),

        /**
         * Return a double from a method.
         */
        DRETURN(Opcodes.DRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DRETURN)
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
         * Set static field to value.
         * Set static field to value in a class, where the field is identified
         * by a field reference index in constant pool.
         */
        PUTSTATIC(Opcodes.PUTSTATIC, (visitor, arguments) ->
            visitor.visitFieldInsn(
                Opcodes.PUTSTATIC,
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
                Boolean.class.cast(arguments.get(3))
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
                Boolean.class.cast(arguments.get(3))
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
                Boolean.class.cast(arguments.get(3))
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
                Boolean.class.cast(arguments.get(3))
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
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is not null, branch to instruction at a label.
         */
        IFNONNULL(Opcodes.IFNONNULL, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFNONNULL,
                Label.class.cast(arguments.get(0))
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
         *
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
         *
         * @param visitor Method visitor.
         * @param arguments Arguments.
         */
        void generate(final MethodVisitor visitor, final List<Object> arguments) {
            this.generator.accept(visitor, arguments);
        }

        /**
         * Get instruction by opcode.
         *
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
