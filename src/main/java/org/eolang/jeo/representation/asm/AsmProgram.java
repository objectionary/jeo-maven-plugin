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

import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * ASM bytecode parser.
 * We used to use the Visitor pattern, but it's too verbose and not very readable.
 * So, we decided to switch to a Tree-API-based approach.
 * You can read more about different approaches right here:
 * <a href="https://asm.ow2.io/asm4-guide.pdf">https://asm.ow2.io/asm4-guide.pdf</a>
 * The recent version with the Visitor pattern is still available in the history:
 * <a href="https://github.com/objectionary/jeo-maven-plugin/tree/29daa0a167b5c2ba4caaceafb6e6bafc381ac05c">github</a>
 * @since 0.6
 */
public final class AsmProgram {

    /**
     * Bytecode as plain bytes.
     */
    private final byte[] bytes;

    /**
     * Constructor.
     * @param bytes Bytes.
     */
    public AsmProgram(final byte... bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Convert to bytecode.
     * @return Bytecode.
     */
    public BytecodeProgram bytecode() {
        final ClassNode node = new ClassNode();
        new ClassReader(this.bytes).accept(node, 0);
        return new BytecodeProgram(
            new ClassName(node.name).pckg(),
            new AsmClass(node).bytecode()
        );
    }
}
