/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;

/**
 * Xml representation of type annotations.
 * Maps to {@link BytecodeTypeAnnotations}.
 * Mirror of {@link org.eolang.jeo.representation.directives.DirectivesTypeAnnotations}.
 * @since 0.15.0
 */
final class XmlTypeAnnotations {
    /**
     * Node of type annotations to parse.
     */
    private final XmlJeoObject node;

    /**
     * Node of type annotations to parse.
     * @param node Node to parse.
     */
    XmlTypeAnnotations(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Node of type annotations to parse.
     * @param node Node to parse.
     */
    private XmlTypeAnnotations(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse to bytecode type annotations.
     * @return Bytecode type annotations.
     */
    public BytecodeTypeAnnotations bytecode() {
        return new BytecodeTypeAnnotations(
            new XmlSeq(this.node).children()
                .map(XmlTypeAnnotation::new)
                .map(XmlTypeAnnotation::bytecode)
                .collect(Collectors.toList())
        );
    }
}
