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

import org.objectweb.asm.AnnotationVisitor;

/**
 * Directives Annotation Visitor.
 * Parses all annotation properties from bytecode and builds a list of directives.
 * These directives then can be used to build an XML document.
 *
 * @since 0.3
 */
public final class DirectivesAnnotationVisitor extends AnnotationVisitor {

    /**
     * Annotation directives which later can be used to build an XML document.
     */
    private final Composite annotation;

    /**
     * Constructor.
     * @param api Java ASM library API version.
     * @param visitor Annotation visitor.
     * @param annotation Directives.
     */
    public DirectivesAnnotationVisitor(
        final int api,
        final AnnotationVisitor visitor,
        final Composite annotation
    ) {
        super(api, visitor);
        this.annotation = annotation;
    }

    @Override
    public void visit(final String name, final Object value) {
        this.annotation.append(DirectivesAnnotationProperty.plain(name, value));
        super.visit(name, value);
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        this.annotation.append(DirectivesAnnotationProperty.enump(name, descriptor, value));
        super.visitEnum(name, descriptor, value);
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        final DirectivesAnnotationProperty prop = new DirectivesAnnotationProperty(
            DirectivesAnnotationProperty.Type.ARRAY
        );
        prop.append(new DirectivesData("name", name));
        this.annotation.append(prop);
        return new DirectivesAnnotationVisitor(this.api, super.visitArray(name), prop);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        final DirectivesAnnotation deep = new DirectivesAnnotation(descriptor, true);
        this.annotation.append(
            DirectivesAnnotationProperty.annotation(name, descriptor, deep)
        );
        return new DirectivesAnnotationVisitor(
            this.api,
            super.visitAnnotation(name, descriptor),
            deep
        );
    }
}
