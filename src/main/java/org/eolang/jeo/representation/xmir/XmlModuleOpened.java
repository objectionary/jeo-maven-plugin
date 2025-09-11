/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeModuleOpened;

/**
 * XML representation of an opened module.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeModuleOpened}
 * </p>
 * @since 0.15.0
 */
final class XmlModuleOpened {

    /**
     * Opened module node to parse.
     */
    private XmlJeoObject node;

    /**
     * Constructor.
     * @param node Opened module node.
     */
    XmlModuleOpened(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node Opened module node.
     */
    private XmlModuleOpened(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse opened module to bytecode.
     * @return Bytecode of the opened module.
     */
    BytecodeModuleOpened bytecode() {
        return new BytecodeModuleOpened(
            this.pckg(),
            this.access(),
            this.modules()
        );
    }

    /**
     * Get package name.
     * @return Package name.
     */
    private String pckg() {
        return new XmlValue(new XmlChildren(this.node).byName("package")).string();
    }

    /**
     * Get access flags.
     * @return Access flags.
     */
    private int access() {
        return (int) new XmlValue(new XmlChildren(this.node).byName("access")).object();
    }

    /**
     * Get modules list.
     * @return Modules list.
     */
    private List<String> modules() {
        return new XmlSeq(new XmlChildren(this.node).byName("modules"))
            .children()
            .map(value -> new XmlValue(value).string())
            .collect(Collectors.toList());
    }
}
