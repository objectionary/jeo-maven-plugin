package org.eolang.jeo;

import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Test;

class BytecodeIRTest {


    @Test
    void parsesBytecode() {
        new BytecodeIR(new ResourceOf("MethodByte.class")).parse();
    }

}