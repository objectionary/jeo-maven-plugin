/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eolang.jeo.representation.directives.DirectivesModuleOpened;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ModuleVisitor;

/**
 * A node that represents an opened package with its name and the module that can access it.
 *
 * @since 0.15.0
 */
public final class BytecodeModuleOpened {

    /**
     * The internal name of the opened package.
     */
    private final String pckg;

    /**
     * The access flag of the opened package.
     * Valid values are among {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     */
    private final int access;

    /**
     * The fully qualified names (using dots) of the modules.
     */
    private final List<String> modules;

    /**
     * Constructor.
     *
     * @param pckg The internal name of the opened package
     * @param access The access flag of the opened package
     * @param modules The fully qualified names (using dots) of the modules
     */
    public BytecodeModuleOpened(final String pckg, final int access, final List<String> modules) {
        this.pckg = pckg;
        this.access = access;
        this.modules = modules;
    }

    /**
     * Writes this opened package to the given module visitor.
     *
     * @param module The module visitor
     */
    public void write(final ModuleVisitor module) {
        module.visitOpen(this.pckg, this.access, this.accessible().toArray(new String[0]));
    }

    /**
     * Converts this opened package to directives.
     *
     * @param format Directive format
     * @return Directives
     */
    public DirectivesModuleOpened directives(final Format format) {
        return new DirectivesModuleOpened(
            format,
            this.pckg,
            this.access,
            this.accessible()
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeModuleOpened) {
            final BytecodeModuleOpened opened = (BytecodeModuleOpened) other;
            result = this.access == opened.access
                && Objects.equals(this.pckg, opened.pckg)
                && Objects.equals(this.accessible(), opened.accessible());
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
            "BytecodeModuleOpened(pckg=%s, access=%d, modules=%s)",
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
