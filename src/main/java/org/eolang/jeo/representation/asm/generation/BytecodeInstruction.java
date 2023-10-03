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
import java.util.function.BiConsumer;
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
        Instructions.of(this.opcode).generate(visitor, this.args);
    }

    private enum Instructions {

        BIPUSH(
            Opcodes.BIPUSH,
            (visitor, args) -> visitor.visitIntInsn(Opcodes.BIPUSH, (int) args.get(0))
        ),
        LDC(
            Opcodes.LDC,
            (visitor, args) -> visitor.visitLdcInsn(args.get(0))
        ),
        IRETURN(
            Opcodes.IRETURN,
            (visitor, args) -> visitor.visitInsn(Opcodes.IRETURN)
        ),
        RETURN(
            Opcodes.RETURN,
            (visitor, args) -> visitor.visitInsn(Opcodes.RETURN)
        ),
        GETSTATIC(
            Opcodes.GETSTATIC,
            (visitor, args) -> visitor.visitFieldInsn(
                Opcodes.GETSTATIC,
                String.valueOf(args.get(0)),
                String.valueOf(args.get(1)),
                String.valueOf(args.get(2))
            )
        ),
        INVOKEVIRTUAL(
            Opcodes.INVOKEVIRTUAL,
            (visitor, args) -> visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                String.valueOf(args.get(0)),
                String.valueOf(args.get(1)),
                String.valueOf(args.get(2)),
                false
            )
        );

        private final int opcode;
        private BiConsumer<MethodVisitor, List<Object>> visit;

        Instructions(final int opcode,
            final BiConsumer<MethodVisitor, List<Object>> visit
        ) {
            this.opcode = opcode;
            this.visit = visit;
        }

        void generate(final MethodVisitor visitor, final List<Object> args) {
            this.visit.accept(visitor, args);
        }

        static Instructions of(final int opcode) {
            for (final Instructions instruction : Instructions.values()) {
                if (instruction.opcode == opcode) {
                    return instruction;
                }
            }
            throw new IllegalStateException(
                String.format("Unexpected value: %d", opcode)
            );
        }
    }
}
