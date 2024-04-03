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
import java.util.ArrayList;
import java.util.Collection;
import lombok.ToString;
import org.eolang.jeo.PluginStartup;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.xmir.XmlAnnotations;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Class useful for generating bytecode for testing purposes.
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
@ToString
public final class BytecodeClass implements Testable {

    /**
     * Class name.
     */
    private final String name;

    /**
     * Class writer.
     */
    @ToString.Exclude
    private final CustomClassVisitor visitor;

    /**
     * Methods.
     */
    @ToString.Exclude
    private final Collection<BytecodeMethod> methods;

    /**
     * Fields.
     */
    @ToString.Exclude
    private final Collection<BytecodeField> fields;

    /**
     * Annotations.
     */
    @ToString.Exclude
    private final Collection<BytecodeAnnotation> annotations;

    /**
     * Class properties (access, signature, supername, interfaces).
     */
    private final BytecodeClassProperties props;

    /**
     * Constructor.
     * Used in tests only.
     */
    public BytecodeClass() {
        this("Simple");
    }

    /**
     * Constructor.
     * Used in tests only.
     * @param name Class name.
     */
    public BytecodeClass(final String name) {
        this(name, Opcodes.ACC_PUBLIC);
    }

    /**
     * Constructor.
     * Used in tests only.
     * @param name Class name.
     * @param access Access modifiers.
     */
    public BytecodeClass(final String name, final int access) {
        this(name, new BytecodeClassProperties(access));
    }

    /**
     * Constructor.
     * Used in tests only.
     * @param name Class name.
     * @param properties Class properties.
     */
    public BytecodeClass(final String name, final BytecodeClassProperties properties) {
        this(name, properties, true);
    }

    /**
     * Constructor.
     * Has real usages.
     * @param name Class name.
     * @param properties Class properties.
     * @param verify Verify bytecode.
     */
    public BytecodeClass(
        final String name,
        final BytecodeClassProperties properties,
        final boolean verify
    ) {
        this(name, BytecodeClass.writer(verify, false), new ArrayList<>(0), properties);
    }

    public BytecodeClass(
        final String name,
        final BytecodeClassProperties properties,
        final boolean verify,
        final boolean frames
    ) {
        this(name, BytecodeClass.writer(verify, frames), new ArrayList<>(0), properties);
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
        final CustomClassVisitor writer,
        final Collection<BytecodeMethod> methods,
        final BytecodeClassProperties properties
    ) {
        this.name = name;
        this.visitor = writer;
        this.methods = methods;
        this.props = properties;
        this.fields = new ArrayList<>(0);
        this.annotations = new ArrayList<>(0);
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
            this.props.write(this.visitor, this.name);
            this.annotations.forEach(annotation -> annotation.write(this.visitor));
            this.fields.forEach(field -> field.write(this.visitor));
            this.methods.forEach(BytecodeMethod::write);
            this.visitor.visitEnd();
            return this.visitor.bytecode();
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Can't create bytecode for the class '%s' ", this.name),
                exception
            );
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format(
                    "Bytecode creation for the '%s' class is not possible due to unmet preconditions. To reproduce the problem, you can write the following test: %n%s%n",
                    this.name,
                    this.testCode()
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
        return this.withMethod(properties, 0, 0);
    }

    /**
     * Add method.
     *
     * @param properties Method properties.
     * @param stack Stack size.
     * @param locals Local variables size.
     * @return This object.
     */
    public BytecodeMethod withMethod(
        final BytecodeMethodProperties properties, final int stack, final int locals
    ) {
        final BytecodeMethod method = new BytecodeMethod(
            properties,
            this.visitor,
            this,
            stack,
            locals
        );
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
            this.visitor,
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
    public BytecodeField withField(
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
        final BytecodeField field = new BytecodeField(fname, descriptor, signature, value, access);
        this.fields.add(field);
        return field;
    }

    /**
     * Add anns.
     *
     * @param all Annotations.
     * @return This object.
     */
    public BytecodeClass withAnnotations(final XmlAnnotations all) {
        all.all()
            .stream()
            .map(ann -> new BytecodeAnnotation(ann.descriptor(), ann.visible(), ann.props()))
            .forEach(this.annotations::add);
        return this;
    }

    @Override
    public String testCode() {
        final StringBuilder builder = new StringBuilder(0);
        for (final BytecodeMethod method : this.methods) {
            builder.append('.').append(method.testCode()).append('\n');
        }
        return String.format(
            "AllLabels labels = new AllLabels();\nnew BytecodeClass(\"%s\")\n%s.bytecode();",
            this.name,
            builder
        );
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
                "(Ljava/lang/String;)V",
                false
            )
            .opcode(Opcodes.RETURN)
            .up();
    }

    /**
     * Which class writer to use.
     * @param verify Verify bytecode.
     * @return Verified class writer if verify is true, otherwise custom class writer.
     */
    private static CustomClassVisitor writer(final boolean verify, final boolean frames) {
        final CustomClassWriter result;
        final int flags;
        if (frames) {
            flags = ClassWriter.COMPUTE_FRAMES;
        } else {
            flags = 0;
        }
        if (verify) {
            result = new VerifiedClassWriter(flags);
        } else {
            result = new CustomClassWriter(flags);
        }
        return new CustomClassVisitor(result);
    }
}
