/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.directives.DirectivesAttributes;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.objectweb.asm.Opcodes;

/**
 * Class useful for generating bytecode for testing purposes.
 * @since 0.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
@ToString
@EqualsAndHashCode
public final class BytecodeClass implements Testable {

    /**
     * Class name.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final String name;

    /**
     * Methods.
     */
    private final List<BytecodeMethod> cmethods;

    /**
     * Fields.
     */
    private final List<BytecodeField> fields;

    /**
     * Annotations.
     */
    private final BytecodeAnnotations annotations;

    /**
     * Attributes.
     */
    private final List<BytecodeAttribute> attributes;

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
     * Has real usages.
     * @param name Class name.
     * @param properties Class properties.
     */
    public BytecodeClass(
        final String name,
        final BytecodeClassProperties properties
    ) {
        this(name, new ArrayList<>(0), properties);
    }

    /**
     * Constructor.
     *
     * @param name Class name.
     * @param methods Methods.
     * @param properties Class properties.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeClass(
        final String name,
        final List<BytecodeMethod> methods,
        final BytecodeClassProperties properties
    ) {
        this(
            name,
            methods,
            new ArrayList<>(0),
            new BytecodeAnnotations(),
            new ArrayList<>(0),
            properties
        );
    }

    /**
     * Constructor.
     * @param name Class name.
     * @param methods Methods.
     * @param fields Fields.
     * @param annotations Annotations.
     * @param attributes Attributes.
     * @param props Class properties.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeClass(
        final String name,
        final List<BytecodeMethod> methods,
        final List<BytecodeField> fields,
        final BytecodeAnnotations annotations,
        final List<BytecodeAttribute> attributes,
        final BytecodeClassProperties props
    ) {
        this.name = name;
        this.cmethods = methods;
        this.fields = fields;
        this.annotations = annotations;
        this.attributes = attributes;
        this.props = props;
    }

    /**
     * Class name.
     * @return Name.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public String name() {
        return this.name;
    }

    /**
     * Add constructor.
     * @param modifiers Constructor modifiers.
     * @return This object.
     */
    public BytecodeMethodBuilder withConstructor(final int... modifiers) {
        return this.withConstructor("()V", modifiers);
    }

    /**
     * Add method.
     * @param properties Method properties.
     * @return This object.
     */
    public BytecodeMethodBuilder withMethod(final BytecodeMethodProperties properties) {
        return this.withMethod(properties, new BytecodeMaxs(0, 0));
    }

    /**
     * Add method.
     * @param properties Method properties.
     * @param maxs Method maxs.
     * @return This object.
     */
    public BytecodeMethodBuilder withMethod(
        final BytecodeMethodProperties properties, final BytecodeMaxs maxs
    ) {
        return this.withMethod(new BytecodeMethod(properties, maxs));
    }

    /**
     * Add constructor.
     * @param descriptor Constructor descriptor.
     * @param modifiers Constructor modifiers.
     * @return This object.
     */
    public BytecodeMethodBuilder withConstructor(final String descriptor, final int... modifiers) {
        return this.withMethod("<init>", descriptor, modifiers);
    }

    /**
     * Add field.
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
     * Add method.
     * @param mname Method name.
     * @param descriptor Method descriptor.
     * @param modifiers Access modifiers.
     * @return This object.
     */
    public BytecodeMethodBuilder withMethod(
        final String mname, final String descriptor, final int... modifiers
    ) {
        return this.withMethod(new BytecodeMethod(mname, descriptor, modifiers));
    }

    @Override
    public String testCode() {
        final StringBuilder builder = new StringBuilder(0);
        for (final BytecodeMethod method : this.cmethods) {
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
     * Properties.
     * @return Class properties.
     */
    public BytecodeClassProperties properties() {
        return this.props;
    }

    /**
     * Retrieve class methods.
     * @return Class methods.
     */
    public List<BytecodeMethod> methods() {
        return Collections.unmodifiableList(this.cmethods);
    }

    /**
     * Without methods.
     * @return The same class without methods.
     */
    public BytecodeClass withoutMethods() {
        this.cmethods.clear();
        return this;
    }

    /**
     * Convert to directives with opcodes' counting.
     * @return Directives.
     */
    public DirectivesClass directives() {
        return this.directives(true);
    }

    /**
     * Convert to directives.
     * @param counting Whether to count opcodes.
     * @return Directives.
     */
    public DirectivesClass directives(final boolean counting) {
        return new DirectivesClass(
            new ClassName(new PrefixedName(this.name).encode()),
            this.props.directives(),
            this.fields.stream().map(BytecodeField::directives).collect(Collectors.toList()),
            this.cmethods.stream().map(method -> method.directives(counting))
                .collect(Collectors.toList()),
            this.annotations.directives(),
            new DirectivesAttributes(
                this.attributes.stream()
                    .map(BytecodeAttribute::directives)
                    .collect(Collectors.toList())
            )
        );
    }

    /**
     * Constructor.
     * @param visitor Writer.
     * @param pckg Package.
     */
    void writeTo(final CustomClassWriter visitor, final String pckg) {
        try {
            visitor.visit(
                this.props.version(),
                this.props.access(),
                new ClassName(pckg, this.name).full(),
                this.props.signature(),
                this.props.supername(),
                this.props.interfaces()
            );
            this.annotations.write(visitor);
            this.fields.forEach(field -> field.write(visitor));
            this.cmethods.forEach(method -> method.write(visitor));
            this.attributes.forEach(attr -> attr.write(visitor));
            visitor.visitEnd();
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
     * Whether the class has opcodes.
     * @return True if it has opcodes, false otherwise.
     */
    boolean hasOpcodes() {
        return this.cmethods.stream().anyMatch(BytecodeMethod::hasOpcodes);
    }

    /**
     * Whether the class has labels.
     * @return True if it has labels, false otherwise.
     */
    boolean hasLabels() {
        return this.cmethods.stream().anyMatch(BytecodeMethod::hasLabels);
    }

    /**
     * Add method.
     * @param method Method.
     * @return This object.
     */
    private BytecodeMethodBuilder withMethod(final BytecodeMethod method) {
        this.cmethods.add(method);
        return new BytecodeMethodBuilder(this, method);
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
    private BytecodeField withField(
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
}
