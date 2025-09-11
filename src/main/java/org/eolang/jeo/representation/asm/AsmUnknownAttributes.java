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
import org.objectweb.asm.Attribute;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.RecordComponentNode;

/**
 * ASM custom attributes.
 * @since 0.15.0
 */
public final class AsmUnknownAttributes {

    /**
     * All custom attributes.
     */
    private final List<Attribute> all;

    /**
     * Constructor.
     * @param node Class node.
     */
    public AsmUnknownAttributes(final ClassNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     * @param node Field node.
     */
    public AsmUnknownAttributes(final FieldNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     * @param node Method node.
     */
    public AsmUnknownAttributes(final MethodNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     * @param node Record component node.
     */
    public AsmUnknownAttributes(final RecordComponentNode node) {
        this(node.attrs);
    }

    /**
     * Bytecode custom attributes.
     * @param all List of bytecode custom attributes
     */
    private AsmUnknownAttributes(final List<Attribute> all) {
        this.all = Optional.ofNullable(all).orElse(Collections.emptyList());
    }

    /**
     * All prototypes of custom attributes.
     * @return All prototypes of custom attributes.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Attribute[] prototypes() {
        return new Attribute[]{
            new AsmUnknownAttribute("ScalaSig"),
            new AsmUnknownAttribute("ScalaInlineInfo"),
            new AsmUnknownAttribute("Scala"),
            new AsmUnknownAttribute("scala.reflect.ScalaSignature"),
            new AsmUnknownAttribute("TASTY"),
            new AsmUnknownAttribute("ModuleTarget"),
        };
    }

    /**
     * Convert to domain bytecode representation.
     * @return Bytecode representation.
     */
    public List<BytecodeAttribute> bytecode() {
        return this.all.stream()
            .filter(AsmUnknownAttributes::isUnknown)
            .map(AsmUnknownAttributes::asUnknown)
            .map(AsmUnknownAttribute::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Whether the attribute is unknown.
     * @param attr Attribute.
     * @return True if the attribute is unknown, false otherwise.
     */
    private static boolean isUnknown(final Attribute attr) {
        return attr.getClass() == AsmUnknownAttribute.class;
    }

    /**
     * Cast to unknown attribute.
     * @param attr Attribute.
     * @return Unknown attribute.
     */
    private static AsmUnknownAttribute asUnknown(final Attribute attr) {
        return (AsmUnknownAttribute) attr;
    }
}
