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
package org.eolang.jeo.representation.bytecode;

import java.util.List;
import java.util.Optional;
import org.objectweb.asm.AnnotationVisitor;

/**
 * Bytecode annotation property.
 * @since 0.3
 */
public final class BytecodeAnnotationProperty implements BytecodeAnnotationValue {

    /**
     * Type of the property.
     */
    private final Type type;

    /**
     * Property parameters.
     */
    private final List<Object> params;

    /**
     * Constructor.
     * @param type Type of the property.
     * @param params Property parameters.
     */
    private BytecodeAnnotationProperty(final Type type, final List<Object> params) {
        this.type = type;
        this.params = params;
    }

    /**
     * Factory method for property by type.
     * @param type Type.
     * @param params Parameters.
     * @return Property.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static BytecodeAnnotationProperty byType(final String type, final List<Object> params) {
        final BytecodeAnnotationProperty result;
        switch (Type.valueOf(type)) {
            case PLAIN:
                result = new BytecodeAnnotationProperty(Type.PLAIN, params);
                break;
            case ENUM:
                result = new BytecodeAnnotationProperty(Type.ENUM, params);
                break;
            case ARRAY:
                result = new BytecodeAnnotationProperty(Type.ARRAY, params);
                break;
            case ANNOTATION:
                result = new BytecodeAnnotationProperty(Type.ANNOTATION, params);
                break;
            default:
                throw new IllegalArgumentException(
                    String.format(
                        "Unknown annotation property type %s",
                        type
                    )
                );
        }
        return result;
    }

    @Override
    public void write(final AnnotationVisitor avisitor) {
        switch (this.type) {
            case PLAIN:
                avisitor.visit((String) this.params.get(0), this.params.get(1));
                break;
            case ENUM:
                avisitor.visitEnum(
                    (String) this.params.get(0),
                    (String) this.params.get(1),
                    (String) this.params.get(2)
                );
                break;
            case ARRAY:
                final AnnotationVisitor array = avisitor.visitArray(
                    Optional.ofNullable(this.params.get(0)).map(String.class::cast).orElse(null)
                );
                for (final Object param : this.params.subList(1, this.params.size())) {
                    ((BytecodeAnnotationValue) param).write(array);
                }
                array.visitEnd();
                break;
            case ANNOTATION:
                final AnnotationVisitor annotation = avisitor.visitAnnotation(
                    (String) this.params.get(0),
                    (String) this.params.get(1)
                );
                for (final Object param : this.params.subList(2, this.params.size())) {
                    ((BytecodeAnnotationValue) param).write(annotation);
                }
                annotation.visitEnd();
                break;
            default:
                throw new IllegalStateException(String.format("Unexpected value: %s", this.type));
        }
    }

    /**
     * Property types.
     * !todo! duplicate
     */
    private enum Type {
        /**
         * Plain property.
         */
        PLAIN,

        /**
         * Enum property.
         */
        ENUM,

        /**
         * Array property.
         */
        ARRAY,

        /**
         * Annotation property.
         */
        ANNOTATION
    }
}
