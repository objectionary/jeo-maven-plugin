/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Stream;

/**
 * Xml representation of a sequence of XML nodes.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.directives.DirectivesSeq}
 * </p>
 * @since 0.11.0
 */
final class XmlSeq {

    /**
     * Jeo object representing the sequence of XML nodes.
     */
    private final XmlJeoObject origin;

    /**
     * Constructor.
     * @param node XML node representing the sequence.
     */
    XmlSeq(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param origin XML Jeo object representing the sequence.
     */
    XmlSeq(final XmlJeoObject origin) {
        this.origin = origin;
    }

    /**
     * Name of the sequence.
     * @return Name of the sequence.
     */
    public String name() {
        return this.origin.name();
    }

    /**
     * Retrieve the child nodes of the sequence.
     * @return Stream of XML nodes representing the children of the sequence.
     */
    public Stream<XmlNode> children() {
        return this.origin.children();
    }

    /**
     * Whether the sequence is named.
     * @return True if the sequence has a name attribute, false otherwise.
     */
    boolean named() {
        return this.origin.named();
    }
}
