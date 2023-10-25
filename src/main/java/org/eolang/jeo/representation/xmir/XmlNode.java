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

import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML smart element.
 * Utility class that simplifies work with XML.
 * @since 0.1
 * @todo #193:90min Add unit tests for XmlNode.
 *  Currently we don't have unit tests for XmlNode. So, it makes sense to add
 *  them to keep code safe and clear.
 */
final class XmlNode {

    /**
     * Parent node.
     */
    private final Node node;

    /**
     * Constructor.
     * @param parent Parent node.
     */
    XmlNode(final Node parent) {
        this.node = parent;
    }

    /**
     * Get child node.
     * @param name Child node name.
     * @return Child node.
     */
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

    /**
     * Convert to class.
     * @return Class.
     */
    XmlClass toClass() {
        return new XmlClass(this.node);
    }

    /**
     * Clean all child nodes.
     * @return The same node.
     */
    XmlNode clean() {
        while (this.node.hasChildNodes()) {
            this.node.removeChild(this.node.getFirstChild());
        }
        return this;
    }

    /**
     * Append child node.
     * @param child Node to append.
     * @return The same node.
     */
    XmlNode append(final Node child) {
        this.node.appendChild(this.node.getOwnerDocument().adoptNode(child.cloneNode(true)));
        return this;
    }

    /**
     * Generate exception if element not found.
     * @param name Element name.
     * @return Exception.
     */
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
