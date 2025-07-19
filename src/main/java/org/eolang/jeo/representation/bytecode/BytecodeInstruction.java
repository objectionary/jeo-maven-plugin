/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.OpcodeName;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Bytecode instruction.
 * @since 0.1
 * @checkstyle FileLengthCheck (2000 lines)
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.GodClass", "PMD.TooManyMethods"})
public final class BytecodeInstruction implements BytecodeEntry {

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
    public BytecodeInstruction(final int opcode, final Object... args) {
        this(opcode, Arrays.asList(args));
    }

    /**
     * Constructor.
     * @param opcode Opcode.
     * @param args Arguments.
     */
    private BytecodeInstruction(
        final int opcode,
        final List<Object> args
    ) {
        this.opcode = opcode;
        this.args = args;
    }

    @Override
    public void writeTo(final MethodVisitor visitor, final AsmLabels labels) {
        Instruction.find(this.opcode)
            .generate(
                visitor,
                this.args.stream()
                    .map(
                        arg -> {
                            final Object result;
                            if (arg instanceof BytecodeLabel) {
                                result = labels.label((BytecodeLabel) arg);
                            } else {
                                result = arg;
                            }
                            return result;
                        }
                    ).collect(Collectors.toList())
            );
    }

    @Override
    public Iterable<Directive> directives() {
        return new DirectivesInstruction(this.opcode, this.args.toArray());
    }

    @Override
    public boolean isLabel() {
        return false;
    }

    @Override
    public boolean isOpcode() {
        return true;
    }


    public int localIndex() {
        return this.varIndex();
    }

    /**
     *   // Standard stack map frame element types.
     *
     *   Integer TOP = Frame.ITEM_TOP;
     *   Integer INTEGER = Frame.ITEM_INTEGER;
     *   Integer FLOAT = Frame.ITEM_FLOAT;
     *   Integer DOUBLE = Frame.ITEM_DOUBLE;
     *   Integer LONG = Frame.ITEM_LONG;
     *   Integer NULL = Frame.ITEM_NULL;
     *   Integer UNINITIALIZED_THIS = Frame.ITEM_UNINITIALIZED_THIS;
     * @return
     */
    public Object elementType() {
        final Object result;
        switch (this.opcode) {
            case Opcodes.ILOAD:
            case Opcodes.ISTORE:
                result = Opcodes.INTEGER;
                break;
            case Opcodes.LLOAD:
            case Opcodes.LSTORE:
                result = Opcodes.LONG;
                break;
            case Opcodes.FLOAD:
            case Opcodes.FSTORE:
                result = Opcodes.FLOAT;
                break;
            case Opcodes.DLOAD:
            case Opcodes.DSTORE:
                result = Opcodes.DOUBLE;
                break;
            case Opcodes.ALOAD:
            case Opcodes.ASTORE:
                result = Opcodes.TOP;
                break;
            default:
                throw new IllegalStateException(
                    String.format(
                        "Unexpected opcode for local variable instruction: %s",
                        new OpcodeName(this.opcode).simplified()
                    )
                );
        }
        return result;
    }

