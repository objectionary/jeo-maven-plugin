/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Directives for module required.
 * Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeModuleRequired}.
 * @since 0.15.0
 */
public final class DirectivesModuleRequired implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * The fully qualified name (using dots) of the dependence.
     * */
    private final String module;

    /**
     * The access flag of the dependence.
     * Among {@code ACC_TRANSITIVE}, {@code ACC_STATIC_PHASE},
     * {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     */
    private final int access;

    /**
     * The module version at compile time.
     */
    private final String version;

    /**
     * Constructor.
     * @param format Directive format
     * @param module The fully qualified name (using dots) of the dependence
     * @param access The access flag of the dependence
     * @param version The module version at compile time
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesModuleRequired(
        final Format format, final String module, final int access, final String version) {
        this.format = format;
        this.module = module;
        this.access = access;
        this.version = version;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "required",
            "required",
            new DirectivesValue(this.format, "module", this.module),
            new DirectivesValue(this.format, "access", this.access),
            new DirectivesValue(this.format, "version", this.version)
        ).iterator();
    }
}
