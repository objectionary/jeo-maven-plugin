package org.eolang.jeo.representation.asm;

import com.jcabi.xml.XML;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlClass {

    /**
     * XML.
     */
    private final Node node;

    public XmlClass(final XML xml) {
        this(XmlClass.findTopLevelClass(xml));
    }

    private XmlClass(final Node xml) {
        this.node = xml;
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

    List<XmlMethod> methods() {
        List<XmlMethod> res = new ArrayList<>();
        final NodeList children = this.node.getChildNodes();
        for (int child = 0; child < children.getLength(); ++child) {
            final Node item = children.item(child);
            if (item.getNodeName().equals("o")) {
                res.add(new XmlMethod(item));
            }
        }
        return res;
    }


    private static Node findTopLevelClass(XML xml) {
        final Node root = xml.node();
        if (XmlClass.isClass(root)) {
            return root;
        } else {
            return XmlClass.findClass(root).orElseThrow(
                () -> new IllegalStateException("No top-level class found")
            );
        }
    }

    private static Optional<Node> findClass(Node node) {
        Optional<Node> res = Optional.empty();
        if (XmlClass.isClass(node)) {
            return Optional.of(node);
        }
        final NodeList children = node.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            final Optional<Node> aClass = XmlClass.findClass(children.item(index));
            if (aClass.isPresent()) {
                res = aClass;
            }

        }
        return res;
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

}
