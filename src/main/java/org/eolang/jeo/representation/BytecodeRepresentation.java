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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.ToString;
import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.InputOf;
import org.eolang.jeo.Representation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Intermediate representation of a class files which can be optimized from bytecode.
 * In order to implement this class you can also use that site to check if bytecode is correct:
 * <a href="https://godbolt.org">https://godbolt.org/</a>
 * @since 0.1.0
 * @todo #84:90min Refactor BytecodeRepresentation class.
 *  The class is too big and it's hard to understand what it does. It's better to split it
 *  into smaller classes. The class should be refactored in order to be more readable.
 *  Maybe it makes sense to move all ASM visitors into a separate package.
 *  When the class is refactored remove that puzzle.
 */
@ToString
@SuppressWarnings({"PMD.UseObjectForClearerAPI", "PMD.AvoidDuplicateLiterals"})
public final class BytecodeRepresentation implements Representation {

    /**
     * Input source.
     */
    private final byte[] input;

    /**
     * Constructor.
     * @param clazz Path to the class file
     */
    public BytecodeRepresentation(final Path clazz) {
        this(new InputOf(clazz));
    }

    /**
     * Constructor.
     * @param input Input source.
     */
    public BytecodeRepresentation(final byte[] input) {
        this.input = Arrays.copyOf(input, input.length);
    }

    /**
     * Constructor.
     * @param input Input source
     */
    BytecodeRepresentation(final Input input) {
        this(new UncheckedBytes(new BytesOf(input)).asBytes());
    }

    @Override
    public String name() {
        final ClassName name = new ClassName();
        new ClassReader(this.input).accept(name, 0);
        return name.asString();
    }

    @Override
    public XML toEO() {
        try {
            final ClassPrinter printer = new ClassPrinter();
            new ClassReader(this.input).accept(printer, 0);
            final XMLDocument res = new XMLDocument(new Xembler(printer.directives).xml());
            new Schema(res).check();
            return res;
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
     * Class name.
     * @since 0.1.0
     * @todo #84:30min Handle constructors in classes.
     *  Right now we just skip constructors. We should handle them in order to
     *  build correct XML representation of the class. When the method is ready
     *  remove that puzzle.
     * @todo #84:30min Handle method parameters.
     *  Right now we just skip method parameters. We should handle them in order to
     *  build correct XML representation of the class. When the method is ready
     *  remove that puzzle.
     */
    private class ClassName extends ClassVisitor {

        /**
         * Atomic reference to store class name.
         */
        private final AtomicReference<String> bag;

        /**
         * Constructor.
         */
        ClassName() {
            this(new AtomicReference<>());
        }

        /**
         * Constructor.
         * @param bag Atomic reference to store class name.
         */
        ClassName(final AtomicReference<String> bag) {
            this(Opcodes.ASM9, bag);
        }

        /**
         * Constructor.
         * @param api ASM API version.
         * @param bag Atomic reference to store class name.
         */
        private ClassName(final int api, final AtomicReference<String> bag) {
            super(api);
            this.bag = bag;
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
            this.bag.set(name.replace('/', '.'));
            super.visit(version, access, name, signature, supername, interfaces);
        }

        /**
         * Get class name.
         * @return Class name.
         */
        String asString() {
            final String last = this.bag.get();
            if (Objects.isNull(last)) {
                throw new IllegalStateException(
                    "Class name is not set, bug is empty. Use #visit() method to set it."
                );
            }
            return last;
        }
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
            final String now = ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
            this.directives.add("program")
                .attr("name", name.replace('/', '.'))
                .attr("version", "0.0.0")
                .attr("revision", "0.0.0")
                .attr("dob", now)
                .attr("time", now)
                .add("listing")
                .set(new Base64Bytecode(BytecodeRepresentation.this.input).asString())
                .up()
                .add("errors").up()
                .add("sheets").up()
                .add("license").up()
                .add("metas").up()
                .attr("ms", System.currentTimeMillis())
                .add("objects");
            this.directives.add("o")
                .attr("abstract", "")
                .attr("name", String.format("class__%s", name));
            super.visit(version, access, name, signature, supername, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions
        ) {
            final MethodVisitor result;
            if (name.equals("<init>")) {
                result = super.visitMethod(access, name, descriptor, signature, exceptions);
            } else {
                this.directives.add("o")
                    .attr("abstract", "")
                    .attr("name", name);
                if (Type.getMethodType(descriptor).getArgumentTypes().length > 0) {
                    this.directives.add("o")
                        .attr("name", "args")
                        .up();
                }
                this.directives.add("o")
                    .attr("base", "seq")
                    .attr("name", "@");
                result = new MethodPrinter(
                    this.directives,
                    super.visitMethod(access, name, descriptor, signature, exceptions)
                );
            }
            return result;
        }

        @Override
        public void visitEnd() {
            this.directives.up();
            super.visitEnd();
        }
    }

    /**
     * Method printer.
     * ASM method visitor which scans the method and builds Xembly directives.
     * @since 0.1
     */
    private static final class MethodPrinter extends MethodVisitor {

        /**
         * Xembly directives.
         */
        private final Directives directives;

        /**
         * Constructor.
         * @param directives Xembly directives
         * @param visitor Method visitor
         */
        MethodPrinter(
            final Directives directives,
            final MethodVisitor visitor
        ) {
            super(Opcodes.ASM9, visitor);
            this.directives = directives;
        }

        @Override
        public void visitInsn(final int opcode) {
            this.opcode(opcode);
            super.visitInsn(opcode);
        }

        @Override
        public void visitFieldInsn(
            final int opcode,
            final String owner,
            final String name,
            final String descriptor
        ) {
            this.opcode(opcode, String.format("%s.%s", owner, name));
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitIntInsn(final int opcode, final int operand) {
            this.opcode(opcode, operand);
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitJumpInsn(final int opcode, final Label label) {
            this.opcode(opcode);
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitTypeInsn(final int opcode, final String type) {
            this.opcode(opcode, type);
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitVarInsn(final int opcode, final int varindex) {
            this.opcode(opcode, varindex);
            super.visitVarInsn(opcode, varindex);
        }

        @Override
        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String descriptor,
            final boolean isinterface
        ) {
            this.opcode(opcode, String.format("%s.%s", owner, name));
            super.visitMethodInsn(opcode, owner, name, descriptor, isinterface);
        }

        @Override
        public void visitLdcInsn(final Object value) {
            this.opcode(Opcodes.LDC, value);
            super.visitLdcInsn(value);
        }

        @Override
        public void visitEnd() {
            this.directives.up().up();
            super.visitEnd();
        }

        /**
         * Add opcode to the directives.
         * @param opcode Opcode
         * @param operands Operands
         */
        private void opcode(final int opcode, final Object... operands) {
            this.directives.add("o")
                .attr("name", new OpcodeName(opcode).asString())
                .attr("base", "opcode");
            for (final Object operand : operands) {
                if (operand instanceof String) {
                    this.directives.add("o")
                        .attr("base", "string")
                        .attr("data", "bytes")
                        .set(((String) operand).replace('/', '.'))
                        .up();
                } else {
                    this.directives.add("o")
                        .attr("base", "int")
                        .attr("data", "bytes")
                        .set(operand)
                        .up();
                }
            }
            this.directives.up();
        }
    }
}
