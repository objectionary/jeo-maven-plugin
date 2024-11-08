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

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
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

    /**
     * Whether to include "opcodes" import.
     */
    private final AtomicBoolean opcodes;

    /**
     * Whether to include "labels" import.
     */
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

    /**
     * Constructor.
     * @param classname Class name.
     * @param opcodes Whether to include opcodes.
     * @param labels Whether to include labels.
     */
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
     * Class name.
     * @return The class name.
     */
    public ClassName className() {
        return this.name;
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
        final String opcode = "jeo.opcode";
        final String label = "jeo.label";
        final String alias = "alias";
        final Directives opdirs = new Directives();
        if (this.opcodes.get()) {
            opdirs.add("meta")
                .add("head").set(alias).up()
                .add("tail").set(opcode).up()
                .add("part").set(opcode).up()
                .up();
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
            .add("tail").set(this.pckg()).up()
            .add("part").set(this.pckg()).up()
            .up()
            .append(opdirs)
            .append(labeldirs)
            .up()
            .iterator();
    }

    /**
     * Prefixed package name.
     * We intentionally add prefix to the packages, because sometimes they can be really
     * strange, <a href="https://github.com/objectionary/jeo-maven-plugin/issues/779">see</a>
     * @return Prefixed package name.
     */
    private String pckg() {
        final String result;
        final String original = this.name.pckg();
        if (original.isEmpty()) {
            result = "";
        } else {
            result = new PrefixedName(original).encode();
        }
        return result;
    }
}
