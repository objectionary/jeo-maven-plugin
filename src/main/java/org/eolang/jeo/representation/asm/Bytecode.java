package org.eolang.jeo.representation.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class Bytecode {

    private final byte[] bytes;

    public Bytecode(final byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] asBytes() {
        return Arrays.copyOf(this.bytes, this.bytes.length);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Bytecode bytecode = (Bytecode) o;
        return Arrays.equals(this.bytes, bytecode.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override
    public String toString() {
        final StringWriter out = new StringWriter();
        new ClassReader(this.bytes)
            .accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(out)), 0);
        return out.toString();
    }
}
