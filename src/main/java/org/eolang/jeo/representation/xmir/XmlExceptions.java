/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;

/**
 * XML representation of method exceptions.
 * @since 0.15.0
 */
public final class XmlExceptions {

    /**
     * XML node representing the exceptions.
     */
    private final XmlSeq node;

    /**
     * Constructor.
     * @param node XML node representing the exceptions.
     */
    XmlExceptions(final XmlNode node) {
        this(new XmlSeq(node));
    }

    /**
     * Constructor.
     * @param node XML node representing the exceptions.
     */
    private XmlExceptions(final XmlSeq node) {
        this.node = node;
    }

    /**
     * Checks if the node represents exceptions.
     * @return True if the node is named "exceptions", false otherwise.
     */
    public boolean isExceptions() {
        final boolean res;
        if (this.node.named()) {
            final String name = this.node.name();
            res = "exceptions".equals(name);
        } else {
            res = false;
        }
        return res;
    }

    /**
     * Converts the XML exceptions to a list of exception class names.
     * @return List of exception class names.
     */
    public List<String> bytecode() {
        try {
            return this.node.children()
                .map(XmlValue::new)
                .map(XmlValue::string)
                .collect(Collectors.toList());
        } catch (final IllegalArgumentException exception) {
            throw new IllegalStateException(
                String.format("Failed to parse exceptions in the node: %s", this.node),
                exception
            );
        }
    }
}
