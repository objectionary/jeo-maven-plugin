/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

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
 * @since 0.6
 */
final class AsmMethod {

    /**
     * Method node.
     */
    private final MethodNode node;

    /**
     * Constructor.
     * @param node Method node.
     */
    AsmMethod(final MethodNode node) {
        this.node = node;
    }

    /**
     * Convert asm method to domain method.
     * @return Domain method.
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

    /**
     * Convert asm method to domain method attributes.
     * @return Domain method attributes.
     */
    private BytecodeAttributes attributes() {
        final List<LocalVariableNode> variables = this.node.localVariables;
        final BytecodeAttributes result;
        if (variables == null) {
            result = new BytecodeAttributes();
        } else {
            result = new BytecodeAttributes(
                variables.stream()
                    .map(LocalVariable::new)
                    .toArray(BytecodeAttribute[]::new)
            );
        }
        return result;
    }

    /**
     * Convert asm method to domain method maxs.
     * @return Domain method maxs.
     */
    private BytecodeMaxs maxs() {
        return new BytecodeMaxs(this.node.maxStack, this.node.maxLocals);
    }

    /**
     * Convert asm method to domain method tryblocks.
     * @return Domain method tryblocks.
     */
    private List<BytecodeEntry> tryblocks() {
        return this.node.tryCatchBlocks.stream()
            .map(
                block -> new BytecodeTryCatchBlock(
                    block.start.getLabel().toString(),
                    block.end.getLabel().toString(),
                    block.handler.getLabel().toString(),
                    block.type
                )
            )
            .collect(Collectors.toList());
    }

    /**
     * Convert asm class to domain class.
     * @return Domain class.
     */
    private List<BytecodeEntry> instructions() {
        return Arrays.stream(this.node.instructions.toArray())
            .map(AsmInstruction::new)
            .map(AsmInstruction::bytecode)
            .collect(Collectors.toList());
    }

    /**
     * Convert asm default value to domain default value.
     * @return Domain default value.
     */
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
