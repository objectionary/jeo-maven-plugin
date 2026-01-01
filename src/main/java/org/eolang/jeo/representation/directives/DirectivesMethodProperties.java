/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Method properties as Xembly directives.
 * All the directives are sorted according to JVM method specification:
 * {@code
 * method_info {
 *     u2             access_flags; {@link DirectivesMethodProperties}
 *     u2             name_index; {@link DirectivesMethodProperties}
 *     u2             descriptor_index; {@link DirectivesMethodProperties}
 *     u2             attributes_count; {@link DirectivesMethod}
 *     attribute_info attributes[attributes_count]; {@link DirectivesMethod}
 * }}
 * @since 0.1
 */
public final class DirectivesMethodProperties implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Method access modifiers.
     */
    private final int access;

    /**
     * Method name.
     */
    private final String name;

    /**
     * Method descriptor.
     */
    private final String descriptor;

    /**
     * Method signature.
     * Attribute "Signature_attribute".
     */
    private final String signature;

    /**
     * Method exceptions.
     * Attribute "Exceptions_attribute".
     */
    private final String[] exceptions;

    /**
     * Method max stack and locals.
     * Inside 'Code' attribute.
     */
    private final AtomicReference<DirectivesMaxs> max;

    /**
     * Method parameters.
     * Attribute "MethodParameters" (since Java 1.8).
     */
    private final DirectivesMethodParams params;

    /**
     * Constructor.
     */
    public DirectivesMethodProperties() {
        this("main");
    }

    /**
     * Constructor.
     * @param name Method name.
     */
    public DirectivesMethodProperties(final String name) {
        this(Opcodes.ACC_PUBLIC, name, "()V", "");
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
    public DirectivesMethodProperties(
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
            exceptions,
            new DirectivesMaxs(),
            new DirectivesMethodParams()
        );
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @param max Max stack and locals.
     * @param params Method parameters.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodProperties(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions,
        final DirectivesMaxs max,
        final DirectivesMethodParams params
    ) {
        this(access, name, descriptor, signature, exceptions, max, params, new Format());
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param name Method name.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @param max Max stack and locals.
     * @param params Method parameters.
     * @param format Format of the directives.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodProperties(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions,
        final DirectivesMaxs max,
        final DirectivesMethodParams params,
        final Format format
    ) {
        this.access = access;
        this.name = name;
        this.descriptor = Optional.ofNullable(descriptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.exceptions = Optional.ofNullable(exceptions).orElse(new String[0]).clone();
        this.max = new AtomicReference<>(max);
        this.params = params;
        this.format = format;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives dirs = new Directives();
        dirs.append(new DirectivesValue(this.format, "access", this.access));
        if (this.format.modifiers()) {
            dirs.append(new DirectivesMethodModifiers(this.format, this.access));
        }
        dirs.append(new DirectivesValue(this.format, "name", this.name));
        dirs.append(new DirectivesValue(this.format, "descriptor", this.descriptor));
        dirs.append(new DirectivesValue(this.format, "signature", this.signature));
        dirs.append(
            new DirectivesValues(this.format, "exceptions", (Object[]) this.exceptions)
        );
        dirs.append(this.max.get());
        dirs.append(this.params);
        return dirs.iterator();
    }
}
