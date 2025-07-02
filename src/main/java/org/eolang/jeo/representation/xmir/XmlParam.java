/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.EncodedString;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.objectweb.asm.Type;

/**
 * Xmir representation of a method parameter.
 * @since 0.4
 */
public final class XmlParam {

    /**
     * Root node from which we will get all required data.
     */
    private final XmlJeoObject root;

    /**
     * Constructor.
     * @param root Parameter xml node.
     */
    XmlParam(final XmlNode root) {
        this(new XmlJeoObject(root));
    }

    /**
     * Constructor.
     * @param root Parameter xml node.
     */
    private XmlParam(final XmlJeoObject root) {
        this.root = root;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode method parameter.
     */
    public BytecodeMethodParameter bytecode() {
        return new BytecodeMethodParameter(
            this.index(),
            this.pure(),
            this.access(),
            this.type(),
            this.annotations()
        );
    }

    /**
     * Type of the parameter.
     * @return Type.
     */
    private Type type() {
        return Type.getType(new EncodedString(this.child("type").string()).decode());
    }

    /**
     * Pure name of the parameter.
     * @return Name.
     */
    private String pure() {
        return this.name();
    }

    /**
     * Access modifier of the parameter.
     * @return Access.
     */
    private int access() {
        return (int) this.child("access").object();
    }

    /**
     * Index of the parameter in the method.
     * @return Index.
     */
    private int index() {
        return (int) this.child("index").object();
    }

    /**
     * Annotations of the parameter.
     * @return Annotations.
     */
    private BytecodeAnnotations annotations() {
        return this.root.children()
            .filter(
                node -> node.attribute("as")
                    .map(name -> name.startsWith("param-annotations-"))
                    .orElse(false)
            )
            .findFirst()
            .map(XmlAnnotations::new)
            .map(XmlAnnotations::bytecode)
            .orElse(new BytecodeAnnotations());
    }

    /**
     * Child node with the given name.
     * @param name Name of the child node.
     * @return Child node.
     */
    private XmlValue child(final String name) {
        return new XmlValue(
            this.root.children()
                .filter(node -> node.attribute("as").map(name::equals).orElse(false))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalStateException(
                        String.format(
                            "Child with attribute 'as'='%s' not found in node '%s'", name,
                            this.root
                        )
                    )
                )
        );
    }

    /**
     * Name attribute of the parameter.
     * @return Name attribute.
     */
    private String name() {
        return this.root.attribute("as").orElseThrow(
            () -> new IllegalStateException(
                String.format("'name' attribute is not present in xml param %n%s%n", this.root)
            )
        );
    }
}
