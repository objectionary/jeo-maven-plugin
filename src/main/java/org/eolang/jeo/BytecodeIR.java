package org.eolang.jeo;

import java.nio.file.Path;

public class BytecodeIR implements IR {

    private final Path klass;

    public BytecodeIR(final Path klass) {
        this.klass = klass;
    }
}
