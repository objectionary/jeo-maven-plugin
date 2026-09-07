/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.ArrayList;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link XmlClassProperties}.
 * @since 0.1
 */
final class XmlClassPropertiesTest {

    @Test
    void createsXmirWithCorrectProperties() {
        final int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
        final String signature = "Ljava/util/ArrayList<Ljava/lang/String;>;";
        final String supername = "java/util/ArrayList";
        final String[] interfaces = {"java/util/List"};
        MatcherAssert.assertThat(
            "We expect that the properties will be created correctly and contain the correct values",
            new XmlClass(
                "Language",
                signature,
                new DirectivesClassProperties(
                    access,
                    supername,
                    interfaces
                )
            ).bytecode().properties(),
            Matchers.is(
                new BytecodeClassProperties(
                    access,
                    signature,
                    supername,
                    interfaces
                )
            )
        );
    }

    @Test
    void keepsClassSignatureSeparateFromSupername() {
        final int access = Opcodes.ACC_PUBLIC;
        final String supername = "java/util/ArrayList";
        final String signature = "<T::Ljava/lang/Number;>Ljava/util/ArrayList<TT;>;";
        final BytecodeClassProperties expected = new BytecodeClassProperties(
            access, signature, supername
        );
        MatcherAssert.assertThat(
            "We expect that the generic class signature and the supername survive the XMIR round-trip separately",
            new XmlObject(
                new BytecodeObject(
                    new BytecodeClass(
                        "Foo",
                        new ArrayList<>(0),
                        expected
                    )
                ).xml()
            ).bytecode().top().properties(),
            Matchers.equalTo(expected)
        );
    }
}
