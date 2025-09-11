/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesModuleProvided;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ModuleVisitor;

/**
 * A node that represents a service and its implementation provided by the current module.
 * @since 0.15.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeModuleProvided {

    /**
     * The internal name of the service.
     */
    private final String service;

    /**
     * The internal names of the implementations of the service (there is at least one provider).
     */
    private final List<String> providers;

    /**
     * Constructor.
     * @param service The internal name of the service
     * @param providers The internal names of the implementations of the service
     */
    public BytecodeModuleProvided(final String service, final List<String> providers) {
        this.service = service;
        this.providers = Optional.ofNullable(providers).orElse(Collections.emptyList());
    }

    /**
     * Writes this provided service to the given module visitor.
     * @param module The module visitor
     */
    public void write(final ModuleVisitor module) {
        module.visitProvide(this.service, this.providers.toArray(new String[0]));
    }

    /**
     * Converts this provided service to directives.
     * @param format Directive format
     * @return Directives for this provided service
     */
    public DirectivesModuleProvided directives(final Format format) {
        return new DirectivesModuleProvided(format, this.service, this.providers);
    }
}
