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
import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import java.util.Arrays;
import lombok.ToString;
import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.InputOf;
import org.eolang.jeo.Details;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.directives.DirectivesClassVisitor;
import org.eolang.parser.Schema;
import org.objectweb.asm.ClassReader;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Intermediate representation of a class files which can be optimized from bytecode.
 * In order to implement this class you can also use that site to check if bytecode is correct:
 * <a href="https://godbolt.org">https://godbolt.org/</a>
 * @since 0.1.0
 */
@ToString
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public final class BytecodeRepresentation implements Representation {

    /**
     * Input source.
     */
    private final byte[] input;

    /**
     * The source of the input.
     */
    private final String source;

    /**
     * Constructor.
     * @param clazz Path to the class file
     */
    public BytecodeRepresentation(final Path clazz) {
        this(new InputOf(clazz), clazz.getFileName().toString());
    }

    /**
     * Constructor.
     * @param bytecode Bytecode
     */
    public BytecodeRepresentation(final Bytecode bytecode) {
        this(bytecode.asBytes());
    }

    /**
     * Constructor.
     * @param input Input source
     */
    BytecodeRepresentation(final Input input) {
        this(new UncheckedBytes(new BytesOf(input)).asBytes());
    }

    /**
     * Constructor.
     * @param input Input source
     * @param source The source of the input
     */
    private BytecodeRepresentation(final Input input, final String source) {
        this(new UncheckedBytes(new BytesOf(input)).asBytes(), source);
    }

    /**
     * Constructor.
     * @param input Input source.
     */
    private BytecodeRepresentation(final byte[] input) {
        this(input, "bytecode");
    }

    /**
     * Constructor.
     * @param input Input.
     * @param source The source of the input.
     */
    private BytecodeRepresentation(
        final byte[] input,
        final String source
    ) {
        this.input = input.clone();
        this.source = source;
    }

    @Override
    public Details details() {
        return new Details(this.className(), this.source);
    }

    @Override
    public XML toEO() {
        final DirectivesClassVisitor directives = new DirectivesClassVisitor(
            new Base64Bytecode(this.input).asString()
        );
        try {
            new ClassReader(this.input).accept(directives, 0);
            final XMLDocument res = new XMLDocument(new Xembler(directives).xml());
            new Schema(res).check();
            return res;
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't build XML from %s by using directives %s",
                    Arrays.toString(this.input),
                    directives
                ),
                exception
            );
        }
    }

    @Override
    public Bytecode toBytecode() {
        return new Bytecode(new UncheckedBytes(new BytesOf(this.input)).asBytes());
    }

    /**
     * Read class name from bytecode.
     * @return Class name.
     */
    private String className() {
        final ClassNameVisitor name = new ClassNameVisitor();
        new ClassReader(this.input).accept(name, 0);
        return name.asString();
    }
}
