/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.eolang.jeo.representation.bytecode.Bytecode;

/**
 * Disassembles a single class file in a separate JVM process.
 *
 * <p>This is a helper entry point for
 * {@link HeapBoundDisassemblyTest}, which spawns a child JVM with a
 * constrained heap to prove that disassembly of a large class fits into
 * modest memory, see
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1875">#1875</a>.</p>
 *
 * @since 0.15.0
 */
public final class SubprocessDisassembler {

    /**
     * Constructor.
     */
    private SubprocessDisassembler() {
        // Utility class.
    }

    /**
     * Disassemble the class file given as the first argument into XMIR.
     *
     * @param args Command line arguments, the first one is a path to a .class file
     * @throws Exception If disassembly fails
     */
    public static void main(final String... args) throws Exception {
        final byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
        new BytecodeRepresentation(new Bytecode(bytes)).toXmir();
        System.out.printf(
            "Disassembled %d bytes successfully%n",
            bytes.length
        );
    }
}
