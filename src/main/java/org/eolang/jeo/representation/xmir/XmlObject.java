/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeObject;

/**
 * XMIR Program.
 * @since 0.1
 */
public final class XmlObject {

    /**
     * Root node.
     */
    private final XmlNode root;

    /**
     * Constructor.
     * @param lines Xmir lines.
     */
    public XmlObject(final String... lines) {
        this(new JcabiXmlNode(lines));
    }

    /**
     * Constructor.
     * @param xml Raw XMIR.
     */
    public XmlObject(final XML xml) {
        this(new JcabiXmlDoc(xml).root());
    }

    /**
     * Constructor.
     * @param root Root node.
     */
    public XmlObject(final XmlNode root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    /**
     * Convert to bytecode.
     * @return Bytecode program.
     */
    public BytecodeObject bytecode() {
        try {
            return new BytecodeObject(this.pckg(), this.top().bytecode());
        } catch (final IllegalStateException exception) {
            throw new ParsingException(
                String.format(
                    "Unexpected exception during parsing the program in package '%s'",
                    this.pckg()
                ),
                exception
            );
        }
    }

    /**
     * Find top-level class.
     *
     * @return Class.
     */
    private XmlClass top() {
        return new XmlClass(this.root.child("o"));
    }

    /**
     * Retrieve program package.
     * In case if metas are empty, or there is no package meta, or there is no tail, return empty
     * string.
     *
     * @return Package.
     */
    private String pckg() {
        return this.root
            .xpath("/object/metas/meta[head='package']/tail/text()")
            .stream()
            .findFirst()
            .map(PrefixedName::new)
            .map(PrefixedName::decode)
            .orElse("");
    }
}
