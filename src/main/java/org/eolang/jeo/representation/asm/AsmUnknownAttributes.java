/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
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
     * Pay Attention! We don't have `ScalaInlineInfo` here because
     * this attribute requires special handling. Actually, we should
     * rebuild `ScalaInlineInfo` after rebuilding the constant pool.
     * Since we can't rebuild the `ScalaInlineInfo` attribute, we
     * simply ignore it.
     * @return All prototypes of custom attributes.
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
     * All attribute prototypes declared in a class file.
     * @param reader Class reader
     * @return Prototypes for every UTF-8 attribute name
     */
    public static Attribute[] prototypes(final ClassReader reader) {
        final Set<String> names = new HashSet<>();
        for (int idx = 1; idx < reader.getItemCount(); ++idx) {
            final int item = reader.getItem(idx);
            if (item > 0 && reader.readByte(item - 1) == 1) {
                final int length = reader.readUnsignedShort(item);
                final byte[] bytes = new byte[length];
                for (int pos = 0; pos < length; ++pos) {
                    bytes[pos] = (byte) reader.readByte(item + 2 + pos);
                }
                names.add(new String(bytes, StandardCharsets.UTF_8));
            }
        }
        final List<Attribute> result = new ArrayList<>(names.size());
        names.forEach(name -> result.add(new AsmUnknownAttribute(name)));
        return result.toArray(new Attribute[0]);
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
