/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;

/**
 * Xml representation of a class attributes.
 * @since 0.4
 */
public final class XmlAttributes {

    /**
     * XML node of attributes.
     */
    private final XmlSeq node;

    /**
     * Constructor.
     * @param xmlnode XML node.
     */
    XmlAttributes(final XmlNode xmlnode) {
        this(new XmlSeq(xmlnode));
    }

    /**
     * Constructor.
     * @param node XML sequence node containing attributes.
     */
    private XmlAttributes(final XmlSeq node) {
        this.node = node;
    }

    /**
     * Get attributes.
     * @return Attributes.
     */
    public BytecodeAttributes attributes() {
        return new BytecodeAttributes(
            this.node.children()
                .map(XmlAttribute::new)
                .map(XmlAttribute::attribute)
                .collect(Collectors.toList())
        );
    }
}
