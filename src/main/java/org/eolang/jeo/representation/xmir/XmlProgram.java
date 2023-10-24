package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Optional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlProgram {

    private final Node root;

    public XmlProgram(XML xml) {
        this(xml.node());
    }

    private XmlProgram(final Node root) {
        this.root = root;
    }

    public XmlClass topClass() {
        final Node result;
        if (XmlProgram.isClass(this.root)) {
            result = this.root;
        } else {
            result = XmlProgram.findClass(this.root)
                .orElseThrow(
                    () -> new IllegalStateException(
                        String.format(
                            "No top-level class found in '%s'",
                            this.root
                        )
                    )
                );
        }
        return new XmlClass(result);
    }

    public XmlProgram withTopClass(XmlClass clazz) {
        final NodeList top = this.root.getChildNodes();
        for (int index = 0; index < top.getLength(); ++index) {
            final Node current = top.item(index);
            if (current.getNodeName().equals("program")) {
                final NodeList subchildren = current.getChildNodes();
                for (int indexnext = 0; indexnext < subchildren.getLength(); ++indexnext) {
                    final Node next = subchildren.item(indexnext);
                    if (next.getNodeName().equals("objects")) {
                        while (next.hasChildNodes()) {
                            next.removeChild(next.getFirstChild());
                        }
                        next.appendChild(
                            next.getOwnerDocument().adoptNode(clazz.node().cloneNode(true))
                        );
                    }
                }
            }
        }
        return this;
    }

    public XML toXMIR() {
        return new XMLDocument(this.root);
    }


    /**
     * Find class node in entire XML.
     * @param xml Entire XML.
     * @return Class node.
     */
    private static Node findClass(final XML xml) {
        final Node result;
        final Node root = xml.node();
        if (XmlProgram.isClass(root)) {
            result = root;
        } else {
            result = XmlProgram.findClass(root)
                .orElseThrow(
                    () -> new IllegalStateException(
                        String.format(
                            "No top-level class found in '%s'",
                            xml
                        )
                    )
                );
        }
        return result;
    }

    /**
     * Find class node in the current node.
     * @param node Current node.
     * @return Class node.
     */
    private static Optional<Node> findClass(final Node node) {
        Optional<Node> res = Optional.empty();
        if (XmlProgram.isClass(node)) {
            res = Optional.of(node);
        } else {
            final NodeList children = node.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Optional<Node> child = XmlProgram.findClass(children.item(index));
                if (child.isPresent()) {
                    res = child;
                }
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
