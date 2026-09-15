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
 * Xml representation of an EO delegate object.
 *
 * <p>Alternative representations of the same idea: - {@link XmlClosedObject} - a closed object
 * with a base attribute, - {@link XmlAbstractObject} - an abstract object with a base
 * attribute,</p>
 *
 * <p>Mirrors: - {@link org.eolang.jeo.representation.directives.DirectivesDelegateObject}.</p>
 *
 * @since 0.12.0
 */
public final class XmlDelegateObject implements XmlEoObject {

    /**
     * Inner XML node representing the delegate object.
     */
    private final XmlNode inner;

    /**
     * Constructor.
     *
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
        return this.delegate().map(child -> new XmlSimpleDelegate(child).base());
    }

    private Optional<XmlNode> delegate() {
        return this.inner.children().findFirst()
            .filter(child -> child.attribute("name").map("φ"::equals).orElse(false));
    }

    private int offset() {
        final int result;
        if (this.delegate().isPresent()) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }

    @Override
    public Optional<String> attribute(final String name) {
        return this.inner.attribute(name);
    }

    @Override
    public Optional<XmlNode> child(final int index) {
        final int indx = index + this.offset();
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
        return collect.subList(this.offset(), collect.size()).stream();
    }
}
