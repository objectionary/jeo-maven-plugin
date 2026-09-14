/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeModuleRequired;

/**
 * XML representation of a required module.
 *
 * <p>Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeModuleRequired}</p>
 *
 * @since 0.15.0
 */
public final class XmlModuleRequired {

    /**
     * Required module node to parse.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node Required module node
     */
    public XmlModuleRequired(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     *
     * @param node Required module node
     */
    public XmlModuleRequired(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse required module to bytecode.
     *
     * @return Bytecode of the required module
     */
    BytecodeModuleRequired bytecode() {
        return new BytecodeModuleRequired(
            this.module(),
            this.access(),
            this.version()
        );
    }

    private String module() {
        return new XmlValue(this.byName("module")).string();
    }

    private int access() {
        return (int) new XmlValue(this.byName("access")).object();
    }

    private String version() {
        return new XmlValue(this.byName("version")).string();
    }

    private XmlNode byName(final String name) {
        return new XmlChildren(this.node).byName(name);
    }
}
