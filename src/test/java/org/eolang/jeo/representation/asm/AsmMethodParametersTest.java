/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

/**
 * Test cases for {@link AsmMethodParameters}.
 * @since 0.14.0
 */
final class AsmMethodParametersTest {

    @Test
    void createsBytecodeMethodParametersWithNullableName() {
        final MethodNode node = new MethodNode();
        final int access = Opcodes.ACC_PUBLIC;
        node.desc = "(I)V";
        node.parameters = new ArrayList<>(1);
        node.parameters.add(new ParameterNode(null, access));
        MatcherAssert.assertThat(
            "We expected to create BytecodeMethodParameters with a nullable name",
            new AsmMethodParameters(node).bytecode(),
            Matchers.equalTo(
                new BytecodeMethodParameters(
                    new BytecodeMethodParameter(
                        0,
                        null,
                        access,
                        Type.INT_TYPE
                    )
                )
            )
        );
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void convertsMethodParamAnnotations() {
        final MethodNode node = new MethodNode();
        final String descriptor = "Ljava/lang/Deprecated()";
        node.visibleParameterAnnotations = new List[1];
        node.visibleParameterAnnotations[0] = Collections.singletonList(
            new AnnotationNode(descriptor)
        );
        node.invisibleParameterAnnotations = new List[1];
        node.invisibleParameterAnnotations[0] = Collections.singletonList(
            new AnnotationNode(descriptor)
        );
        MatcherAssert.assertThat(
            "We expect parameter annotations to be successfully parsed from bytecode",
            new AsmMethodParameters(node).bytecode(),
            Matchers.equalTo(
                new BytecodeMethodParameters(
                    new ArrayList<>(0),
                    Collections.singletonList(
                        new BytecodeParamAnnotations(
                            0,
                            new BytecodeAnnotations(
                                new BytecodeAnnotation(descriptor, true),
                                new BytecodeAnnotation(descriptor, false)
                            )
                        )
                    )
                )
            )
        );
    }
}
