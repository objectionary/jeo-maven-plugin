package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class XmlMethod {

    private final Node node;

    XmlMethod(final Node node) {
        this.node = node;
    }

    /**
     * Check if the node is a method.
     * @return True if the node is a method.
     * @checkstyle BooleanExpressionComplexityCheck (10 lines)
     */
    boolean isMethod() {
        return !XmlMethod.isOpcode(this.node) && this.node.getNodeName().equals("o")
            && this.node.getAttributes().getNamedItem("name") != null
            && !(this.node.getAttributes().getNamedItem("name") != null
            && this.node.getAttributes().getNamedItem("name").getNodeValue().equals("args"))
            && !this.node.getParentNode().getNodeName().equals("objects")
            && !(this.node.getAttributes().getNamedItem("base") != null
            && this.node.getAttributes().getNamedItem("base").getNodeValue().equals("seq"));
    }

    int access() {
        final Node name = this.node.getAttributes().getNamedItem("name");
        final String content = name.getTextContent();
        final String[] split = content.split("__");
        return Integer.parseInt(split[0]);
    }

    String name() {
        final Node name = this.node.getAttributes().getNamedItem("name");
        final String content = name.getTextContent();
        final String[] split = content.split("__");
        return String.valueOf(split[1]);
    }

    String descriptor() {
        final Node name = this.node.getAttributes().getNamedItem("name");
        final String content = name.getTextContent();
        final String[] split = content.split("__");
        return String.valueOf(split[2]);
    }

    List<XmlInstruction> instructions() {
        final Optional<Node> sequence = this.sequence();
        if (!sequence.isPresent()) {
            return Collections.emptyList();
        }
        final Node seq = sequence.get();
        final List<XmlInstruction> instructions = new ArrayList<>();
        for (int index = 0; index < seq.getChildNodes().getLength(); ++index) {
            final Node instruction = seq.getChildNodes().item(index);
            if (XmlMethod.isInstruction(instruction)) {
                instructions.add(new XmlInstruction(instruction));
            }
        }
        return instructions;
    }

    private static boolean isInstruction(final Node node) {
        boolean result = false;
        final NamedNodeMap attrs = node.getAttributes();
        if (attrs != null && attrs.getNamedItem("name") != null) {
            result = true;
        }
        return result;
    }

    private Optional<Node> sequence() {
        Optional<Node> result = Optional.empty();
        final NodeList children = this.node.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            final Node item = children.item(index);
            final NamedNodeMap attributes = item.getAttributes();
            if (attributes == null) {
                continue;
            }
            final Node base = attributes.getNamedItem("base");
            if (base == null) {
                continue;
            }
            if (base.getNodeValue().equals("seq")) {
                result = Optional.of(item);
                break;
            }
        }
        return result;
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
