/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesModule;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.xembly.Directive;

/**
 * Bytecode module.
 *
 * @since 0.14.0
 */
public final class BytecodeModule implements BytecodeAttribute {

    /** The fully qualified name (using dots) of this module. */
    private final String name;

    /**
     * The module's access flags.
     * Among {@code ACC_OPEN}, {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     */
    private final int access;

    /**
     * The version of this module.
     * */
    private final String version;

    /**
     * The internal name of the main class of this module.
     */
    private final String main;

    /**
     * The internal name of the packages declared by this module.
     */
    private final List<String> packages;

    /**
     * The dependencies of this module.
     */
    private final List<BytecodeModuleRequired> requires;

    /**
     * The packages exported by this module. May be {@literal null}.
     */
    private final List<BytecodeModuleExported> exports;

    /**
     * The packages opened by this module. May be {@literal null}.
     */
    private final List<BytecodeModuleOpened> opens;

    /**
     *  The services provided by this module.
     *  */
    private final List<BytecodeModuleProvided> provides;

    /**
     * The internal names of the services used by this module.
     */
    private final List<String> uses;

    /**
     * Constructor.
     *
     * @param name Module name
     * @param access Module access flags
     * @param version Module version
     * @param main Module main class
     * @param packages Module packages
     * @param requires Module dependencies
     * @param exports Module exports
     * @param opens Module opens
     * @param provides Module provides
     * @param uses Module uses
     */
    public BytecodeModule(
        final String name,
        final int access,
        final String version,
        final String main,
        final List<String> packages,
        final List<BytecodeModuleRequired> requires,
        final List<BytecodeModuleExported> exports,
        final List<BytecodeModuleOpened> opens,
        final List<BytecodeModuleProvided> provides,
        final List<String> uses
    ) {
        this.name = name;
        this.access = access;
        this.version = version;
        this.main = main;
        this.packages = packages == null ? Collections.emptyList() : packages;
        this.requires = requires == null ? Collections.emptyList() : requires;
        this.exports = exports == null ? Collections.emptyList() : exports;
        this.opens = opens == null ? Collections.emptyList() : opens;
        this.provides = provides == null ? Collections.emptyList() : provides;
        this.uses = uses == null ? Collections.emptyList() : uses;
    }

    @Override
    public void write(final ClassVisitor visitor) {
        final ModuleVisitor module = visitor.visitModule(
            this.name,
            this.access,
            this.version
        );
        if (this.main != null) {
            module.visitMainClass(this.main);
        }
        this.packages().forEach(module::visitPackage);
        this.requires.forEach(req -> req.write(module));
        this.exports.forEach(exp -> exp.write(module));
        this.opens.forEach(opn -> opn.write(module));
        this.provides.forEach(prov -> prov.write(module));
        this.uses().forEach(module::visitUse);
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        throw new UnsupportedOperationException("Module attribute is not applicable to methods");
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesModule(
            format,
            this.name,
            this.access,
            this.version,
            this.main,
            this.packages(),
            this.requires.stream()
                .map(req -> req.directives(format))
                .collect(Collectors.toList()),
            this.exports.stream()
                .map(exp -> exp.directives(format))
                .collect(Collectors.toList()),
            this.opens.stream()
                .map(opn -> opn.directives(format))
                .collect(Collectors.toList()),
            this.provides.stream()
                .map(prov -> prov.directives(format))
                .collect(Collectors.toList()),
            this.uses()
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeModule) {
            final BytecodeModule module = (BytecodeModule) other;
            result = this.sameHeader(module) && this.sameBody(module);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.name, this.access, this.version, this.main, this.packages(),
            this.requires, this.exports, this.opens, this.provides, this.uses()
        );
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeModule(name=%s, access=%d, version=%s, main=%s, packages=%s, requires=%s, exports=%s, opens=%s, provides=%s, uses=%s)",
            this.name, this.access, this.version, this.main, this.packages(),
            this.requires, this.exports, this.opens, this.provides, this.uses()
        );
    }

    private boolean sameHeader(final BytecodeModule module) {
        return this.access == module.access
            && Objects.equals(this.name, module.name)
            && Objects.equals(this.version, module.version)
            && Objects.equals(this.main, module.main);
    }

    private boolean sameBody(final BytecodeModule module) {
        return this.samePackaging(module) && this.sameUsage(module);
    }

    private boolean samePackaging(final BytecodeModule module) {
        return Objects.equals(this.packages(), module.packages())
            && Objects.equals(this.requires, module.requires)
            && Objects.equals(this.exports, module.exports);
    }

    private boolean sameUsage(final BytecodeModule module) {
        return Objects.equals(this.opens, module.opens)
            && Objects.equals(this.provides, module.provides)
            && Objects.equals(this.uses(), module.uses());
    }

    private List<String> packages() {
        final List<String> result;
        if (this.packages == null) {
            result = Collections.emptyList();
        } else {
            result = this.packages;
        }
        return result;
    }

    private List<String> uses() {
        final List<String> result;
        if (this.uses == null) {
            result = Collections.emptyList();
        } else {
            result = this.uses;
        }
        return result;
    }
}
