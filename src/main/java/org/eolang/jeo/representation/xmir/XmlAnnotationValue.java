/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeEnumAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * Xmir annotation property.
 * @since 0.3
 */
public final class XmlAnnotationValue {

    /**
     * Annotation property base FQN.
     */
    private static final String APROPERTY_BASE = new JeoFqn("annotation-property").fqn();

    /**
     * Annotation property XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node XML node representing an annotation property.
     */
    public XmlAnnotationValue(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param xmlnode XML node.
     */
    private XmlAnnotationValue(final XmlJeoObject xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Transform to bytecode.
     * @return Bytecode annotation property.
     */
    public BytecodeAnnotationValue bytecode() {
        final List<Object> params = this.params();
        final BytecodeAnnotationValue result;
        switch (this.type()) {
            case "PLAIN":
                result = new BytecodePlainAnnotationValue(
                    String.valueOf(params.get(0)),
                    params.get(1)
                );
                break;
            case "ARRAY":
                result = new BytecodeArrayAnnotationValue(
                    String.valueOf(params.get(0)),
                    params.stream()
                        .skip(1)
                        .map(BytecodeAnnotationValue.class::cast)
                        .collect(Collectors.toList())
                );
                break;
            case "ANNOTATION":
                result = new BytecodeAnnotationAnnotationValue(
                    String.valueOf(params.get(0)),
                    String.valueOf(params.get(1)),
                    params.stream()
                        .skip(2)
                        .map(BytecodeAnnotationValue.class::cast)
                        .collect(Collectors.toList())
                );
                break;
            case "ENUM":
                result = new BytecodeEnumAnnotationValue(
                    String.valueOf(params.get(0)),
                    String.valueOf(params.get(1)),
                    String.valueOf(params.get(2))
                );
                break;
            default:
                throw new IllegalArgumentException(
                    String.format("Unknown annotation property type: %s", this.type())
                );
        }
        return result;
    }

    /**
     * Is this a valid annotation value?
     * @return True if this is a valid annotation value, false otherwise.
     */
    boolean isValue() {
        return this.node.base().map(XmlAnnotationValue.APROPERTY_BASE::equals).orElse(false);
    }

    /**
     * Type of the property.
     * @return Type.
     */
    private String type() {
        final List<XmlNode> collect = this.node.children().collect(Collectors.toList());
        if (collect.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    "The '%s' node doesn't have any children, but it should have at least one",
                    this.node
                )
            );
        }
        return (String) new XmlOperand(
            collect.get(0)
        ).asObject();
    }

    /**
     * Property parameters.
     * @return Parameters.
     */
    private List<Object> params() {
        return this.node.children()
            .skip(1)
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .collect(Collectors.toList());
    }
}
