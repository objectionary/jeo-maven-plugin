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
public final class XmlParams {

    /**
     * Xml representation of a method params.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node Xml representation of a method params.
     */
    public XmlParams(final XmlNode node) {
        this.node = node;
    }

    /**
     * Get method params.
     * @return Method params.
     */
    public BytecodeMethodParameters params() {
        return new BytecodeMethodParameters(
            this.node.children()
                .filter(element -> element.hasAttribute("base", new JeoFqn("param").fqn()))
                .map(XmlParam::new)
                .map(XmlParam::bytecode)
                .collect(Collectors.toList())
        );
    }
}
