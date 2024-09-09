package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.Label;

public final class BytecodeMethodBuilder {

    private final BytecodeClass clazz;
    private final BytecodeMethod method;

    public BytecodeMethodBuilder(final BytecodeClass clazz, final BytecodeMethod method) {
        this.clazz = clazz;
        this.method = method;
    }

    /**
     * Return to the original class.
     * @return Original class.
     * @checkstyle MethodNameCheck (3 lines)
     */
    @SuppressWarnings("PMD.ShortMethodName")
    public BytecodeClass up() {
        return this.clazz;
    }

    /**
     * Add label.
     * @param uid Label uid.
     * @return This object.
     */
    public BytecodeMethodBuilder label(final String uid) {
        this.method.label(uid);
        return this;
    }

    /**
     * Add label.
     * @param label Label.
     * @return This object.
     */
    public BytecodeMethodBuilder label(final Label label) {
        this.method.label(label);
        return this;
    }

    /**
     * Add instruction.
     * @param opcode Opcode.
     * @param args Arguments.
     * @return This object.
     */
    public BytecodeMethodBuilder opcode(final int opcode, final Object... args) {
        this.method.opcode(opcode, args);
        return this;
    }

    /**
     * Add try-catch block.
     * @param entry Try-catch block.
     * @return This object.
     */
    public BytecodeMethodBuilder trycatch(final BytecodeEntry entry) {
        this.method.trycatch(entry);
        return this;
    }

}
