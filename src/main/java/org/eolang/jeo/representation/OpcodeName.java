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
package org.eolang.jeo.representation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.sisu.space.asm.Opcodes;

/**
 * Opcode name.
 * The name of bytecode instruction. The name combined with unique number in order to
 * avoid name collisions.
 * @since 0.1.0
 * @todo #84:30min Add unit test for OpcodeName class.
 *  The unit test should check that the name of opcode is correct.
 *  The name should be the same as in {@link Opcodes} class.
 *  When the unit test is ready remove that puzzle.
 */
final class OpcodeName {

    /**
     * Opcode names.
     */
    private static final Map<Integer, String> NAMES = OpcodeName.init();

    /**
     * Opcode counter.
     */
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    /**
     * Bytecode operation code.
     */
    private final int opcode;

    /**
     * Constructor.
     * @param opcode Bytecode operation code.
     */
    OpcodeName(final int opcode) {
        this.opcode = opcode;
    }

    /**
     * Get string representation of a bytecode.
     * @return String representation of a bytecode.
     */
    String asString() {
        return String.format(
            "%s-%d-%d",
            OpcodeName.NAMES.getOrDefault(this.opcode, "UNKNOWN"),
            this.opcode,
            OpcodeName.COUNTER.incrementAndGet()
        );
    }

    /**
     * Initialize opcode names.
     * @return Opcode names.
     */
    private static Map<Integer, String> init() {
        try {
            final Map<Integer, String> res = new HashMap<>();
            for (final Field field : Opcodes.class.getFields()) {
                if (field.getType() == int.class) {
                    res.put(field.getInt(Opcodes.class), field.getName());
                }
            }
            return res;
        } catch (final IllegalAccessException exception) {
            throw new IllegalStateException(
                String.format("Can't retrieve opcode names from '%s'", Opcodes.class),
                exception
            );
        }
    }
}

