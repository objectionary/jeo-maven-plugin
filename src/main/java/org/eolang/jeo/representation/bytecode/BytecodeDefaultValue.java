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
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.directives.DirectivesDefaultValue;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode default value.
 *
 * @since 0.3
 */
public final class BytecodeDefaultValue {

    /**
     * Annotation property as a value.
     */
    private final BytecodeAnnotationValue property;

    /**
     * Constructor.
     * @param property Annotation property as a value.
     */
    public BytecodeDefaultValue(final BytecodeAnnotationValue property) {
        this.property = property;
    }

    /**
     * Write the default value to the given visitor.
     * @param mvisitor Visitor.
     */
    public void writeTo(final MethodVisitor mvisitor) {
        final AnnotationVisitor visitor = mvisitor.visitAnnotationDefault();
        this.property.writeTo(visitor);
        visitor.visitEnd();
    }

    public Iterable<Directive> directives() {
        return new DirectivesDefaultValue(this.property.directives());
    }
}
