/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
 *
 * @since 0.15.0
 */
public final class XmlModule {

    /**
     * Module node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node Module node
     */
    public XmlModule(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     *
     * @param node Module node
     */
    public XmlModule(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse bytecode module from XML.
     *
     * @return Bytecode of the module
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

    private String name() {
        return new XmlValue(this.byName("name")).string();
    }

    private int access() {
        return (int) new XmlValue(this.byName("access")).object();
    }

    private String version() {
        return new XmlValue(this.byName("version")).string();
    }

    private String main() {
        return new XmlValue(this.byName("main")).string();
    }

    private List<String> packages() {
        return new XmlSeq(this.byName("packages")).children()
            .map(n -> new XmlValue(n).string())
            .collect(Collectors.toList());
    }

    private List<BytecodeModuleRequired> requires() {
        return new XmlSeq(this.byName("requires")).children()
            .map(n -> new XmlModuleRequired(n).bytecode())
            .collect(Collectors.toList());
    }

    private List<BytecodeModuleExported> exports() {
        return new XmlSeq(this.byName("exports")).children()
            .map(n -> new XmlModuleExported(n).bytecode())
            .collect(Collectors.toList());
    }

    private List<BytecodeModuleOpened> opens() {
        return new XmlSeq(this.byName("opens")).children()
            .map(n -> new XmlModuleOpened(n).bytecode())
            .collect(Collectors.toList());
    }

    private List<BytecodeModuleProvided> provides() {
        return new XmlSeq(this.byName("provides")).children()
            .map(n -> new XmlModuleProvided(n).bytecode())
            .collect(Collectors.toList());
    }

    private List<String> uses() {
        return new XmlSeq(this.byName("uses")).children()
            .map(n -> new XmlValue(n).string())
            .collect(Collectors.toList());
    }

    private XmlNode byName(final String name) {
        return new XmlChildren(this.node).byName(name);
    }
}
