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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.eolang.jeo.representation.MethodName;
import org.eolang.jeo.representation.Signature;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Method.
 * @since 0.1
 * @todo #534:90min Refactor DirectivesMethod class to simplify the code.
 *  Currently, the class has a lot of methods and fields. It's kind of hard to understand
 *  what this class does. We need to refactor it to make it more readable and understandable.
 *  Moreover, in many places this class uses null checks, which is not good.
 */
public final class DirectivesMethod implements Iterable<Directive> {

    /**
     * Method name.
     */
    private final Signature name;

    /**
     * Method properties.
     */
    private final DirectivesMethodProperties properties;

    /**
     * Method instructions.
     */
    private final List<Iterable<Directive>> instructions;

    /**
     * Method exceptions.
     */
    private final List<Iterable<Directive>> exceptions;

    /**
     * Method annotations.
     */
    private final DirectivesAnnotations annotations;

    /**
     * Default value.
     */
    private final List<Iterable<Directive>> dvalue;

    /**
     * Opcodes counting.
     */
    private final boolean counting;

    /**
     * Constructor.
     * @param name Method name
     */
    public DirectivesMethod(final String name) {
        this(name, new DirectivesMethodProperties());
    }

    /**
     * Constructor.
     * @param name Method name
     * @param properties Method properties
     */
    public DirectivesMethod(final String name, final DirectivesMethodProperties properties) {
        this(name, true, properties);
    }

    /**
     * Constructor.
     * @param name Method name
     * @param counting Opcodes counting
     * @param properties Method properties
     */
    public DirectivesMethod(
        final String name,
        final boolean counting,
        final DirectivesMethodProperties properties
    ) {
        this(
            new Signature(new MethodName(name).xmir(), properties.descr()),
            properties,
            new ArrayList<>(0),
            new ArrayList<>(0),
            new DirectivesAnnotations(),
            new ArrayList<>(0),
            counting
        );
    }

    /**
     * Constructor.
     * @param name Method name
     * @param properties Method properties
     * @param instructions Method instructions
     * @param exceptions Method exceptions
     * @param annotations Method annotations
     * @param dvalue Annotation default value
     * @param counting Opcodes counting
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public DirectivesMethod(
        final Signature name,
        final DirectivesMethodProperties properties,
        final List<Iterable<Directive>> instructions,
        final List<Iterable<Directive>> exceptions,
        final DirectivesAnnotations annotations,
        final List<Iterable<Directive>> dvalue,
        final boolean counting
    ) {
        this.name = name;
        this.properties = properties;
        this.instructions = instructions;
        this.exceptions = exceptions;
        this.annotations = annotations;
        this.dvalue = dvalue;
        this.counting = counting;
    }

    /**
     * Add opcode to the directives.
     * @param opcode Opcode
     * @param operands Operands
     * @return This object
     */
    public DirectivesMethod withOpcode(final int opcode, final Object... operands) {
        this.instructions.add(new DirectivesInstruction(opcode, this.counting, operands));
        return this;
    }

    /**
     * Add annotation to the directives.
     * @param annotation Annotation directives.
     * @return This object.
     */
    public DirectivesMethod withAnnotation(final DirectivesAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives();
        directives.add("o")
            .attr("abstract", "")
            .attr("name", this.name.encoded())
            .append(this.properties)
            .append(this.annotations)
            .add("o")
            .attr("base", "seq")
            .attr("name", "@")
            .add("o")
            .attr("base", "tuple")
            .attr("name", "instructions")
            .attr("line", new Random().nextInt(Integer.MAX_VALUE))
            .attr("star", "");
        this.instructions.forEach(directives::append);
        directives.up();
        directives.up();
        if (!this.exceptions.isEmpty()) {
            directives.add("o")
                .attr("base", "tuple")
                .attr("star", "")
                .attr("name", String.format("trycatchblocks-%s", this.name.name()));
            this.exceptions.forEach(directives::append);
            directives.up();
        }
        this.dvalue.forEach(directives::append);
        directives.up();
        return directives.iterator();
    }
}
