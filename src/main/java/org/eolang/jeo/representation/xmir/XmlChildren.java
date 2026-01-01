/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * Node with children.
 * @since 0.15.0
 */
final class XmlChildren {

    /**
     * Node with children.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Node with children.
     */
    XmlChildren(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Find child node by name.
     * @param name Name of the child node.
     * @return Child node.
     * @throws IllegalStateException When child node is missing.
     */
    XmlNode byName(final String name) {
        return this.node.children()
            .filter(n -> n.attribute("name").map(name::equals).orElse(false))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format("'%s' is missing in `%s`", name, this.node)
                )
            );
    }
}
