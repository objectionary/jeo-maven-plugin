package org.eolang.jeo.representation.asm;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlBytecode extends ClassWriter {

    private final XML xml;

    public XmlBytecode(final XML xml) {
        super(ClassWriter.COMPUTE_MAXS);
        this.xml = xml;
    }

    @Override
    public byte[] toByteArray() {
        this.travers(this.xml.node());
        return super.toByteArray();
    }

    private void travers(Node node) {
        if (this.isClass(node)) {
            final Node name = node.getAttributes().getNamedItem("name");
            final String content = name.getTextContent();
            final String[] split = content.split("__");
            this.visit(
                new AsmVersion().java(),
                Integer.parseInt(split[0]),
                String.valueOf(split[1]),
                null,
                "java/lang/Object",
                null
            );
        } else if (this.isMethod(node)) {
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
        for (int i = 0; i < children.getLength(); i++) {
            this.travers(children.item(i));
        }
    }

    private boolean isClass(final Node node) {
        return node.getNodeName().equals("o")
            && node.getParentNode().getNodeName().equals("objects");
    }

    private boolean isMethod(final Node node) {
        return !isOpcode(node) && node.getNodeName().equals("o")
            && node.getAttributes().getNamedItem("name") != null
            && !(node.getAttributes().getNamedItem("name") != null
            && node.getAttributes().getNamedItem("name").getNodeValue().equals("args"))
            && !node.getParentNode().getNodeName().equals("objects")
            && !(node.getAttributes().getNamedItem("base") != null
            && node.getAttributes().getNamedItem("base").getNodeValue().equals("seq"));
    }


    private static void visitMethod(MethodVisitor visitor, Node node) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node item = node.getChildNodes().item(i);
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
                for (int j = 0; j < opcodes.getLength(); j++) {
                    final Node opcode = opcodes.item(j);
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

    private static Object[] arguments(final Node node) {
        final NodeList children = node.getChildNodes();
        List<Object> res = new ArrayList<>(children.getLength());
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child.getNodeName().equals("o")) {
                res.add(XmlBytecode.hexToString(child.getTextContent()));
            }
        }
        return res.toArray();
    }

    public static String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        String[] hexValues = hex.split(" ");
        for (String hexValue : hexValues) {
            int charCode = Integer.parseInt(hexValue, 16);
            output.append((char) charCode);
        }

        return output.toString();
    }

    private boolean isOpcode(final Node node) {
        final Node parentNode = node.getParentNode();
        if (parentNode == null) {
            return false;
        } else {
            final NamedNodeMap attributes = parentNode.getAttributes();
            if (attributes == null) {
                return false;
            }
            final Node base = attributes.getNamedItem("base");
            if (base == null) {
                return false;
            }
            return base.getNodeValue().equals("seq");
        }
    }

}
