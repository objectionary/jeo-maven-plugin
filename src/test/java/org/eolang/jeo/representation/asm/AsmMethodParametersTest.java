/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
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
                        "arg0",
                        access,
                        Type.INT_TYPE,
                        new BytecodeAnnotations()
                    )
                )
            )
        );
    }
}
