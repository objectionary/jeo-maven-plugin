/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesAttribute;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Inner class attribute.
 * @since 0.4
 */
@ToString
@EqualsAndHashCode
public final class InnerClass implements BytecodeAttribute {

    /**
     * Internal name of the class.
     */
    private final String name;

    /**
     * The internal name of the class or interface class is a member of.
     */
    private final String outer;

    /**
     * The simple name of the class.
     */
    private final String inner;

    /**
     * Access flags of the inner class as originally declared in the enclosing class.
     */
    private final int access;

    /**
     * Constructor.
     * @param name Internal name of the class.
     * @param outer The internal name of the class or interface class is a member of.
     * @param inner The simple name of the class.
     * @param access Access flags of the inner class as originally declared in the
     *  enclosing class.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public InnerClass(
        final String name,
        final String outer,
        final String inner,
        final int access
    ) {
        this.name = name;
        this.outer = outer;
        this.inner = inner;
        this.access = access;
    }

    @Override
    public void write(final ClassVisitor clazz) {
        clazz.visitInnerClass(this.name, this.outer, this.inner, this.access);
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        throw new UnsupportedOperationException(
            String.format(
                "Inner class '%s' cannot be written to method attributes",
                this
            )
        );
    }

    @Override
    public DirectivesAttribute directives() {
        return new DirectivesAttribute(
            "inner-class",
            new DirectivesValue("", this.name),
            new DirectivesValue("", this.outer),
            new DirectivesValue("", this.inner),
            new DirectivesValue("", this.access)
        );
    }
}
