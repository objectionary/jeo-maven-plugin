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
package org.eolang.jeo.representation.asm;

import org.objectweb.asm.ClassReader;

/**
 * Disassemble mode.
 * @since 0.6
 */
public enum DisassembleMode {
    /**
     * Short mode.
     * This mode will disassemble the bytecode without any additional information.
     */
    SHORT,
    /**
     * Assemble mode.
     */
    DEBUG;

    /**
     * Unknown mode message.
     */
    private static final String UNKNOWN = "Unknown disassemble mode: %s";

    /**
     * Convert from string.
     * @param mode Mode.
     * @return Disassemble mode.
     */
    public static DisassembleMode fromString(final String mode) {
        switch (mode) {
            case "short":
                return DisassembleMode.SHORT;
            case "debug":
                return DisassembleMode.DEBUG;
            default:
                throw new IllegalArgumentException(String.format(DisassembleMode.UNKNOWN, mode));
        }
    }

    /**
     * Convert to ASM options.
     * @return ASM options.
     */
    public int asmOptions() {
        switch (this) {
            case SHORT:
                return ClassReader.SKIP_DEBUG;
            case DEBUG:
                return 0;
            default:
                throw new IllegalArgumentException(String.format(DisassembleMode.UNKNOWN, this));
        }
    }
}
