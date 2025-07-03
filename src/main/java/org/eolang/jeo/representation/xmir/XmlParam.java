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
            this.name(),
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
    private String name() {
        return this.root.name();
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
            .map(XmlJeoObject::new)
            .filter(XmlJeoObject::named)
            .filter(node -> node.name().startsWith("param-annotations-"))
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
                .filter(node -> XmlParam.hasName(node, name))
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
     * Check if the node has the specified name.
     * @param node XML node to check.
     * @param name Name to check against the node's attributes.
     * @return True if the node has the specified name, false otherwise.
     */
    private static boolean hasName(final XmlNode node, final String name) {
        return node.attribute("name").map(name::equals).orElse(false);
    }
}
