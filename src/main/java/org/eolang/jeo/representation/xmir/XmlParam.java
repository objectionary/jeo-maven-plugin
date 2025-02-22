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
    private final XmlNode root;

    /**
     * Constructor.
     * @param root Parameter xml node.
     */
    public XmlParam(final XmlNode root) {
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
        return Type.getType(new EncodedString(this.suffix(1)).decode());
    }

    /**
     * Pure name of the parameter.
     * @return Name.
     */
    private String pure() {
        return this.suffix(2);
    }

    /**
     * Access modifier of the parameter.
     * @return Access.
     */
    private int access() {
        return Integer.parseInt(this.suffix(3));
    }

    /**
     * Index of the parameter in the method.
     * @return Index.
     */
    private int index() {
        return Integer.parseInt(this.suffix(4));
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
     * Get the suffix of the name attribute.
     * position[0]-position[1]-position[2].
     * param      -type       -index
     * @param position Position of the suffix.
     * @return Suffix.
     */
    private String suffix(final int position) {
        return this.name().split("-")[position];
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
