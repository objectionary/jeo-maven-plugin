/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;
import org.objectweb.asm.tree.RecordComponentNode;
import org.objectweb.asm.tree.TypeAnnotationNode;

/**
 * Asm type annotations.
 * @since 0.15.0
 */
final class AsmTypeAnnotations {

    /**
     * Visible type annotations.
     */
    private final List<TypeAnnotationNode> visible;

    /**
     * Invisible type annotations.
     */
    private final List<TypeAnnotationNode> invisible;

    /**
     * Constructor.
     * @param comp Record component node.
     */
    AsmTypeAnnotations(final RecordComponentNode comp) {
        this(comp.visibleTypeAnnotations, comp.invisibleTypeAnnotations);
    }

    /**
     * Constructor.
     * @param visible Visible type annotations.
     * @param invisible Invisible type annotations.
     */
    private AsmTypeAnnotations(
        final List<TypeAnnotationNode> visible, final List<TypeAnnotationNode> invisible
    ) {
        this.visible = visible;
        this.invisible = invisible;
    }

    /**
     * Convert to bytecode type annotations.
     * @return Bytecode type annotations.
     */
    public BytecodeTypeAnnotations bytecode() {
        final Stream.Builder<BytecodeTypeAnnotation> annotations = Stream.builder();
        if (this.visible != null) {
            this.visible.stream()
                .map(ann -> AsmTypeAnnotations.parse(ann, true))
                .forEach(annotations);
        }
        if (this.invisible != null) {
            this.invisible.stream()
                .map(ann -> AsmTypeAnnotations.parse(ann, false))
                .forEach(annotations);
        }
        return new BytecodeTypeAnnotations(annotations.build().collect(Collectors.toList()));
    }

    /**
     * Parse type annotation node.
     * @param node Type annotation node.
     * @param visible Visibility of the annotation.
     * @return Bytecode type annotation.
     */
    private static BytecodeTypeAnnotation parse(
        final TypeAnnotationNode node, final boolean visible
    ) {
        return new BytecodeTypeAnnotation(
            node.typeRef,
            node.typePath,
            node.desc,
            visible,
            new AsmAnnotationValues(node.values).bytecode()
        );
    }
}
