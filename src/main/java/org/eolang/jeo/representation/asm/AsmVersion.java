package org.eolang.jeo.representation.asm;

import org.objectweb.asm.Opcodes;

public class AsmVersion {

    private int bytecode;
    private int asm;

    public AsmVersion() {
        this(Opcodes.V11, Opcodes.ASM9);
    }

    public AsmVersion(int java, int api) {
        this.bytecode = java;
        this.asm = api;
    }

    public int java() {
        return this.bytecode;
    }

    public int api(){
        return this.asm;
    }
}
