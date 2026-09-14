/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.log.Logger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bytecode method properties.
 *
 * @since 0.1
 */
public final class BytecodeMethodProperties {

    /**
     * Access modifiers.
     */
    private final int access;

    /**
     * Method name.
     */
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
     *
     * @param name Method name
     * @param descriptor Method descriptor
     * @param modifiers Access modifiers
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
     *
     * @param name Method name
     * @param descriptor Method descriptor
     * @param signature Method signature
     * @param modifiers Access modifiers
     */
    public BytecodeMethodProperties(
        final String name,
        final String descriptor,
        final String signature,
        final int... modifiers
    ) {
        this(
            IntStream.of(modifiers).reduce(0, (a, b) -> a | b),
            name,
            descriptor,
            signature,
            new String[0]
        );
    }

    /**
     * Constructor.
     *
     * @param access Access modifiers
     * @param name Method name
     * @param descriptor Method descriptor
     * @param signature Method signature
     * @param exceptions Method exceptions
     */
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
     *
     * @param access Access modifiers
     * @param name Method name
     * @param descriptor Method descriptor
     * @param signature Method signature
     * @param parameters Method parameters
     * @param exceptions Method exceptions
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
     *
     * @return Method name
     */
    public String name() {
        return this.name;
    }

    /**
     * Method descriptor.
     *
     * @return Method descriptor
     */
    public String descriptor() {
        return this.descr;
    }

    /**
     * Is method abstract.
     *
     * @return True if the method is abstract
     */
    public boolean isAbstract() {
        return (this.access & Opcodes.ACC_ABSTRACT) != 0;
    }

    /**
     * Is method static.
     *
     * @return True if the method is static
     */
    public boolean isStatic() {
        return (this.access & Opcodes.ACC_STATIC) != 0;
    }

    /**
     * Convert to directives.
     *
     * @param maxs Maxs
     * @param format Format of the directives
     * @return Directives
     */
    public DirectivesMethodProperties directives(final BytecodeMaxs maxs, final Format format) {
        return new DirectivesMethodProperties(
            this.access,
            this.name,
            this.descr,
            this.signature,
            this.exceptions,
            maxs.directives(format),
            this.parameters.directives(format),
            format
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeMethodProperties) {
            final BytecodeMethodProperties props = (BytecodeMethodProperties) other;
            result = this.access == props.access
                && Objects.equals(this.name, props.name)
                && Objects.equals(this.descr, props.descr)
                && Objects.equals(this.signature, props.signature)
                && Objects.equals(this.parameters, props.parameters)
                && Arrays.equals(this.exceptions, props.exceptions);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.access, this.name, this.descr, this.signature, this.parameters,
            Arrays.hashCode(this.exceptions)
        );
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeMethodProperties(access=%d, name=%s, descr=%s, signature=%s, parameters=%s, exceptions=%s)",
            this.access, this.name, this.descr, this.signature, this.parameters,
            Arrays.toString(this.exceptions)
        );
    }

    /**
     * Add method to a class writer.
     *
     * @param writer Class writer
     * @param compute If frames should be computed
     * @return Method visitor
     */
    MethodVisitor writeMethod(final CustomClassWriter writer, final boolean compute) {
        Logger.debug(this, "Creating method visitor with the following properties %s", this);
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
