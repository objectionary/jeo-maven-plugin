/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.manifests.Manifests;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Program representation as Xembly directives.
 * @since 0.1
 */
public final class DirectivesObject implements Iterable<Directive> {

    /**
     * Program listing.
     */
    private final String listing;

    /**
     * How much time it took to transform bytecode to directives.
     * Approximate value.
     */
    private final long milliseconds;

    /**
     * Top-level class.
     * This field uses atomic reference because the field can't be initialized in the constructor.
     * It is the Java ASM framework limitation.
     */
    private final DirectivesClass klass;

    /**
     * Metas.
     */
    private final DirectivesMetas metas;

    /**
     * Constructor.
     * @param klass Top-level class.
     * @param metas Metas.
     */
    public DirectivesObject(final DirectivesClass klass, final DirectivesMetas metas) {
        this("", klass, metas);
    }

    /**
     * Constructor.
     * @param code Program listing.
     * @param clazz Class.
     * @param name Metas.
     */
    public DirectivesObject(
        final String code,
        final DirectivesClass clazz,
        final DirectivesMetas name
    ) {
        this(code, 0L, clazz, name);
    }

    /**
     * Constructor.
     * @param listing Listing.
     * @param milliseconds Milliseconds.
     * @param klass Class.
     * @param metas Metas.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesObject(
        final String listing,
        final long milliseconds,
        final DirectivesClass klass,
        final DirectivesMetas metas
    ) {
        this.listing = listing;
        this.milliseconds = milliseconds;
        this.klass = klass;
        this.metas = metas;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        final Directives directives = new Directives();
        directives.add("object")
            .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
            .attr("version", Manifests.read("JEO-Version"))
            .attr("revision", Manifests.read("JEO-Revision"))
            .attr("dob", Manifests.read("JEO-Dob"))
            .attr("time", now)
            .attr("xsi:noNamespaceSchemaLocation", "https://www.eolang.org/xsd/XMIR-0.56.2.xsd")
            .add("listing")
            .set(this.listing)
            .up()
            .append(this.metas)
            .attr("ms", this.milliseconds);
        directives.append(this.klass);
        directives.up();
        return directives.iterator();
    }
}
