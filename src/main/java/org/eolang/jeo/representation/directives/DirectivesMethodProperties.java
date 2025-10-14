/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
     * Method descriptor.
     */
    private final String descriptor;

    /**
     * Method signature.
     */
    private final String signature;

    /**
     * Method exceptions.
     */
    private final String[] exceptions;

    /**
     * Method max stack and locals.
     */
    private final AtomicReference<DirectivesMaxs> max;

    /**
     * Method parameters.
     */
    private final DirectivesMethodParams params;

    /**
     * Constructor.
     */
    public DirectivesMethodProperties() {
        this(Opcodes.ACC_PUBLIC, "()V", "");
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodProperties(
        final int access,
        final String descriptor,
        final String signature,
        final String... exceptions
    ) {
        this(
            access,
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
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @param max Max stack and locals.
     * @param params Method parameters.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodProperties(
        final int access,
        final String descriptor,
        final String signature,
        final String[] exceptions,
        final DirectivesMaxs max,
        final DirectivesMethodParams params
    ) {
        this(access, descriptor, signature, exceptions, max, params, new Format());
    }

    /**
     * Constructor.
     * @param access Access modifiers.
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
        final String descriptor,
        final String signature,
        final String[] exceptions,
        final DirectivesMaxs max,
        final DirectivesMethodParams params,
        final Format format
    ) {
        this.access = access;
        this.descriptor = Optional.ofNullable(descriptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.exceptions = Optional.ofNullable(exceptions).orElse(new String[0]).clone();
        this.max = new AtomicReference<>(max);
        this.params = params;
        this.format = format;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives dirs = new Directives()
            .append(new DirectivesValue(this.format, "access", this.access))
            .append(new DirectivesValue(this.format, "descriptor", this.descriptor))
            .append(new DirectivesValue(this.format, "signature", this.signature))
            .append(
                new DirectivesValues(this.format, "exceptions", (Object[]) this.exceptions)
            )
            .append(this.max.get())
            .append(this.params);
        if (this.format.modifiers()) {
            dirs.append(new DirectivesMethodModifiers(this.format, this.access));
        }
        return dirs.iterator();
    }
}