    /**
     * Impact of each instruction on the stack.
     * @return Stack impact.
     * @checkstyle CyclomaticComplexityCheck (350 lines)
     * @checkstyle MethodLengthCheck (350 lines)
     * @checkstyle JavaNCSSCheck (350 lines)
     * @checkstyle AvoidNestedBlocksCheck (350 lines)
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.ExcessiveMethodLength"})
    public int impact() {
        final int result;
        final Instruction instruction = Instruction.find(this.opcode);
        switch (instruction) {
            case LASTORE:
            case DASTORE:
                result = -4;
                break;
            case IASTORE:
            case FASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
            case LCMP:
            case DCMPL:
            case DCMPG:
                result = -3;
                break;
            case LSTORE:
            case DSTORE:
            case POP2:
            case LADD:
            case LSUB:
            case LMUL:
            case LDIV:
            case LREM:
            case DADD:
            case DSUB:
            case DMUL:
            case DDIV:
            case DREM:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case LOR:
            case LAND:
            case LXOR:
            case LRETURN:
            case DRETURN:
                result = -2;
                break;
            case IALOAD:
            case FALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case IADD:
            case ISUB:
            case IMUL:
            case IDIV:
            case IREM:
            case FADD:
            case FSUB:
            case FMUL:
            case FDIV:
            case FREM:
            case LSHL:
            case LSHR:
            case LUSHR:
            case POP:
            case FCMPL:
            case FCMPG:
            case L2I:
            case L2F:
            case D2I:
            case D2F:
            case ISTORE:
            case FSTORE:
            case ASTORE:
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IFNULL:
            case IFNONNULL:
            case IOR:
            case IAND:
            case IXOR:
            case ISHL:
            case ISHR:
            case IUSHR:
            case IRETURN:
            case FRETURN:
            case ARETURN:
            case MONITORENTER:
            case MONITOREXIT:
            case TABLESWITCH:
            case LOOKUPSWITCH:
            case ATHROW:
                result = -1;
                break;
            case NOP:
            case SWAP:
            case I2F:
            case F2I:
            case I2B:
            case I2C:
            case I2S:
            case L2D:
            case D2L:
            case LALOAD:
            case DALOAD:
            case DNEG:
            case FNEG:
            case LNEG:
            case INEG:
            case GOTO:
            case JSR:
            case RET:
            case RETURN:
            case IINC:
            case NEWARRAY:
            case ANEWARRAY:
            case ARRAYLENGTH:
            case CHECKCAST:
            case INSTANCEOF:
                result = 0;
                break;
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case BIPUSH:
            case SIPUSH:
            case ILOAD:
            case FLOAD:
            case ALOAD:
            case DUP:
            case DUP_X1:
            case DUP_X2:
            case I2L:
            case I2D:
            case F2L:
            case F2D:
            case NEW:
                result = 1;
                break;
            case LCONST_0:
            case LCONST_1:
            case DCONST_0:
            case DCONST_1:
            case LLOAD:
            case DLOAD:
            case DUP2:
            case DUP2_X1:
            case DUP2_X2:
                result = 2;
                break;
            case LDC: {
                final Class<?> clazz = this.args.get(0).getClass();
                if (clazz == Long.class || clazz == Double.class) {
                    result = 2;
                    break;
                } else {
                    result = BytecodeInstruction.size(Type.getType(clazz));
                    break;
                }
            }
            case GETSTATIC:
                result = BytecodeInstruction.size(Type.getType(String.valueOf(this.args.get(2))));
                break;
            case PUTSTATIC:
                result = BytecodeInstruction.size(
                    Type.getType(String.valueOf(this.args.get(2)))
                ) * -1;
                break;
            case GETFIELD:
                result = BytecodeInstruction.size(
                    Type.getType(String.valueOf(this.args.get(2)))
                ) - 1;
                break;
            case PUTFIELD:
                result = BytecodeInstruction.size(
                    Type.getType(String.valueOf(this.args.get(2)))
                ) * -1 - 1;
                break;
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKEINTERFACE:
                result = BytecodeInstruction.methodImpact(String.valueOf(this.args.get(2))) - 1;
                break;
            case INVOKESTATIC:
                result = BytecodeInstruction.methodImpact(String.valueOf(this.args.get(2)));
                break;
            case INVOKEDYNAMIC:
                result = BytecodeInstruction.methodImpact(String.valueOf(this.args.get(1)));
                break;
            case MULTIANEWARRAY:
                result = -(int) (this.args.get(1)) + 1;
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format(
                        "Unsupported opcode: %s", new OpcodeName(this.opcode).simplified()
                    )
                );
        }
        return result;
    }

    /**
     * Is this instruction a variable instruction?
     * @return True if it is.
     */
    boolean isVarInstruction() {
        return Instruction.find(this.opcode).isVarInstruction();
    }

    /**
     * Local variable index.
     * @return Local variable index.
     */
    int varIndex() {
        this.assertVarInstruction();
        return (int) this.args.get(0);
    }

    /**
     * Local variable size.
     * @return Local variable size.
     */
    int varSize() {
        this.assertVarInstruction();
        return Instruction.find(this.opcode).size();
    }

    /**
     * Is this instruction a jump instruction?
     * Is it a goto or jsr?
     * @return True if it is.
     */
    @Override
    public boolean isJump() {
        return Instruction.find(this.opcode) == Instruction.GOTO
            || Instruction.find(this.opcode) == Instruction.JSR;
    }

