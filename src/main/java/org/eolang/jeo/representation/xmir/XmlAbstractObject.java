/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
final class XmlAbstractObject implements XmlEoObject {

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

    @Override
    public Optional<String> base() {
        return this.node.children().findFirst()
            .filter(child -> child.attribute("name").map("base"::equals).orElse(false))
            .map(child -> new XmlValue(child).string());
    }

    @Override
    public Optional<String> attribute(final String name) {
        return this.node.attribute(name);
    }

    @Override
    public Optional<XmlNode> child(final int index) {
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

    @Override
    public Stream<XmlNode> children() {
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
