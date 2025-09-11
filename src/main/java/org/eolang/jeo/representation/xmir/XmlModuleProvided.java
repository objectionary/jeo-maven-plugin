/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeModuleProvided;

/**
 * XML representation of a provided module.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeModuleProvided}
 * </p>
 * @since 0.15.0
 */
final class XmlModuleProvided {

    /**
     * Provided module node to parse.
     */
    private XmlJeoObject node;

    /**
     * Constructor.
     * @param node Provided module node.
     */
    XmlModuleProvided(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node Provided module node.
     */
    private XmlModuleProvided(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse provided module to bytecode.
     * @return Bytecode of the provided module.
     */
    BytecodeModuleProvided bytecode() {
        return new BytecodeModuleProvided(
            this.service(),
            this.providers()
        );
    }

    /**
     * Get package name.
     * @return Package name.
     */
    private String service() {
        return new XmlValue(new XmlChildren(this.node).byName("service")).string();
    }

    /**
     * Get modules list.
     * @return Modules list.
     */
    private List<String> providers() {
        return new XmlSeq(new XmlChildren(this.node).byName("providers"))
            .children()
            .map(value -> new XmlValue(value).string())
            .collect(Collectors.toList());
    }
}
