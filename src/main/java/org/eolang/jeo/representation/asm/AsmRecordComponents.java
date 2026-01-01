/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeRecordComponent;
import org.objectweb.asm.tree.RecordComponentNode;

/**
 * Asm record components.
 * @since 0.15.0
 */
final class AsmRecordComponents {

    /**
     * Nodes of record components.
     */
    private final List<RecordComponentNode> nodes;

    /**
     * Constructor.
     * @param components List of record component nodes
     */
    AsmRecordComponents(final List<RecordComponentNode> components) {
        this.nodes = Optional.ofNullable(components).orElse(Collections.emptyList());
    }

    /**
     * Bytecode record components.
     * @return List of bytecode record components
     */
    public List<BytecodeRecordComponent> bytecode() {
        return this.nodes.stream()
            .map(
                comp -> new BytecodeRecordComponent(
                    comp.name,
                    comp.descriptor,
                    comp.signature,
                    new AsmAnnotations(comp).bytecode(),
                    new AsmTypeAnnotations(comp).bytecode()
                )
            ).collect(Collectors.toList());
    }
}
