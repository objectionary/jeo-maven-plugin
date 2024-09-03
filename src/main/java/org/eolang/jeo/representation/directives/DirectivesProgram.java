/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.eolang.jeo.representation.ClassName;
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
     * It is ASM framework limitation.
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
     * @param name Classname.
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
     * @param clazz Top-level class.
     * @return The same instance.
     */
    public DirectivesProgram withClass(final DirectivesMetas metas, final DirectivesClass clazz) {
        this.metas.set(metas);
        this.klass.set(clazz);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        final Directives directives = new Directives();
        directives.add("program")
            .attr("name", this.metas.get().name())
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

    /**
     * Retrieve top-level class.
     * @return Top-level class.
     */
    public DirectivesClass top() {
        if (Objects.isNull(this.klass.get())) {
            throw new IllegalStateException(
                String.format("Class is not initialized here %s", this)
            );
        }
        return this.klass.get();
    }
}
