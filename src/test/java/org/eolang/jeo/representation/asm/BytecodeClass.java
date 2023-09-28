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
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collection;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Class useful for generating bytecode for testing purposes.
 * @since 0.1.0
 */
@SuppressWarnings({"JTCOP.RuleAllTestsHaveProductionClass", "JTCOP.RuleCorrectTestName"})
final class BytecodeClass {

    /**
     * Class name.
     */
    private final String name;

    /**
     * ASM class writer.
     */
    private final ClassWriter writer;

    /**
     * Methods.
     */
    private final Collection<BytecodeMethod> methods;

    /**
     * Constructor.
     */
    BytecodeClass() {
        this("Simple");
    }

    /**
     * Constructor.
     * @param name Class name.
     */
    BytecodeClass(final String name) {
        this(name, new ClassWriter(0));
    }

    /**
     * Constructor.
     * @param name Class name.
     * @param writer ASM class writer.
     */
    private BytecodeClass(final String name, final ClassWriter writer) {
        this.name = name;
        this.writer = writer;
        this.methods = new ArrayList<>(0);
    }

    /**
     * Add method.
     * @param mname Method name.
     * @return This object.
     */
    BytecodeMethod withMethod(final String mname) {
        final BytecodeMethod method = new BytecodeMethod(mname, this.writer, this);
        this.methods.add(method);
        return method;
    }

    /**
     * Generate bytecode.
     * @return Bytecode.
     */
    byte[] bytes() {
        this.writer.visit(
            Opcodes.ASM9,
            Opcodes.ACC_PUBLIC,
            this.name,
            null,
            "java/lang/Object",
            null
        );
        this.methods.forEach(BytecodeMethod::generate);
        return this.writer.toByteArray();
    }

    /**
     * Bytecode method.
     * @since 0.1.0
     */
    static final class BytecodeMethod {

        /**
         * Method name.
         */
        private final String name;

        /**
         * ASM class writer.
         */
        private final ClassWriter writer;

        /**
         * Original class.
         */
        private final BytecodeClass clazz;

        /**
         * Constructor.
         * @param name Method name.
         * @param writer ASM class writer.
         */
        private BytecodeMethod(
            final String name,
            final ClassWriter writer,
            final BytecodeClass clazz
        ) {
            this.name = name;
            this.writer = writer;
            this.clazz = clazz;
        }

        BytecodeClass up() {
            return this.clazz;
        }

        /**
         * Generate bytecode.
         */
        private void generate() {
            this.writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                this.name,
                "()V",
                null,
                null
            );
        }
    }
}
