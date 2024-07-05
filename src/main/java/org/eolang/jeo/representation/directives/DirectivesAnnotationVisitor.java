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

import com.jcabi.log.Logger;
import java.util.Optional;
import org.objectweb.asm.AnnotationVisitor;

/**
 * Directives Annotation Visitor.
 * Parses all annotation properties from bytecode and builds a list of directives.
 * These directives then can be used to build an XML document.
 *
 * @since 0.3
 * @todo #534:90min Refactor the code to reduce complexity and remove null values.
 *  Currently many classes use null checks in many places, including DirectivesAnnotationVisitor.
 *  This happens because the Java ASM library produce them a lot.
 *  I added accidentally checks, but they look awful.
 *  We need to refactor the code to remove these checks and make it more readable.
 *  Moreover, I would pay attention to all Null usages in the project.
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
        Logger.debug(
            this,
            "Visit annotation with name '%s', value '%s' and type '%s'",
            name,
            value,
            value.getClass().getName()
        );
        this.annotation.append(DirectivesAnnotationProperty.plain(name, value));
        super.visit(name, value);
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        Logger.debug(
            this,
            "Visit annotation with enum type with name '%s', descriptor '%s' and value '%s'",
            name,
            descriptor,
            value
        );
        this.annotation.append(DirectivesAnnotationProperty.enump(name, descriptor, value));
        super.visitEnum(name, descriptor, value);
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        Logger.debug(this, "Visit array annotation with name '%s'", name);
        final DirectivesAnnotationProperty prop = new DirectivesAnnotationProperty(
            DirectivesAnnotationProperty.Type.ARRAY
        );
        prop.append(new DirectivesData(Optional.ofNullable(name).orElse("")));
        this.annotation.append(prop);
        return new DirectivesAnnotationVisitor(this.api, super.visitArray(name), prop);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        Logger.debug(
            this,
            "Visit annotation with name '%s' and descriptor '%s'",
            name,
            descriptor
        );
        final DirectivesAnnotationProperty ann = DirectivesAnnotationProperty.annotation(
            name, descriptor, new DirectivesAnnotation(descriptor, true)
        );
        this.annotation.append(ann);
        return new DirectivesAnnotationVisitor(
            this.api,
            super.visitAnnotation(name, descriptor),
            ann
        );
    }
}
