/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.directives.Format;

/**
 * Disassembling transformation.
 *
 * <p>This class implements the transformation process that converts Java bytecode
 * into XMIR representation. It reads .class files and produces corresponding
 * XMIR files in the specified target directory with configurable detail levels.</p>
 * @since 0.6.0
 */
public final class Disassembling implements Transformation {

    /**
     * Source folder where the bytecode representation is located.
     */
    private final Path from;

    /**
     * Target folder where to save the disassembled class.
     */
    private final Path folder;

    /**
     * Representation to disassemble.
     */
    private final Path clazz;

    /**
     * Disassemble params.
     */
    private final Format params;

    /**
     * Constructor.
     * @param from Source folder where the bytecode representation is located
     * @param target Target folder where the disassembled XMIR will be saved
     * @param representation Path to the bytecode representation to disassemble
     * @param params Disassemble parameters including mode and listings options
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    Disassembling(
        final Path from,
        final Path target,
        final Path representation,
        final Format params
    ) {
        this.from = from;
        this.folder = target;
        this.clazz = representation;
        this.params = params;
    }

    @Override
    public Path source() {
        return this.clazz;
    }

    @Override
    public Path target() {
        final Path relative = this.from.relativize(this.clazz);
        final Path parent = relative.getParent();
        final Path address;
        if (parent != null) {
            address = this.folder.resolve(parent);
        } else {
            address = this.folder;
        }
        return address.resolve(String.format("%s.xmir", this.fileName()));
    }

    @Override
    public byte[] transform() {
        return new BytecodeRepresentation(this.clazz)
            .toEO(this.params)
            .getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Retrieve file name without extension from the source path.
     * @return File name without extension.
     */
    private String fileName() {
        final String name = this.clazz.getFileName().toString();
        final int index = name.lastIndexOf('.');
        final String result;
        if (index > 0) {
            result = name.substring(0, index);
        } else {
            result = name;
        }
        return result;
    }
}
