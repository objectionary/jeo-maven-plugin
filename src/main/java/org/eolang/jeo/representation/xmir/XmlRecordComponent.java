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
 * @since 0.15.0
 */
final class XmlRecordComponent {

    /**
     * Xml node of record component to parse.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Node to parse.
     */
    XmlRecordComponent(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node Node to parse.
     */
    private XmlRecordComponent(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Bytecode representation of the record component.
     * @return Bytecode record component.
     */
    public BytecodeRecordComponent bytecode() {
        return new BytecodeRecordComponent(
            this.name(),
            this.descriptor(),
            this.signature(),
            this.annotations(),
            this.typeAnnotations()
        );
    }

    /**
     * Parses the name of the record component.
     * @return Name or null if not present.
     */
    private String name() {
        return new XmlValue(this.byName("name")).string();
    }

    /**
     * Parses the descriptor of the record component.
     * @return Descriptor or null if not present.
     */
    private String descriptor() {
        return new XmlValue(this.byName("descriptor")).string();
    }

    /**
     * Parses the signature of the record component.
     * @return Signature or null if not present.
     */
    private String signature() {
        return new XmlValue(this.byName("signature")).string();
    }

    /**
     * Parses the annotations of the record component.
     * @return Annotations.
     */
    private BytecodeAnnotations annotations() {
        return this.byNameOpt("annotations")
            .map(XmlAnnotations::new)
            .map(XmlAnnotations::bytecode)
            .orElseGet(BytecodeAnnotations::new);
    }

    /**
     * Parses the type annotations of the record component.
     * @return Type annotations.
     */
    private BytecodeTypeAnnotations typeAnnotations() {
        return this.byNameOpt("type-annotations")
            .map(XmlTypeAnnotations::new)
            .map(XmlTypeAnnotations::bytecode)
            .orElseGet(BytecodeTypeAnnotations::new);
    }

    /**
     * Parses a child node by its name attribute.
     * @param name Name of the child node.
     * @return Child node.
     */
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

    /**
     * Parses a child node by its name attribute.
     * @param name Name of the child node.
     * @return Child node or empty if not found.
     */
    private Optional<XmlNode> byNameOpt(final String name) {
        return this.node.children()
            .filter(n -> n.attribute("name").orElse("").equals(name))
            .findFirst();
    }
}
