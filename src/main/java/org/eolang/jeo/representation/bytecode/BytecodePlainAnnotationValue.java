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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesPlainAnnotationValue;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is a plain value.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodePlainAnnotationValue implements BytecodeAnnotationValue {

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The actual value.
     */
    private final Object value;

    /**
     * Constructor.
     * @param name The name of the annotation property.
     * @param value The actual value.
     */
    public BytecodePlainAnnotationValue(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        visitor.visit(this.name, this.value);
    }

    @Override
    public Iterable<Directive> directives() {
        return new DirectivesPlainAnnotationValue(this.name, this.value);
    }
}
