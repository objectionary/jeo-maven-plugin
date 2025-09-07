/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode representation of a Java class.
 *
 * <p>This class represents a Java class in bytecode form, containing methods, fields,
 * annotations, attributes, and class properties. It provides functionality for
 * building bytecode classes programmatically and converting them to various formats.</p>
 * @since 0.1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
@ToString
@EqualsAndHashCode
public final class BytecodeClass {

    /**
     * Class name.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final ClassName name;

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
    private final BytecodeAttributes attributes;

    /**
     * Class properties (access, signature, supername, interfaces).
     */
    private final BytecodeClassProperties props;

    /**
     * Default constructor.
     * Creates a simple class with default settings.
     */
    public BytecodeClass() {
        this("Simple");
    }

    /**
     * Constructor.
     * @param name The class name
     */
    public BytecodeClass(final String name) {
        this(name, Opcodes.ACC_PUBLIC);
    }

    /**
     * Constructor.
     * @param name The class name
     * @param access The access modifiers
     */
    public BytecodeClass(final String name, final int access) {
        this(name, new BytecodeClassProperties(access));
    }

    /**
     * Constructor.
     * @param name The class name
     * @param properties The class properties
     */
    public BytecodeClass(
        final String name,
        final BytecodeClassProperties properties
    ) {
        this(name, new ArrayList<>(0), properties);
    }

    /**
     * Constructor.
     * @param name The class name
     * @param methods The class methods
     * @param properties The class properties
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeClass(
        final String name,
        final List<BytecodeMethod> methods,
        final BytecodeClassProperties properties
    ) {
        this(
            new ClassName(name),
            methods,
            new ArrayList<>(0),
            new BytecodeAnnotations(),
            new BytecodeAttributes(),
            properties
        );
    }

    /**
     * Constructor.
     * @param name The class name
     * @param methods The class methods
     * @param fields The class fields
     * @param annotations The class annotations
     * @param attributes The class attributes
     * @param props The class properties
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeClass(
        final ClassName name,
        final List<BytecodeMethod> methods,
        final List<BytecodeField> fields,
        final BytecodeAnnotations annotations,
        final BytecodeAttributes attributes,
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
    public ClassName name() {
        return this.name;
    }

    /**
     * Add constructor to the class.
     * @param modifiers The constructor access modifiers
     * @return The method builder for chaining
     */
    public BytecodeMethodBuilder withConstructor(final int... modifiers) {
        return this.withConstructor("()V", modifiers);
    }

    /**
     * Add method to the class.
     * @param properties The method properties
     * @return The method builder for chaining
     */
    public BytecodeMethodBuilder withMethod(final BytecodeMethodProperties properties) {
        return this.withMethod(properties, new BytecodeMaxs());
    }

    /**
     * Add method to the class.
     * @param properties The method properties
     * @param maxs The method stack/locals maxs
     * @return The method builder for chaining
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
            .label()
            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.LDC, "Hello, world!")
            .opcode(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            )
            .label()
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
     * Convert to directives.
     * @return Directives.
     */
    public DirectivesClass directives() {
        return this.directives(new Format());
    }

    /**
     * Convert to directives.
     * @param format Format of the directives.
     * @return Directives.
     */
    public DirectivesClass directives(final Format format) {
        return new DirectivesClass(
            format,
            this.name,
            this.props.directives(format),
            this.fields.stream().map(f -> f.directives(format)).collect(Collectors.toList()),
            this.cmethods.stream()
                .map(method -> method.directives(this.mnumber(method), format))
                .collect(Collectors.toList()),
            this.annotations.directives(format),
            this.attributes.directives(format, "attributes")
        );
    }

    /**
     * Constructor.
     * @param visitor Writer.
     */
    void writeTo(final CustomClassWriter visitor) {
        try {
            visitor.visit(
                this.props.version(),
                this.props.access(),
                this.name.full(),
                this.props.signature(),
                this.props.supername(),
                this.props.interfaces()
            );
            this.annotations.write(visitor);
            this.fields.forEach(field -> field.write(visitor));
            this.cmethods.forEach(method -> method.write(visitor));
            this.attributes.write(visitor);
            visitor.visitEnd();
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Can't create bytecode for the class '%s' ", this.name),
                exception
            );
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format(
                    "Bytecode creation for the '%s' class is not possible due to unmet preconditions.",
                    this.name.full()
                ),
                exception
            );
        }
    }

    /**
     * Method number.
     * @param method Method.
     * @return Method number.
     */
    private int mnumber(final BytecodeMethod method) {
        return this.methods().stream()
            .filter(m -> m.name().equals(method.name()))
            .collect(Collectors.toList())
            .indexOf(method) + 1;
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
