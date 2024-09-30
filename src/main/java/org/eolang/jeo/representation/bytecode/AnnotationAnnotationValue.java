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

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesAnnotationAnnotationValue;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

@ToString
@EqualsAndHashCode
public final class AnnotationAnnotationValue implements BytecodeAnnotationValue {

    private final String name;
    private final String descriptor;

    private final List<BytecodeAnnotationValue> values;

    public AnnotationAnnotationValue(
        final String name,
        final String descriptor,
        final List<BytecodeAnnotationValue> values
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.values = values;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        final AnnotationVisitor annotation = visitor.visitAnnotation(this.name, this.descriptor);
        this.values.forEach(value -> value.writeTo(annotation));
        annotation.visitEnd();
    }

    @Override
    public Iterable<Directive> directives() {
        return new DirectivesAnnotationAnnotationValue(
            this.name,
            this.descriptor,
            this.values.stream()
                .map(BytecodeAnnotationValue::directives)
                .collect(Collectors.toList())
        );
//        return DirectivesAnnotationProperty.annotation(
//            this.name,
//            this.descriptor,
//            this.values.stream()
//                .map(BytecodeAnnotationValue::directives)
//                .collect(Collectors.toList())
//        );
    }
}
