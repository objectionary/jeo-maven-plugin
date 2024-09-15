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
package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.Label;

/**
 * Bytecode method builder.
 * @since 0.6
 */
public final class BytecodeMethodBuilder {

    /**
     * Class for the method.
     */
    private final BytecodeClass clazz;

    /**
     * Bytecode method.
     */
    private final BytecodeMethod method;

    /**
     * Constructor.
     * @param clazz Class.
     * @param method Method.
     */
    public BytecodeMethodBuilder(final BytecodeClass clazz, final BytecodeMethod method) {
        this.clazz = clazz;
        this.method = method;
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
     * Add label.
     * @param uid Label uid.
     * @return This object.
     */
    public BytecodeMethodBuilder label(final String uid) {
        this.method.label(uid);
        return this;
    }

    /**
     * Add label.
     * @param label Label.
     * @return This object.
     */
    public BytecodeMethodBuilder label(final Label label) {
        this.method.label(label);
        return this;
    }

    /**
     * Add instruction.
     * @param opcode Opcode.
     * @param args Arguments.
     * @return This object.
     */
    public BytecodeMethodBuilder opcode(final int opcode, final Object... args) {
        this.method.opcode(opcode, args);
        return this;
    }

    /**
     * Add try-catch block.
     * @param entry Try-catch block.
     * @return This object.
     */
    public BytecodeMethodBuilder trycatch(final BytecodeEntry entry) {
        this.method.trycatch(entry);
        return this;
    }

}
