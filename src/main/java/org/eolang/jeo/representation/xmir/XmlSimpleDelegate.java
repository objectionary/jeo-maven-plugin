/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * Xml representation of a simple EO delegate object.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.directives.DirectivesSimpleDelegate}
 * </p>
 * @since 0.12.0
 */
public final class XmlSimpleDelegate {

    /**
     * Inner XML node representing the delegate object.
     */
    private final XmlNode inner;

    /**
     * Constructor.
     * @param node XML node representing the delegate object
     */
    XmlSimpleDelegate(final XmlNode node) {
        this.inner = node;
    }

    /**
     * Base of the delegate object.
     * @return Base of the delegate object.
     */
    public String base() {
        return this.inner.attribute("base").orElseThrow(
            () -> new IllegalStateException(
                String.format(
                    "Base attribute is not set in the XML node %s",
                    this.inner
                )
            )
        );
    }
}
