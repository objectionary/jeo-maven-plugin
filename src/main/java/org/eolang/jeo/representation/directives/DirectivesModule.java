/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.xembly.Directive;

/**
 * Module of directives.
 * Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeModule}
 * @since 0.15.0
 */
public final class DirectivesModule implements Iterable<Directive> {

    /**
     * Directive format.
     */
    private final Format format;

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
    private final List<String> pckgs;

    /**
     * The dependencies of this module.
     */
    private final List<DirectivesModuleRequired> requires;

    /**
     * The packages exported by this module. May be {@literal null}.
     */
    private final List<DirectivesModuleExported> exports;

    /**
     * The packages opened by this module. May be {@literal null}.
     */
    private final List<DirectivesModuleOpened> opens;

    /**
     *  The services provided by this module.
     *  */
    private final List<DirectivesModuleProvided> provides;

    /**
     * The internal names of the services used by this module.
     */
    private final List<String> uses;

    /**
     * Constructor.
     * @param format Directive format
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
     * @checkstyle ParameterNumberCheck (15 lines)
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public DirectivesModule(
        final Format format,
        final String name,
        final int access,
        final String version,
        final String main,
        final List<String> packages,
        final List<DirectivesModuleRequired> requires,
        final List<DirectivesModuleExported> exports,
        final List<DirectivesModuleOpened> opens,
        final List<DirectivesModuleProvided> provides,
        final List<String> uses
    ) {
        this.format = format;
        this.name = name;
        this.access = access;
        this.version = version;
        this.main = main;
        this.pckgs = packages;
        this.requires = requires;
        this.exports = exports;
        this.opens = opens;
        this.provides = provides;
        this.uses = uses;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "module",
            "module",
            new DirectivesValue(this.format, "name", this.name),
            new DirectivesValue(this.format, "access", this.access),
            new DirectivesValue(this.format, "version", this.version),
            new DirectivesValue(this.format, "main", this.main),
            new DirectivesSeq("packages", this.packages()),
            new DirectivesSeq("requires", this.requires),
            new DirectivesSeq("exports", this.exports),
            new DirectivesSeq("opens", this.opens),
            new DirectivesSeq("provides", this.provides),
            new DirectivesSeq("uses", this.use())
        ).iterator();
    }

    /**
     * Packages as directives.
     * @return List of directives
     */
    private List<DirectivesValue> packages() {
        final AtomicInteger counter = new AtomicInteger(0);
        return this.pckgs.stream().map(
            pkg -> new DirectivesValue(
                this.format, String.format("p%d", counter.getAndIncrement()),
                pkg
            )
        ).collect(Collectors.toList());
    }

    /**
     * Uses as directives.
     * @return List of directives
     */
    private List<DirectivesValue> use() {
        final AtomicInteger counter = new AtomicInteger(0);
        return this.uses.stream().map(
            use -> new DirectivesValue(
                this.format, String.format("u%d", counter.getAndIncrement()),
                use
            )
        ).collect(Collectors.toList());
    }
}
