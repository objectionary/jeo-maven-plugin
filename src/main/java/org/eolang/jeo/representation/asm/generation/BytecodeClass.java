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
package org.eolang.jeo.representation.asm.generation;

import com.jcabi.xml.XML;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.asm.Bytecode;
import org.eolang.jeo.representation.asm.DefaultVersion;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Class useful for generating bytecode for testing purposes.
 * @since 0.1.0
 */
public final class BytecodeClass {

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

    private final int access;

    /**
     * Constructor.
     */
    public BytecodeClass() {
        this("Simple");
    }

    /**
     * Constructor.
     * @param name Class name.
     */
    public BytecodeClass(final String name) {
        this(name, Opcodes.ACC_PUBLIC);
    }

    /**
     * Constructor.
     * @param name Class name.
     * @param access Access modifiers.
     */
    public BytecodeClass(final String name, final int access) {
        this(name.replace(".", "/"), access, new ClassWriter(ClassWriter.COMPUTE_MAXS));
    }

    /**
     * Constructor.
     * @param name Class name.
     * @param access Access modifiers.
     * @param writer ASM class writer.
     */
    private BytecodeClass(
        final String name,
        final int access,
        final ClassWriter writer
    ) {
        this.name = name;
        this.writer = writer;
        this.methods = new ArrayList<>(0);
        this.access = access;
    }

    /**
     * Converts bytecode into XML.
     * @return XML representation of bytecode.
     */
    public XML xml() {
        return new BytecodeRepresentation(this.bytecode().asBytes()).toEO();
    }

    /**
     * Generate bytecode.
     * @return Bytecode.
     */
    public Bytecode bytecode() {
        this.writer.visit(
            new DefaultVersion().java(),
            this.access,
            this.name,
            null,
            "java/lang/Object",
            null
        );
        this.methods.forEach(BytecodeMethod::generate);
        this.writer.visitEnd();
        final byte[] bytes = this.writer.toByteArray();
        CheckClassAdapter.verify(new ClassReader(bytes), true, new PrintWriter(System.out));
        return new Bytecode(bytes);
    }

    /**
     * Add method.
     * @param mname Method name.
     * @param modifiers Access modifiers.
     * @return This object.
     */
    public BytecodeMethod withMethod(final String mname, final int... modifiers) {
        final BytecodeMethod method = new BytecodeMethod(mname, this.writer, this, modifiers);
        this.methods.add(method);
        return method;
    }

    /**
     * Add method.
     * @param mname Method name.
     * @param modifiers Access modifiers.
     * @return This object.
     */
    public BytecodeMethod withMethod(
        final String mname,
        final String descriptor,
        final int... modifiers
    ) {
        final BytecodeMethod method = new BytecodeMethod(mname, this.writer, this, modifiers);
        this.methods.add(method.descriptor(descriptor));
        return method;
    }

    /**
     * Hello world bytecode.
     * @return The same class with the hello world method.
     */
    public BytecodeClass helloWorldMethod() {
        return this.withMethod("main", Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC)
            .descriptor("([Ljava/lang/String;)V")
            .instruction(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .instruction(Opcodes.LDC, "Hello, world!")
            .instruction(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V"
            )
            .instruction(Opcodes.RETURN)
            .up();
    }

}
