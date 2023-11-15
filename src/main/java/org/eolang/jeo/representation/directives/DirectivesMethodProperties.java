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
import org.objectweb.asm.Type;
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
final class DirectivesMethodProperties implements Iterable<Directive> {

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
     * Constructor.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param signature Method signature.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    DirectivesMethodProperties(
        final int access,
        final String descriptor,
        final String signature,
        final String... exceptions
    ) {
        this.access = access;
        this.descriptor = Optional.ofNullable(descriptor).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.exceptions = Optional.ofNullable(exceptions).orElse(new String[0]).clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .append(new DirectivesData("access", this.access))
            .append(new DirectivesData("descriptor", this.descriptor))
            .append(new DirectivesData("signature", this.signature))
            .append(new DirectivesTuple("exceptions", this.exceptions))
            .append(new DirectivesMethodParams(this.descriptor))
            .iterator();
    }

    /**
     * Method arguments.
     * @return Arguments.
     */
    private Directives arguments() {
        final Directives directives = new Directives();
        final Type[] arguments = Type.getArgumentTypes(this.descriptor);
        for (int index = 0; index < arguments.length; ++index) {
            directives.add("o")
                .attr("abstract", "")
                .attr("name", String.format("arg__%s__%d", arguments[index], index))
                .up();
        }
        return directives;
    }
}
