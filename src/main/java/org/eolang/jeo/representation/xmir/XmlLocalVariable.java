/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import java.util.stream.Collectors;
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
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node.
     */
    XmlLocalVariable(final XmlNode node) {
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
        return this.integer(0);
    }

    /**
     * Name.
     * @return Name.
     */
    private String name() {
        return this.string(1);
    }

    /**
     * Descriptor.
     * @return Descriptor.
     */
    private String descriptor() {
        return this.string(2);
    }

    /**
     * Local variable signature.
     * @return Signature.
     */
    private String signature() {
        return this.string(3);
    }

    /**
     * Start label.
     * @return Label.
     */
    private BytecodeLabel start() {
        return this.label(4);
    }

    /**
     * End label.
     * @return Label.
     */
    private BytecodeLabel end() {
        return this.label(5);
    }

    /**
     * Get integer by index.
     * @param index Index.
     * @return Integer.
     */
    private int integer(final int index) {
        return this.operand(index).map(Integer.class::cast).orElse(0);
    }

    /**
     * Get string by index.
     * @param index Index.
     * @return String.
     */
    private String string(final int index) {
        return this.operand(index).map(String.class::cast).orElse(null);
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
        return Optional.ofNullable(this.node.children().collect(Collectors.toList()).get(index))
            .map(XmlOperand::new)
            .map(XmlOperand::asObject);
    }
}
