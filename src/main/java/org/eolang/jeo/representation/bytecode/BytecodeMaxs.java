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

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Bytecode maxs.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public final class BytecodeMaxs {

    /**
     * Stack max size.
     */
    private final int stack;

    /**
     * Locals variables size.
     */
    private final int locals;

    /**
     * Constructor.
     */
    public BytecodeMaxs() {
        this(0, 0);
    }

    /**
     * Constructor.
     * @param stack Stack size.
     * @param locals Locals size.
     */
    public BytecodeMaxs(final int stack, final int locals) {
        this.stack = stack;
        this.locals = locals;
    }

    /**
     * Stack size.
     * @return Stack size.
     */
    public int stack() {
        return this.stack;
    }

    /**
     * Locals size.
     * @return Locals size.
     */
    public int locals() {
        return this.locals;
    }

    /**
     * Is maxs stack and locals are zero?
     * @return True if both are zero.
     */
    boolean compute() {
        return this.stack == 0 && this.locals == 0;
    }
}
