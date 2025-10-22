/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeModule;
import org.eolang.jeo.representation.bytecode.BytecodeModuleExported;
import org.eolang.jeo.representation.bytecode.BytecodeModuleOpened;
import org.eolang.jeo.representation.bytecode.BytecodeModuleProvided;
import org.eolang.jeo.representation.bytecode.BytecodeModuleRequired;

/**
 * XML representation module.
 * Mirror of {@link org.eolang.jeo.representation.bytecode.BytecodeModule}.
 * @since 0.15.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlModule {

    /**
     * Module node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Module node.
     */
    public XmlModule(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node Module node.
     */
    public XmlModule(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse bytecode module from XML.
     * @return Bytecode of the module.
     */
    public BytecodeModule bytecode() {
        return new BytecodeModule(
            this.name(),
            this.access(),
            this.version(),
            this.main(),
            this.packages(),
            this.requires(),
            this.exports(),
            this.opens(),
            this.provides(),
            this.uses()
        );
    }

    /**
     * Parse name from XML.
     * @return Name of the module.
     */
    private String name() {
        return new XmlValue(this.byName("name")).string();
    }

    /**
     * Parse access from XML.
     * @return Access flags of the module.
     */
    private int access() {
        return (int) new XmlValue(this.byName("access")).object();
    }

    /**
     * Parse version from XML.
     * @return Version of the module.
     */
    private String version() {
        return new XmlValue(this.byName("version")).string();
    }

    /**
     * Parse main from XML.
     * @return Main class of the module.
     */
    private String main() {
        return new XmlValue(this.byName("main")).string();
    }

    /**
     * Parse packages from XML.
     * @return Packages of the module.
     */
    private List<String> packages() {
        return new XmlSeq(this.byName("packages")).children()
            .map(n -> new XmlValue(n).string())
            .collect(Collectors.toList());
    }

    /**
     * Parse requires from XML.
     * @return Requires of the module.
     */
    private List<BytecodeModuleRequired> requires() {
        return new XmlSeq(this.byName("requires")).children()
            .map(n -> new XmlModuleRequired(n).bytecode())
            .collect(Collectors.toList());
    }

    /**
     * Parse exports from XML.
     * @return Exports of the module.
     */
    private List<BytecodeModuleExported> exports() {
        return new XmlSeq(this.byName("exports")).children()
            .map(n -> new XmlModuleExported(n).bytecode())
            .collect(Collectors.toList());
    }

    /**
     * Parse opens from XML.
     * @return Opens of the module.
     */
    private List<BytecodeModuleOpened> opens() {
        return new XmlSeq(this.byName("opens")).children()
            .map(n -> new XmlModuleOpened(n).bytecode())
            .collect(Collectors.toList());
    }

    /**
     * Parse provides from XML.
     * @return Provides of the module.
     */
    private List<BytecodeModuleProvided> provides() {
        return new XmlSeq(this.byName("provides")).children()
            .map(n -> new XmlModuleProvided(n).bytecode())
            .collect(Collectors.toList());
    }

    /**
     * Parse uses from XML.
     * @return Uses of the module.
     */
    private List<String> uses() {
        return new XmlSeq(this.byName("uses")).children()
            .map(n -> new XmlValue(n).string())
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
