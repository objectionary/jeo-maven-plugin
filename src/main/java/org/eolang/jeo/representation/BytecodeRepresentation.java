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
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import java.nio.file.Path;
import java.util.Arrays;
import lombok.ToString;
import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.InputOf;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;
import org.eolang.jeo.Details;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.directives.DirectivesClassVisitor;
import org.objectweb.asm.ClassReader;
import org.xembly.ImpossibleModificationException;

/**
 * Intermediate representation of a class files which can be optimized from bytecode.
 * You can also use that site to check if bytecode is correct:
 * <a href="https://godbolt.org">https://godbolt.org/</a>
 *
 * @since 0.1.0
 */
@ToString
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public final class BytecodeRepresentation implements Representation {

    /**
     * Input source.
     */
    private final Unchecked<byte[]> input;

    /**
     * The source of the input.
     */
    private final String source;

    /**
     * Constructor.
     *
     * @param clazz Path to the class file
     */
    public BytecodeRepresentation(final Path clazz) {
        this(BytecodeRepresentation.fromFile(clazz), String.valueOf(clazz));
    }

    /**
     * Constructor.
     *
     * @param bytecode Bytecode
     */
    public BytecodeRepresentation(final Bytecode bytecode) {
        this(BytecodeRepresentation.fromBytes(bytecode.asBytes()), "bytecode");
    }

    /**
     * Constructor.
     *
     * @param input Input source
     */
    BytecodeRepresentation(final Input input) {
        this(BytecodeRepresentation.fromInput(input), "bytecode");
    }

    /**
     * Constructor.
     * @param input Input.
     * @param source The source of the input.
     */
    public BytecodeRepresentation(
        final Unchecked<byte[]> input,
        final String source
    ) {
        this.input = input;
        this.source = source;
    }

    @Override
    public Details details() {
        return new Details(this.className(), this.source);
    }

    @Override
    public XML toEO() {
        return this.toEO(true);
    }

    /**
     * Converts bytecode into XML.
     * @param count Do we add number to opcode name or not?
     * @return XML representation of bytecode.
     */
    public XML toEO(final boolean count) {
        final DirectivesClassVisitor directives = new DirectivesClassVisitor(
            new Base64Bytecode(this.input.value()).asString(),
            count
        );
        try {
            new ClassReader(this.input.value()).accept(directives, 0);
            return new VerifiedEo(directives).asXml();
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format(
                    "Something went wrong during transformation %s into XML by using directives %n%s%n",
                    this.className(),
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

    @Override
    public Bytecode toBytecode() {
        return new Bytecode(new UncheckedBytes(new BytesOf(this.input.value())).asBytes());
    }

    /**
     * Read class name from bytecode.
     *
     * @return Class name.
     */
    private String className() {
        final ClassNameVisitor name = new ClassNameVisitor();
        new ClassReader(this.input.value()).accept(name, 0);
        return name.asString();
    }

    /**
     * Prestructor that converts a file to a byte source.
     * @param path Path to the file.
     * @return Byte source.
     */
    private static Unchecked<byte[]> fromFile(final Path path) {
        return BytecodeRepresentation.fromInput(new InputOf(path));
    }

    /**
     * Prestructor that converts input to a byte source.
     * @param input Input.
     * @return Byte source.
     */
    private static Unchecked<byte[]> fromInput(final Input input) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> new BytesOf(input).asBytes())));
    }

    /**
     * Prestructor that converts bytes to a byte source.
     * @param bytes Bytes.
     * @return Byte source.
     */
    private static Unchecked<byte[]> fromBytes(final byte[] bytes) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> bytes)));
    }
}
