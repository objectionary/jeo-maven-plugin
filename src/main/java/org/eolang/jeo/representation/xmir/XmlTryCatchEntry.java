/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.directives.EoFqn;

/**
 * XML try-catch entry.
 * @since 0.1
 */
public final class XmlTryCatchEntry implements XmlBytecodeEntry {

    /**
     * XML node.
     */
    private final XmlNode xmlnode;

    /**
     * Constructor.
     * @param node XML node
     */
    public XmlTryCatchEntry(final XmlNode node) {
        this.xmlnode = node;
    }

    /**
     * Converts XML to bytecode.
     * @return Bytecode try-catch block.
     */
    public BytecodeTryCatchBlock bytecode() {
        return new BytecodeTryCatchBlock(this.start(), this.end(), this.handler(), this.type());
    }

    /**
     * Retrieves the start label.
     * @return Start label.
     */
    private BytecodeLabel start() {
        return this.label(0).orElse(null);
    }

    /**
     * Retrieves the end label.
     * @return End label.
     */
    private BytecodeLabel end() {
        return this.label(1).orElse(null);
    }

    /**
     * Retrieves the handler label.
     * @return Handler label.
     */
    private BytecodeLabel handler() {
        return this.label(2).orElse(null);
    }

    /**
     * Retrieves the exception type.
     * @return Exception type.
     */
    private String type() {
        return Optional.ofNullable(this.xmlnode.children().collect(Collectors.toList()).get(3))
            .filter(node -> !node.hasAttribute("base", new EoFqn("nop").fqn()))
            .map(XmlValue::new)
            .map(XmlValue::string)
            .filter(s -> !s.isEmpty())
            .orElse(null);
    }

    /**
     * Retrieves the label.
     * @param id Label uid.
     * @return Label.
     */
    private Optional<BytecodeLabel> label(final int id) {
        return Optional.ofNullable(this.xmlnode.children().collect(Collectors.toList()).get(id))
            .filter(node -> !node.hasAttribute("base", new EoFqn("nop").fqn()))
            .map(XmlValue::new)
            .map(XmlValue::object)
            .map(BytecodeLabel.class::cast);
    }
}
