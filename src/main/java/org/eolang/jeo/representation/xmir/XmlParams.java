/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * XML method params.
 * @since 0.6
 */
final class XmlParams {

    /**
     * Params fully qualified name.
     */
    private static final String PARAMS_BASE = new JeoFqn("params").fqn();

    /**
     * Xml representation of a method params.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Xml representation of a method params.
     */
    XmlParams(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node XML Jeo object node representing the method params.
     */
    private XmlParams(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Is this node a method params?
     * @return True if this node is a method params.
     */
    boolean isParams() {
        return this.node.base()
            .map(XmlParams.PARAMS_BASE::equals)
            .orElse(false);
    }

    /**
     * Get method params.
     * @return Method params.
     */
    BytecodeMethodParameters params() {
        return new BytecodeMethodParameters(
            this.node.children()
                .map(XmlParam::new)
                .map(XmlParam::bytecode)
                .collect(Collectors.toList())
        );
    }
}
