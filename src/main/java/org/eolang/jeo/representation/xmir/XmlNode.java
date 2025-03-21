/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * XML document node.
 * This abstraction is used to represent an XML node in the XML document.
 * We added it to be able to use two different implementations of XML nodes:
 * - one from the jcabi library
 * - another native implementation
 * @since 0.7
 */
public interface XmlNode {

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
    Optional<String> attribute(String name);

    /**
     * Get child node.
     * @param name Child node name.
     * @return Child node.
     */
    XmlNode child(String name);

    /**
     * Find elements by xpath.
     * @param xpath XPath.
     * @return List of elements.
     */
    List<String> xpath(String xpath);

    /**
     * Get child node by attribute.
     * @param attribute Attribute name.
     * @param value Attribute value.
     * @return Child node.
     */
    XmlNode child(String attribute, String value);

    /**
     * Get optional child node by attribute.
     * @param attribute Attribute name.
     * @param value Attribute value.
     * @return Child node.
     */
    Optional<XmlNode> optchild(String attribute, String value);

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
    boolean hasAttribute(String name, String value);

    /**
     * Validate the node.
     */
    void validate();

}
