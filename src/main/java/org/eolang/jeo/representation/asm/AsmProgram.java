/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.nio.ByteBuffer;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
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
 *
 * @since 0.6
 */
public final class AsmProgram {

    /**
     * Class file magic header.
     */
    private static final int MAGIC = 0xCAFEBABE;

    /**
     * Minimum length of a class file header (magic, version, and constant pool count).
     */
    private static final int HEADER = 10;

    /**
     * Bytecode as plain bytes.
     */
    private final byte[] bytes;

    /**
     * Constructor.
     *
     * @param bytes Bytes
     */
    public AsmProgram(final byte... bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Convert to bytecode.
     *
     * @return Bytecode
     */
    public BytecodeObject bytecode() {
        return this.bytecode(ClassReader.SKIP_DEBUG);
    }

    /**
     * Convert to bytecode.
     *
     * @param flags Flags
     * @return Bytecode
     */
    public BytecodeObject bytecode(final int flags) {
        final ClassNode node = new ClassNode();
        final ClassReader reader = this.reader();
        try {
            reader.accept(node, AsmUnknownAttributes.prototypes(reader), flags);
            if ((flags & ClassReader.SKIP_DEBUG) != 0) {
                new AsmParameters(this.bytes).fill(node);
            }
        } catch (final IndexOutOfBoundsException exception) {
            throw this.truncated(exception);
        }
        return new BytecodeObject(
            new ClassName(node.name).pckg(),
            new AsmClass(node).bytecode()
        );
    }

    /**
     * Create a validated ASM class reader for the raw bytes.
     *
     * @return Validated class reader
     */
    public ClassReader reader() {
        if (this.bytes.length < AsmProgram.HEADER) {
            throw new IllegalArgumentException(
                String.format(
                    "Not a class file: expected at least %d header bytes, got %d",
                    AsmProgram.HEADER,
                    this.bytes.length
                )
            );
        }
        final int magic = ByteBuffer.wrap(this.bytes).getInt();
        if (magic != AsmProgram.MAGIC) {
            throw new IllegalArgumentException(
                String.format(
                    "Not a class file: invalid magic header 0x%08X (expected 0x%08X)",
                    magic,
                    AsmProgram.MAGIC
                )
            );
        }
        final ClassReader result;
        try {
            result = new ClassReader(this.bytes);
        } catch (final IndexOutOfBoundsException exception) {
            throw this.truncated(exception);
        }
        return result;
    }

    private IllegalArgumentException truncated(final IndexOutOfBoundsException cause) {
        return new IllegalArgumentException(
            String.format(
                "Not a valid class file: truncated or corrupted bytecode (%d bytes)",
                this.bytes.length
            ),
            cause
        );
    }
}
