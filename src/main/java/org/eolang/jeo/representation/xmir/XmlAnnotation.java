/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.directives.JeoFqn;

/**
 * Xmir representation of an annotation.
 * @since 0.1
 */
public class XmlAnnotation {

    /**
     * Xmir node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param xmlnode XML node.
     */
    XmlAnnotation(final XmlNode xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode annotation.
     */
    public BytecodeAnnotation bytecode() {
        try {
            return new BytecodeAnnotation(
                this.descriptor(),
                this.visible(),
                this.values()
            );
        } catch (final IllegalStateException | IllegalArgumentException exception) {
            throw new ParsingException(
                String.format(
                    "Failed to convert annotation %s to bytecode",
                    this.node.toString()
                ),
                exception
            );
        }
    }

    /**
     * Annotation descriptor.
     * @return Descriptor.
     */
    private String descriptor() {
        return new XmlValue(this.child(0)).string();
    }

    /**
     * Annotation visible.
     * Is it runtime-visible?
     * @return True if visible at runtime, false otherwise.
     */
    private boolean visible() {
        return (boolean) new XmlValue(this.child(1)).object();
    }

    /**
     * Get child by index.
     * @param index Index.
     * @return Child.
     */
    private XmlNode child(final int index) {
        final List<XmlNode> all = this.node.children().collect(Collectors.toList());
        if (index >= all.size()) {
            throw new IllegalArgumentException(
                String.format(
                    "Annotation %s has no child at index %d",
                    this.node,
                    index
                )
            );
        }
        return all.get(index);
    }

    /**
     * Annotation properties.
     * @return Properties.
     */
    private List<BytecodeAnnotationValue> values() {
        return this.node.children()
            .filter(
                xmlnode -> xmlnode.hasAttribute("base", new JeoFqn("annotation-property").fqn())
            )
            .map(XmlAnnotationValue::new)
            .map(XmlAnnotationValue::bytecode)
            .collect(Collectors.toList());
    }
}
