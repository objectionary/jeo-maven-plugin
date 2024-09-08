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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Method properties as Xembly directives.
 * @since 0.1.0
 * @todo #91:60min Move all Directives* classes to a separate package.
 *  Right now they are in the same package as Xml* classes.
 *  We need to move them to a separate package. It will make it possible to hide
 *  some classes and probably remove prefixes like Directives*.
 */
public final class DirectivesMethodProperties implements Iterable<Directive> {

    /**
     * Method access modifiers.
     */
    private final int access;

    /**
     * Method descriptor.
     */
    private final String descriptor;

    /**
     * Method signature.
     */
    private final String signature;

    /**
     * Method exceptions.
     */
    private final String[] exceptions;

    /**
     * Method max stack and locals.
     */
    private final AtomicReference<DirectivesMaxs> max;

    /**
     * Method parameters.
     */
    private final DirectivesMethodParams params;

    /**
     * Constructor.
     */
    public DirectivesMethodProperties() {
        this(Opcodes.ACC_PUBLIC, "()V", "");
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodProperties(
        final int access,
        final String descriptor,
        final String signature,
        final String... exceptions
    ) {
        this.access = access;
        this.descriptor = Optional.ofNullable(descriptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.exceptions = Optional.ofNullable(exceptions).orElse(new String[0]).clone();
        this.max = new AtomicReference<>(new DirectivesMaxs());
        this.params = new DirectivesMethodParams(this.descriptor);
    }

    /**
     * Set parameter annotation.
     * @param index Parameter index.
     * @param annotation Annotation.
     */
    public void paramAnnotation(final int index, final DirectivesAnnotation annotation) {
        this.params.annotation(index, annotation);
    }

    /**
     * Set max stack and locals.
     * @param stack Max stack size.
     * @param locals Max locals size.
     */
    public void maxs(final int stack, final int locals) {
        this.max.set(new DirectivesMaxs(stack, locals));
    }

    /**
     * Method descriptor.
     * @return Descriptor.
     */
    public String descr() {
        return this.descriptor;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .append(new DirectivesData("access", this.access))
            .append(new DirectivesData("descriptor", this.descriptor))
            .append(new DirectivesData("signature", this.signature))
            .append(new DirectivesTuple("exceptions", this.exceptions))
            .append(this.max.get())
            .append(this.params)
            .iterator();
    }

}
