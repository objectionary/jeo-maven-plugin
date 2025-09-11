/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * Node with children.
 * @since 0.15.0
 * @todo #1297:30min Use {@link XmlModuleOpened} in other places.
 *  Currently, this class is used only in {@link XmlModule}.
 *  It should be used in other places where child nodes are accessed by name.
 *  This will help to reduce code duplication and improve the quality of the code.
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
