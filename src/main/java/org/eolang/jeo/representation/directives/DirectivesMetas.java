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
import org.eolang.jeo.representation.ClassName;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for meta information of a class.
 * @since 0.1
 */
public final class DirectivesMetas implements Iterable<Directive> {

    /**
     * Class name.
     */
    private final ClassName name;

    /**
     * Constructor.
     */
    DirectivesMetas() {
        this(new ClassName());
    }

    /**
     * Constructor.
     * @param classname Class name.
     */
    DirectivesMetas(final ClassName classname) {
        this.name = classname;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String opcode = "org.eolang.jeo.opcode";
        final String label = "org.eolang.jeo.label";
        final String alias = "alias";
        return new Directives()
            .add("metas")
            .add("meta")
            .add("head").set("package").up()
            .add("tail").set(this.name.pckg()).up()
            .add("part").set(this.name.pckg()).up()
            .up()
            .add("meta")
            .add("head").set(alias).up()
            .add("tail").set(opcode).up()
            .add("part").set(opcode).up()
            .up()
            .add("meta")
            .add("head").set(alias).up()
            .add("tail").set(label).up()
            .add("part").set(label).up()
            .up()
            .up()
            .iterator();
    }
}
