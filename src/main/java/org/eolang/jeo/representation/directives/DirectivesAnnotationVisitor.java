package org.eolang.jeo.representation.directives;

import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.AnnotationVisitor;

public final class DirectivesAnnotationVisitor extends AnnotationVisitor {

    protected DirectivesAnnotationVisitor(final AnnotationVisitor visitor) {
        this(new DefaultVersion().api(), visitor);
    }

    protected DirectivesAnnotationVisitor(final int api, final AnnotationVisitor visitor) {
        super(api, visitor);
    }

    @Override
    public void visit(final String name, final Object value) {
        super.visit(name, value);
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        super.visitEnum(name, descriptor, value);
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        return super.visitArray(name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        return super.visitAnnotation(name, descriptor);
    }
}