    /**
     * Is this instruction a conditional branch instruction?
     * @return True if it is.
     * @checkstyle CyclomaticComplexityCheck (100 lines)
     */
    @Override
    public boolean isIf() {
        final boolean result;
        switch (Instruction.find(this.opcode)) {
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case IFNULL:
            case IFNONNULL:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * Is this instruction a switch instruction?
     * @return True if it is.
     */
    @Override
    public boolean isSwitch() {
        final boolean result;
        switch (Instruction.find(this.opcode)) {
            case TABLESWITCH:
            case LOOKUPSWITCH:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * Is this instruction a return instruction?
     * @return True if it is.
     */
    @Override
    public boolean isReturn() {
        final boolean result;
        switch (Instruction.find(this.opcode)) {
            case IRETURN:
            case FRETURN:
            case ARETURN:
            case LRETURN:
            case DRETURN:
            case RETURN:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * Jump to a label.
     * Where to jump.
     * @return Jump label.
     * @checkstyle CyclomaticComplexityCheck (100 lines)
     */
    public List<BytecodeLabel> jumps() {
        final List<BytecodeLabel> result;
        switch (Instruction.find(this.opcode)) {
            case GOTO:
            case JSR:
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case IFNULL:
            case IFNONNULL:
                result = Collections.singletonList((BytecodeLabel) this.args.get(0));
                break;
            case TABLESWITCH:
            case LOOKUPSWITCH:
                result = this.args.stream()
                    .filter(BytecodeLabel.class::isInstance)
                    .map(BytecodeLabel.class::cast)
                    .collect(Collectors.toList());
                break;
            default:
                throw new IllegalStateException(
                    String.format(
                        "Instruction %s is not a branch instruction",
                        new OpcodeName(this.opcode).simplified()
                    )
                );
        }
        return result;
    }

    @Override
    public String view() {
        return String.format(
            "%s %s",
            new OpcodeName(this.opcode).simplified(),
            this.args.stream().map(Object::toString).collect(Collectors.joining(" "))
        );
    }

    /**
     * Is this instruction a variable instruction?
     * @throws IllegalStateException If it is not.
     */
    private void assertVarInstruction() {
        if (!this.isVarInstruction()) {
            throw new IllegalStateException(
                String.format(
                    "Instruction %s is not a variable instruction",
                    new OpcodeName(this.opcode).simplified()
                )
            );
        }
    }

    /**
     * Size of the type.
     * @param type Type.
     * @return Size.
     */
    private static int size(final Type type) {
        final int result;
        if (Objects.equals(type, Type.DOUBLE_TYPE) || Objects.equals(type, Type.LONG_TYPE)) {
            result = 2;
        } else if (Objects.equals(type, Type.VOID_TYPE)) {
            result = 0;
        } else {
            result = 1;
        }
        return result;
    }

    /**
     * Impact of the method invocation on stack.
     * @param descriptor Method descriptor.
     * @return Impact.
     */
    private static int methodImpact(final String descriptor) {
        return BytecodeInstruction.size(Type.getReturnType(descriptor))
            - Arrays.stream(Type.getArgumentTypes(descriptor))
            .mapToInt(BytecodeInstruction::size)
            .sum();
    }

    public boolean isThrow() {
        return Instruction.find(this.opcode) == Instruction.ATHROW;
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
        NOP(
            Opcodes.NOP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.NOP)
        ),

        /**
         * Push null.
         */
        ACONST_NULL(
            Opcodes.ACONST_NULL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ACONST_NULL)
        ),

        /**
         * Load the int value −1 onto the stack.
         */
        ICONST_M1(
            Opcodes.ICONST_M1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_M1)
        ),

        /**
         * Load the int value 0 onto the stack.
         */
        ICONST_0(
            Opcodes.ICONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_0)
        ),

        /**
         * Load the int value 1 onto the stack.
         */
        ICONST_1(
            Opcodes.ICONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_1)
        ),

        /**
         * Load the int value 2 onto the stack.
         */
        ICONST_2(
            Opcodes.ICONST_2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_2)
        ),

        /**
         * Load the int value 3 onto the stack.
         */
        ICONST_3(
            Opcodes.ICONST_3, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_3)
        ),

