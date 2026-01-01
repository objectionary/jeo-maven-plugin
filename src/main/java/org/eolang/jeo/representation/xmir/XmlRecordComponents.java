/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;

/**
 * Xml representation of record components.
 * Maps to {@link BytecodeAttribute.RecordComponents}.
 * Mirror of {@link org.eolang.jeo.representation.directives.DirectivesRecordComponents}.
 *
 * @since 0.15.0
 */
final class XmlRecordComponents {

    /**
     * Xml node of record components to parse.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param xmlnode XML node.
     */
    XmlRecordComponents(final XmlNode xmlnode) {
        this(new XmlJeoObject(xmlnode));
    }

    /**
     * Constructor.
     * @param node Node to parse.
     */
    XmlRecordComponents(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode attribute.
     * @return Bytecode attribute.
     */
    public BytecodeAttribute bytecode() {
        final XmlNode seq = this.node.child(0).orElseThrow(
            () -> new IllegalStateException(
                "Record components must contain a sequence as the first child"
            )
        );
        return new BytecodeAttribute.RecordComponents(
            new XmlSeq(seq).children()
                .map(XmlRecordComponent::new)
                .map(XmlRecordComponent::bytecode)
                .collect(Collectors.toList())
        );
    }
}
