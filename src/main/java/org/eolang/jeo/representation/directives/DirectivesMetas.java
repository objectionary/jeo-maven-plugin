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

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eolang.jeo.representation.ClassName;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for meta-information of a class.
 * @since 0.1
 */
public final class DirectivesMetas implements Iterable<Directive> {

    /**
     * Class name.
     */
    private final ClassName name;

    private final AtomicBoolean opcodes;

    private final AtomicBoolean labels;

    /**
     * Constructor.
     */
    public DirectivesMetas() {
        this(new ClassName());
    }

    /**
     * Constructor.
     * @param classname Class name.
     */
    public DirectivesMetas(final ClassName classname) {
        this(classname, false, false);
    }

    public DirectivesMetas(
        final ClassName classname,
        final boolean opcodes,
        final boolean labels
    ) {
        this.name = classname;
        this.opcodes = new AtomicBoolean(opcodes);
        this.labels = new AtomicBoolean(labels);
    }

    /**
     * With opcodes.
     * @return The same instance with opcodes.
     */
    public DirectivesMetas withOpcodes() {
        this.opcodes.set(true);
        return this;
    }

    /**
     * With labels.
     * @return The same instance with labels.
     */
    public DirectivesMetas withLabels() {
        this.labels.set(true);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String opcode = "org.eolang.jeo.opcode";
        final String label = "org.eolang.jeo.label";
        final String alias = "alias";

        final Directives opdirs;
        if (this.opcodes.get()) {
            opdirs = new Directives()
                .add("meta")
                .add("head").set(alias).up()
                .add("tail").set(opcode).up()
                .add("part").set(opcode).up()
                .up();
        } else {
            opdirs = new Directives();
        }
        final Directives labeldirs = new Directives();
        if (this.labels.get()) {
            labeldirs.add("meta")
                .add("head").set(alias).up()
                .add("tail").set(label).up()
                .add("part").set(label).up()
                .up();
        }
        return new Directives()
            .add("metas")
            .add("meta")
            .add("head").set("package").up()
            .add("tail").set(this.name.pckg()).up()
            .add("part").set(this.name.pckg()).up()
            .up()
            .append(opdirs)
            .append(labeldirs)
            .up()
            .iterator();
    }
}
