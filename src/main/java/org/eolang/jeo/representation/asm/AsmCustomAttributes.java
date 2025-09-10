/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeCustomAttribute;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.RecordComponentNode;

/**
 * ASM custom attributes.
 * @since 0.15.0
 */
public final class AsmCustomAttributes {

    /**
     * All custom attributes.
     */
    private final List<Attribute> all;

    /**
     * Constructor.
     * @param node Class node.
     */
    public AsmCustomAttributes(final ClassNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     * @param node Field node.
     */
    public AsmCustomAttributes(final FieldNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     * @param node Method node.
     */
    public AsmCustomAttributes(final MethodNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     * @param node Record component node.
     */
    public AsmCustomAttributes(final RecordComponentNode node) {
        this(node.attrs);
    }

    /**
     * Bytecode custom attributes.
     * @param all List of bytecode custom attributes
     */
    private AsmCustomAttributes(final List<Attribute> all) {
        this.all = Optional.ofNullable(all).orElse(Collections.emptyList());
    }

    /**
     * Convert to domain bytecode representation.
     * @return Bytecode representation.
     */
    public List<BytecodeAttribute> bytecode() {
        return this.all.stream()
            .map(a -> AsmCustomAttributes.parse())
            .collect(Collectors.toList());
    }

    /**
     * Parse a single attrib¬ute.
     * @return Bytecode attribute.
     */
    private static BytecodeAttribute parse() {
        return new BytecodeCustomAttribute();
    }
}
