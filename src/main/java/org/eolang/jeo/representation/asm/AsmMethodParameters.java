/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

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
        final Type[] types = Type.getArgumentTypes(this.node.desc);
        final List<BytecodeMethodParameter> params = new ArrayList<>(types.length);
        for (int index = 0; index < types.length; ++index) {
            params.add(
                new BytecodeMethodParameter(
                    index,
                    AsmMethodParameters.name(this.node, index),
                    AsmMethodParameters.access(this.node, index),
                    types[index],
                    new AsmAnnotations(
                        AsmMethodParameters.annotations(
                            this.node.visibleParameterAnnotations, index
                        ),
                        AsmMethodParameters.annotations(
                            this.node.invisibleParameterAnnotations, index
                        )
                    ).bytecode()
                )
            );
        }
        return new BytecodeMethodParameters(params);
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
        final String result;
        if (node.parameters != null && node.parameters.size() > index) {
            result = node.parameters.get(index).name;
        } else {
            result = String.format("arg%d", index);
        }
        return result;
    }
}
