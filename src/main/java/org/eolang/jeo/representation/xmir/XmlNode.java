package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML smart element.
 */
final class XmlNode {

    /**
     * Parent node.
     */
    private final Node node;

    public XmlNode(final Node parent) {
        this.node = parent;
    }

    XmlNode child(final String name) {
        final NodeList children = this.node.getChildNodes();
        for (int index = 0; index < children.getLength(); ++index) {
            final Node current = children.item(index);
            if (current.getNodeName().equals(name)) {
                return new XmlNode(current);
            }
        }
        throw this.notFound(name);
    }

    XmlClass toClass() {
        return new XmlClass(this.node);
    }

    XmlNode clean() {
        while (this.node.hasChildNodes()) {
            this.node.removeChild(this.node.getFirstChild());
        }
        return this;
    }

    XmlNode append(final Node node) {
        this.node.appendChild(this.node.getOwnerDocument().adoptNode(node.cloneNode(true)));
        return this;
    }

    private IllegalStateException notFound(final String name) {
        return new IllegalStateException(
            String.format(
                "Can't find '%s' element in '%s'",
                name,
                new XMLDocument(this.node)
            )
        );
    }
}
