/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeFrame;

/**
 * Xmir representation of bytecode frame.
 *
 * @since 0.3
 */
public final class XmlFrame implements XmlBytecodeEntry {

    /**
     * Xmir node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param xmlnode Xmir node
     */
    public XmlFrame(final XmlNode xmlnode) {
        this(new XmlJeoObject(xmlnode));
    }

    /**
     * Constructor.
     * @param node XML Jeo object node
     */
    private XmlFrame(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Constructor.
     * @param lines Lines of XML
     */
    XmlFrame(final String... lines) {
        this(String.join("\n", lines));
    }

    /**
     * Constructor.
     * @param xml XML
     */
    private XmlFrame(final String xml) {
        this(new NativeXmlNode(xml));
    }

    /**
     * Convert to bytecode.
     * @return Bytecode frame.
     */
    public BytecodeFrame bytecode() {
        final Object[] locals = this.locals();
        final Object[] stack = this.stack();
        return new BytecodeFrame(
            this.type(),
            this.nlocals().orElse(locals.length),
            locals,
            this.nstack().orElse(stack.length),
            stack
        );
    }

    /**
     * Type of frame.
     * @return Type.
     */
    private int type() {
        return (int) new XmlOperand(
            this.byName("type").orElseThrow(
                () -> new IllegalStateException(
                    String.format("Can't find 'type' child in '%s'", this.node)
                )
            )
        ).asObject();
    }

    /**
     * Local variables.
     * @return Local variables.
     */
    private Object[] locals() {
        return new XmlFrameValues(
            this.byName("locals").orElseThrow(
                () -> new IllegalStateException(
                    String.format("Can't find 'locals' child in '%s'", this.node)
                )
            )
        ).values();
    }

    /**
     * Stack elements.
     * @return Stack elements.
     */
    private Object[] stack() {
        return new XmlFrameValues(
            this.byName("stack").orElseThrow(
                () -> new IllegalStateException(
                    String.format("Can't find 'stack' child in '%s'", this.node)
                )
            )
        ).values();
    }

    private Optional<Integer> nlocals() {
        return this.byName("nlocals")
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .map(Integer.class::cast);
    }

    private Optional<Integer> nstack() {
        return this.byName("nstack")
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .map(Integer.class::cast);
    }

    private Optional<XmlNode> byName(final String name) {
        return this.node.children()
            .filter(
                child -> child.attribute("name")
                    .map(s -> s.startsWith(name))
                    .orElse(false))
            .findFirst();
    }
}
