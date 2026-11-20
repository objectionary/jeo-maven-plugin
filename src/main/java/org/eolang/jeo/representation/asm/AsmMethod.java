/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;
import org.eolang.jeo.representation.bytecode.LocalVariable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Asm method.
 * Asm parser for a method.
 *
 * @since 0.6
 */
final class AsmMethod {

    /**
     * Method node.
     */
    private final MethodNode node;

    /**
     * Constructor.
     *
     * @param node Method node
     */
    AsmMethod(final MethodNode node) {
        this.node = node;
    }

    /**
     * Convert asm method to domain method.
     *
     * @return Domain method
     */
    BytecodeMethod bytecode() {
        final AsmLabelIds ids = new AsmLabelIds();
        final AsmLocalVariableAnnotations annotations =
            new AsmLocalVariableAnnotations(this.node, ids);
        return new BytecodeMethod(
            this.tryblocks(ids),
            this.instructions(ids, annotations.unattached()),
            new AsmAnnotations(this.node).bytecode(),
            new BytecodeMethodProperties(
                this.node.access,
                this.node.name,
                this.node.desc,
                this.node.signature,
                new AsmMethodParameters(this.node).bytecode(),
                this.node.exceptions.toArray(new String[0])
            ),
            this.defvalue(),
            this.maxs(),
            this.attributes(ids, annotations.attached())
        );
    }

    private BytecodeAttributes attributes(
        final AsmLabelIds ids,
        final Map<LocalVariableNode, BytecodeTypeAnnotations> attached
    ) {
        final List<BytecodeAttribute> all = new ArrayList<>(0);
        all.addAll(new AsmUnknownAttributes(this.node).bytecode());
        final List<LocalVariableNode> variables = this.node.localVariables;
        if (variables != null) {
            all.addAll(
                variables.stream().map(
                    variable -> new LocalVariable(
                        variable.index,
                        variable.name,
                        variable.desc,
                        variable.signature,
                        ids.label(variable.start),
                        ids.label(variable.end),
                        attached.getOrDefault(variable, new BytecodeTypeAnnotations())
                    )
                ).collect(Collectors.toList())
            );
        }
        return new BytecodeAttributes(all);
    }

    private BytecodeMaxs maxs() {
        return new BytecodeMaxs(this.node.maxStack, this.node.maxLocals);
    }

    private List<BytecodeEntry> tryblocks(final AsmLabelIds ids) {
        return this.node.tryCatchBlocks.stream().map(
            block -> new BytecodeTryCatchBlock(
                ids.label(block.start).uid(),
                ids.label(block.end).uid(),
                ids.label(block.handler).uid(),
                block.type,
                new AsmTypeAnnotations(block).bytecode()
            )
        ).collect(Collectors.toList());
    }

    private List<BytecodeEntry> instructions(
        final AsmLabelIds ids,
        final Map<LabelNode, List<BytecodeTypeAnnotation>> unattached
    ) {
        final List<BytecodeEntry> entries = new ArrayList<>(0);
        for (final AbstractInsnNode instruction : this.node.instructions.toArray()) {
            final BytecodeEntry entry = new AsmInstruction(instruction, ids).bytecode();
            final List<BytecodeTypeAnnotation> scopes = unattached.get(instruction);
            if (scopes == null || scopes.isEmpty()) {
                entries.add(entry);
            } else {
                entries.add(
                    BytecodeLabel.class.cast(entry).types(new BytecodeTypeAnnotations(scopes))
                );
            }
        }
        return entries;
    }

    private List<BytecodeDefaultValue> defvalue() {
        final List<BytecodeDefaultValue> result;
        if (this.node.annotationDefault == null) {
            result = Collections.emptyList();
        } else {
            result = Collections.singletonList(
                new BytecodeDefaultValue(
                    new AsmAnnotationProperty(this.node.annotationDefault).bytecode()
                )
            );
        }
        return result;
    }
}
