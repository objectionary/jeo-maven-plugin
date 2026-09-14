/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eolang.jeo.representation.directives.DirectivesModuleExported;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ModuleVisitor;

/**
 * A node that represents an exported package with its name and the module that can access to it.
 *
 * @since 0.15.0
 */
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
     *
     * @param pckg The internal name of the exported package
     * @param access The access flags
     * @param modules The list of modules that can access this exported package
     */
    public BytecodeModuleExported(final String pckg, final int access, final List<String> modules) {
        this.pckg = pckg;
        this.access = access;
        this.modules = modules;
    }

    /**
     * Writes this exported package to the given module visitor.
     *
     * @param module Uhe module visitor
     */
    public void write(final ModuleVisitor module) {
        module.visitExport(this.pckg, this.access, this.accessible().toArray(new String[0]));
    }

    /**
     * Converts this exported package to directives.
     *
     * @param format Directive format
     * @return Directives
     */
    public DirectivesModuleExported directives(final Format format) {
        return new DirectivesModuleExported(format, this.pckg, this.access, this.accessible());
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeModuleExported) {
            final BytecodeModuleExported exported = (BytecodeModuleExported) other;
            result = this.access == exported.access
                && Objects.equals(this.pckg, exported.pckg)
                && Objects.equals(this.accessible(), exported.accessible());
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pckg, this.access, this.accessible());
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeModuleExported(pckg=%s, access=%d, modules=%s)",
            this.pckg, this.access, this.accessible()
        );
    }

    private List<String> accessible() {
        final List<String> result;
        if (this.modules == null) {
            result = Collections.emptyList();
        } else {
            result = this.modules;
        }
        return result;
    }
}
