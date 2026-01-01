/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

/**
 * Test case for {@link ClassNameVisitor}.
 * @since 0.1.0
 */
final class ClassNameVisitorTest {

    @Test
    void retrievesClassName() {
        final ClassNameVisitor name = new ClassNameVisitor();
        final String expected = "representation/asm/ClassNameTest";
        new ClassReader(new BytecodeObject(new BytecodeClass(expected)).bytecode().bytes())
            .accept(name, 0);
        MatcherAssert.assertThat(
            "Can't retrieve class name, or it's incorrect",
            name.asString(),
            Matchers.equalTo(expected)
        );
    }
}
