/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.objectweb.asm.TypePath;

/**
 * Xml representation of a type annotation.
 * Mirror of {@link org.eolang.jeo.representation.directives.DirectivesTypeAnnotation}.
 * Maps to {@link BytecodeTypeAnnotation}.
 * @since 0.15.0
 */
final class XmlTypeAnnotation {

    /**
     * Xml node of type annotation to parse.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Node to parse.
     */
    XmlTypeAnnotation(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node Node to parse.
     */
    private XmlTypeAnnotation(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse to bytecode type annotation.
     * @return Bytecode type annotation.
     */
    public BytecodeTypeAnnotation bytecode() {
        return new BytecodeTypeAnnotation(
            this.ref(),
            this.path(),
            this.descriptor(),
            this.visible(),
            this.values()
        );
    }

    /**
     * Parses a reference to the annotated type.
     * @return Reference.
     */
    private int ref() {
        return (int) new XmlValue(
            this.node.child(0).orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Expected 'ref' node for type annotation: %s",
                        this.node
                    )
                )
            )
        ).object();
    }

    /**
     * Parses the path to the annotated type argument.
     * @return Path.
     */
    private TypePath path() {
        return TypePath.fromString(
            new XmlValue(
                this.node.child(1).orElseThrow(
                    () -> new IllegalStateException(
                        String.format(
                            "Expected 'path' node for type annotation: %s",
                            this.node
                        )
                    )
                )
            ).string()
        );
    }

    /**
     * Parses the class descriptor of the annotation class.
     * @return Descriptor.
     */
    private String descriptor() {
        return new XmlValue(
            this.node.child(2).orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Expected 'descriptor' node for type annotation: %s",
                        this.node
                    )
                )
            )
        ).string();
    }

    /**
     * Parses the visibility of the annotation.
     * @return Visibility.
     */
    private boolean visible() {
        return (boolean) new XmlValue(
            this.node.child(3).orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Expected 'visible' node for type annotation: %s",
                        this.node
                    )
                )
            )
        ).object();
    }

    /**
     * Parses the properties of the annotation.
     * @return Properties.
     */
    private List<BytecodeAnnotationValue> values() {
        return this.node.children()
            .map(XmlAnnotationValue::new)
            .filter(XmlAnnotationValue::isValue)
            .map(XmlAnnotationValue::bytecode)
            .collect(Collectors.toList());
    }

}
