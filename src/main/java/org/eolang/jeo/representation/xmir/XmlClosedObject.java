/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;

/**
 * Closed object in XMIR format.
 * @since 0.11.0
 */
final class XmlClosedObject {

    /**
     * XML node of the closed object.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node of the closed object.
     */
    XmlClosedObject(final XmlNode node) {
        this.node = node;
    }

    /**
     * Get the type of the object.
     * @return Type.
     */
    String base() {
        return this.node.attribute("base").orElseThrow(() -> this.notFound("base"));
    }

    /**
     * Get the option type of the object.
     * @return Optional type.
     */
    Optional<String> optbase() {
        return this.node.attribute("base");
    }

    /**
     * Create a new exception if the attribute is not found.
     * @param name Name of the attribute that is not found.
     * @return Exception indicating that the attribute is not found.
     */
    private IllegalStateException notFound(final String name) {
        return new IllegalStateException(
            String.format(
                "The '%s' node doesn't have '%s' attribute, but it should have one",
                this.node, name
            )
        );
    }

}
