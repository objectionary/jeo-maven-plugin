/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.asm;

import java.util.Iterator;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
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
@SuppressWarnings("PMD.TooManyMethods")
public final class DirectivesMethod extends MethodVisitor implements Iterable<Directive> {

    /**
     * Xembly directives.
     */
    private final Directives directives;

    /**
     * Constructor.
     * @param directives Xembly directives
     * @param visitor Method visitor
     */
    DirectivesMethod(
        final Directives directives,
        final MethodVisitor visitor
    ) {
        super(new DefaultVersion().api(), visitor);
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
        this.opcode(opcode, owner, name, descriptor);
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.opcode(opcode, operand);
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.opcode(opcode, label);
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
        this.opcode(opcode, owner, name, descriptor);
        super.visitMethodInsn(opcode, owner, name, descriptor, false);
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

    @Override
    public Iterator<Directive> iterator() {
        return this.directives.iterator();
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
            this.directives.append(new DirectivesData(operand).directives());
        }
        this.directives.up();
    }
}
