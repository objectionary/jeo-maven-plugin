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
    public DirectivesProgram(final DirectivesClass klass, final DirectivesMetas metas) {
        this("", klass, metas);
    }

    /**
     * Constructor.
     * @param code Program listing.
     * @param clazz Class.
     * @param name Metas.
     */
    public DirectivesProgram(
        final String code,
        final DirectivesClass clazz,
        final DirectivesMetas name
    ) {
        this.listing = code;
        this.klass = clazz;
        this.metas = name;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        final Directives directives = new Directives();
        directives.add("program")
            .attr("name", this.metas.className().name())
            .attr("version", Manifests.read("JEO-Version"))
            .attr("revision", Manifests.read("JEO-Revision"))
            .attr("dob", Manifests.read("JEO-Dob"))
            .attr("time", now)
            .add("listing")
            .set(this.listing)
            .up()
            .add("errors").up()
            .add("sheets").up()
            .add("license").up()
            .append(this.metas)
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        directives.append(this.klass);
        directives.up();
        return directives.iterator();
    }
}
