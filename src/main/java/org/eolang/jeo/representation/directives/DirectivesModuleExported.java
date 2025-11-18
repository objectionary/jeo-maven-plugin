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
 * Directives for module exported.
 * JVM Specification:
 * {@code
 *     {   u2 exports_index; {@link #pckg}
 *         u2 exports_flags; {@link #access}
 *         u2 exports_to_count; {@link #modules.size()}
 *         u2 exports_to_index[exports_to_count]; {@link #modules}
 *     }}
 * @since 0.15.0
 */
public final class DirectivesModuleExported implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Exported package.
     */
    private final String pckg;

    /**
     * Access flags, among {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     */
    private final int access;

    /**
     * Modules to which the package is exported.
     */
    private final List<String> modules;

    /**
     * Constructor.
     * @param format Directive format
     * @param pckg Exported package
     * @param access Access flags
     * @param modules Modules to which the package is exported
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesModuleExported(
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
            "exported",
            "exported",
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
