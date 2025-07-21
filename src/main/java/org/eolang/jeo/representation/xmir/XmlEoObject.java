/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * XML EO object representation.
 * @since 0.12.0
 */
interface XmlEoObject {

    /**
     * Get the option type of the object.
     * @return Optional type.
     */
    Optional<String> base();

    /**
     * Get the attribute by name.
     * @param name Name of the attribute.
     * @return Optional attribute value.
     */
    Optional<String> attribute(String name);

    /**
     * Get the child node by index.
     * @param index Index of the child node.
     * @return Optional child node.
     */
    Optional<XmlNode> child(int index);

    /**
     * Retrieve the children of the XML node.
     * @return Stream of child nodes.
     */
    Stream<XmlNode> children();
}
