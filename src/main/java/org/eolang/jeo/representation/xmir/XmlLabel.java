/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;

/**
 * XML representation of bytecode label.
 *
 * @since 0.1
 */
public final class XmlLabel implements XmlBytecodeEntry {

    /**
     * Label node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node Label node
     */
    XmlLabel(final XmlNode node) {
        this.node = new XmlJeoObject(node);
    }

    @Override
    public BytecodeLabel bytecode() {
        try {
            return new BytecodeLabel(
                new XmlValue(
                    this.node.children()
                        .findFirst().orElseThrow(
                            () -> new IllegalStateException(
                                "Label node must have at least one child"
                            )
                        )
                ).string(),
                this.types()
            );
        } catch (final ClassCastException exception) {
            throw new IllegalStateException(
                String.format("Incorrect label node: %n%s", this.node),
                exception
            );
        }
    }

    private BytecodeTypeAnnotations types() {
        return this.node.children()
            .filter(child -> "type-annotations".equals(child.attribute("name").orElse("")))
            .findFirst()
            .map(XmlTypeAnnotations::new)
            .map(XmlTypeAnnotations::bytecode)
            .orElseGet(BytecodeTypeAnnotations::new);
    }
}
