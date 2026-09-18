/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.RecordComponentNode;

/**
 * ASM custom attributes.
 *
 * @since 0.15.0
 */
public final class AsmUnknownAttributes {

    /**
     * All custom attributes.
     */
    private final List<Attribute> all;

    /**
     * Constructor.
     *
     * @param node Class node
     */
    public AsmUnknownAttributes(final ClassNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     *
     * @param node Field node
     */
    public AsmUnknownAttributes(final FieldNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     *
     * @param node Method node
     */
    public AsmUnknownAttributes(final MethodNode node) {
        this(node.attrs);
    }

    /**
     * Constructor.
     *
     * @param node Record component node
     */
    public AsmUnknownAttributes(final RecordComponentNode node) {
        this(node.attrs);
    }

    /**
     * Bytecode custom attributes.
     *
     * @param all List of bytecode custom attributes
     */
    private AsmUnknownAttributes(final List<Attribute> all) {
        this.all = all;
    }

    /**
     * All prototypes of custom attributes.
     * Pay Attention! We don't have `ScalaInlineInfo` here because
     * this attribute requires special handling. Actually, we should
     * rebuild `ScalaInlineInfo` after rebuilding the constant pool.
     * Since we can't rebuild the `ScalaInlineInfo` attribute, we
     * simply ignore it.
     *
     * @return All prototypes of custom attributes
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Attribute[] prototypes() {
        return new Attribute[]{
            new AsmUnknownAttribute("ScalaSig"),
            new AsmUnknownAttribute("Scala"),
            new AsmUnknownAttribute("TASTY"),
            new AsmUnknownAttribute("ModuleTarget"),
        };
    }

    /**
     * All prototypes declared by a class, including attributes not known to ASM.
     *
     * @param reader Class reader
     * @return Prototypes for every unknown attribute
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Attribute[] prototypes(final ClassReader reader) {
        final Set<String> types = new LinkedHashSet<>();
        for (final Attribute prototype : AsmUnknownAttributes.prototypes()) {
            types.add(prototype.type);
        }
        reader.accept(
            new AttributesVisitor(types),
            ClassReader.SKIP_DEBUG
        );
        return types.stream().map(AsmUnknownAttribute::new).toArray(Attribute[]::new);
    }

    /** Visitor that collects custom attribute names from a class. */
    private static final class AttributesVisitor extends ClassVisitor {

        /** Names collected so far. */
        private final Set<String> types;

        AttributesVisitor(final Set<String> types) {
            super(Opcodes.ASM9);
            this.types = types;
        }

        @Override
        public void visitAttribute(final Attribute attribute) {
            this.types.add(attribute.type);
        }

        @Override
        public FieldVisitor visitField(
            final int access, final String name, final String descriptor,
            final String signature, final Object value
        ) {
            return new FieldAttributesVisitor(this.types);
        }

        @Override
        public MethodVisitor visitMethod(
            final int access, final String name, final String descriptor,
            final String signature, final String[] exceptions
        ) {
            return new MethodAttributesVisitor(this.types);
        }
    }

    /** Visitor that collects custom attributes from a field. */
    private static final class FieldAttributesVisitor extends FieldVisitor {

        /** Names collected so far. */
        private final Set<String> types;

        FieldAttributesVisitor(final Set<String> types) {
            super(Opcodes.ASM9);
            this.types = types;
        }

        @Override
        public void visitAttribute(final Attribute attribute) {
            this.types.add(attribute.type);
        }
    }

    /** Visitor that collects custom attributes from a method. */
    private static final class MethodAttributesVisitor extends MethodVisitor {

        /** Names collected so far. */
        private final Set<String> types;

        MethodAttributesVisitor(final Set<String> types) {
            super(Opcodes.ASM9);
            this.types = types;
        }

        @Override
        public void visitAttribute(final Attribute attribute) {
            this.types.add(attribute.type);
        }
    }

    /**
     * Convert to domain bytecode representation.
     *
     * @return Bytecode representation
     */
    public List<BytecodeAttribute> bytecode() {
        return this.attributes().stream()
            .filter(AsmUnknownAttributes::isUnknown)
            .map(AsmUnknownAttributes::asUnknown)
            .map(AsmUnknownAttribute::bytecode)
            .collect(Collectors.toList());
    }

    private static boolean isUnknown(final Attribute attr) {
        return attr.getClass() == AsmUnknownAttribute.class;
    }

    private static AsmUnknownAttribute asUnknown(final Attribute attr) {
        return (AsmUnknownAttribute) attr;
    }

    private List<Attribute> attributes() {
        final List<Attribute> result;
        if (this.all == null) {
            result = Collections.emptyList();
        } else {
            result = this.all;
        }
        return result;
    }
}
