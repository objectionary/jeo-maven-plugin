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
 * Xml representation of an EO delegate object.
 * <p>
 *     Alternative representations of the same idea:
 *     - {@link XmlClosedObject} - a closed object with a base attribute,
 *     - {@link XmlAbstractObject} - an abstract object with a base attribute,
 * </p>
 * <p>
 *     Mirrors:
 *     - {@link org.eolang.jeo.representation.directives.DirectivesDelegateObject}.
 * </p>
 * @since 0.12.0
 */
public final class XmlDelegateObject implements XmlEoObject {

    /**
     * Inner XML node representing the delegate object.
     */
    private final XmlNode inner;

    /**
     * Constructor.
     * @param inner XML node representing the delegate object
     */
    XmlDelegateObject(final XmlNode inner) {
        this.inner = inner;
    }

    @Override
    public String toString() {
        return this.inner.toString();
    }

    @Override
    public Optional<String> base() {
        return this.inner.children().findFirst()
            .filter(child -> child.attribute("name").map("@"::equals).orElse(false))
            .map(child -> new XmlSimpleDelegate(child).base());
    }

    @Override
    public Optional<String> attribute(final String name) {
        return this.inner.attribute(name);
    }

    @Override
    public Optional<XmlNode> child(final int index) {
        final int indx = index + 1;
        final Optional<XmlNode> result;
        final List<XmlNode> children = this.inner.children().collect(Collectors.toList());
        if (indx < 0 || indx >= children.size()) {
            result = Optional.empty();
        } else {
            result = Optional.ofNullable(children.get(indx));
        }
        return result;
    }

    @Override
    public Stream<XmlNode> children() {
        final List<XmlNode> collect = this.inner.children().collect(Collectors.toList());
        if (collect.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    "The '%s' node doesn't have any children, but it should have at least one",
                    this.inner
                )
            );
        }
        return collect.subList(1, collect.size()).stream();
    }
}
