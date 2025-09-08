/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.ModuleVisitor;

/**
 * A node that represents a required module with its name and access of a module descriptor.
 * @since 0.15.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeModuleRequired {

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
     * @param module The fully qualified name (using dots) of the dependence
     * @param access The access flag of the dependence
     * @param version The module version at compile time
     */
    public BytecodeModuleRequired(final String module, final int access, final String version) {
        this.module = module;
        this.access = access;
        this.version = version;
    }

    /**
     * Writes this required module to the given module visitor.
     * @param visitor The module visitor
     */
    public void write(final ModuleVisitor visitor) {
        visitor.visitRequire(this.module, this.access, this.version);
    }
}