        /**
         * Load the int value 4 onto the stack.
         */
        ICONST_4(
            Opcodes.ICONST_4, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_4)
        ),

        /**
         * Load the int value 5 onto the stack.
         */
        ICONST_5(
            Opcodes.ICONST_5, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ICONST_5)
        ),

        /**
         * Load the long value 0 onto the stack.
         */
        LCONST_0(
            Opcodes.LCONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LCONST_0)
        ),

        /**
         * Load the long value 1 onto the stack.
         */
        LCONST_1(
            Opcodes.LCONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LCONST_1)
        ),

        /**
         * Load the float value 0 onto the stack.
         */
        FCONST_0(
            Opcodes.FCONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCONST_0)
        ),

        /**
         * Load the float value 1 onto the stack.
         */
        FCONST_1(
            Opcodes.FCONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCONST_1)
        ),

        /**
         * Load the float value 2 onto the stack.
         */
        FCONST_2(
            Opcodes.FCONST_2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCONST_2)
        ),

        /**
         * Load the double value 0 onto the stack.
         */
        DCONST_0(
            Opcodes.DCONST_0, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCONST_0)
        ),

        /**
         * Load the double value 1 onto the stack.
         */
        DCONST_1(
            Opcodes.DCONST_1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCONST_1)
        ),

        /**
         * Push a byte onto the stack as an integer value.
         */
        BIPUSH(
            Opcodes.BIPUSH, (visitor, arguments) ->
            visitor.visitIntInsn(Opcodes.BIPUSH, (int) arguments.get(0))
        ),

        /**
         * Push a short onto the stack as an integer value.
         */
        SIPUSH(
            Opcodes.SIPUSH, (visitor, arguments) ->
            visitor.visitIntInsn(Opcodes.SIPUSH, (int) arguments.get(0))
        ),

        /**
         * Push a constant #index from a constant pool onto the stack.
         */
        LDC(
            Opcodes.LDC, (visitor, arguments) ->
            visitor.visitLdcInsn(arguments.get(0))
        ),

        /**
         * Load an int value from a local variable #index.
         */
        ILOAD(
            Opcodes.ILOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.ILOAD, (int) arguments.get(0))
        ),

        /**
         * Load a long value from a local variable #index.
         */
        LLOAD(
            Opcodes.LLOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.LLOAD, (int) arguments.get(0))
        ),

        /**
         * Load a float value from a local variable #index.
         */
        FLOAD(
            Opcodes.FLOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.FLOAD, (int) arguments.get(0))
        ),

        /**
         * Load a double value from a local variable #index.
         */
        DLOAD(
            Opcodes.DLOAD, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.DLOAD, (int) arguments.get(0))
        ),

        /**
         * Load a reference onto the stack from a local variable #index.
         */
        ALOAD(
            Opcodes.ALOAD, (visitor, arguments) -> {
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
        IALOAD(
            Opcodes.IALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IALOAD)
        ),

        /**
         * Load a long from an array.
         */
        LALOAD(
            Opcodes.LALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LALOAD)
        ),

        /**
         * Load a float from an array.
         */
        FALOAD(
            Opcodes.FALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FALOAD)
        ),

        /**
         * Load a double from an array.
         */
        DALOAD(
            Opcodes.DALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DALOAD)
        ),

        /**
         * Load an object reference from an array.
         */
        AALOAD(
            Opcodes.AALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.AALOAD)
        ),

        /**
         * Load a byte or Boolean value from an array.
         */
        BALOAD(
            Opcodes.BALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.BALOAD)
        ),

        /**
         * Load a char from an array.
         */
        CALOAD(
            Opcodes.CALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.CALOAD)
        ),

        /**
         * Load a short from an array.
         */
        SALOAD(
            Opcodes.SALOAD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.SALOAD)
        ),

        /**
         * Store int value into variable #index.
         */
        ISTORE(
            Opcodes.ISTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.ISTORE, (int) arguments.get(0))
        ),

        /**
         * Store long value into variable #index.
         */
        LSTORE(
            Opcodes.LSTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.LSTORE, (int) arguments.get(0))
        ),

        /**
         * Store float value into variable #index.
         */
        FSTORE(
            Opcodes.FSTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.FSTORE, (int) arguments.get(0))
        ),

        /**
         * Store double value into variable #index.
         */
        DSTORE(
            Opcodes.DSTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.DSTORE, (int) arguments.get(0))
        ),

        /**
         * Store a reference into a local variable #index.
         */
        ASTORE(
            Opcodes.ASTORE, (visitor, arguments) ->
            visitor.visitVarInsn(Opcodes.ASTORE, (int) arguments.get(0))
        ),

        /**
         * Store int into array.
         */
        IASTORE(
            Opcodes.IASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IASTORE)
        ),

        /**
         * Store long into array.
         */
        LASTORE(
            Opcodes.LASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LASTORE)
        ),

        /**
         * Store float into array.
         */
        FASTORE(
            Opcodes.FASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FASTORE)
        ),

        /**
         * Store double into array.
         */
        DASTORE(
            Opcodes.DASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DASTORE)
        ),

        /**
         * Store reference into array.
         */
        AASTORE(
            Opcodes.AASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.AASTORE)
        ),

        /**
         * Store byte or boolean into array.
         */
        BASTORE(
            Opcodes.BASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.BASTORE)
        ),

        /**
         * Store char into array.
         */
        CASTORE(
            Opcodes.CASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.CASTORE)
        ),

        /**
         * Store short into array.
         */
        SASTORE(
            Opcodes.SASTORE, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.SASTORE)
        ),

        /**
         * Discard the top value on the stack.
         */
        POP(
            Opcodes.POP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.POP)
        ),

        /**
         * Discard the top two values on the stack.
         */
        POP2(
            Opcodes.POP2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.POP2)
        ),

        /**
         * Duplicate the value on top of the stack.
         */
        DUP(
            Opcodes.DUP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP)
        ),

        /**
         * Insert a copy of the top value into the stack two values from the top.
         * value1 and value2 must not be of the type double or long.
         */
        DUP_X1(
            Opcodes.DUP_X1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP_X1)
        ),

        /**
         * Copy the top one or two operand stack values and insert two or three values down.
         * Insert a copy of the top value into the stack two (if value2 is double or long it takes
         * up the entry of value3, too) or three values (if value2 is neither double nor long) from
         * the top.
         */
        DUP_X2(
            Opcodes.DUP_X2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP_X2)
        ),

        /**
         * Duplicate top operand stack word or value2 word.
         */
        DUP2(
            Opcodes.DUP2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP2)
        ),

        /**
         * Duplicate two words and insert beneath third word.
         */
        DUP2_X1(
            Opcodes.DUP2_X1, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP2_X1)
        ),

        /**
         * Duplicate two words and insert beneath fourth word.
         */
        DUP2_X2(
            Opcodes.DUP2_X2, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DUP2_X2)
        ),

        /**
         * Swap the top two values on the stack.
         * Note that value1 and value2 must not be double or long
         */
        SWAP(
            Opcodes.SWAP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.SWAP)
        ),

        /**
         * Add two integers.
         */
        IADD(
            Opcodes.IADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IADD)
        ),
        /**
         * Add two longs.
         */
        LADD(
            Opcodes.LADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LADD)
        ),

        /**
         * Add two floats.
         */
        FADD(
            Opcodes.FADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FADD)
        ),

        /**
         * Add two doubles.
         */
        DADD(
            Opcodes.DADD, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DADD)
        ),

        /**
         * Subtract two integers.
         */
        ISUB(
            Opcodes.ISUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ISUB)
        ),

        /**
         * Subtract two longs.
         */
        LSUB(
            Opcodes.LSUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LSUB)
        ),

        /**
         * Subtract two floats.
         */
        FSUB(
            Opcodes.FSUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FSUB)
        ),

        /**
         * Subtract two doubles.
         */
        DSUB(
            Opcodes.DSUB, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DSUB)
        ),

        /**
         * Multiply two integers.
         */
        IMUL(
            Opcodes.IMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IMUL)
        ),

        /**
         * Multiply two longs.
         */
        LMUL(
            Opcodes.LMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LMUL)
        ),

        /**
         * Multiply two floats.
         */
        FMUL(
            Opcodes.FMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FMUL)
        ),

        /**
         * Multiply two doubles.
         */
        DMUL(
            Opcodes.DMUL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DMUL)
        ),

        /**
         * Divide two integers.
         */
        IDIV(
            Opcodes.IDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IDIV)
        ),

        /**
         * Divide two longs.
         */
        LDIV(
            Opcodes.LDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LDIV)
        ),

        /**
         * Divide two floats.
         */
        FDIV(
            Opcodes.FDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FDIV)
        ),

        /**
         * Divide two doubles.
         */
        DDIV(
            Opcodes.DDIV, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DDIV)
        ),

        /**
         * Remainder of division of two integers.
         */
        IREM(
            Opcodes.IREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IREM)
        ),

        /**
         * Remainder of division of two longs.
         */
        LREM(
            Opcodes.LREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LREM)
        ),

        /**
         * Remainder of division of two floats.
         */
        FREM(
            Opcodes.FREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FREM)
        ),

        /**
         * Remainder of division of two doubles.
         */
        DREM(
            Opcodes.DREM, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DREM)
        ),

        /**
         * Negate int.
         */
        INEG(
            Opcodes.INEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.INEG)
        ),

        /**
         * Negate long.
         */
        LNEG(
            Opcodes.LNEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LNEG)
        ),

        /**
         * Negate float.
         */
        FNEG(
            Opcodes.FNEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FNEG)
        ),

        /**
         * Negate double.
         */
        DNEG(
            Opcodes.DNEG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DNEG)
        ),

        /**
         * Shift left int.
         */
        ISHL(
            Opcodes.ISHL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ISHL)
        ),

        /**
         * Shift left long.
         */
        LSHL(
            Opcodes.LSHL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LSHL)
        ),

        /**
         * Arithmetic shift right int.
         */
        ISHR(
            Opcodes.ISHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ISHR)
        ),

        /**
         * Arithmetic shift right long.
         */
        LSHR(
            Opcodes.LSHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LSHR)
        ),

        /**
         * Logical shift right int.
         */
        IUSHR(
            Opcodes.IUSHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IUSHR)
        ),

        /**
         * Logical shift right long.
         */
        LUSHR(
            Opcodes.LUSHR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LUSHR)
        ),

        /**
         * Boolean AND int.
         */
        IAND(
            Opcodes.IAND, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IAND)
        ),

        /**
         * Boolean AND long.
         */
        LAND(
            Opcodes.LAND, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LAND)
        ),

        /**
         * Boolean OR int.
         */
        IOR(
            Opcodes.IOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IOR)
        ),

        /**
         * Boolean OR long.
         */
        LOR(
            Opcodes.LOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LOR)
        ),

        /**
         * Boolean XOR int.
         */
        IXOR(
            Opcodes.IXOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IXOR)
        ),

        /**
         * Boolean XOR long.
         */
        LXOR(
            Opcodes.LXOR, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LXOR)
        ),

        /**
         * Increment local variable #index by signed byte const in next byte.
         */
        IINC(
            Opcodes.IINC, (visitor, arguments) ->
            visitor.visitIincInsn(
                (int) arguments.get(0),
                (int) arguments.get(1)
            )
        ),

        /**
         * Convert int to long.
         */
        I2L(
            Opcodes.I2L, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2L)
        ),

        /**
         * Convert int to float.
         */
        I2F(
            Opcodes.I2F, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2F)
        ),

        /**
         * Convert int to double.
         */
        I2D(
            Opcodes.I2D, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2D)
        ),

        /**
         * Convert long to int.
         */
        L2I(
            Opcodes.L2I, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.L2I)
        ),

        /**
         * Convert long to float.
         */
        L2F(
            Opcodes.L2F, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.L2F)
        ),

        /**
         * Convert long to double.
         */
        L2D(
            Opcodes.L2D, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.L2D)
        ),

        /**
         * Convert float to int.
         */
        F2I(
            Opcodes.F2I, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.F2I)
        ),

        /**
         * Convert float to long.
         */
        F2L(
            Opcodes.F2L, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.F2L)
        ),

        /**
         * Convert float to double.
         */
        F2D(
            Opcodes.F2D, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.F2D)
        ),

        /**
         * Convert double to int.
         */
        D2I(
            Opcodes.D2I, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.D2I)
        ),

        /**
         * Convert double to long.
         */
        D2L(
            Opcodes.D2L, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.D2L)
        ),

        /**
         * Convert double to float.
         */
        D2F(
            Opcodes.D2F, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.D2F)
        ),

        /**
         * Convert int to byte.
         */
        I2B(
            Opcodes.I2B, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2B)
        ),

        /**
         * Convert int to char.
         */
        I2C(
            Opcodes.I2C, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2C)
        ),

        /**
         * Convert int to short.
         */
        I2S(
            Opcodes.I2S, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.I2S)
        ),

        /**
         * Push 0 if the two longs are the same, 1 if value1 is greater than value2, -1 otherwise.
         */
        LCMP(
            Opcodes.LCMP, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LCMP)
        ),

        /**
         * Push 0 if the two floats are the same, 1 if value1 is greater than value2, -1 otherwise.
         */
        FCMPL(
            Opcodes.FCMPL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCMPL)
        ),

        /**
         * Compare two floats, 1 on NaN.
         */
        FCMPG(
            Opcodes.FCMPG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FCMPG)
        ),

        /**
         * Push 0 if the two doubles are the same, 1 if value1 is greater than value2, -1 otherwise.
         */
        DCMPL(
            Opcodes.DCMPL, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCMPL)
        ),

        /**
         * Compare two doubles, 1 on NaN.
         */
        DCMPG(
            Opcodes.DCMPG, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DCMPG)
        ),

        /**
         * If value is 0, branch to instruction at branchoffset.
         */
        IFEQ(
            Opcodes.IFEQ, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFEQ,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is not 0, branch to instruction at branchoffset.
         */
        IFNE(
            Opcodes.IFNE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFNE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is less than 0, branch to instruction at branchoffset.
         */
        IFLT(
            Opcodes.IFLT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFLT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is greater than or equal to 0, branch to instruction at branchoffset.
         */
        IFGE(
            Opcodes.IFGE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFGE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is greater than 0, branch to instruction at branchoffset.
         */
        IFGT(
            Opcodes.IFGT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFGT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is less than or equal to 0, branch to instruction at branchoffset.
         */
        IFLE(
            Opcodes.IFLE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFLE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If the two integer values are equal, branch to instruction at branchoffset.
         */
        IF_ICMPEQ(
            Opcodes.IF_ICMPEQ, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPEQ,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If the two integer values are not equal, branch to instruction at branchoffset.
         */
        IF_ICMPNE(
            Opcodes.IF_ICMPNE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPNE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is less than value2, branch to instruction at branchoffset.
         */
        IF_ICMPLT(
            Opcodes.IF_ICMPLT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPLT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is greater than or equal to value2, branch to instruction at branchoffset.
         */
        IF_ICMPGE(
            Opcodes.IF_ICMPGE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPGE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is greater than value2, branch to instruction at branchoffset.
         */
        IF_ICMPGT(
            Opcodes.IF_ICMPGT, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPGT,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value1 is less than or equal to value2, branch to instruction at branchoffset.
         */
        IF_ICMPLE(
            Opcodes.IF_ICMPLE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ICMPLE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If references are equal, branch to instruction at branchoffset.
         */
        IF_ACMPEQ(
            Opcodes.IF_ACMPEQ, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ACMPEQ,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If references are not equal, branch to instruction at branchoffset.
         */
        IF_ACMPNE(
            Opcodes.IF_ACMPNE, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IF_ACMPNE,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * Goes to another instruction at branchoffset.
         */
        GOTO(
            Opcodes.GOTO, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.GOTO,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * Jump to subroutine at branch offset.
         */
        JSR(
            Opcodes.JSR, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.JSR,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * Return from subroutine.
         */
        RET(
            Opcodes.RET, (visitor, arguments) ->
            visitor.visitVarInsn(
                Opcodes.RET,
                (int) arguments.get(0)
            )
        ),

        /**
         * Access jump table by key match and jump.
         * Continue execution from an address in the table at offset index
         */
        TABLESWITCH(
            Opcodes.TABLESWITCH, (visitor, arguments) -> {
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
        LOOKUPSWITCH(
            Opcodes.LOOKUPSWITCH, (visitor, arguments) -> {
            final List<Label> lbls = arguments.stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .collect(Collectors.toList());
            visitor.visitLookupSwitchInsn(
                lbls.get(0),
                arguments.stream()
                    .filter(Integer.class::isInstance)
                    .mapToInt(Integer.class::cast)
                    .toArray(),
                lbls.subList(1, lbls.size()).toArray(new Label[0])
            );
        }
        ),

        /**
         * Return an integer from a method.
         */
        IRETURN(
            Opcodes.IRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.IRETURN)
        ),

        /**
         * Return a long from a method.
         */
        LRETURN(
            Opcodes.LRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.LRETURN)
        ),

        /**
         * Return a float from a method.
         */
        FRETURN(
            Opcodes.FRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.FRETURN)
        ),

        /**
         * Return a double from a method.
         */
        DRETURN(
            Opcodes.DRETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.DRETURN)
        ),

        /**
         * Return a reference from a method.
         */
        ARETURN(
            Opcodes.ARETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ARETURN)
        ),

        /**
         * Return void from a method.
         */
        RETURN(
            Opcodes.RETURN, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.RETURN)
        ),

        /**
         * Get a static field value of a class, where the field is
         * identified by field reference in the constant pool index.
         */
        GETSTATIC(
            Opcodes.GETSTATIC, (visitor, arguments) ->
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
        PUTSTATIC(
            Opcodes.PUTSTATIC, (visitor, arguments) ->
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
        GETFIELD(
            Opcodes.GETFIELD, (visitor, arguments) ->
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
        PUTFIELD(
            Opcodes.PUTFIELD, (visitor, arguments) ->
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
        INVOKEVIRTUAL(
            Opcodes.INVOKEVIRTUAL, (visitor, arguments) ->
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
        INVOKESPECIAL(
            Opcodes.INVOKESPECIAL, (visitor, arguments) ->
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
        INVOKESTATIC(
            Opcodes.INVOKESTATIC, (visitor, arguments) ->
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
        INVOKEINTERFACE(
            Opcodes.INVOKEINTERFACE, (visitor, arguments) ->
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
        INVOKEDYNAMIC(
            Opcodes.INVOKEDYNAMIC, (visitor, arguments) ->
            visitor.visitInvokeDynamicInsn(
                String.valueOf(arguments.get(0)),
                String.valueOf(arguments.get(1)),
                Handle.class.cast(arguments.get(2)),
                arguments.subList(3, arguments.size()).toArray()
            )
        ),

        /**
         * Create a new object of a type identified by class reference in constant pool index.
         */
        NEW(
            Opcodes.NEW, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.NEW,
                String.valueOf(arguments.get(0))
            )
        ),

        /**
         * Create new array with count elements of primitive type identified by atype.
         */
        NEWARRAY(
            Opcodes.NEWARRAY, (visitor, arguments) ->
            visitor.visitIntInsn(
                Opcodes.NEWARRAY,
                (int) arguments.get(0)
            )
        ),

        /**
         * Create new array of reference type identified by class reference in constant pool index.
         */
        ANEWARRAY(
            Opcodes.ANEWARRAY, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.ANEWARRAY,
                String.valueOf(arguments.get(0))
            )
        ),

        /**
         * Get the length of an array.
         */
        ARRAYLENGTH(
            Opcodes.ARRAYLENGTH, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ARRAYLENGTH)
        ),

        /**
         * Throws an error or exception.
         * Notice that the rest of the stack is cleared, leaving only a reference to the Throwable.
         */
        ATHROW(
            Opcodes.ATHROW, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.ATHROW)
        ),

        /**
         * Checks whether an objectref is of a certain type.
         * The class reference of which is in the constant pool at index.
         */
        CHECKCAST(
            Opcodes.CHECKCAST, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.CHECKCAST,
                String.valueOf(arguments.get(0))
            )
        ),

        /**
         * Determines if an object objectref is of a given type.
         * Identified by class reference index in constant pool .
         */
        INSTANCEOF(
            Opcodes.INSTANCEOF, (visitor, arguments) ->
            visitor.visitTypeInsn(
                Opcodes.INSTANCEOF,
                String.valueOf(arguments.get(0))
            )
        ),

        /**
         * Enter monitor for object ("grab the lock" – start of synchronized() section).
         */
        MONITORENTER(
            Opcodes.MONITORENTER, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.MONITORENTER)
        ),

        /**
         * Exit monitor for object ("release the lock" – end of synchronized() section).
         */
        MONITOREXIT(
            Opcodes.MONITOREXIT, (visitor, arguments) ->
            visitor.visitInsn(Opcodes.MONITOREXIT)
        ),

        /**
         * Create new multidimensional array.
         */
        MULTIANEWARRAY(
            Opcodes.MULTIANEWARRAY, (visitor, arguments) ->
            visitor.visitMultiANewArrayInsn(
                String.valueOf(arguments.get(0)),
                (int) arguments.get(1)
            )
        ),

        /**
         * If value is null, branch to instruction at a label.
         */
        IFNULL(
            Opcodes.IFNULL, (visitor, arguments) ->
            visitor.visitJumpInsn(
                Opcodes.IFNULL,
                Label.class.cast(arguments.get(0))
            )
        ),

        /**
         * If value is not null, branch to instruction at a label.
         */
        IFNONNULL(
            Opcodes.IFNONNULL, (visitor, arguments) ->
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
         * Check if the instruction is a variable instruction.
         * @return True if the instruction is a variable instruction.
         * @checkstyle CyclomaticComplexityCheck (50 lines)
         */
        boolean isVarInstruction() {
            final boolean result;
            switch (this) {
                case ILOAD:
                case LLOAD:
                case FLOAD:
                case DLOAD:
                case ALOAD:
                case ISTORE:
                case LSTORE:
                case FSTORE:
                case DSTORE:
                case ASTORE:
                case RET:
                    result = true;
                    break;
                default:
                    result = false;
                    break;
            }
            return result;
        }

        /**
         * Local variable size.
         * @return Local variable size.
         * @checkstyle CyclomaticComplexityCheck (50 lines)
         */
        int size() {
            final int res;
            switch (this) {
                case ILOAD:
                case FLOAD:
                case ALOAD:
                case ISTORE:
                case FSTORE:
                case ASTORE:
                    res = 1;
                    break;
                case LLOAD:
                case LSTORE:
                case DLOAD:
                case DSTORE:
                    res = 2;
                    break;
                case RET:
                    res = 0;
                    break;
                default:
                    throw new IllegalStateException(
                        String.format(
                            "Instruction %s is not a variable instruction",
                            this.name()
                        )
                    );
            }
            return res;
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
