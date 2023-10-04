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
import java.util.List;
import java.util.Optional;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML class.
 * @since 0.1
 */
final class XmlClass {

    /**
     * Class node from entire XML.
     */
    private final Node node;

    /**
     * Constructor.
     * @param xml Entire XML.
     */
    XmlClass(final XML xml) {
        this(XmlClass.findClass(xml));
    }

    /**
     * Constructor.
     * @param xml Class node.
     */
    private XmlClass(final Node xml) {
        this.node = xml;
    }

    /**
     * Class name.
     * @return Name.
     */
    String name() {
        return String.valueOf(this.node.getAttributes().getNamedItem("name").getTextContent());
    }

    /**
     * Class properties.
     * @return Class properties.
     */
    XmlClassProperties properties() {
        return new XmlClassProperties(this.node);
    }

    /**
     * Methods.
     * @return Class methods.
     */
    List<XmlMethod> methods() {
        final List<XmlMethod> res = new ArrayList<>(0);
        final NodeList children = this.node.getChildNodes();
        for (int child = 0; child < children.getLength(); ++child) {
            final Node item = children.item(child);
            final NamedNodeMap attrs = item.getAttributes();
            if (item.getNodeName().equals("o") && attrs.getNamedItem("base") == null) {
                res.add(new XmlMethod(item));
            }
        }
        return res;
    }

    /**
     * Find class node in entire XML.
     * @param xml Entire XML.
     * @return Class node.
     */
    private static Node findClass(final XML xml) {
        final Node result;
        final Node root = xml.node();
        if (XmlClass.isClass(root)) {
            result = root;
        } else {
            result = XmlClass.findClass(root)
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
        if (XmlClass.isClass(node)) {
            res = Optional.of(node);
        } else {
            final NodeList children = node.getChildNodes();
            for (int index = 0; index < children.getLength(); ++index) {
                final Optional<Node> child = XmlClass.findClass(children.item(index));
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
