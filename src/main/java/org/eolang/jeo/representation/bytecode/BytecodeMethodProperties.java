/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.log.Logger;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode method properties.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class BytecodeMethodProperties {

    /**
     * Access modifiers.
     */
    private final int access;

    /**
     * Method name.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final String name;

    /**
     * Method descriptor.
     */
    private final String descr;

    /**
     * Method signature.
     */
    private final String signature;

    /**
     * Method parameters.
     */
    private final BytecodeMethodParameters parameters;

    /**
     * Method exceptions.
     */
    private final String[] exceptions;

    /**
     * Constructor.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param modifiers Access modifiers.
     */
    public BytecodeMethodProperties(
        final String name,
        final String descriptor,
        final int... modifiers
    ) {
        this(name, descriptor, null, modifiers);
    }

    /**
     * Constructor.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param modifiers Access modifiers.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeMethodProperties(
        final String name,
        final String descriptor,
        final String signature,
        final int... modifiers
    ) {
        this(IntStream.of(modifiers).sum(), name, descriptor, signature, new String[0]);
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    public BytecodeMethodProperties(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String... exceptions
    ) {
        this(
            access,
            name,
            descriptor,
            signature,
            new BytecodeMethodParameters(descriptor),
            exceptions
        );
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param parameters Method parameters.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeMethodProperties(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final BytecodeMethodParameters parameters,
        final String... exceptions
    ) {
        this.access = access;
        this.name = name;
        this.descr = descriptor;
        this.signature = signature;
        this.parameters = parameters;
        this.exceptions = exceptions.clone();
    }

    /**
     * Method name.
     * @return Method name.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public String name() {
        return this.name;
    }

    /**
     * Method descriptor.
     * @return Method descriptor.
     */
    public String descriptor() {
        return this.descr;
    }

    /**
     * Is method abstract.
     * @return True if the method is abstract.
     */
    public boolean isAbstract() {
        return (this.access & Opcodes.ACC_ABSTRACT) != 0;
    }

    /**
     * Is method static.
     * @return True if the method is static.
     */
    public boolean isStatic() {
        return (this.access & Opcodes.ACC_STATIC) != 0;
    }

    /**
     * Convert to directives.
     * @param maxs Maxs.
     * @return Directives.
     */
    public DirectivesMethodProperties directives(final BytecodeMaxs maxs) {
        return new DirectivesMethodProperties(
            this.access,
            this.descr,
            this.signature,
            this.exceptions,
            maxs.directives(),
            this.parameters.directives()
        );
    }

    /**
     * Add method to a class writer.
     * @param writer Class writer.
     * @param compute If frames should be computed.
     * @return Method visitor.
     */
    MethodVisitor writeMethod(final CustomClassWriter writer, final boolean compute) {
        Logger.debug(
            this,
            String.format("Creating method visitor with the following properties %s", this)
        );
        final MethodVisitor visitor = writer.visitMethod(
            this.access,
            this.name,
            this.descr,
            Optional.ofNullable(this.signature).filter(s -> !s.isEmpty()).orElse(null),
            this.exceptions,
            compute
        );
        this.parameters.write(visitor);
        return visitor;
    }
}
