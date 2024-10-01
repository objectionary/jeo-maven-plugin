/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.directives;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Program representation as Xembly directives.
 * @since 0.1
 */
public final class DirectivesProgram implements Iterable<Directive> {

    /**
     * Program listing.
     */
    private final String listing;

    /**
     * Top-level class.
     * This field uses atomic reference because the field can't be initialized in the constructor.
     * It is the Java ASM framework limitation.
     */
    private final AtomicReference<DirectivesClass> klass;

    /**
     * Metas.
     */
    private final AtomicReference<DirectivesMetas> metas;

    /**
     * Simple constructor with empty listing.
     */
    public DirectivesProgram() {
        this("");
    }

    /**
     * Constructor.
     * @param code Program listing.
     */
    public DirectivesProgram(final String code) {
        this(code, new AtomicReference<>(), new AtomicReference<>());
    }

    /**
     * Constructor.
     * @param code Program listing.
     * @param clazz Class.
     * @param metas Metas.
     */
    public DirectivesProgram(
        final String code, final DirectivesClass clazz, final DirectivesMetas metas
    ) {
        this(code, new AtomicReference<>(clazz), new AtomicReference<>(metas));
    }

    /**
     * Constructor.
     * @param code Program listing.
     * @param clazz Class.
     * @param name Metas.
     */
    public DirectivesProgram(
        final String code,
        final AtomicReference<DirectivesClass> clazz,
        final AtomicReference<DirectivesMetas> name
    ) {
        this.listing = code;
        this.klass = clazz;
        this.metas = name;
    }

    /**
     * Append top-level class.
     * @param meta Metas.
     * @param clazz Top-level class.
     * @return The same instance.
     * @todo!!!!
     */
    public DirectivesProgram withClass(final DirectivesMetas meta, final DirectivesClass clazz) {
        this.metas.set(meta);
        this.klass.set(clazz);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        final Directives directives = new Directives();
        directives.add("program")
            .attr("name", this.metas.get().className().name())
            .attr("version", "0.0.0")
            .attr("revision", "0.0.0")
            .attr("dob", now)
            .attr("time", now)
            .add("listing")
            .set(this.listing)
            .up()
            .add("errors").up()
            .add("sheets").up()
            .add("license").up()
            .append(this.metas.get())
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        directives.append(this.klass.get());
        directives.up();
        return directives.iterator();
    }
}
