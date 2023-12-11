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
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xml directives for label entry in byrecode.
 * @since 0.1
 */
public final class DirectivesLabel implements Iterable<Directive> {

    /**
     * Bytecode label.
     */
    private final Label label;

    /**
     * Label name.
     */
    private final String name;

    /**
     * All labels.
     */
    private final AllLabels all;

    /**
     * Constructor.
     * @param label Bytecode label.
     */
    DirectivesLabel(final Label label) {
        this(label, new AllLabels());
    }


    /**
     * Constructor.
     * @param label Bytecode label.
     * @param all All labels.
     */
    private DirectivesLabel(final Label label, final AllLabels all) {
        this(label, "", all);
    }

    /**
     * Constructor.
     * @param label Bytecode label.
     * @param name Label name.
     */
    public DirectivesLabel(final Label label, final String name) {
        this(label, name, new AllLabels());
    }

    /**
     * Constructor.
     * @param label Bytecode label.
     * @param name Label name.
     * @param all All labels.
     */
    public DirectivesLabel(
        final Label label,
        final String name,
        final AllLabels all
    ) {
        this.label = label;
        this.name = name;
        this.all = all;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String uid = this.all.uid(this.label);
        final Directives directives = new Directives().add("o")
            .attr("base", "label");
        if (!this.name.isEmpty()) {
            directives.attr("name", this.name);
        }
        return directives
            .append(new DirectivesData(uid))
            .up()
            .iterator();
    }
}
