/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * XML document node.
 * This abstraction is used to represent an XML node in the XML document.
 * We added it to be able to use two different implementations of XML nodes:
 * - one from the jcabi library
 * - another native implementation
 * @since 0.7
 */
public interface MyXmlNode {

    /**
     * Get all child nodes.
     * @return Child nodes.
     */
    Stream<XmlNode> children();

    /**
     * Retrieve node text content.
     * @return Text content.
     */
    String text();

    /**
     * Get attribute.
     * @param name Attribute name.
     * @return Attribute.
     */
    Optional<String> attribute(final String name);

    /**
     * Get child node.
     * @param name Child node name.
     * @return Child node.
     */
    XmlNode child(final String name);

    /**
     * Find elements by xpath.
     * @param xpath XPath.
     * @return List of elements.
     */
    List<String> xpath(final String xpath);

    /**
     * Get child node by attribute.
     * @param attribute Attribute name.
     * @param value Attribute value.
     * @return Child node.
     */
    XmlNode child(final String attribute, final String value);

    /**
     * Get optional child node by attribute.
     * @param attribute Attribute name.
     * @param value Attribute value.
     * @return Child node.
     */
    Optional<XmlNode> optchild(final String attribute, final String value);

    /**
     * Get first child.
     * @return First child node.
     */
    XmlNode firstChild();

    /**
     * Check if an attribute exists.
     * @param name Attribute name.
     * @param value Attribute value.
     * @return True if an attribute with specified value exists.
     */
    boolean hasAttribute(final String name, final String value);

}
