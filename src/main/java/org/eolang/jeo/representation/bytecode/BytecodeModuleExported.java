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
import org.objectweb.asm.ModuleVisitor;

/**
 * A node that represents an exported package with its name and the module that can access to it.
 * @since 0.15.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeModuleExported {

    /**
     * The internal name of the exported package.
     */
    private final String pckg;

    /**
     * The access flags.
     * Valid values are {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     */
    private final int access;

    /**
     * The list of modules that can access this exported package.
     * Specified with fully qualified names using dots.
     */
    private final List<String> modules;

    /**
     * Constructor.
     * @param pckg The internal name of the exported package
     * @param access The access flags
     * @param modules The list of modules that can access this exported package
     */
    public BytecodeModuleExported(final String pckg, final int access, final List<String> modules) {
        this.pckg = pckg;
        this.access = access;
        this.modules = Optional.ofNullable(modules).orElse(Collections.emptyList());
    }

    /**
     * Writes this exported package to the given module visitor.
     * @param module Uhe module visitor
     */
    public void write(final ModuleVisitor module) {
        module.visitExport(this.pckg, this.access, this.modules.toArray(new String[0]));
    }
}
