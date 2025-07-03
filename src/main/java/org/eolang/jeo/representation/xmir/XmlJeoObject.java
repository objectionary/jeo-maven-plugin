/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * XML Jeo object representation.
 * Mirrors {@link org.eolang.jeo.representation.directives.DirectivesJeoObject}
 * @since 0.11.0
 */
final class XmlJeoObject {

    /**
     * Origin XML node.
     */
    private final XmlAbstractObject origin;

    /**
     * Constructor.
     * @param node XML node.
     */
    XmlJeoObject(final XmlNode node) {
        this(new XmlAbstractObject(node));
    }

    /**
     * Constructor.
     * @param origin XML abstract object.
     */
    private XmlJeoObject(final XmlAbstractObject origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    /**
     * Retrieve the name of the Jeo object.
     * @return Name of the Jeo object.
     */
    public String name() {
        return this.origin.attribute("name").orElseThrow(
            () -> new IllegalStateException(
                String.format("Attribute 'name' not found in %s", this.origin)
            )
        );
    }

    /**
     * Base of the Jeo object.
     * @return Optional containing the base of the Jeo object if present, otherwise empty.
     */
    public Optional<String> base() {
        return this.origin.base();
    }

    /**
     * Children of the Jeo object.
     * @return Stream of XML nodes representing the children of the Jeo object.
     */
    public Stream<XmlNode> children() {
        return this.origin.children();
    }

    /**
     * Retrieve a child node by index.
     * @param index Index of the child node to retrieve.
     * @return Optional containing the child node if present, otherwise empty.
     */
    public Optional<XmlNode> child(final int index) {
        return this.origin.child(index);
    }

    /**
     * Whether this XML Jeo object has a name attribute.
     * @return True if the object has a name attribute, false otherwise.
     */
    boolean named() {
        return this.origin.attribute("name").isPresent();
    }

    /**
     * Is this a Jeo object?
     * @return True if this is a Jeo object, false otherwise.
     */
    boolean isJeoObject() {
        return this.base().isPresent();
    }
}
