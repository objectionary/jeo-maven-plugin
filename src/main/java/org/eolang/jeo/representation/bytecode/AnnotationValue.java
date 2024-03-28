package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.AnnotationVisitor;

public interface AnnotationValue {

    void write(AnnotationVisitor visitor);
}
