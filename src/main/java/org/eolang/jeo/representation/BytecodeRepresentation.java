/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import java.util.Arrays;
import lombok.ToString;
import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;
import org.eolang.jeo.representation.asm.AsmProgram;
import org.eolang.jeo.representation.asm.DisassembleMode;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassReader;
import org.xembly.Directive;
import org.xembly.ImpossibleModificationException;

/**
 * Intermediate representation of class files from bytecode.
 *
 * <p>This class provides a unified interface for working with Java bytecode.
 * It can read bytecode from various sources (files, byte arrays, input streams)
 * and convert it to XMIR (EO XML representation) format with configurable detail levels.</p>
 * @since 0.1.0
 */
@ToString
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public final class BytecodeRepresentation {

    /**
     * Input source.
     */
    private final Unchecked<byte[]> input;

    /**
     * Constructor.
     * @param clazz The path to the class file to read
     */
    public BytecodeRepresentation(final Path clazz) {
        this(BytecodeRepresentation.fromFile(clazz));
    }

    /**
     * Constructor.
     * @param bytecode The bytecode object containing raw bytes
     */
    public BytecodeRepresentation(final Bytecode bytecode) {
        this(BytecodeRepresentation.fromBytes(bytecode.bytes()));
    }

    /**
     * Constructor.
     * @param input The input source containing bytecode
     */
    public BytecodeRepresentation(final Input input) {
        this(BytecodeRepresentation.fromInput(input));
    }

    /**
     * Constructor.
     * @param input The unchecked byte array supplier
     */
    private BytecodeRepresentation(final Unchecked<byte[]> input) {
        this.input = input;
    }

    /**
     * Read class name from bytecode.
     * @return Fully qualified class name
     */
    public String name() {
        final ClassNameVisitor name = new ClassNameVisitor();
        new ClassReader(this.input.value()).accept(name, 0);
        return name.asString();
    }

    /**
     * Convert to EOlang XML representation (XMIR).
     * @return XML representation of the bytecode
     */
    public XML toXmir() {
        return this.toXmir(new Format());
    }

    /**
     * Convert bytecode into XMIR format.
     * @param format Disassemble output format
     * @return XML representation of the bytecode
     */
    public XML toXmir(final Format format) {
        return new XMLDocument(this.toEO(format));
    }

    /**
     * Convert bytecode into XMIR format.
     * @param format The disassemble params controlling the level of detail
     * @return XMIR representation of the bytecode
     */
    public String toEO(final Format format) {
        final Format fmt;
        if (format.withListing()) {
            fmt = new Format(
                format,
                Format.LISTING, new BytecodeListing(this.input.value()).toString()
            );
        } else {
            fmt = format;
        }
        final BytecodeObject bytecode = new AsmProgram(this.input.value())
            .bytecode(DisassembleMode.fromString(fmt.mode()).asmOptions());
        final Iterable<Directive> directives = bytecode
            .directives(fmt);
        try {
            final XML measured = new MeasuredEo(directives).asXml();
            final String res;
            if (fmt.pretty()) {
                res = new PrettyXml(measured).toString();
            } else {
                res = measured.toString();
            }
            return res;
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format(
                    "Something went wrong during transformation %s into XML by using directives %n%s%n",
                    this.name(),
                    directives
                ),
                exception
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't build XML from %s by using directives %s",
                    Arrays.toString(this.input.value()),
                    directives
                ),
                exception
            );
        }
    }

    /**
     * Prestructor that converts a file to a byte source.
     * @param path The path to the file containing bytecode
     * @return The unchecked byte array supplier
     */
    private static Unchecked<byte[]> fromFile(final Path path) {
        return BytecodeRepresentation.fromInput(new InputOf(path));
    }

    /**
     * Prestructor that converts input to a byte source.
     * @param input The input source to read from
     * @return The unchecked byte array supplier
     */
    private static Unchecked<byte[]> fromInput(final Input input) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> new BytesOf(input).asBytes())));
    }

    /**
     * Prestructor that converts bytes to a byte source.
     * @param bytes The raw byte array
     * @return The unchecked byte array supplier
     */
    private static Unchecked<byte[]> fromBytes(final byte[] bytes) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> bytes)));
    }
}
