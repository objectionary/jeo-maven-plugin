/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.AnnotationScope;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotations;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableAnnotationNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Asm local variable annotations.
 * Extracts type annotations that target local variables from an ASM method.
 * All scopes of one annotation are kept together in a single annotation, so
 * an annotation with a split live range (which ASM models with parallel
 * start/end/index lists) neither splits into several single-range annotations
 * nor duplicates when two local variable table entries match different ranges.
 *
 * @since 0.15.0
 */
final class AsmLocalVariableAnnotations {

    /**
     * Asm method node.
     */
    private final MethodNode node;

    /**
     * Label identifiers.
     */
    private final AsmLabelIds ids;

    /**
     * Constructor.
     *
     * @param method Asm method node
     * @param label Label identifiers
     */
    AsmLocalVariableAnnotations(final MethodNode method, final AsmLabelIds label) {
        this.node = method;
        this.ids = label;
    }

    /**
     * Type annotations that target local variables with a local variable
     * table entry. The whole annotation is attached to the local variable
     * matched by its first scope.
     *
     * @return Annotations per local variable node
     */
    Map<LocalVariableNode, BytecodeTypeAnnotations> attached() {
        final Map<LocalVariableNode, List<BytecodeTypeAnnotation>> result = new HashMap<>(0);
        for (final LocalVariableAnnotationNode annotation : this.all()) {
            final List<Integer> matched = this.matched(annotation);
            if (matched.isEmpty()) {
                continue;
            }
            final LocalVariableNode owner = this.owner(annotation, matched.get(0));
            result.computeIfAbsent(owner, key -> new ArrayList<>(0)).add(
                this.annotation(annotation, annotation.index.size() == 1)
            );
        }
        return result.entrySet().stream().collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> new BytecodeTypeAnnotations(entry.getValue())
            )
        );
    }

    /**
     * Type annotations that target local variable scopes without a local
     * variable table entry. Such an annotation is written next to the label
     * that opens its scope and carries all its scope ranges inside, since
     * they cannot be borrowed from a table entry.
     *
     * @return Annotations per scope opening label
     */
    Map<LabelNode, List<BytecodeTypeAnnotation>> unattached() {
        final Map<LabelNode, List<BytecodeTypeAnnotation>> result = new HashMap<>(0);
        for (final LocalVariableAnnotationNode annotation : this.all()) {
            if (!this.matched(annotation).isEmpty()) {
                continue;
            }
            final BytecodeTypeAnnotation detached;
            if (annotation.index.size() == 1) {
                detached = new BytecodeTypeAnnotation(
                    annotation.typeRef,
                    annotation.typePath,
                    annotation.desc,
                    this.visible(annotation),
                    new AsmAnnotationValues(annotation.values).bytecode(),
                    Collections.singletonList(
                        new AnnotationScope(
                            null,
                            this.ids.label(annotation.end.get(0)),
                            annotation.index.get(0)
                        )
                    )
                );
            } else {
                detached = this.annotation(annotation, false);
            }
            result.computeIfAbsent(
                annotation.start.get(0),
                key -> new ArrayList<>(0)
            ).add(detached);
        }
        return result;
    }

    // All local variable annotations of the method, visible first.
    private List<LocalVariableAnnotationNode> all() {
        final List<LocalVariableAnnotationNode> all = new ArrayList<>(0);
        if (this.node.visibleLocalVariableAnnotations != null) {
            all.addAll(this.node.visibleLocalVariableAnnotations);
        }
        if (this.node.invisibleLocalVariableAnnotations != null) {
            all.addAll(this.node.invisibleLocalVariableAnnotations);
        }
        return all;
    }

    // Whether the annotation is visible at runtime.
    private boolean visible(final LocalVariableAnnotationNode annotation) {
        final List<LocalVariableAnnotationNode> visible =
            this.node.visibleLocalVariableAnnotations;
        return visible != null && visible.contains(annotation);
    }

    // Build a bytecode annotation from an ASM annotation node.
    // The range of a single-scope annotation is borrowed from the context:
    // the enclosing local variable entry, or the scope opening label.
    private BytecodeTypeAnnotation annotation(
        final LocalVariableAnnotationNode annotation,
        final boolean single
    ) {
        final List<AnnotationScope> scopes;
        if (single) {
            scopes = Collections.emptyList();
        } else {
            scopes = this.ranges(annotation);
        }
        return new BytecodeTypeAnnotation(
            annotation.typeRef,
            annotation.typePath,
            annotation.desc,
            this.visible(annotation),
            new AsmAnnotationValues(annotation.values).bytecode(),
            scopes
        );
    }

    // All scopes of the annotation with their start labels.
    private List<AnnotationScope> ranges(final LocalVariableAnnotationNode annotation) {
        final List<AnnotationScope> ranges = new ArrayList<>(0);
        for (int scope = 0; scope < annotation.index.size(); ++scope) {
            ranges.add(
                new AnnotationScope(
                    this.ids.label(annotation.start.get(scope)),
                    this.ids.label(annotation.end.get(scope)),
                    annotation.index.get(scope)
                )
            );
        }
        return ranges;
    }

    // Indexes of the scopes that match a local variable table entry.
    private List<Integer> matched(final LocalVariableAnnotationNode annotation) {
        final List<Integer> result = new ArrayList<>(0);
        final List<LocalVariableNode> variables = this.node.localVariables;
        if (variables != null) {
            for (int scope = 0; scope < annotation.index.size(); ++scope) {
                if (this.matches(annotation, scope)) {
                    result.add(scope);
                }
            }
        }
        return result;
    }

    // Local variable matched by the given annotation scope.
    private LocalVariableNode owner(
        final LocalVariableAnnotationNode annotation,
        final int scope
    ) {
        final List<LocalVariableNode> variables = this.node.localVariables;
        for (final LocalVariableNode variable : variables) {
            if (annotation.start.get(scope).equals(variable.start)
                && annotation.end.get(scope).equals(variable.end)
                && annotation.index.get(scope) == variable.index) {
                return variable;
            }
        }
        throw new IllegalStateException(
            String.format(
                "Scope %d of annotation %s does not match any local variable",
                scope,
                annotation.desc
            )
        );
    }

    // Whether the given annotation scope matches some local variable entry.
    private boolean matches(final LocalVariableAnnotationNode annotation, final int scope) {
        final List<LocalVariableNode> variables = this.node.localVariables;
        boolean result = false;
        if (variables != null) {
            for (final LocalVariableNode variable : variables) {
                if (annotation.start.get(scope).equals(variable.start)
                    && annotation.end.get(scope).equals(variable.end)
                    && annotation.index.get(scope) == variable.index) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
