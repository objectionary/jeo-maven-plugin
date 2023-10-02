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

import com.jcabi.xml.XML;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML to Java bytecode.
 * @since 0.1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class XmlBytecodeClass extends ClassWriter {

    /**
     * XML.
     */
    private final XML xml;

    /**
     * Constructor.
     * @param xml XML.
     */
    public XmlBytecodeClass(final XML xml) {
        super(ClassWriter.COMPUTE_MAXS);
        this.xml = xml;
    }

    @Override
    public byte[] toByteArray() {
        this.travers();
        return super.toByteArray();
    }

    /**
     * Traverse XML.
     */
    private void travers() {
        final XmlClass clazz = new XmlClass(this.xml);
        this.visit(
            new DefaultVersion().java(),
            clazz.access(),
            clazz.name(),
            null,
            "java/lang/Object",
            null
        );
        for (final XmlMethod method : clazz.methods()) {
            final MethodVisitor visitor = this.visitMethod(
                method.access(),
                method.name(),
                method.descriptor(),
                null,
                null
            );
            XmlBytecodeClass.visitMethod(visitor, method);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
    }

    /**
     * Visit method.
     * @param visitor Method visitor.
     * @param node XML Node.
     * @checkstyle CyclomaticComplexityCheck (100 lines)
     */
    private static void visitMethod(final MethodVisitor visitor, final XmlMethod node) {
        for (final XmlInstruction instruction : node.instructions()) {
            final int code = instruction.code();
            final Object[] arguments = instruction.arguments();
            switch (code) {
                case Opcodes.GETSTATIC:
                    visitor.visitFieldInsn(
                        code,
                        String.valueOf(arguments[0]),
                        String.valueOf(arguments[1]),
                        String.valueOf(arguments[2])
                    );
                    break;
                case Opcodes.LDC:
                    visitor.visitLdcInsn(arguments[0]);
                    break;
                case Opcodes.INVOKEVIRTUAL:
                    visitor.visitMethodInsn(
                        code,
                        String.valueOf(arguments[0]),
                        String.valueOf(arguments[1]),
                        String.valueOf(arguments[2]),
                        false
                    );
                    break;
                case Opcodes.RETURN:
                    visitor.visitInsn(Opcodes.RETURN);
                    break;
                default:
                    throw new IllegalStateException(
                        String.format("Unexpected value: %d", code)
                    );
            }
        }
    }
}
