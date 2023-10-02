package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collection;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class XmlInstruction {

    private final Node node;

    XmlInstruction(final Node node) {
        this.node = node;
    }


    public int code() {
        return Integer.parseInt(
            this.node.getAttributes()
                .getNamedItem("name")
                .getNodeValue().split("-")[1]);
    }

    Object[] arguments() {
        return XmlInstruction.arguments(this.node);
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
                res.add(XmlInstruction.hexToString(child.getTextContent()));
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
}
