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
 * Directives for module opened.
 * @since 0.15.0
 */
public final class DirectivesModuleOpened implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

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
     * @param format Directive format
     * @param pckg The internal name of the opened package
     * @param access The access flag of the opened package
     * @param modules The fully qualified names (using dots) of the modules
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesModuleOpened(
        final Format format,
        final String pckg,
        final int access,
        final List<String> modules
    ) {
        this.format = format;
        this.pckg = pckg;
        this.access = access;
        this.modules = modules;
    }

    @Override
    public Iterator<Directive> iterator() {
        final AtomicInteger counter = new AtomicInteger(0);
        return new DirectivesJeoObject(
            "opened",
            "opened",
            new DirectivesValue(this.format, "package", this.pckg),
            new DirectivesValue(this.format, "access", this.access),
            new DirectivesSeq(
                "modules",
                this.modules.stream()
                    .map(
                        module -> new DirectivesValue(
                            this.format,
                            String.format("m%d", counter.getAndIncrement()),
                            module
                        )
                    ).collect(Collectors.toList())
            )
        ).iterator();
    }
}
