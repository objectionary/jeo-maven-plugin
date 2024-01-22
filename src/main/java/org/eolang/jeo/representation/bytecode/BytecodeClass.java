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
package org.eolang.jeo.representation.bytecode;

import com.jcabi.xml.XML;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import org.eolang.jeo.PluginStartup;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.xmir.XmlAnnotations;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Class useful for generating bytecode for testing purposes.
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
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

    /**
     * Class properties (access, signature, supername, interfaces).
     */
    private final BytecodeClassProperties props;

    /**
     * Constructor.
     */
    public BytecodeClass() {
        this("Simple");
    }

    /**
     * Constructor.
     *
     * @param name Class name.
     */
    public BytecodeClass(final String name) {
        this(name, Opcodes.ACC_PUBLIC);
    }

    /**
     * Constructor.
     *
     * @param name Class name.
     * @param access Access modifiers.
     */
    public BytecodeClass(final String name, final int access) {
        this(name, access, new CustomClassWriter());
    }

    /**
     * Constructor.
     *
     * @param name Class name.
     * @param properties Class properties.
     */
    public BytecodeClass(final String name, final BytecodeClassProperties properties) {
        this(
            name,
            new CustomClassWriter(),
            new ArrayList<>(0),
            properties
        );
    }

    /**
     * Constructor.
     *
     * @param name Class name.
     * @param access Access modifiers.
     * @param writer ASM class writer.
     */
    private BytecodeClass(
        final String name,
        final int access,
        final ClassWriter writer
    ) {
        this(name, writer, new ArrayList<>(0), new BytecodeClassProperties(access));
    }

    /**
     * Constructor.
     *
     * @param name Class name.
     * @param writer ASM class writer.
     * @param methods Methods.
     * @param properties Class properties.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeClass(
        final String name,
        final ClassWriter writer,
        final Collection<BytecodeMethod> methods,
        final BytecodeClassProperties properties
    ) {
        this.name = name;
        this.writer = writer;
        this.methods = methods;
        this.props = properties;
    }

    /**
     * Converts bytecode into XML.
     *
     * @return XML representation of bytecode.
     */
    public XML xml() {
        return new BytecodeRepresentation(this.bytecode()).toEO();
    }

    /**
     * Generate bytecode.
     * <p>
     * In this method we intentionally use the Thread.currentThread().getContextClassLoader()
     * for the class loader of the
     * {@link CheckClassAdapter#verify(ClassReader, ClassLoader, boolean, PrintWriter)}
     * instead of default implementation. This is because the default class loader doesn't
     * know about classes compiled on the previous maven step.
     * You can read more about the problem here:
     * {@link PluginStartup#init()} ()}
     * </p>
     * @return Bytecode.
     */
    public Bytecode bytecode() {
        try {
            this.props.write(this.writer, this.name);
            this.methods.forEach(BytecodeMethod::write);
            this.writer.visitEnd();
            final byte[] bytes = this.writer.toByteArray();
            this.verify(bytes);
            return new Bytecode(bytes);
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Can't create bytecode for the class '%s' ", this.name),
                exception
            );
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format(
                    "Bytecode creation for the '%s' class is not possible due to unmet preconditions.",
                    this.name
                ),
                exception
            );
        }
    }

    /**
     * Add constructor.
     *
     * @param modifiers Constructor modifiers.
     * @return This object.
     */
    public BytecodeMethod withConstructor(final int... modifiers) {
        return this.withConstructor("()V", modifiers);
    }

    /**
     * Add method.
     *
     * @param properties Method properties.
     * @return This object.
     */
    public BytecodeMethod withMethod(final BytecodeMethodProperties properties) {
        final BytecodeMethod method = new BytecodeMethod(properties, this.writer, this);
        this.methods.add(method);
        return method;
    }

    /**
     * Add constructor.
     *
     * @param descriptor Constructor descriptor.
     * @param modifiers Constructor modifiers.
     * @return This object.
     */
    public BytecodeMethod withConstructor(final String descriptor, final int... modifiers) {
        return this.withMethod("<init>", descriptor, modifiers);
    }

    /**
     * Add method.
     *
     * @param mname Method name.
     * @param descriptor Method descriptor.
     * @param modifiers Access modifiers.
     * @return This object.
     */
    public BytecodeMethod withMethod(
        final String mname,
        final String descriptor,
        final int... modifiers
    ) {
        final BytecodeMethod method = new BytecodeMethod(
            mname,
            this.writer,
            this,
            descriptor,
            modifiers
        );
        this.methods.add(method);
        return method;
    }

    /**
     * Add field.
     *
     * @param fname Field name.
     * @return This object.
     */
    public BytecodeClass withField(final String fname) {
        this.withField(
            fname,
            "Ljava/lang/String;",
            null,
            "bar",
            Opcodes.ACC_PUBLIC
        );
        return this;
    }

    /**
     * Add field.
     *
     * @param fname Field name.
     * @param descriptor Field descriptor.
     * @param signature Field signature.
     * @param value Field value.
     * @param modifiers Access modifiers.
     * @return This object.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public FieldVisitor withField(
        final String fname,
        final String descriptor,
        final String signature,
        final Object value,
        final int... modifiers
    ) {
        int access = 0;
        for (final int modifier : modifiers) {
            access |= modifier;
        }
        return this.writer.visitField(
            access,
            fname,
            descriptor,
            signature,
            value
        );
    }

    /**
     * Add annotations.
     *
     * @param annotations Annotations.
     * @return This object.
     */
    public BytecodeClass withAnnotations(final XmlAnnotations annotations) {
        annotations.all().forEach(
            annotation -> this.writer.visitAnnotation(
                annotation.descriptor(),
                annotation.visible()
            )
        );
        return this;
    }

    /**
     * Hello world bytecode.
     *
     * @return The same class with the hello world method.
     */
    public BytecodeClass helloWorldMethod() {
        final BytecodeMethodProperties properties = new BytecodeMethodProperties(
            "main",
            "([Ljava/lang/String;)V",
            Opcodes.ACC_PUBLIC,
            Opcodes.ACC_STATIC
        );
        return this.withMethod(properties)
            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.LDC, "Hello, world!")
            .opcode(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V"
            )
            .opcode(Opcodes.RETURN)
            .up();
    }

    /**
     * Verify bytecode.
     * @param bytes Bytecode to verify.
     * @todo #427:90min Use custom bytecode verifier instead of CheckClassAdapter#verify.
     *  This class can only print the error message to the console. We need to have a custom
     *  bytecode verifier that can throw an exception with the error message.
     *  Here you can find the detailed problem description:
     *  <a href="https://stackoverflow.com/q/77854100/10423604">link</a>
     */
    private void verify(final byte[] bytes) {
        final StringWriter errors = new StringWriter();
        CheckClassAdapter.verify(
            new ClassReader(bytes),
            Thread.currentThread().getContextClassLoader(),
            false,
            new PrintWriter(errors)
        );
        if (!errors.toString().isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    "Bytecode verification failed for the class '%s' due to the following reasons: %s",
                    this.name,
                    errors
                )
            );
        }
    }
}
