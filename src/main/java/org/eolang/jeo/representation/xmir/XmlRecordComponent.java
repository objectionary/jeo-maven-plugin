/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeRecordComponent;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;

/**
 * Xml representation of a record component.
 * Maps to {@link BytecodeRecordComponent}.
 * Mirror of {@link org.eolang.jeo.representation.directives.DirectivesRecordComponent}.
 *
 * @since 0.15.0
 */
final class XmlRecordComponent {

    /**
     * Xml node of record component to parse.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node Node to parse
     */
    XmlRecordComponent(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     *
     * @param node Node to parse
     */
    private XmlRecordComponent(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Bytecode representation of the record component.
     *
     * @return Bytecode record component
     */
    BytecodeRecordComponent bytecode() {
        return new BytecodeRecordComponent(
            this.name(),
            this.descriptor(),
            this.signature(),
            this.annotations(),
            this.typeAnnotations()
        );
    }

    private String name() {
        return new XmlValue(this.byName("name")).string();
    }

    private String descriptor() {
        return new XmlValue(this.byName("descriptor")).string();
    }

    private String signature() {
        return new XmlValue(this.byName("signature")).string();
    }

    private BytecodeAnnotations annotations() {
        return this.byNameOpt("annotations")
            .map(XmlAnnotations::new)
            .map(XmlAnnotations::bytecode)
            .orElseGet(BytecodeAnnotations::new);
    }

    private BytecodeTypeAnnotations typeAnnotations() {
        return this.byNameOpt("type-annotations")
            .map(XmlTypeAnnotations::new)
            .map(XmlTypeAnnotations::bytecode)
            .orElseGet(BytecodeTypeAnnotations::new);
    }

    private XmlNode byName(final String name) {
        return this.byNameOpt(name).orElseThrow(
            () ->
                new IllegalStateException(
                    String.format(
                        "Record component '%s' is not defined in '%s'",
                        name,
                        this.node
                    )
                )
        );
    }

    private Optional<XmlNode> byNameOpt(final String name) {
        return this.node.children()
            .filter(n -> n.attribute("name").orElse("").equals(name))
            .findFirst();
    }
}
