/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

/**
 * Asm method parameters.
 * @since 0.6
 */
final class AsmMethodParameters {

    /**
     * Method node.
     */
    private final MethodNode node;

    /**
     * Constructor.
     * @param node Method node.
     */
    AsmMethodParameters(final MethodNode node) {
        this.node = node;
    }

    /**
     * Convert asm method to domain method parameters.
     * @return Domain method parameters.
     */
    BytecodeMethodParameters bytecode() {
        final List<ParameterNode> params = Optional.ofNullable(this.node.parameters)
            .orElse(Collections.emptyList());
        final Type[] types = this.types();
        final List<BytecodeMethodParameter> res = new ArrayList<>(types.length);
        for (int index = 0; index < params.size(); ++index) {
            res.add(
                new BytecodeMethodParameter(
                    index,
                    AsmMethodParameters.name(this.node, index),
                    AsmMethodParameters.access(this.node, index),
                    types[index]
                )
            );
        }
        return new BytecodeMethodParameters(res, this.paramAnnotations());
    }

    /**
     * Method parameter types.
     * @return Method parameter types.
     */
    private Type[] types() {
        final Type[] result;
        if (this.node.desc != null) {
            result = Type.getArgumentTypes(this.node.desc);
        } else {
            result = new Type[0];
        }
        return result;
    }

    /**
     * Parameter annotations.
     * @return Parameter annotations.
     */
    private List<BytecodeParamAnnotations> paramAnnotations() {
        final List<AnnotationNode>[] visible = this.node.visibleParameterAnnotations;
        final List<AnnotationNode>[] invisible = this.node.invisibleParameterAnnotations;
        final int max = Math.max(
            Optional.ofNullable(visible).map(a -> a.length).orElse(0),
            Optional.ofNullable(invisible).map(a -> a.length).orElse(0)
        );
        final List<BytecodeParamAnnotations> annotations = new ArrayList<>(0);
        for (int index = 0; index < max; ++index) {
            final AsmAnnotations all = new AsmAnnotations(
                AsmMethodParameters.annotations(visible, index),
                AsmMethodParameters.annotations(invisible, index)
            );
            if (!all.isEmpty()) {
                annotations.add(
                    new BytecodeParamAnnotations(
                        index,
                        all.bytecode()
                    )
                );
            }
        }
        return annotations;
    }

    /**
     * Retrieve parameter annotations from asm method.
     * @param all All parameter annotations.
     * @param index Parameter index.
     * @return Parameter annotations.
     */
    private static List<AnnotationNode> annotations(
        final List<AnnotationNode>[] all, final int index
    ) {
        final List<AnnotationNode> result;
        if (Objects.isNull(all) || all.length <= index) {
            result = new ArrayList<>(0);
        } else {
            result = all[index];
        }
        return result;
    }

    /**
     * Retrieve method parameter access from asm method.
     * @param node Asm method node.
     * @param index Parameter index.
     * @return Parameter access.
     */
    private static int access(final MethodNode node, final int index) {
        final int result;
        if (node.parameters != null && node.parameters.size() > index) {
            result = node.parameters.get(index).access;
        } else {
            result = 0;
        }
        return result;
    }

    /**
     * Retrieve method parameter name from asm method.
     * @param node Asm method node.
     * @param index Parameter index.
     * @return Parameter name.
     */
    private static String name(final MethodNode node, final int index) {
        String result = null;
        if (node.parameters != null && node.parameters.size() > index) {
            final ParameterNode pnode = node.parameters.get(index);
            if (pnode.name != null) {
                result = pnode.name;
            }
        }
        return result;
    }
}
