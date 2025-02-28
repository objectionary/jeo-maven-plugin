/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;

/**
 * Class representation.
 * Used in benchmarks to assemble and disassemble class representation.
 * @since 0.8
 */
final class Representation {

    /**
     * Class name.
     */
    private final String clazz;

    /**
     * Constructor.
     */
    Representation() {
        this(String.format("%s.class", Integer.class.getName().replace('.', '/')));
    }

    /**
     * Constructor.
     * @param clazz Class name.
     */
    private Representation(final String clazz) {
        this.clazz = clazz;
    }

    /**
     * Disassembled XML representation as bytes.
     * @return Bytes.
     */
    byte[] disassemble() {
        return new BytecodeRepresentation(new Bytecode(this.bytecode()))
            .toEO()
            .toString()
            .getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Raw bytecode as bytes.
     * @return Bytes.
     */
    byte[] bytecode() {
        try (
            InputStream stream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(
                    String.format(
                        "%s.class",
                        Integer.class.getName().replace('.', '/')
                    )
                )
        ) {
            return Representation.bytes(
                Optional.ofNullable(stream).orElseThrow(
                    () -> new IllegalStateException(
                        String.format("Failed to load '%s' class", this.clazz)
                    )
                )
            );
        } catch (final IOException exception) {
            throw new IllegalStateException("Failed to load bytecode", exception);
        }
    }

    /**
     * Read all bytes from input stream.
     * @param stream Input stream.
     * @return Bytes.
     */
    private static byte[] bytes(final InputStream stream) {
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final byte[] data = new byte[stream.available()];
            int nread = stream.read(data, 0, data.length);
            while (nread != -1) {
                buffer.write(data, 0, nread);
                nread = stream.read(data, 0, data.length);
            }
            return buffer.toByteArray();
        } catch (final IOException ioex) {
            throw new IllegalStateException("Failed to read all bytes from input stream", ioex);
        }
    }
}
