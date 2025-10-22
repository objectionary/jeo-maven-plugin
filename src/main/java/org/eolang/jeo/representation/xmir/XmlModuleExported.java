/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeModuleExported;

/**
 * XML representation of an exported module.
 * <p>
 *     Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeModuleExported}
 * </p>
 * @since 0.15.0
 */
public final class XmlModuleExported {

    /**
     * Node of the exported module to parse.
     */
    private XmlJeoObject node;

    /**
     * Constructor.
     * @param node Exported module node.
     */
    XmlModuleExported(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node Exported module node.
     */
    private XmlModuleExported(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse exported module to bytecode.
     * @return Bytecode of the exported module.
     */
    public BytecodeModuleExported bytecode() {
        return new BytecodeModuleExported(
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
        return new XmlValue(this.byName("package")).string();
    }

    /**
     * Get access flags.
     * @return Access flags.
     */
    private int access() {
        return (int) new XmlValue(this.byName("access")).object();
    }

    /**
     * Get list of modules.
     * @return List of module names.
     */
    private List<String> modules() {
        return new XmlSeq(this.byName("modules"))
            .children()
            .map(XmlValue::new)
            .map(XmlValue::string)
            .collect(Collectors.toList());
    }

    /**
     * Find child node by name.
     * @param name Name of the child node.
     * @return Child node.
     * @throws IllegalStateException When child node is missing.
     */
    private XmlNode byName(final String name) {
        return new XmlChildren(this.node).byName(name);
    }
}
