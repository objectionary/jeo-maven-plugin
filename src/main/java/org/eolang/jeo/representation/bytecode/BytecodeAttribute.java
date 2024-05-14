package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.ClassVisitor;

public interface BytecodeAttribute {

    void write(final ClassVisitor bytecode);

    class InnerClass implements BytecodeAttribute {

        private final String name;
        private final String outer;
        private final String inner;
        private final int access;

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
        public void write(final ClassVisitor bytecode) {
            bytecode.visitInnerClass(this.name, this.outer, this.inner, this.access);
        }
    }
}
