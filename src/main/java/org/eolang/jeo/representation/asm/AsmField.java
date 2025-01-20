/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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

import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.objectweb.asm.tree.FieldNode;

/**
 * Asm field.
 * Asm parser for a field.
 * @since 0.6
 */
final class AsmField {

    /**
     * Field node.
     */
    private final FieldNode node;

    /**
     * Constructor.
     * @param node Field node.
     */
    AsmField(final FieldNode node) {
        this.node = node;
    }

    /**
     * Convert asm field to domain field.
     * @return Domain field.
     */
    BytecodeField bytecode() {
        return new BytecodeField(
            this.node.name,
            this.node.desc,
            this.node.signature,
            this.node.value,
            this.node.access,
            new AsmAnnotations(this.node).bytecode()
        );
    }
}
