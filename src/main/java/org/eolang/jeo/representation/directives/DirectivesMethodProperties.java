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

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Method properties as Xembly directives.
 * @since 0.1
 */
public final class DirectivesMethodProperties implements Iterable<Directive> {

    private static final Random RANDOM = new SecureRandom();

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
        this(
            access,
            descriptor,
            signature,
            exceptions,
            new DirectivesMaxs(),
            new DirectivesMethodParams()
        );
    }

    /**
     * Constructor.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @param max Max stack and locals.
     * @param params Method parameters.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodProperties(
        final int access,
        final String descriptor,
        final String signature,
        final String[] exceptions,
        final DirectivesMaxs max,
        final DirectivesMethodParams params
    ) {
        this.access = access;
        this.descriptor = Optional.ofNullable(descriptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.exceptions = Optional.ofNullable(exceptions).orElse(new String[0]).clone();
        this.max = new AtomicReference<>(max);
        this.params = params;
    }

    @Override
    public Iterator<Directive> iterator() {
        final int number = Math.abs(DirectivesMethodProperties.RANDOM.nextInt());
        return new Directives()
            .append(new DirectivesValue(String.format("access-%d", number), this.access))
            .append(new DirectivesValue(String.format("descriptor-%d", number), this.descriptor))
            .append(new DirectivesValue(String.format("signature-%d", number), this.signature))
            .append(new DirectivesValues(String.format("exceptions-%d", number), this.exceptions))
            .append(this.max.get())
            .append(this.params)
            .iterator();
    }

    /**
     * Method descriptor.
     * @return Descriptor.
     */
    String descr() {
        return this.descriptor;
    }
}
