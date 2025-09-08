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
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Bytecode module.
 * @since 0.14.0
 */
@ToString
@EqualsAndHashCode
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
     * @checkstyle ParameterNumber (10 lines)
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
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
        this.packages = Optional.ofNullable(packages).orElse(Collections.emptyList());
        this.requires = requires;
        this.exports = exports;
        this.opens = opens;
        this.provides = provides;
        this.uses = Optional.ofNullable(uses).orElse(Collections.emptyList());
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
        this.packages.forEach(module::visitPackage);
        this.requires.forEach(req -> req.write(module));
        this.exports.forEach(exp -> exp.write(module));
        this.opens.forEach(opn -> opn.write(module));
        this.provides.forEach(prov -> prov.write(module));
        this.uses.forEach(module::visitUse);
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        throw new UnsupportedOperationException("Module attribute is not applicable to methods");
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new Directives();
    }
}
