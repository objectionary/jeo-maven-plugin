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
public class DirectivesProgram implements Iterable<Directive> {

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
     * Top-level class name.
     * This field uses atomic reference because the field can't be initialized in the constructor.
     * It is ASM framework limitation.
     */
    private final AtomicReference<ClassName> classname;

    /**
     * Simple constructor with empty listing.
     */
    DirectivesProgram() {
        this("");
    }

    /**
     * Constructor.
     * @param listing Program listing.
     */
    DirectivesProgram(final String listing) {
        this(listing, new AtomicReference<>(), new AtomicReference<>());
    }

    /**
     * Constructor.
     * @param listing Program listing.
     * @param klass Class.
     * @param classname Classname.
     */
    private DirectivesProgram(
        final String listing,
        final AtomicReference<DirectivesClass> klass,
        final AtomicReference<ClassName> classname
    ) {
        this.listing = listing;
        this.klass = klass;
        this.classname = classname;
    }

    /**
     * Append top-level class.
     * @param name Class Name.
     * @param klass Top-level class.
     * @return The same instance.
     */
    DirectivesProgram withClass(ClassName name, DirectivesClass klass) {
        this.classname.set(name);
        this.klass.set(klass);
        return this;
    }

    /**
     * Retrieve top-level class.
     * @return Top-level class.
     */
    DirectivesClass top() {
        if (Objects.isNull(this.klass.get())) {
            throw new IllegalStateException(
                String.format("Class is not initialized here %s", this)
            );
        }
        return this.klass.get();
    }

    @Override
    public Iterator<Directive> iterator() {
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        Directives directives = new Directives();
        directives.add("program")
            .attr("name", this.classname.get().name())
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
            .append(new DirectivesMetas(this.classname.get()))
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        directives.append(this.klass.get());
        directives.up();
        return directives.iterator();
    }
}
