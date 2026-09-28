/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.directives.EoFqn;

/**
 * XML try-catch entry.
 *
 * @since 0.1
 */
public final class XmlTryCatchEntry implements XmlBytecodeEntry {

    /**
     * NOP base FQN.
     * NOP is used to represent an empty label in try-catch blocks.
     */
    private static final String NOP = new EoFqn("nop").fqn();

    /**
     * XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node XML node
     */
    public XmlTryCatchEntry(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     *
     * @param node XML Jeo object node
     */
    private XmlTryCatchEntry(final XmlJeoObject node) {
        this.node = node;
    }

    @Override
    public BytecodeTryCatchBlock bytecode() {
        return new BytecodeTryCatchBlock(this.start(), this.end(), this.handler(), this.type());
    }

    private BytecodeLabel start() {
        return this.label(0).orElse(null);
    }

    private BytecodeLabel end() {
        return this.label(1).orElse(null);
    }

    private BytecodeLabel handler() {
        return this.label(2).orElse(null);
    }

    private String type() {
        final List<XmlNode> all = this.node.children().collect(Collectors.toList());
        if (all.size() <= 3) {
            throw new IllegalStateException(
                String.format(
                    "Expected at least %d element, but found %d in %s",
                    4,
                    all.size(),
                    this.node
                )
            );
        }
        return Optional.ofNullable(all.get(3))
            .filter(n -> !XmlTryCatchEntry.NOP.equals(new XmlClosedObject(n).base()))
            .map(XmlValue::new)
            .map(XmlValue::string)
            .filter(s -> !s.isEmpty())
            .orElse(null);
    }

    private Optional<BytecodeLabel> label(final int id) {
        final List<XmlNode> all = this.node.children().collect(Collectors.toList());
        if (all.size() <= id) {
            throw new IllegalStateException(
                String.format(
                    "Expected at least %d element, but found %d in %s",
                    id + 1,
                    all.size(),
                    this.node
                )
            );
        }
        return Optional.ofNullable(all.get(id))
            .filter(n -> new XmlJeoObject(n).base().isPresent()).filter(
                based -> !new XmlJeoObject(based).base()
                    .map(XmlTryCatchEntry.NOP::equals)
                    .orElse(false)
            )
            .map(XmlLabel::new)
            .map(XmlLabel::bytecode);
    }
}
