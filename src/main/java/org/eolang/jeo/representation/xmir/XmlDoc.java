/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * XML document abstraction interface.
 *
 * <p>This interface provides an abstraction layer for XML documents, allowing
 * the use of different XML implementations:</p>
 * <ul>
 * <li>JCabi XML library implementation</li>
 * <li>Native Java XML implementation</li>
 * </ul>
 * @since 0.7.0
 */
public interface XmlDoc {

    /**
     * Get the root node of the XML document.
     * @return The root XML node
     */
    XmlNode root();

    /**
     * Validate the XML document.
     */
    void validate();
}
