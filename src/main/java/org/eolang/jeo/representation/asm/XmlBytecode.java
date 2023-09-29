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

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.util.ArrayList;
import java.util.Collection;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML to Java bytecode.
 * @since 0.1.0
 * @todo #108:90min Refactor XmlBytecode class.
 *  XmlBytecode class is too big and complex. It should be refactored
 *  to be more readable and maintainable. Pay attention to checkstyle
 *  and PMD warnings. When refactoring, make sure that all tests are
 *  passing, especially integration tests.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class XmlBytecode extends ClassWriter {

    /**
     * XML.
     */
    private final XML xml;

    /**
     * Constructor.
     * @param xml XML.
     */
    public XmlBytecode(final XML xml) {
        super(ClassWriter.COMPUTE_MAXS);
        this.xml = xml;
    }

    @Override
    public byte[] toByteArray() {
        this.travers(this.xml.node());
        return super.toByteArray();
    }

    /**
     * Traverse XML.
     * @param node XML node.
     */
    private void travers(final Node node) {
        if (XmlBytecode.isClass(node)) {
            final Node name = node.getAttributes().getNamedItem("name");
            final String content = name.getTextContent();
            final String[] split = content.split("__");
            this.visit(
                new DefaultVersion().java(),
                Integer.parseInt(split[0]),
                String.valueOf(split[1]),
                null,
                "java/lang/Object",
                null
            );
        } else if (XmlBytecode.isMethod(node)) {
            final Node name = node.getAttributes().getNamedItem("name");
            final String content = name.getTextContent();
            final String[] split = content.split("__");
            final MethodVisitor visitor = this.visitMethod(
                Integer.parseInt(split[0]),
                String.valueOf(split[1]),
                String.valueOf(split[2]),
                null,
                null
            );
            XmlBytecode.visitMethod(visitor, node);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        } else {
            Logger.warn(this, String.format("Skip node: %s", node));
        }
        final NodeList children = node.getChildNodes();
        for (int child = 0; child < children.getLength(); ++child) {
            this.travers(children.item(child));
        }
    }

    /**
     * Check if the node is a class.
     * @param node Node.
     * @return True if the node is a class.
     */
    private static boolean isClass(final Node node) {
        return node.getNodeName().equals("o")
            && node.getParentNode().getNodeName().equals("objects");
    }

    /**
     * Check if the node is a method.
     * @param node Node.
     * @return True if the node is a method.
     * @checkstyle BooleanExpressionComplexityCheck (10 lines)
     */
    private static boolean isMethod(final Node node) {
        return !XmlBytecode.isOpcode(node) && node.getNodeName().equals("o")
            && node.getAttributes().getNamedItem("name") != null
            && !(node.getAttributes().getNamedItem("name") != null
            && node.getAttributes().getNamedItem("name").getNodeValue().equals("args"))
            && !node.getParentNode().getNodeName().equals("objects")
            && !(node.getAttributes().getNamedItem("base") != null
            && node.getAttributes().getNamedItem("base").getNodeValue().equals("seq"));
    }

    /**
     * Visit method.
     * @param visitor Method visitor.
     * @param node XML Node.
     * @checkstyle CyclomaticComplexityCheck (100 lines)
     */
    private static void visitMethod(final MethodVisitor visitor, final Node node) {
        for (int index = 0; index < node.getChildNodes().getLength(); ++index) {
            final Node item = node.getChildNodes().item(index);
            final NamedNodeMap attributes = item.getAttributes();
            if (attributes == null) {
                continue;
            }
            final Node base = attributes.getNamedItem("base");
            if (base == null) {
                continue;
            }
            if (base.getNodeValue().equals("seq")) {
                final NodeList opcodes = item.getChildNodes();
                for (int instruction = 0; instruction < opcodes.getLength(); ++instruction) {
                    final Node opcode = opcodes.item(instruction);
                    final NamedNodeMap attrs = opcode.getAttributes();
                    if (attrs == null) {
                        continue;
                    }
                    final String name = attrs.getNamedItem("name").getNodeValue();
                    final String[] split = name.split("-");
                    final int code = Integer.parseInt(split[1]);
                    final Object[] arguments = XmlBytecode.arguments(opcode);
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
    }

    /**
     * Get opcode arguments.
     * @param node Node.
     * @return Arguments.
     */
    private static Object[] arguments(final Node node) {
        final NodeList children = node.getChildNodes();
        final Collection<Object> res = new ArrayList<>(children.getLength());
        for (int index = 0; index < children.getLength(); ++index) {
            final Node child = children.item(index);
            if (child.getNodeName().equals("o")) {
                res.add(XmlBytecode.hexToString(child.getTextContent()));
            }
        }
        return res.toArray();
    }

    /**
     * Convert hex string to human-readable string.
     * @param hex Hex string.
     * @return Human-readable string.
     */
    private static String hexToString(final String hex) {
        final StringBuilder output = new StringBuilder();
        for (final String value : hex.split(" ")) {
            output.append((char) Integer.parseInt(value, 16));
        }
        return output.toString();
    }

    /**
     * Check if the node is an opcode.
     * @param node Node.
     * @return True if the node is an opcode.
     */
    private static boolean isOpcode(final Node node) {
        boolean result = false;
        final Node parent = node.getParentNode();
        if (parent != null) {
            final NamedNodeMap attributes = parent.getAttributes();
            if (attributes != null && attributes.getNamedItem("base") != null) {
                result = attributes.getNamedItem("base")
                    .getNodeValue()
                    .equals("seq");
            }
        }
        return result;
    }
}
