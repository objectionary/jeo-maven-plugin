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
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Optional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMIR Program.
 * @since 0.1
 * @todo #174:90min Add unit tests for XmlProgram.
 *  Currently we don't have unit tests for XmlProgram. So, it makes sense to add
 *  them to keep code safe and clear.
 * @todo #174:90min Refactor XmlProgram.
 *  Currently some methods of XmlProgram have high code complexity. It makes sense to
 *  simplify this code in order to make it more readable and maintainable.
 */
public class XmlProgram {

    /**
     * Root node.
     */
    private final Node root;

    /**
     * Constructor.
     * @param xml Raw XMIR.
     */
    public XmlProgram(final XML xml) {
        this(xml.node());
    }

    /**
     * Constructor.
     * @param root Root node.
     */
    private XmlProgram(final Node root) {
        this.root = root;
    }

    /**
     * Find top-level class.
     * @return Class.
     */
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

    /**
     * Set top-level class and return new XmlProgram.
     * @param clazz Class.
     * @return New XmlProgram.
     */
    public XmlProgram withTopClass(final XmlClass clazz) {
        final Node res = new XMLDocument(this.root).node();
        final NodeList top = res.getChildNodes();
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
        return new XmlProgram(res);
    }

    /**
     * Convert to XMIR .
     * @return XMIR.
     */
    public XML toXmir() {
        return new XMLDocument(this.root);
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
