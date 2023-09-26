package org.eolang.jeo.representation.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;

/**
 * Method printer.
 * ASM method visitor which scans the method and builds Xembly directives.
 * @since 0.1
 * @todo #84:30min Handle method parameters.
 *  Right now we just skip method parameters. We should handle them in order to
 *  build correct XML representation of the class. When the method is ready
 *  remove that puzzle.
 */
public final class MethodDirectives extends MethodVisitor {

    /**
     * Xembly directives.
     */
    private final Directives directives;

    /**
     * Constructor.
     * @param directives Xembly directives
     * @param visitor Method visitor
     */
    public MethodDirectives(
        final Directives directives,
        final MethodVisitor visitor
    ) {
        super(Opcodes.ASM9, visitor);
        this.directives = directives;
    }

    @Override
    public void visitInsn(final int opcode) {
        this.opcode(opcode);
        super.visitInsn(opcode);
    }

    @Override
    public void visitFieldInsn(
        final int opcode,
        final String owner,
        final String name,
        final String descriptor
    ) {
        this.opcode(opcode, String.format("%s.%s", owner, name));
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.opcode(opcode, operand);
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.opcode(opcode);
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.opcode(opcode, type);
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitVarInsn(final int opcode, final int varindex) {
        this.opcode(opcode, varindex);
        super.visitVarInsn(opcode, varindex);
    }

    @Override
    public void visitMethodInsn(
        final int opcode,
        final String owner,
        final String name,
        final String descriptor,
        final boolean isinterface
    ) {
        this.opcode(opcode, String.format("%s.%s", owner, name));
        super.visitMethodInsn(opcode, owner, name, descriptor, isinterface);
    }

    @Override
    public void visitLdcInsn(final Object value) {
        this.opcode(Opcodes.LDC, value);
        super.visitLdcInsn(value);
    }

    @Override
    public void visitEnd() {
        this.directives.up().up();
        super.visitEnd();
    }

    /**
     * Add opcode to the directives.
     * @param opcode Opcode
     * @param operands Operands
     */
    private void opcode(final int opcode, final Object... operands) {
        this.directives.add("o")
            .attr("name", new OpcodeName(opcode).asString())
            .attr("base", "opcode");
        for (final Object operand : operands) {
            if (operand instanceof String) {
                this.directives.add("o")
                    .attr("base", "string")
                    .attr("data", "bytes")
                    .set(((String) operand).replace('/', '.'))
                    .up();
            } else {
                this.directives.add("o")
                    .attr("base", "int")
                    .attr("data", "bytes")
                    .set(operand)
                    .up();
            }
        }
        this.directives.up();
    }
}
