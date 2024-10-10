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

import java.util.List;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;

/**
 * Bytecode instructions flow.
 * @since 0.6
 */
public final class InstructionsFlow {

    /**
     * Method instructions.
     */
    private final List<? extends BytecodeEntry> instructions;

    /**
     * Constructor.
     * @param instructions Instructions.
     */
    InstructionsFlow(final List<? extends BytecodeEntry> instructions) {
        this.instructions = instructions;
    }

    /**
     * Get size.
     * @return Size.
     */
    public int size() {
        return this.instructions.size();
    }

    /**
     * Get instruction.
     * @param index Index.
     * @return Instruction.
     */
    public BytecodeEntry get(final int index) {
        return this.instructions.get(index);
    }

    /**
     * Index of the label.
     * @param label Label.
     * @return Index.
     */
    int index(final Label label) {
        for (int index = 0; index < this.instructions.size(); ++index) {
            if (this.instructions.get(index).equals(new BytecodeLabel(label, new AllLabels()))) {
                return index;
            }
        }
        throw new IllegalStateException(String.format("Label %s not found", label));
    }
}
