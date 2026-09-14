/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeAttribute;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeDefaultValue;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.bytecode.LocalVariable;
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
        return new BytecodeMethod(
            this.tryblocks(),
            this.instructions(),
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
            this.attributes()
        );
    }

    private BytecodeAttributes attributes() {
        final List<BytecodeAttribute> all = new ArrayList<>(0);
        all.addAll(new AsmUnknownAttributes(this.node).bytecode());
        final List<LocalVariableNode> variables = this.node.localVariables;
        if (variables != null) {
            all.addAll(
                variables.stream()
                    .map(LocalVariable::new)
                    .collect(Collectors.toList())
            );
        }
        return new BytecodeAttributes(all);
    }

    private BytecodeMaxs maxs() {
        return new BytecodeMaxs(this.node.maxStack, this.node.maxLocals);
    }

    private List<BytecodeEntry> tryblocks() {
        return this.node.tryCatchBlocks.stream().map(
            block -> new BytecodeTryCatchBlock(
                block.start.getLabel().toString(),
                block.end.getLabel().toString(),
                block.handler.getLabel().toString(),
                block.type
            )
        ).collect(Collectors.toList());
    }

    private List<BytecodeEntry> instructions() {
        return Arrays.stream(this.node.instructions.toArray())
            .map(AsmInstruction::new)
            .map(AsmInstruction::bytecode)
            .collect(Collectors.toList());
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
