/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.manifests.Manifests;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Program representation as Xembly directives.
 * @since 0.1
 */
public final class DirectivesObject implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

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
        this(new Format(), klass, metas);
    }

    /**
     * Constructor.
     * @param format Format.
     * @param clazz Class.
     * @param name Metas.
     */
    public DirectivesObject(
        final Format format,
        final DirectivesClass clazz,
        final DirectivesMetas name
    ) {
        this(format, 0L, clazz, name);
    }

    /**
     * Constructor.
     * @param format Format.
     * @param milliseconds Milliseconds.
     * @param klass Class.
     * @param metas Metas.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesObject(
        final Format format,
        final long milliseconds,
        final DirectivesClass klass,
        final DirectivesMetas metas
    ) {
        this.format = format;
        this.milliseconds = milliseconds;
        this.klass = klass;
        this.metas = metas;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String now = DirectivesObject.time(
            this.format, System.getenv("SOURCE_DATE_EPOCH")
        );
        final Directives directives = new Directives()
            .add("object")
            .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
            .attr("version", Manifests.read("JEO-Version"))
            .attr("revision", Manifests.read("JEO-Revision"))
            .attr("dob", Manifests.read("JEO-Dob"))
            .attr("time", now)
            .attr("author", "jeo-maven-plugin")
            .attr("xsi:noNamespaceSchemaLocation", "https://www.eolang.org/xsd/XMIR-0.58.6.xsd");
        final String listing = this.format.listing();
        if (!listing.isEmpty()) {
            directives.add("listing").set(listing).up();
        }
        directives.append(this.metas).attr("ms", this.milliseconds);
        directives.append(this.klass);
        directives.up();
        return directives.iterator();
    }

    /**
     * Timestamp of the generated XMIR.
     * <p>The timestamp is reproducible: when the {@link Format#TIME} property is set it is used
     * as is; otherwise the standard {@code SOURCE_DATE_EPOCH} environment variable (Unix epoch in
     * seconds) is honored; only when neither is available the current wall-clock time is used.</p>
     * @param format Format to read the configured timestamp from.
     * @param epoch Value of the {@code SOURCE_DATE_EPOCH} environment variable, may be null.
     * @return Timestamp in the ISO-8601 instant format.
     */
    static String time(final Format format, final String epoch) {
        final String result;
        final Optional<String> fromepoch = Optional.ofNullable(epoch)
            .filter(value -> !value.isEmpty())
            .map(Long::parseLong)
            .map(Instant::ofEpochSecond)
            .map(Instant::toString);
        if (format.time().isEmpty()) {
            result = fromepoch.orElseGet(DirectivesObject::current);
        } else {
            result = format.time();
        }
        return result;
    }

    /**
     * Current wall-clock timestamp.
     * @return Current timestamp in the ISO-8601 instant format.
     */
    private static String current() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }
}
