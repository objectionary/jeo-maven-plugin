/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
        return this.bytecode(ClassReader.SKIP_DEBUG);
    }

    /**
     * Convert to bytecode.
     * @param flags Flags.
     * @return Bytecode.
     */
    public BytecodeProgram bytecode(final int flags) {
        final ClassNode node = new ClassNode();
        new ClassReader(this.bytes).accept(node, flags);
        return new BytecodeProgram(
            new ClassName(node.name).pckg(),
            new AsmClass(node).bytecode()
        );
    }
}
