package org.eolang.jeo;

import java.nio.file.Path;
import lombok.ToString;

@ToString
final class BytecodeIR implements IR {

    private final Path klass;

    BytecodeIR(final Path klass) {
        this.klass = klass;
    }
}
