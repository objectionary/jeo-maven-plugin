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
package org.eolang.jeo.representation.directives;

import java.io.File;
import java.util.Iterator;
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;

/**
 * Method printer.
 * ASM method visitor which scans the method and builds Xembly directives.
 * @since 0.1
 * @todo #329:30min Test invokedymanic instruction.
 *  Add test for parsing of invokedymanic instruction.
 *  Currently it is not tested and in some cases causes errors.
 *  Probably in this issue you will need to add some additional
 *  parsing logic.
 *  See {@link #visitInvokeDynamicInsn} method.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class DirectivesMethodVisitor extends MethodVisitor implements Iterable<Directive> {

    /**
     * Method directives.
     */
    private final DirectivesMethod method;

    /**
     * Constructor.
     * @param method Method
     * @param visitor Method visitor
     */
    DirectivesMethodVisitor(
        final DirectivesMethod method,
        final MethodVisitor visitor
    ) {
        super(new DefaultVersion().api(), visitor);
        this.method = method;
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
    public void visitLabel(final Label label) {
        this.method.operand(new DirectivesOperand(label));
        super.visitLabel(label);
    }

    @Override
    public void visitInvokeDynamicInsn(
        final String name,
        final String descriptor,
        final Handle handler,
        final Object... arguments
    ) {
        super.visitInvokeDynamicInsn(
            name,
            descriptor,
            handler,
            arguments
        );
    }

    @Override
    public void visitTryCatchBlock(
        final Label start,
        final Label end,
        final Label handler,
        final String type
    ) {
        this.method.exception(new DirectivesTryCatch(start, end, handler, type));
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.method.iterator();
    }

    /**
     * Add opcode to the directives.
     * @param opcode Opcode
     * @param operands Operands
     */
    private void opcode(final int opcode, final Object... operands) {
        this.method.opcode(opcode, operands);
    }
}
