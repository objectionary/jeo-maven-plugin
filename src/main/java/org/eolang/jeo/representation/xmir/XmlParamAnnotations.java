/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations;

/**
 * Mirrors {@link org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations}.
 * @since 0.15.0
 */
public final class XmlParamAnnotations {

    /**
     * Xmir node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Xmir node.
     */
    XmlParamAnnotations(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode parameter annotations.
     */
    public BytecodeParamAnnotations bytecode() {
        return new BytecodeParamAnnotations(
            Integer.parseInt(this.node.name().split("-")[2]),
            new XmlAnnotations(this.node).bytecode()
        );
    }

    /**
     * Whether this node represents parameter annotations.
     * @return True if it does.
     */
    boolean isParamAnnotations() {
        return this.node.name().startsWith("param-annotations");
    }
}
