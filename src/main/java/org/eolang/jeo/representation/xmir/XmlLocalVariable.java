/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.LocalVariable;

/**
 * Xml representation of a local variable.
 * @since 0.6
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlLocalVariable {

    /**
     * XML node of local variable.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node XML node.
     */
    XmlLocalVariable(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to domain attribute.
     * @return Attribute.
     */
    public BytecodeAttribute attribute() {
        return new LocalVariable(
            this.index(),
            this.name(),
            this.descriptor(),
            this.signature(),
            this.start(),
            this.end()
        );
    }

    /**
     * Local variable index.
     * @return Local variable index.
     */
    private int index() {
        return this.integer("index");
    }

    /**
     * Name.
     * @return Name.
     */
    private String name() {
        return this.string("name");
    }

    /**
     * Descriptor.
     * @return Descriptor.
     */
    private String descriptor() {
        return this.string("descr");
    }

    /**
     * Local variable signature.
     * @return Signature.
     */
    private String signature() {
        return this.string("signature");
    }

    /**
     * Start label.
     * @return Label.
     */
    private BytecodeLabel start() {
        return this.label(0);
    }

    /**
     * End label.
     * @return Label.
     */
    private BytecodeLabel end() {
        return this.label(1);
    }

    /**
     * Get integer by name.
     * @param name Name.
     * @return Integer.
     */
    private int integer(final String name) {
        return this.byName(name).map(Integer.class::cast).orElse(0);
    }

    /**
     * Get string by index.
     * @param name String.
     * @return String.
     */
    private String string(final String name) {
        return this.byName(name).map(String.class::cast).orElse(null);
    }

    /**
     * Get label by index.
     * @param index Index.
     * @return Label.
     */
    private BytecodeLabel label(final int index) {
        return this.operand(index).map(BytecodeLabel.class::cast).orElse(null);
    }

    /**
     * Get operand by index.
     * @param index Index.
     * @return Optional operand.
     */
    private Optional<Object> operand(final int index) {
        return this.node.child(index).map(XmlOperand::new).map(XmlOperand::asObject);
    }

    /**
     * Find child node by name.
     * @param name Name of the child node.
     * @return Child node.
     * @throws IllegalStateException When child node is missing.
     */
    private Optional<Object> byName(final String name) {
        return Optional.ofNullable(
            new XmlOperand(new XmlChildren(this.node).byName(name)).asObject()
        );
    }
}
