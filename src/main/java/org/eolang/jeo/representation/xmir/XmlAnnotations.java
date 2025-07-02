/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;

/**
 * Xmir annotations.
 * @since 0.1
 */
public class XmlAnnotations {

    /**
     * XML node representing annotations.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param xmlnode XML node.
     */
    XmlAnnotations(final XmlNode xmlnode) {
        this.node = new XmlJeoObject(xmlnode);
    }

    /**
     * Convert to bytecode.
     * @return Bytecode annotations.
     */
    public BytecodeAnnotations bytecode() {
        return new BytecodeAnnotations(this.all().stream().map(XmlAnnotation::bytecode));
    }

    /**
     * All annotations.
     * @return Annotations.
     */
    private List<XmlAnnotation> all() {
        return this.node.children()
            .map(XmlAnnotation::new)
            .collect(Collectors.toList());
    }
}
