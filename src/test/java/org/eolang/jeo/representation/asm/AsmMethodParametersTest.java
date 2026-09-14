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
import org.junit.jupiter.api.Assertions;
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
    void refusesATableLongerThanTheDescriptor() {
        final MethodNode node = new MethodNode();
        node.name = "foo";
        node.desc = "(I)V";
        node.parameters = new ArrayList<>(3);
        for (int index = 0; index < 3; ++index) {
            node.parameters.add(new ParameterNode("p", Opcodes.ACC_FINAL));
        }
        MatcherAssert.assertThat(
            "the message must name the method and both counts",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new AsmMethodParameters(node).bytecode(),
                "a table longer than the descriptor must be refused"
            ).getMessage(),
            Matchers.equalTo("Method 'foo(I)V' declares 1 parameters, while its table has 3")
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
