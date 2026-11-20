/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.AnnotationScope;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.objectweb.asm.TypePath;

/**
 * Xml representation of a type annotation.
 * Mirror of {@link org.eolang.jeo.representation.directives.DirectivesTypeAnnotation}.
 * Maps to {@link BytecodeTypeAnnotation}.
 *
 * @since 0.15.0
 */
final class XmlTypeAnnotation {

    /**
     * Xml node of type annotation to parse.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node Node to parse
     */
    XmlTypeAnnotation(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     *
     * @param node Node to parse
     */
    private XmlTypeAnnotation(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse to bytecode type annotation.
     *
     * @return Bytecode type annotation
     */
    BytecodeTypeAnnotation bytecode() {
        return new BytecodeTypeAnnotation(
            this.ref(),
            this.path(),
            this.descriptor(),
            this.visible(),
            this.values(),
            this.scopes()
        );
    }

    // Scopes of the annotated local variable.
    // A single scope without a start label borrows it from the enclosing
    // context, a scope with a start label carries all three values.
    private List<AnnotationScope> scopes() {
        final List<BytecodeLabel> starts = this.namedChildren("scope-start")
            .stream()
            .map(XmlTypeAnnotation::label)
            .collect(Collectors.toList());
        final List<BytecodeLabel> ends = this.namedChildren("scope-end")
            .stream()
            .map(XmlTypeAnnotation::label)
            .collect(Collectors.toList());
        final List<Integer> slots = this.namedChildren("scope-slot")
            .stream()
            .map(XmlTypeAnnotation::slot)
            .collect(Collectors.toList());
        final List<AnnotationScope> result = new ArrayList<>(0);
        if (starts.isEmpty() && !ends.isEmpty()) {
            result.add(new AnnotationScope(null, ends.get(0), slots.get(0)));
        }
        for (int position = 0; position < starts.size(); ++position) {
            result.add(
                new AnnotationScope(
                    starts.get(position), ends.get(position), slots.get(position)
                )
            );
        }
        return result;
    }

    private static BytecodeLabel label(final XmlNode xml) {
        return new BytecodeLabel(new XmlValue(xml).string());
    }

    private static int slot(final XmlNode xml) {
        return (int) new XmlValue(xml).object();
    }

    private List<XmlNode> namedChildren(final String name) {
        return this.node.children()
            .filter(child -> name.equals(child.attribute("name").orElse("")))
            .collect(Collectors.toList());
    }

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

    private List<BytecodeAnnotationValue> values() {
        return this.node.children()
            .map(XmlAnnotationValue::new)
            .filter(XmlAnnotationValue::isValue)
            .map(XmlAnnotationValue::bytecode)
            .collect(Collectors.toList());
    }
}
