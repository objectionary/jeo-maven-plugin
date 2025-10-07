/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * XPath for the base of an object.
 * This class is the response to the frequent changes in a way we represent 'base' attribute in
 * XMIR. These frequent changes made it difficult to maintain tests. Thus, to ensure stability,
 * we encapsulate the XPath creation logic here.
 * @since 0.11.0
 */
final class JeoBaseXpath {

    /**
     * Element where the base is located.
     */
    private final String element;

    /**
     * The base of the instruction.
     */
    private final String base;

    /**
     * Constructor.
     * @param element Element of the instruction.
     * @param base Base of the instruction.
     */
    JeoBaseXpath(final String element, final String base) {
        this.element = element;
        this.base = base;
    }

    /**
     * Base of the instruction.
     * @return String base.
     */
    String toXpath() {
        return String.format("%s/o[@name='φ' and contains(@base,'%s')]", this.element, this.base);
    }
}
