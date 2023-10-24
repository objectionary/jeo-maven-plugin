package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlProgram {

    private final Node root;

    public XmlProgram(XML xml) {
        this(xml.node());
    }

    public XmlProgram(final Node root) {
        this.root = root;
    }

    XmlClass topClass() {


        return null;
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

}
