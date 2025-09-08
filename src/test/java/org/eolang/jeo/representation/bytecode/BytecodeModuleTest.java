/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import org.eolang.jeo.representation.asm.AsmModule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;

/**
 * Test case for {@link BytecodeModule}.
 * @since 0.15.0
 */
final class BytecodeModuleTest {

    @Test
    void writesModuleToAsmClass() {
        final BytecodeModule original = new BytecodeModule(
            "name",
            0,
            "0.1.0",
            "main",
            Collections.singletonList("org.eolang.jeo"),
            Collections.singletonList(new BytecodeModuleRequired("required", 0, "0.1.0")),
            Collections.singletonList(
                new BytecodeModuleExported("exported", 1, Collections.singletonList("m1"))
            ),
            Collections.singletonList(
                new BytecodeModuleOpened("opened", 2, Collections.singletonList("m2"))
            ),
            Collections.singletonList(
                new BytecodeModuleProvided("provided", Collections.singletonList("impl"))
            ),
            Collections.singletonList("used")
        );
        final ClassNode node = new ClassNode();
        original.write(node);
        MatcherAssert.assertThat(
            "We expect the module to be written to ASM class and read back correctly",
            new AsmModule(node.module).bytecode(),
            Matchers.equalTo(original)
        );
    }
}
