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
package org.eolang.jeo;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import lombok.ToString;
import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.InputOf;
import org.cactoos.io.UncheckedInput;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Intermediate representation of a class files which can be optimized from bytecode.
 *
 * @since 0.1.0
 * @todo #13:90min Implement BytecodeIR class.
 *  The class should implement IR interface and represent a class file.
 *  It should be able to read the class file and provide access to its bytecode.
 *  Also we have to define methods which we need from IR to provide future optimizations.
 *  When the class is ready, just remove that puzzle.
 *  Also remove SuppressWarnings annotation from the class.
 */
@ToString
@SuppressWarnings({
    "PMD.UnusedPrivateField",
    "PMD.SingularField",
    "PMD.UseObjectForClearerAPI"
})
final class BytecodeIr implements IR {

    /**
     * Input source.
     */
    private final Input input;

    /**
     * Constructor.
     * @param clazz Path to the class file
     */
    BytecodeIr(final Path clazz) {
        this(new InputOf(clazz));
    }

    /**
     * Constructor.
     * @param input Input source
     */
    BytecodeIr(final Input input) {
        this.input = input;
    }

    @Override
    public XML toEO() {
        try (InputStream stream = new UncheckedInput(this.input).stream()) {
            final ClassPrinter printer = new ClassPrinter();
            new ClassReader(stream).accept(printer, 0);
            return new XMLDocument(new Xembler(printer.directives).xml());
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't read input source %s", this.input),
                exception
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                String.format("Can't build XML from %s", this.input),
                exception
            );
        }
    }

    @Override
    public byte[] toBytecode() {
        return new UncheckedBytes(new BytesOf(this.input)).asBytes();
    }

    /**
     * Class printer.
     * ASM class visitor which scans the class and builds Xembly directives.
     * You can read more about Xembly right here:
     * - https://github.com/yegor256/xembly
     * - https://www.xembly.org
     * Firther all this directives will be used to build XML representation of the class.
     * @since 0.1
     */
    private class ClassPrinter extends ClassVisitor {

        /**
         * Xembly directives.
         */
        private final Directives directives;

        /**
         * Constructor.
         */
        ClassPrinter() {
            this(Opcodes.ASM9, new Directives());
        }

        /**
         * Constructor.
         * @param api ASM API version.
         * @param directives Xembly directives.
         */
        private ClassPrinter(
            final int api,
            final Directives directives
        ) {
            super(api);
            this.directives = directives;
        }

        @Override
        public void visit(
            final int version,
            final int access,
            final String name,
            final String signature,
            final String supername,
            final String[] interfaces
        ) {
            this.directives.add("o").attr("name", name);
            super.visit(version, access, name, signature, supername, interfaces);
        }
    }
}
