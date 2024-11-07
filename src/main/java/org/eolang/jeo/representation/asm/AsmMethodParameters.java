/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
                    AsmMethodParameters.paramName(this.node, index),
                    AsmMethodParameters.paramAccess(this.node, index),
                    types[index],
                    new AsmAnnotations(
                        AsmMethodParameters.annotations(
                            this.node.visibleParameterAnnotations, index),
                        AsmMethodParameters.annotations(
                            this.node.invisibleParameterAnnotations, index)
                    ).annotations()
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
        if (Objects.isNull(all)) {
            result = new ArrayList<>(0);
        } else {
            if (all.length > index) {
                result = all[index];
            } else {
                result = new ArrayList<>(0);
            }
        }
        return result;
    }

    /**
     * Retrieve method parameter access from asm method.
     * @param node Asm method node.
     * @param index Parameter index.
     * @return Parameter access.
     */
    private static int paramAccess(final MethodNode node, final int index) {
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
    private static String paramName(final MethodNode node, final int index) {
        final String result;
        if (node.parameters != null && node.parameters.size() > index) {
            result = node.parameters.get(index).name;
        } else {
            result = String.format("arg%d", index);
        }
        return result;
    }

}
