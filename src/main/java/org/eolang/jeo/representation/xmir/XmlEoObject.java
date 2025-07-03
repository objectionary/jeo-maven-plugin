/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * This is EO object in XML representation.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.directives.DirectivesEoObject}
 * </p>
 * @since 0.11.0
 */
public final class XmlEoObject {

    /**
     * Inner XML node representing the EO object.
     */
    private final XmlNode inner;

    /**
     * Constructor.
     * @param inner XML node representing the EO object.
     */
    XmlEoObject(final XmlNode inner) {
        this.inner = inner;
    }

    /**
     * Retrieve the name of the EO object.
     * @return Name of the EO object.
     */
    public String name() {
        return this.inner.attribute("name")
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format("Attribute 'name' not found in %s", this.inner)
                )
            );
    }

    /**
     * Retrieve the inner XML node representing the EO object.
     * @return XML node of the EO object.
     */
    public XmlNode node() {
        return this.inner;
    }

    /**
     * Whether the EO object has a name attribute.
     * @return True if the EO object has a name attribute, false otherwise.
     */
    boolean named() {
        return this.inner.attribute("name").isPresent();
    }
}
