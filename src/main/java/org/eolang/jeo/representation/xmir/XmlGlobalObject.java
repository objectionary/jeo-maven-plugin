/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Global XML object representation.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.directives.DirectivesGlobalObject}
 * </p>
 * @since 0.11.0
 */
final class XmlGlobalObject {

    /**
     * Abstract XML object representation.
     */
    private final XmlEoObject origin;

    /**
     * Constructor.
     * @param node XML node representing the global object.
     */
    XmlGlobalObject(final XmlNode node) {
        this(new XmlDelegateObject(node));
    }

    /**
     * Constructor.
     * @param origin XML abstract object representing the global object.
     */
    private XmlGlobalObject(final XmlEoObject origin) {
        this.origin = origin;
    }

    /**
     * Retrieve attribute value by name.
     * @param name Name of the attribute to retrieve.
     * @return Optional containing the attribute value if present, otherwise empty.
     */
    public Optional<String> attribute(final String name) {
        return this.origin.attribute(name);
    }

    /**
     * Retrieve child nodes.
     * @return Stream of XML nodes representing the children of the global object.
     */
    public Stream<XmlNode> children() {
        return this.origin.children();
    }
}
