/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract XML object representation.
 * <p>
 * This class keeps the base attribute as a separate object (argument) instead of XML attribute.
 * This class is similar to {@link XmlClosedObject}.
 * </p>
 * <p>
 *     Mirrors from {@link org.eolang.jeo.representation.directives.DirectivesAbsractObject}
 * </p>
 * @since 0.11.0
 */
final class XmlAbstractObject {

    /**
     * XML node of the closed object.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node of the closed object.
     */
    XmlAbstractObject(final XmlNode node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return this.node.toString();
    }

    /**
     * Get the option type of the object.
     * @return Optional type.
     */
    Optional<String> base() {
        return this.node.children().findFirst()
            .filter(child -> child.attribute("as").map("base"::equals).orElse(false))
            .map(
                child -> {
                    return new XmlValue(child).string();
                }
            );
    }

    /**
     * Get the attribute by name.
     * @param name Name of the attribute.
     * @return Optional attribute value.
     */
    Optional<String> attribute(final String name) {
        return this.node.attribute(name);
    }

    /**
     * Get the child node by index.
     * @param index Index of the child node.
     * @return Optional child node.
     */
    Optional<XmlNode> child(final int index) {
        final int indx = index + 1;
        final Optional<XmlNode> result;
        final List<XmlNode> children = this.node.children().collect(Collectors.toList());
        if (indx < 0 || indx >= children.size()) {
            result = Optional.empty();
        } else {
            result = Optional.ofNullable(children.get(indx));
        }
        return result;
    }

    /**
     * Retrieve the children of the XML node.
     * @return Stream of child nodes.
     */
    Stream<XmlNode> children() {
        final List<XmlNode> collect = this.node.children().collect(Collectors.toList());
        if (collect.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    "The '%s' node doesn't have any children, but it should have at least one",
                    this.node
                )
            );
        }
        return collect.subList(1, collect.size()).stream();
    }
}
