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
package org.eolang.jeo.representation.asm.generation;

import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode instruction.
 * @since 0.1.0
 */
final class BytecodeInstruction {

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
