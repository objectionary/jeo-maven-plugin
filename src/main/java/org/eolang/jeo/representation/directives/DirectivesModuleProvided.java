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
 * Directives for module provided.
 * @since 0.15.0
 */
public final class DirectivesModuleProvided implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * The internal name of the service.
     */
    private final String service;

    /**
     * The internal names of the implementations of the service (there is at least one provider).
     */
    private final List<String> providers;

    /**
     * Constructor.
     * @param format Directive format.
     * @param service The internal name of the service.
     * @param providers The internal names of the implementations of the service.
     */
    public DirectivesModuleProvided(
        final Format format,
        final String service,
        final List<String> providers
    ) {
        this.format = format;
        this.service = service;
        this.providers = providers;
    }

    @Override
    public Iterator<Directive> iterator() {
        final AtomicInteger counter = new AtomicInteger(0);
        return new DirectivesJeoObject(
            "provided",
            "provided",
            new DirectivesValue(this.format, "service", this.service),
            new DirectivesSeq(
                "providers",
                this.providers.stream()
                    .map(
                        provider -> new DirectivesValue(
                            this.format,
                            String.format("p%d", counter.getAndIncrement()),
                            provider
                        )
                    ).collect(Collectors.toList())
            )
        ).iterator();
    }
}
